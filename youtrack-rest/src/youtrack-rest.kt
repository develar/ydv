package org.jetbrains.youtrackRest

import html.XMLHttpRequest

public fun login(host:String, login:String, password:String, errorCallback:((statusText:String)->Unit)? = null, callback:(youtrack:Youtrack)->Unit) {
  val request = XMLHttpRequest()
  request.open("POST", "$host/rest/user/login?login=$login&password=$password")
  request.setRequestHeader("Accept", "application/json")
  request.onreadystatechange = {
    if (request.readyState == XMLHttpRequest.DONE) {
      if (request.status == 200) {
        callback(Youtrack(host))
      }
      else {
        errorCallback?.invoke(request.statusText)
      }
    }
  }
  request.send()
}

public class Youtrack(private val host:String) {
  public fun getIssues(query:String, errorCallback:((statusText:String)->Unit)? = null, callback:(data:Any)->Unit):Unit {
    val request = XMLHttpRequest()
    request.open("GET", "$host/rest/issue?filter=$query")
    request.setRequestHeader("Accept", "application/json")
    //request.setRequestHeader("Cookie", cookie!!)
    request.withCredentials = true
    request.onreadystatechange = {
      if (request.readyState == XMLHttpRequest.DONE) {
        if (request.status == 200) {
          callback(request.response!!)
        }
        else {
          errorCallback?.invoke(request.statusText)
        }
      }
    }
    request.send()
  }
}