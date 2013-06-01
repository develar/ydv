package com.jetbrains.browserConnection

// KT-1908
public object SelectorSubjects {
  public val AS_IS:SelectorSubject = SelectorSubject()
  public val PARENT:SelectorSubject = SelectorSubject()
  public val HTML:SelectorSubject = SelectorSubject()

  public fun valueOf(ordinal:Int):SelectorSubject {
    when (ordinal) {
      0 ->  return SelectorSubjects.AS_IS
      1 ->  return SelectorSubjects.PARENT
      2 ->  return SelectorSubjects.HTML
      else -> throw Exception()
    }
  }
}

public class SelectorSubject()

public trait Dom {
  public fun collectStyleSheets(result:MutableSet<String>)

  public fun setOuterHtml(selector:String, selectorSubject:SelectorSubject, outerHtml:String)
  public fun setProperty(selector:String, selectorSubject:SelectorSubject, name:String, value:String?, isStyle:Boolean)

  public fun reloadPageIfContains(selector:String)
  // sourceUrls — all urls point to the same file, it is just aliases (IDE doesn't know definitely browser file source url)
  public fun reloadPageIfContainsStyleSheet(sourceUrls:Array<String>)
  public fun reloadPageIfUseScript(filename:String)

  public fun setCssProperty(sourceUrls:Array<String>, rulesetLine:Int, name:String, value:String, onlyExisting:Boolean)
  public fun setStyleSheetText(sourceUrls:Array<String>, source:String)

  public fun setScriptSource(filename:String, source:String)

  public fun highlightElement(selector:String, selectorSubject:SelectorSubject)
  public fun hideHighlight()
}

private class DomService<T>(private val pageManager:PageManager<T>) {
  fun setProperty(projectId:String, selector:String, selectorSubjectOrdinal:Int, name:String, value:String?, isStyle:Boolean) {
    val selectorSubject = SelectorSubjects.valueOf(selectorSubjectOrdinal)
    pageManager.execute(projectId) {
      it.setProperty(selector, selectorSubject, name, value, isStyle)
    }
  }

  fun setCssProperty(projectId:String, sourceUrls:Array<String>, rulesetLine:Int, name:String, value:String, onlyExisting:Boolean) {
    pageManager.execute(projectId) {
      it.setCssProperty(sourceUrls, rulesetLine, name, value, onlyExisting)
    }
  }

  fun setOuterHtml(projectId:String, selector:String, selectorSubjectOrdinal:Int, value:String) {
    val selectorSubject = SelectorSubjects.valueOf(selectorSubjectOrdinal)
    pageManager.execute(projectId) {
      it.setOuterHtml(selector, selectorSubject, value)
    }
  }

  fun setStyleSheetText(projectId:String, sourceUrls:Array<String>, source:String) {
    pageManager.execute(projectId) { it.setStyleSheetText(sourceUrls, source) }
  }

  fun setScriptSource(projectId:String, filename:String, source:String) {
    pageManager.execute(projectId) { it.setScriptSource(filename, source) }
  }

  fun reloadPagesContainingElement(projectId:String, selector:String) {
    pageManager.execute(projectId) { it.reloadPageIfContains(selector) }
  }

  fun reloadPagesContainingStyleSheet(projectId:String, sourceUrls:Array<String>) {
    pageManager.execute(projectId) { it.reloadPageIfContainsStyleSheet(sourceUrls) }
  }

  fun reloadPagesContainingScript(projectId:String, filename:String) {
    pageManager.execute(projectId) { it.reloadPageIfUseScript(filename) }
  }

  fun reloadPages() {
    pageManager.reload()
  }

  fun openUrl(url:String) {
    pageManager.getOrCreateTab(url)
  }

  fun highlightElement(projectId:String, selector:String, selectorSubjectOrdinal:Int) {
    val selectorSubject = SelectorSubjects.valueOf(selectorSubjectOrdinal)
    pageManager.execute(projectId) {
      it.highlightElement(selector, selectorSubject)
    }
  }

  fun hideHighlight() {
    pageManager.execute2(null, true) {it.hideHighlight()}
  }
}