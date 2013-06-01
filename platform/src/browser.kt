package org.jetbrains.browserConnection

public trait TabService<T: Any> {
  public fun query(callback:(tabs:Iterator<T>)->Unit)
  public fun reload(tab:T, bypassCache:Boolean = true)

  public fun load(url:String, uriToOpen:String?, existingTab:T?, newTab:T?, focusWindow:Boolean, callback:((tab:T)->Unit)?) {
    val normalizedUriToOpen = uriToOpen ?: url
    if (existingTab == null) {
      if (newTab == null) {
        createTab(normalizedUriToOpen, focusWindow, callback)
      }
      else {
        updateTab(newTab, normalizedUriToOpen, focusWindow, callback)
      }
    }
    else {
      updateTab(existingTab, uriToOpen, focusWindow, callback)
    }
  }

  public fun createTab(uri:String, focusWindow:Boolean, callback:((tab:T)->Unit)?)
  public fun updateTab(tab:T, uri:String?, focusWindow:Boolean, callback:((tab:T)->Unit)?)
}