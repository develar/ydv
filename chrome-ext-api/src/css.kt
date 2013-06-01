package org.jetbrains.chromium.debug

import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto

// todo kotlin bug
//enum class CssRuleOrigin(val v:String) {
//  REGULAR : CssRuleOrigin("regular")
//}

public object StyleSheetOrigin {
  public val REGULAR:String = "regular"
}

native
public trait CssStyleSheetHeader {
  public val styleSheetId:String
  public val sourceURL:String
  public val title:String
  public val disabled:Boolean
  public val origin:String
}

native
public trait CssStyleSheetBody {
  public val styleSheetId:String
  public val rules:Array<CssRule>
  public val text:String
}

native
public trait CssRule {
  public val ruleId:Int
  public val selectorText:String
  public val sourceURL:String
  public val sourceLine:Int
  public val origin:String
  public val style:CssStyle
}

native
public class CssStyleId(val styleSheetId:String, val ordinal:Int)
native
public class CssStyle(val styleId:CssStyleId, val cssProperties:Array<CssProperty>)
native
public class CssProperty(val name:String, val value:String, val parsedOk:Boolean?, val text:String?)

public class RemoteCss(debuggee:Debuggee) : RemoteCommandDomain(debuggee) {
  protected override val domain:String
    get() = "CSS"

  public fun getInlineStylesForNode(nodeId:Int, callback:(inlineStyle:CssStyle?, attributesStyle:CssStyle?)->Unit) {
    callWhenEnabled {
      sendCommand(debuggee, "CSS.getInlineStylesForNode", dto("nodeId", nodeId)) {
        callback(it.get("inlineStyle"), it.get("attributesStyle"))
      }
    }
  }

  public fun getStyleSheets(callback:(headers:Array<CssStyleSheetHeader>)->Unit) {
    callWhenEnabled {
      sendCommand(debuggee, "CSS.getAllStyleSheets", null:Any?, "headers", callback)
    }
  }

  public fun getStyleSheet(id:String, callback:(styleSheet:CssStyleSheetBody)->Unit) {
    sendCommand(debuggee, "CSS.getStyleSheet", dto("styleSheetId", id), "styleSheet", callback)
  }

  public fun setStyleSheetText(id:String, text:String) {
    sendCommand(debuggee, "CSS.setStyleSheetText", dto("styleSheetId", id, "text", text))
  }

  public fun setProperty(style:CssStyle, name:String, value:String?, onlyExisting:Boolean):Boolean {
    var overwrite = false
    var index = 0
    for (property in style.cssProperties) {
      if (property.name == name) {
        overwrite = true
        break
      }
      index++
    }

    if (!overwrite && onlyExisting) {
      return false
    }

    setProperty(style.styleId, index, name, value, overwrite)
    return true
  }

  public fun setProperty(styleId:CssStyleId, propertyIndex:Int, name:String, value:String?, overwrite:Boolean) {
    val v = if (value == null) "" else value
    sendCommand<CssStyle>(debuggee, "CSS.setPropertyText", dto("styleId", styleId, "propertyIndex", propertyIndex, "text", "$name: $v;", "overwrite", overwrite), "style", {
      val property = it.cssProperties[propertyIndex]
      if (property.parsedOk != null && !property.parsedOk) {
        LOG.error("parsedOk false for set css property, actual text: ${property.text} passed name: $name, actual name: ${property.name}, passed value: $value, actual value: ${property.value}")
      }
    }, null)
  }
}