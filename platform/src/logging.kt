package org.jetbrains.logging

import org.jetbrains.extensions.ExtensionPointName
import org.jetbrains.extensions.Extensions

import html.console

public val LOGGER_FACTORY_EP_NAME:ExtensionPointName<(category:String)->Logger?> = ExtensionPointName("org.jetbrains.logging.logger")

private val doInit = {
  val extensionPoint = Extensions.rootArea.registerExtensionPoint(LOGGER_FACTORY_EP_NAME)
  extensionPoint.registerExtension() {
    ConsoleLogger()
  }
}()

public fun getLogger(category:String):Logger {
  for (factory in LOGGER_FACTORY_EP_NAME.extensions) {
    val logger = factory(category)
    if (logger != null) {
      return logger;
    }
  }

  throw IllegalStateException()
}

public trait Logger {
  public val debugEnabled:Boolean
    get;

  public fun info(message:String)
  public fun warn(message:String)

  public fun error(message:String)
  public fun error(e:Throwable)

  public fun debug(vararg objects:Any?)
}

private class ConsoleLogger : Logger {
  override val debugEnabled:Boolean = true

  override fun info(message:String) {
    console.info(message)
  }

  override fun warn(message:String) {
    console.warn(message)
  }

  override fun error(message:String) {
    console.error(message);
  }

  override fun error(e:Throwable) {
    console.error(e, (e as Exception).stack);
  }

  override fun debug(vararg objects:Any?) {
    if (debugEnabled) {
      console.log(objects)
    }
  }
}