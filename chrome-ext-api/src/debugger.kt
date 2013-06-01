package org.jetbrains.chromium.debug

import org.jetbrains.util.setTimeout
import java.util.HashMap
import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto

public val SCRIPT_PARSED: String = "Debugger.scriptParsed"

native
public trait CallFrame {
  public val url:String
  public val lineNumber:Int
  public val columnNumber:Int
  public val functionName:String
}

native
public trait ScriptParsedData : Json {
  public val scriptId:String
  public val url:String
  public val isContentScript:Boolean?
  public val sourceMapURL:String?

  public val startLine:Int
  public val startColumn:Int
  public val endLine:Int
  public val endColumn:Int

  public val hasSourceURL:Boolean?
}

native
public trait ScriptPausedData {
  public val callFrames:Array<CallFrame>
  public val reason:String
  public val data:Any?
}

public class RemoteDebugger(debuggee:Debuggee) : RemoteCommandDomain(debuggee) {
  private val scripts = HashMap<String, ScriptParsedData>()

  protected override val domain:String
    get() = "Debugger"

  public fun getScripts(callback:(list:Collection<ScriptParsedData>)->Unit) {
    if (enabled) {
      callback(scripts.values())
    }
    else {
      callWhenEnabled {
        // wait, Debugger.scriptParsed is also fired for all known and uncollected scripts upon enabling debugger
        setTimeout(1000) {
          callback(scripts.values())
        }
      }
    }
  }

  public fun reset() {
    scripts.clear()
  }

  public fun scriptParsed(info:ScriptParsedData) {
    scripts.put(info.url, info)
  }

  public fun setScriptSource(scriptId:String, source:String) {
    callWhenEnabled {
      sendCommand(debuggee, "Debugger.setScriptSource", dto("scriptId", scriptId, "scriptSource", source))
    }
  }
}