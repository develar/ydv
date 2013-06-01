package org.jetbrains.ydv

import html.*
import chrome.*
import org.jetbrains.chrome.getAll

private val importer:Worker = Worker("importer-wrapper.js")

private enumerable class ImportMessage(val host:String, val login:String, val password:String)

fun main(args:Array<String>) {
    document.addEventListener("DOMContentLoaded", {
      (document.getElementById("importButton") as HTMLButtonElement).onclick = {
        importIssues("", "")
      }
    })
}

fun importIssues(login:String, password:String) {
  importer.postMessage(ImportMessage("http://youtrack.jetbrains.com", login, password))
  importer.onmessage = {
    html.console.log(it.data!!);
  }
}