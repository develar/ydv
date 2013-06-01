package org.jetbrains.io

import html.XMLHttpRequest

// KT-2187
public native fun kt_invoke(o:Any, m:String, args:Array<Any?>?):Unit = noImpl

private native fun parseUrl(spec:String):Url? = noImpl

public native fun dto<T>(vararg  p:Any?):T = noImpl

public native class Url(public val scheme:String, public val host:String, public val port:String?, public val path:String, public val directory:String?, public val authority:String?)

private val queryOrFragmentRegExp = RegExp("(\\?|#|;).*$", "g")

public fun String.trimQueryOrFragment():String = replace(queryOrFragmentRegExp, "")

public val String.isDataUri:Boolean
  get() = startsWith("data:")

public val String.isInLocalFileSystem:Boolean
  get() = startsWith("file://")

public val String.asParsedUrl:Url?
  get() = parseUrl(this)

public fun loadResource(url:String, errorCallback:((statusText:String)->Unit)?, callback:(data:String)->Unit) {
  loadResource(url, true, errorCallback, callback)
}

public fun loadResource(url:String, preventCaching:Boolean = true, errorCallback:((statusText:String)->Unit)? = null, callback:(data:String)->Unit) {
  val request = XMLHttpRequest()
  request.open("GET", if (preventCaching && !url.startsWith("file:") && !url.contains('?')) "$url?time=${Date.now()}" else url)
  request.onreadystatechange = {
    if (request.readyState == XMLHttpRequest.DONE) {
      if (request.status == 200 || request.status == 0) {
        callback(request.responseText!!)
      }
      else {
        errorCallback?.invoke(request.statusText)
      }
    }
  }
  request.send()
}

public fun canonicalizeUri(uri:String, baseUrl:String):String {
  if (uri.isDataUri || uri.asParsedUrl != null) {
    return uri;
  }

  val base = baseUrl.asParsedUrl!!
  var baseHost = base.scheme + "://"
  if (base.authority != null) {
    baseHost += base.authority
  }
  if (uri[0] == '/') {
    if (uri.length > 1 && uri[1] == '/') {
      // href starts with "//" which is a full URL with the protocol dropped (use the baseUrl protocol)
      return base.scheme + ":" + uri;
    }
    else {
      return baseHost + uri;
    }
  }

  var result = baseHost + (base.directory ?: "/") + uri
  val queryOrFragmentInfo = queryOrFragmentRegExp.exec(result)
  var path:String = if (queryOrFragmentInfo == null) result else result.substring(0, queryOrFragmentInfo.index)
  // remove any single dots
  path = path.replace(RegExp("/\\./", "g"), "/")
  // remove any double dots and the path previous
  val doubleDotRegExp = RegExp("/((?!\\.\\./)[^/]*)/\\.\\./")
  while (doubleDotRegExp.matches(path)) {
    path = path.replace(doubleDotRegExp, "/")
  }

  if (queryOrFragmentInfo == null) {
    return path
  }

  return path + queryOrFragmentInfo[0]
}