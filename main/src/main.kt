package org.jetbrains.ydv

import html.*
import chrome.*
import org.jetbrains.chrome.get

private val importer:Worker = Worker("importer-wrapper.js")

private enumerable class ImportMessage(val host:String)

fun main(args:Array<String>) {
  chrome.storage.sync.get<String>("currentHost") {
    if (it == null) {

    }
    else {
      //
    }
  }

  document.addEventListener("DOMContentLoaded", {
    (document.getElementById("importButton") as HTMLButtonElement).onclick = {
      importIssues("", "")
    }
  })
}

fun importIssues(login:String, password:String) {
  importer.postMessage(ImportMessage("http://youtrack.jetbrains.com"))
  importer.onmessage = {
    html.console.log(it.data!!);
  }
}