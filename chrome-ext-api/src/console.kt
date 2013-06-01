package org.jetbrains.chromium.debug

import org.jetbrains.chrome.Debuggee

public native trait ConsoleMessage {
  public val level:String
  public val text:String
  public val url:String?
  public val line:Int?

  public val parameters:Array<RemoteObject>?
  public val stackTrace:Array<CallFrame>?

  public val `type`:String
}

public class RemoteConsole(debuggee:Debuggee) : RemoteCommandDomain(debuggee) {
  protected override val domain:String
    get() = "Console"
}