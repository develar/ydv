package org.jetbrains.ydv

import org.jetbrains.youtrackRest.Youtrack

public native trait MessageEvent {
  val data:Any
}

public native trait ImportMessage {
  val host:String
  val login:String
  val password:String
}

public native var onmessage:((event:MessageEvent)->Unit)? = noImpl

public native fun postMessage(message:Any?):Unit = noImpl
public native fun importScripts(path:String):Unit = noImpl

fun main(args:Array<String>) {
  onmessage = {
    val data = it.data as ImportMessage
    val youtrack = Youtrack()
    if (youtrack.connect(data.host, data.login, data.password)) {
      postMessage("connected")
    }
    importIssues(youtrack)
  }
}

private fun importIssues(youtrack:Youtrack) {
  val issues = youtrack.getIssues("for: me")
}