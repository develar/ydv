package com.jetbrains.browserConnection

import java.util.ArrayList
import org.jetbrains.browserConnection.TabService
import org.jetbrains.io.jsonRpc.JsonRpcServer
import org.jetbrains.io.webSocket.Socket
import org.jetbrains.util.concurrency.QueueProcessor

public val DEFAULT_JB_HOST:String = "127.0.0.1"
public val DEFAULT_JB_PORT:String = "63342"

public class BuildInfo(public val name:String, public val baselineVersion:Int, public val buildNumber:Int?)

public fun BuildInfo.greaterOrEquals(baseline:Int, build:Int):Boolean {
  if (baselineVersion < baseline) {
    return false
  }
  val bn = buildNumber
  if (bn != null && bn != 0 && bn < build) {
    return false
  }
  return true
}


public fun connect(version:String, family:String, host:String = DEFAULT_JB_HOST, port:String = DEFAULT_JB_PORT, preProcess:((socket:Socket<String>)->Unit)? = null, postProcess:((rpcServer:JsonRpcServer)->Unit)? = null, reconnectTimeout:Int = 5000):JsonRpcServer {
  val socket:Socket<String> = Socket(null, reconnectTimeout)
  preProcess?.invoke(socket)
  socket.connect("ws://$host:$port/jsonRpc?v=$version&f=$family")
  val rpcServer:JsonRpcServer = JsonRpcServer(socket)
  postProcess?.invoke(rpcServer)
  return rpcServer
}

public fun registerDomService<T>(pageManager:PageManager<T>, rpcServer:JsonRpcServer) {
  rpcServer.registerDomain("Dom", DomService(pageManager))
}

public trait DebuggerCommand {
  public val id:Int
  public val method:String // Domain.command
  public val params:Any
}

public abstract class DebuggerService<T, P : PageManager<T>>(protected val pageManager:P) {
  protected val queueProcessor:QueueProcessor<(callback:()->Unit)->Unit> = QueueProcessor({item, done -> item(done)})

  fun attach(url:String, usePreliminaryPage:Boolean, callback:(tabId:Int)->Unit, errorCallback:(message:String)->Unit) {
    queueProcessor.add {done ->
      try {
        // we don't need focusWindow — it is done implicitly by attachDebugger (chrome native behaviour)
        val urlToOpen = if (usePreliminaryPage) "data:text/html;base64," + html.window.btoa("<!DOCTYPE html><title>Loading $url</title>") else null
        pageManager.getOrCreateTab(url, urlToOpen, false) {
          pageManager.attachDebugger(it, !usePreliminaryPage, wrapCallFinally(callback, done), wrapCallFinally(errorCallback, done))
        }
      }
      catch (e:Exception) {
        try {
          errorCallback(e.toString())
        }
        finally {
          done()
        }
      }
    }
  }

  protected fun <T> wrapCallFinally(callback:(parameter:T)->Unit, finallyCallback:()->Unit):(parameter:T)->Unit = {
    try {
      callback(it)
    }
    finally {
      finallyCallback()
    }
  }

  public abstract fun sendCommand(tabId:Int, command:DebuggerCommand)
}

public fun compareUrls(a:String, b:String):Boolean {
  if (a == b) {
    return true
  }

  val filePrefix = "file://"
  if (a.startsWith(filePrefix) && b.startsWith(filePrefix)) {
    return normalizeFileUrl(a) == normalizeFileUrl(b)
  }

  return a.length == b.length || a.trimTrailing('/') == b.trimTrailing('/')
}

private fun String.trimTrailing(char:Char):String {
  var index = length - 1;
  while (index >= 0 && charAt(index) == char) {
    index--
  }
  return substring(0, index + 1);
}

private val LOCALHOST_FILE_PREFIX = "file://localhost/"

public fun normalizeFileUrl(url:String):String {
  if (url.startsWith(LOCALHOST_FILE_PREFIX)) {
    return "file:///" + url.substring(LOCALHOST_FILE_PREFIX.length)
  }
  else {
    return url
  }
}

public fun normalizeTabUriPath(path:String?):String? {
  return if (path == "/") null else path
}

public fun isInspectableBackedByPattern(scheme:String, host:String?):Int {
  if (scheme == "file" || scheme == "data") {
    return 1
  }
  else if (!(scheme == "http" || scheme == "https")) {
    return -1
  }

  if (host == null) {
    throw IllegalArgumentException("host can be null only if protocol equals file")
  }

  // assume myDomain as localhost (i.e. without top-level domain)
  // subdomains of localhost
  if (!host.contains(".") || host.endsWith(".localhost") || host.endsWith(".local") || host.endsWith(".dev")) {
    return 1
  }

  return 0
}

public abstract class PageManager<T>(public val tabService:TabService<T>, protected val rpcServer:JsonRpcServer) {
  protected fun filterInspectable(projectId:String?, hostAndPathPairs:ArrayList<String?>, callback:((result:Array<Int>)->Unit)) {
    rpcServer.send<Array<Int>>("Pages", "filterInspectable", "\"$projectId\", ${JSON.stringify(hostAndPathPairs)}", callback)
  }

  public fun execute(projectId:String, handler:(dom:Dom)->Unit) {
    execute2(projectId, false, handler)
  }

  public fun execute2(projectId:String?, onlyIfAttached:Boolean, handler:(dom:Dom)->Unit) {
    process(projectId) {
      executeForTab(it, onlyIfAttached, handler)
    }
  }

  protected abstract fun executeForTab(tab:T, onlyIfAttached:Boolean, handler:(dom:Dom)->Unit)

  public fun reload() {
    process(null) {
      tabService.reload(it)
    }
  }

  protected abstract fun process(projectId:String?, processor:(tab:T)->Unit)

  public abstract fun getOrCreateTab(url:String, urlToOpen:String? = null, focusWindow:Boolean = true, callback:((tab:T)->Unit)? = null)
  public abstract fun attachDebugger(tab:T, externalEventEnabled:Boolean, callback:(tabId:Int)->Unit, errorCallback:(message:String)->Unit)
}