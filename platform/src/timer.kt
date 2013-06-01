package org.jetbrains.util

import html.window
import org.jetbrains.extensions.ExtensionPointName
import org.jetbrains.extensions.Extensions

public val TIMER_FACTORY_EP_NAME:ExtensionPointName<(delay:Int, callback:()->Unit)->Timer> = ExtensionPointName("org.jetbrains.util.timer")

private val doInit = {
  val extensionPoint = Extensions.rootArea.registerExtensionPoint(TIMER_FACTORY_EP_NAME)
  extensionPoint.registerExtension() {delay, callback ->
    WindowTimer(delay, callback)
  }
}()

public fun setTimeout(delay:Int, callback:()->Unit):Timer {
  val timer = TIMER_FACTORY_EP_NAME.extension(delay, callback)
  timer.start()
  return timer
}

public abstract class Timer(protected val callback:()->Unit) {
  public abstract var delay:Int

  public abstract val running:Boolean
     get;

  public abstract fun start()
  public abstract fun stop()
}

private class WindowTimer(delay:Int, callback:()->Unit) : Timer(callback) {
  private var timeoutId:Long = -1.toLong()

  override public var delay:Int = delay
    set (value) {
      $delay = value
      if (running) {
        stop()
        start()
      }
    }

  override val running:Boolean
    get() = timeoutId != -1.toLong()

  private val callbackWrapper = {
    timeoutId = -1.toLong()
    callback()
  }

  override fun start() {
    if (!running) {
      timeoutId = window.setTimeout(callbackWrapper, delay)
    }
  }

  override fun stop() {
    if (!running) {
      return;
    }

    window.clearTimeout(timeoutId)
    timeoutId = -1.toLong()
  }
}