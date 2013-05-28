package org.jetbrains.youtrackRest

import html.XMLHttpRequest

public class Youtrack {
  public fun connect(host:String, login:String, password:String) {
    val request = XMLHttpRequest()
    request.setRequestHeader("Accept", "application/json")
    request.open("POST", "$host/rest/user/login?login=$login&password=$password", false)
    request.send()
    if (request.response != null) {

    }
  }
}