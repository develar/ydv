package org.jetbrains.ydv.background

import chrome.chrome
import org.jetbrains.chrome.launched

fun main(args:Array<String>) {
  chrome.app.runtime.launched {
    chrome.app.window.create("main.html")
  }
}