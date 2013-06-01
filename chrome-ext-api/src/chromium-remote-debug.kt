package org.jetbrains.chromium.debug

import chrome.*
import org.jetbrains.logging.getLogger
import org.jetbrains.chrome.successfully
import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto

// http://trac.webkit.org/browser/trunk/Source/WebCore/inspector/Inspector.json?format=txt

private val LOG = getLogger("org.jetbrains.chromium.debug")

private fun sendCommand<T>(debuggee:Debuggee, method:String, params:Any?, resultPropertyName:String, callback:(result:T)->Unit, errorCallback:(()->Unit)? = null) {
  sendCommand(debuggee, method, params, errorCallback, {
    callback(it.get<T>(resultPropertyName))
  })
}

private fun sendCommand(debuggee:Debuggee, method:String, params:Any? = null, errorCallback:(()->Unit)? = null, callback:((result:Json)->Unit)? = null) {
  if (LOG.debugEnabled) {
    LOG.debug("DC ${debuggee.tabId} $method${if (params == null) "" else JSON.stringify(params)}")
  }

  chrome.debugger.sendCommand<Json>(debuggee, method, params) {
    if (successfully()) {
      if (LOG.debugEnabled) {
        val m = "DR ${debuggee.tabId} $method"
        when (method) {
          "DOM.getDocument" -> LOG.debug(m, it.get<Node>("root").nodeId)
          "CSS.setStyleSheetText" -> LOG.debug(m)
          else -> LOG.debug(m, it)
        }
      }
      callback?.invoke(it)
    }
    else {
      errorCallback?.invoke()
    }
  }
}

public abstract class RemoteCommandDomain(protected val debuggee:Debuggee) {
  protected abstract val domain:String

  public var enabled:Boolean = false
    protected set

  protected fun callWhenEnabled(callback:()->Unit) {
    if (enabled) callback() else enable(callback)
  }

  public fun enable(errorCallback:(()->Unit)? = null, callback:(()->Unit)? = null) {
    if (enabled) {
      callback?.invoke()
      return
    }

    sendCommand(debuggee, "$domain.enable", null, errorCallback) {
      enabled = true
      callback?.invoke()
    }
  }

  // callback will be called also if error was occurred
  public fun disable(callback:(()->Unit)? = null) {
    if (enabled) {
      sendCommand(debuggee, "$domain.disable", null, callback) {
        enabled = false
        callback?.invoke()
      }
    }
  }
}

public class RemotePage(debuggee:Debuggee) : RemoteCommandDomain(debuggee) {
  protected override val domain:String
    get() = "Page"

  public fun reload() {
    sendCommand(debuggee, "$domain.reload", dto("ignoreCache", true))
  }
}