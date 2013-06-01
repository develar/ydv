package org.jetbrains.ydv.import

import org.jetbrains.youtrackRest.Youtrack
import org.jetbrains.youtrackRest.login

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
    init(it.data as ImportMessage)
  }
}

private fun init(data:ImportMessage) {
  //login(data.host, data.login, data.password, { postMessage("cannot connect: " + it) }) {
  //  postMessage("connected")
    importIssues(Youtrack(data.host))
  //}
}

private fun importIssues(youtrack:Youtrack) {
  youtrack.getIssues("for: me", { postMessage(it) }) {
    postMessage(it)
  }
}