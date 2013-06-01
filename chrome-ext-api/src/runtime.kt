package org.jetbrains.chromium.debug

import java.util.ArrayList
import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto

native
public trait RemoteObject {
  public val objectId:String?
  public val value:Any?
}

native
public trait CallArgument {
  public val objectId:String
}

public object RemoteRuntime {
  public fun callFunctionOn(debuggee:Debuggee, objectId:String, functionDeclaration:String, callback:(remoteObject:RemoteObject)->Unit, errorCallback:(()->Unit)? = null, args:(Array<CallArgument>)? = null, returnByValue:Boolean = false) {
    sendCommand<RemoteObject>(debuggee, "Runtime.callFunctionOn", dto("objectId", objectId, "functionDeclaration", functionDeclaration, "returnByValue", returnByValue), "result", callback, errorCallback)
  }

  public fun releaseObject(debuggee:Debuggee, objectId:String) {
    sendCommand(debuggee, "Runtime.releaseObject", dto("objectId", objectId))
  }

  public fun releaseObjectGroup(debuggee:Debuggee, objectGroup:String) {
    sendCommand(debuggee, "Runtime.releaseObjectGroup", dto("objectGroup", objectGroup))
  }
}

class Disposer(val debuggee:Debuggee) {
  private val ids = ArrayList<String>()
  private var released = false

  val releaseCallback = {():Unit ->
    if (released) {
      // todo assert
      throw Exception()
    }

    released = true

    for (id in ids) {
      RemoteRuntime.releaseObject(debuggee, id)
    }
  }

  fun add(remoteObject:RemoteObject) {
    if (released) {
      // todo assert
      throw Exception()
    }

    val objectId = remoteObject.objectId
    if (objectId != null) {
      ids.add(objectId)
    }
  }

  fun release() {
    releaseCallback()
  }
}