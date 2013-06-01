package chrome

import org.jetbrains.chrome.Debuggee
import org.jetbrains.io.dto
import java.util.ArrayList

// http://src.chromium.org/viewvc/chrome/trunk/src/chrome/common/extensions/api/

public native trait Node {
  public val nodeId:Int
  public val nodeType:Int
  public val children:Array<Node>?
}

public native trait ChromeEvent<T> {
  public fun addListener(callback:T, filter:Any? = null, extraInfoSpec:Array<String>? = null)
  public fun removeListener(callback:T)

  public fun addRules(rules:Array<Any>, callback:(()->Unit)? = null)
}

public native trait MessageEvent : ChromeEvent<(message:Any)->Unit> {
  public fun addListener<T>(callback:(message:T)->Any?)
  override fun removeListener(callback:(message:Any)->Unit)
}

public native trait Port {
  public val name:String
  public val onDisconnect:ChromeEvent<(port:Port)->Unit>
  public val onMessage:MessageEvent
  public fun postMessage(message:Any):Unit = noImpl
}

private native open class PortImpl(override public val name:String) : Port {
  override val onMessage:MessageEvent = noImpl
  override val onDisconnect:ChromeEvent<(port:Port)->Unit> = noImpl
}

public native class PortOnConnect(name:String, public val sender:MessageSender) : PortImpl(name)

public native trait MessageSender {
  public val tab:Tab?
  public val id:String?
}

public native trait Debugger {
  // reason since chrome 22
  public val onDetach:ChromeEvent<(debuggee:Debuggee, reason:String? = null)->Any?>
  public val onEvent:ChromeEvent<(debuggee:Debuggee, method:String, data:Json)->Any?>

  public fun sendCommand<T>(target:Debuggee, method:String, params:Any?, callback:((result:T)->Unit)? = null)

  public fun attach(target:Debuggee, protocol:String, callback:(()->Unit)? = null)
  public fun detach(target:Debuggee, callback:(()->Unit)? = null)
}

public native trait Window {
  public val id:Int

  public val top:Int
  public val left:Int

  public val height:Int
  public val width:Int

  public val focused:Boolean
  public val alwaysOnTop:Boolean
  public val incognito:Boolean

  public val state:String

  public val tabs:Array<Tab>?

  public val `type`:String
}

public val Window.isNormal:Boolean
  get() = `type` == "normal"

public native trait Tab {
  public val url:String
  public val id:Int
  public val windowId:Int
  public val incognito:Boolean

  public val status:String?
}

public native trait ChangeInfo {
  public val url:String?
  public val status:String?
  public val pinned:Boolean?
}

public native trait Windows {
  public fun getCurrent(properties:Any? = null, callback:(window:Window?)->Unit)
  public fun getLastFocused(properties:Any? = null, callback:(window:Window?)->Unit)
  public fun getAll(properties:Any? = null, callback:(window:Array<Window>)->Unit)

  public fun create(properties:Any, callback:((window:Window)->Unit)? = null)
  public fun update(windowId:Int, properties:Any, callback:((window:Window)->Unit)? = null)
}

public native trait Tabs {
  public val onUpdated:ChromeEvent<(tabId:Int, changeInfo:ChangeInfo, tab:Tab)->Unit>
  public val onRemoved:ChromeEvent<(tabId:Int, removeInfo:Any)->Any?>

  public fun get(tabId:Int, callback:(tab:Tab)->Unit)

  public fun query(query:Any, callback:(tabs:Array<Tab>)->Unit)

  public fun sendRequest<T>(tabId:Int, request:Any, callback:(response:T)->Unit)
  public fun sendRequest(tabId:Int, request:Any)

  public fun reload(tabId:Int, properties:Any? = null)

  public fun create(properties:Any, callback:((tab:Tab)->Unit)? = null)
  public fun update(tabId:Int, properties:Any, callback:((tab:Tab)->Unit)? = null)
}

public native trait LastError {
  public val message:String
}

public native trait Extension {
  public val lastError:LastError?
  public val onRequest:ChromeEvent<(request:Any, sender:Any, sendResponse:(response:Any)->Unit)->Unit>
  public val onConnect:ChromeEvent<(port:PortOnConnect)->Any>

  public fun sendRequest<T>(extensionId:String?, request:Any, callback:((response:T)->Unit)?)
  public fun sendRequest<T>(request:Any, callback:((response:T)->Unit)? = null)
  public fun sendRequest(request:Any)

  public fun getURL(path:String)
}

