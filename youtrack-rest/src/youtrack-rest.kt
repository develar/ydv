package org.jetbrains.youtrackRest

import html.XMLHttpRequest

public class Youtrack {
  public fun connect(host:String, login:String, password:String):Boolean {
    val request = XMLHttpRequest()
    request.open("POST", "$host/rest/user/login", false)
    request.setRequestHeader("Accept", "application/json")
    request.send("login=$login&password=$password")
    html.console.log(request.response!!)
    if (request.response != null) {

    }
    return true
  }
}