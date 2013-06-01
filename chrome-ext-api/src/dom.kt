package org.jetbrains.chromium.debug

import chrome.Node
import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto

enumerable
class RGBA(val r:Int, val g:Int, val b:Int, val a:Double = 1.0)

enumerable
private class HighlightConfig {
  val contentColor = RGBA(111, 168, 220, 0.66)
  val paddingColor = RGBA(147, 196, 125, 0.55)
  val borderColor = RGBA(255, 229, 153, 0.66)
  val marginColor = RGBA(246, 178, 107, 0.66)

  val showInfo = true
}

public fun String.escapeQuotes():String = replace(RegExp("('|\")","g"), "\\$1")

public object RemoteDom {
  private val highlightConfig = HighlightConfig()

  public fun resolveNode(debuggee:Debuggee, nodeId:Int, callback:(remoteObject:RemoteObject)->Unit) {
    sendCommand<RemoteObject>(debuggee, "DOM.resolveNode", dto("nodeId", nodeId), "object", callback)
  }

  public fun callFunctionOn(debuggee:Debuggee, nodeId:Int, functionDeclaration:String, callback:(remoteObject:RemoteObject, disposer:Disposer)->Unit) {
    resolveNode(debuggee, nodeId, {remoteObject ->
      val disposer = Disposer(debuggee)
      disposer.add(remoteObject)
      RemoteRuntime.callFunctionOn(debuggee, remoteObject.objectId!!, functionDeclaration, {remoteObject ->
        disposer.add(remoteObject)
        callback(remoteObject, disposer)
      }, disposer.releaseCallback)
    })
  }

  public fun computeObject<T>(debuggee:Debuggee, nodeId:Int, functionDeclaration:String, callback:((result:T)->Unit)? = null) {
    callFunctionOn(debuggee, nodeId, functionDeclaration, {remoteObject, disposer ->
      disposer.release()
      if (callback != null) {
        callback(remoteObject.value as T)
      }
    })
  }

  public fun computeNode(debuggee:Debuggee, nodeId:Int, functionDeclaration:String, callback:(nodeId:Int)->Unit) {
    callFunctionOn(debuggee, nodeId, functionDeclaration, {remoteObject, disposer ->
      requestNode(debuggee, remoteObject.objectId!!, {nodeId ->
        disposer.release()
        callback(nodeId)
      }, disposer.releaseCallback)
    })
  }

  public fun requestNode(debuggee:Debuggee, objectId:String, callback:(nodeId:Int)->Unit, errorCallback:(()->Unit)? = null) {
    sendCommand<Int>(debuggee, "DOM.requestNode", dto("objectId", objectId), "nodeId", callback, errorCallback)
  }

  public fun getDocument(debuggee:Debuggee, callback:(node:Node)->Unit) {
    sendCommand(debuggee, "DOM.getDocument", null, "root", callback)
  }

  public fun querySelector(debuggee:Debuggee, nodeId:Int, selector:String, callback:(nodeId:Int)->Unit) {
    sendCommand<Int?>(debuggee, "DOM.querySelector", dto("nodeId", nodeId, "selector", selector), "nodeId", {nodeId ->
      // chrome issue — nodeId will be 0 instead of -1 if not found
      callback(if (nodeId == null || nodeId == 0) -1 else nodeId)
    })
  }

  public fun setAttributeValue(debuggee:Debuggee, nodeId:Int, name:String, value:String) {
    sendCommand(debuggee, "DOM.setAttributeValue", dto("nodeId", nodeId, "name", name, "value", value))
  }

  public fun setNodeValue(debuggee:Debuggee, nodeId:Int, value:String) {
    sendCommand(debuggee, "DOM.setNodeValue", dto("nodeId", nodeId, "value", value))
  }

  public fun setOuterHtml(debuggee:Debuggee, nodeId:Int, outerHtml:String) {
    sendCommand(debuggee, "DOM.setOuterHTML", dto("nodeId", nodeId, "outerHTML", outerHtml))
  }

  public fun requestChildNodes(debuggee:Debuggee, nodeId:Int) {
    sendCommand(debuggee, "DOM.requestChildNodes", dto("nodeId", nodeId))
  }

  public fun highlightNode(debuggee:Debuggee, nodeId:Int) {
    sendCommand(debuggee, "DOM.highlightNode", dto("nodeId", nodeId, "highlightConfig", highlightConfig))
  }

  public fun hideHighlight(debuggee:Debuggee) {
    sendCommand(debuggee, "DOM.hideHighlight")
  }
}