public native trait App {
  // http://developer.chrome.com/trunk/apps/app.runtime.html
  public native trait Runtime {
    public native trait LaunchData {
      public val id:String?
      public val items:Array<Any>?
    }

    // http://developer.chrome.com/apps/app.window.html
    public native trait WindowApi {
      public native trait AppWindow {
        public val contentWindow:Any
      }

      public fun create(url:String, options:Any? = null, callback:((window:AppWindow)->Unit)? = null)
    }

    public val onLaunched:ChromeEvent<(data:LaunchData? = null)->Unit>
  }

  public fun getDetails():AppDetails
  public val runtime:Runtime
  public val window:Runtime.WindowApi
}

public native trait AppDetails {
  public val version:String
}

public native class StorageChange(val oldValue:Any, val newValue:Any)

public native trait Storage {
  public val local:StorageArea
  public val sync:StorageArea

  public val onChanged:ChromeEvent<(changes:Map<String, StorageChange>, areaName:String)->Unit>
}

public native trait StorageArea {
  fun get<T>(key:String?, callback:(data:T)->Unit):Unit

  public fun set(items:Any, callback:(()->Unit)? = null):Unit
}

public native trait SuggestResult {

}

public native trait Omnibox {
  public val onInputChanged:ChromeEvent<(text:String, suggest:((suggestResults:Array<SuggestResult>)->Unit)? = null)->Any>
}

public native trait BrowserAction {
  public val onClicked:ChromeEvent<(tab:Tab)->Unit>

  public fun disable(tabId:Int? = null)
  public fun enable(tabId:Int? = null)

  // you should use chrome jb extension methods
  fun setTitle(details:Json)
  fun setIcon(details:Any, callback:(()->Unit)? = null)
}

public native trait WebRequest {
  public native trait RequestDetails {
    public val requestId:String
    public val url:String
    public val method:String
    public val tabId:Int
  }

  public native trait BeforeRequestDetails : RequestDetails{
  }

  public native trait HttpHeader {
    public val name:String
    public var value:String?
  }

  public native trait HeadersReceivedDetails : RequestDetails {
    public val responseHeaders:ArrayList<HttpHeader>?
  }

  public native trait BeforeSendHeadersDetails : RequestDetails {
    public val requestHeaders:ArrayList<HttpHeader>?
  }

  public native trait CompletedDetails : RequestDetails {
    public val fromCache:Boolean
    public val statusCode:Int
  }

  public native trait ErrorDetails : RequestDetails {
    public val fromCache:Boolean
    public val error:String
  }

  public val onBeforeRequest:ChromeEvent<(details:BeforeRequestDetails)->Any?>
  public val onBeforeSendHeaders:ChromeEvent<(details:BeforeSendHeadersDetails)->Any?>
  public val onHeadersReceived:ChromeEvent<(details:HeadersReceivedDetails)->Any?>
  public val onCompleted:ChromeEvent<(details:CompletedDetails)->Unit>
  public val onErrorOccurred:ChromeEvent<(details:ErrorDetails)->Unit>

  public fun handlerBehaviorChanged(callback:(()->Unit)? = null)
}

public native fun createRequestMatcher(vararg p:Any?):Any = noImpl
public native fun createRedirectRequest(url:String):Any = noImpl

public native trait DeclarativeWebRequest {
  public val onRequest:ChromeEvent<()->Unit>
}

public native trait Chrome {
  public trait Cookies {
    public trait Cookie {
      public val name:String
      public val value:String
      public val domain:String
      public val path:String
      public val secure:Boolean
      public val httpOnly:Boolean
      public val hostOnly:Boolean
      public val session:Boolean
      public val expirationDate:Double
      public val storeId:String
    }

    public fun get(query:Any, callback:(cookie:Cookie)->Unit):Unit
    public fun getAll(query:Any, callback:(cookies:Array<Cookie>)->Unit):Unit
  }

  public val debugger:Debugger
  public val tabs:Tabs
  public val windows:Windows
  public val extension:Extension
  public val app:App
  public val storage:Storage
  public val omnibox:Omnibox
  public val browserAction:BrowserAction
  public val webRequest:WebRequest
  public val declarativeWebRequest:DeclarativeWebRequest
  public val cookies:Cookies
}

public native val chrome:Chrome = noImpl