package org.jetbrains.youtrackRest

import html.XMLHttpRequest

public class Youtrack {
  //private var cookie:String? = null
  private var host:String? = null

  public fun connect(host:String, login:String, password:String):Boolean {
    val request = XMLHttpRequest()
    request.open("POST", "$host/rest/user/login?login=$login&password=$password", false)
    request.setRequestHeader("Accept", "application/json")
    request.send()
    if (request.status != 200) {
      return false
    }

    //cookie = request.getResponseHeader("Set-Cookie")
    this.host = host
    return true
  }

  public fun getIssues(query:String):Any {
    val request = XMLHttpRequest()
    request.open("GET", "$host/rest/issue?filter=$query", false)
    request.setRequestHeader("Accept", "application/json")
    //request.setRequestHeader("Cookie", cookie!!)
    request.withCredentials = true
    request.send()
    return request.response!!
  }
}