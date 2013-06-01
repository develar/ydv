package org.jetbrains.chrome

import chrome.*
import org.jetbrains.io.dto
import org.jetbrains.logging.getLogger
import org.jetbrains.util.Disposable
import chrome.WebRequest.BeforeRequestDetails
import chrome.WebRequest.HeadersReceivedDetails
import chrome.WebRequest.BeforeSendHeadersDetails
import chrome.WebRequest.HttpHeader
import java.util.ArrayList
import chrome.WebRequest.CompletedDetails
import chrome.WebRequest.ErrorDetails
import chrome.App.Runtime.LaunchData

private val LOG = getLogger("org.jetbrains.chrome")

public fun successfully():Boolean {
  val lastError = chrome.extension.lastError
  if (lastError == null) {
    return true
  }

  LOG.error(lastError.message)
  return false
}

public enumerable class Debuggee (public val tabId:Int) {
  public fun toString():String = tabId.toString()
}

private native fun isObjectEmpty(o:Any) = noImpl

public fun StorageArea.get<T : Any>(key:String? = null, callback:(data:T?)->Unit) {
  this.get<T>(key) {
    callback(if (!successfully() || isObjectEmpty(it)) null else it)
  }
}

private class DisposableChromeEvent<T>(private val event:ChromeEvent<T>, private val listener:T) : Disposable {
  override public fun dispose() {
    event.removeListener(listener)
  }
}

private fun <T> addListener(event:ChromeEvent<T>, listener:T, disposable:Disposable? = null, filter:Any? = null, extraInfoSpec:Array<String>? = null) {
  if (extraInfoSpec == null) {
    event.addListener(listener, filter)
  }
  else {
    event.addListener(listener, filter, extraInfoSpec)
  }

  if (disposable != null) {
    disposable.register(DisposableChromeEvent(event, listener))
  }
}

public fun Tabs.updated(disposable:Disposable? = null, listener:(tabId:Int, changeInfo:ChangeInfo, tab:Tab)->Unit) {
  addListener(onUpdated, listener, disposable)
}

public fun Tabs.removed(disposable:Disposable? = null, listener:(tabId:Int, removeInfo:Any)->Unit) {
  addListener(onRemoved, listener, disposable)
}

public fun Debugger.detached(disposable:Disposable? = null, listener:(debuggee:Debuggee, reason:String? = null)->Unit) {
  addListener(onDetach, listener, disposable)
}

public fun Debugger.eventEmitted(disposable:Disposable? = null, listener:(debuggee:Debuggee, method:String, data:Json)->Unit) {
  addListener(onEvent, listener, disposable)
}

public fun Extension.connected(disposable:Disposable? = null, listener:(port:PortOnConnect)->Unit) {
  addListener(onConnect, listener, disposable)
}

public fun <T : Any> Port.message(disposable:Disposable? = null, listener:(message:T)->Unit) {
  addListener<(message:Any)->Unit>(onMessage, listener as (message:Any)->Unit, disposable)
}

public fun Port.disconnected(disposable:Disposable? = null, listener:(port:Port)->Unit) {
  addListener(onDisconnect, listener, disposable)
}

public fun Storage.changed(disposable:Disposable? = null, listener:(changes:Map<String, StorageChange>, areaName:String)->Unit) {
  addListener(onChanged, listener, disposable)
}

public fun Storage.localChanged(disposable:Disposable? = null, listener:(changes:Map<String, StorageChange>)->Unit) {
  changed(disposable) {changes, areaName ->
    if (areaName == "local") {
      listener(changes)
    }
  }
}

public fun BrowserAction.clicked(disposable:Disposable? = null, listener:(tab:Tab)->Unit) {
  addListener(onClicked, listener, disposable)
}

// https://plus.google.com/106049295903830073464/posts/3KCAUbAPauJ
public fun BrowserAction.setIcon(path19:String, path38:String) {
  this.setIcon(dto("path", dto("19", chrome.extension.getURL(path19), "38", chrome.extension.getURL(path38))))
}
public fun BrowserAction.setTitle(title:String, tabId:Int? = null) {
  this.setTitle(dto("title", title, "tabId", tabId))
}

public fun WebRequest.beforeRequested(disposable:Disposable, filter:Any, blocking:Boolean = false, listener:(details:BeforeRequestDetails)->Any?) {
  addListener(onBeforeRequest, listener, disposable, filter, if (blocking) array("blocking") else null)
}

public fun WebRequest.headersReceived(disposable:Disposable?, filter:Any, blocking:Boolean = false, listener:(details:HeadersReceivedDetails)->Any?) {
  addListener(onHeadersReceived, listener, disposable, filter, if (blocking) array("blocking") else null)
}

public fun WebRequest.beforeSendHeaders(disposable:Disposable?, filter:Any, blocking:Boolean = false, listener:(details:BeforeSendHeadersDetails)->Any?) {
  addListener(onBeforeSendHeaders, listener, disposable, filter, if (blocking) array("blocking") else null)
}

public fun WebRequest.completed(disposable:Disposable?, filter:Any, listener:(details:CompletedDetails)->Unit) {
  addListener(onCompleted, listener, disposable, filter)
}

public fun WebRequest.errorOccurred(disposable:Disposable?, filter:Any, listener:(details:ErrorDetails)->Unit) {
  addListener(onErrorOccurred, listener, disposable, filter)
}

public fun httpHeader(name:String, value:String):HttpHeader = dto("name", name, "value", value)

public fun asResponseHeaders(result:Array<HttpHeader>):Any = dto("responseHeaders", result)
public fun asResponseHeaders(result:ArrayList<HttpHeader>):Any = dto("responseHeaders", result)

public fun App.Runtime.launched(disposable:Disposable? = null, listener:(details:LaunchData)->Unit) {
  addListener(onLaunched, listener, disposable)
}

public fun focusWindow(id:Int) {
  chrome.windows.update(id, dto("focused", true))
}