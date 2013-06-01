package org.jetbrains.util

import java.util.ArrayList
import org.jetbrains.logging.getLogger
import java.util.HashSet
import java.util.HashMap

private val LOG = getLogger("org.jetbrains.disposable")

public trait Disposable {
  public fun dispose()

  public fun register(dispose:()->Unit) {
    disposer.register(this, dispose)
  }

  public fun register(child:Disposable) {
    disposer.register(this, child)
  }

  public fun disposeTree() {
    disposer.dispose(this)
  }
}

private class ObjectNode<T : Any>(private val tree:ObjectTree<T>, parent:ObjectNode<T>?, public val o:T) {
  public var parent:ObjectNode<T>? = parent
    private set

  private var children:ArrayList<ObjectNode<T>>? = null

  public fun removeChild(child:ObjectNode<T>) {
    val index = children!!.lastIndexOf(child)
    if (index != -1) {
      children!!.remove(index)
    }
  }

  public fun addChild(child:ObjectNode<T>) {
    if (children == null) {
      children = ArrayList<ObjectNode<T>>()
    }
    children!!.add(child)
    child.parent = this
  }

  public fun execute(disposeTree:Boolean, action:(o:T)->Unit) {
    executeActionWithRecursiveGuard(this, tree.executedNodes) {
      try {
        // todo is it really needed?
        //action.beforeTreeExecution(myObject)
      }
      catch (e:Throwable) {
        LOG.error(e);
      }

      val list = children
      if (list != null) {
        var n = list.size()
        while (n-- > 0) {
          list[n].execute(disposeTree, action)
        }
      }

      if (disposeTree) {
        children = null
      }

      try {
        action(o)
        //myTree.fireExecuted(myObject)
      }
      catch (e:Throwable) {
        LOG.error(e)
      }

      if (disposeTree) {
        remove()
      }
    }
  }

  private fun remove() {
    tree.objectToNodeMap.remove(o)
    if (parent == null) {
      tree.rootObjects.remove(o);
    }
    else {
      parent!!.removeChild(this)
    }
  }
}

private fun <T> executeActionWithRecursiveGuard(o:T, recursiveGuard:MutableList<T>, action:(o:T)->Unit) {
  if (recursiveGuard.indexOf(o) != -1) {
    return
  }
  recursiveGuard.add(o)
  try {
    action(o)
  }
  finally {
    recursiveGuard.remove(o)
  }
}

private class ObjectTree<T : Any> {
  val objectToNodeMap = HashMap<T, ObjectNode<T>>()
  val rootObjects = HashSet<T>()

  private val executedUnregisteredNodes = ArrayList<T>()
  val executedNodes = ArrayList<ObjectNode<T>>()

  public fun register(parent:T, child:T) {
    val parentNode = getOrCreateNodeFor(parent, null);
    var childNode = objectToNodeMap.get(child)
    if (childNode == null) {
      childNode = createNodeFor(child, parentNode)
    }
    else {
      val oldParent = childNode!!.parent;
      if (oldParent != null) {
        oldParent.removeChild(childNode!!)
      }
    }
    rootObjects.remove(child)
    checkWasNotAddedAlready(childNode!!, child)
    parentNode.addChild(childNode!!)
  }

  private fun getOrCreateNodeFor(o:T, defaultParent:ObjectNode<T>?):ObjectNode<T> {
    val node = objectToNodeMap.get(o)
    if (node != null) {
      return node
    }
    return createNodeFor(o, defaultParent)
  }

  private fun createNodeFor(o:T, parentNode:ObjectNode<T>?):ObjectNode<T> {
    val node = ObjectNode<T>(this, parentNode, o)
    if (parentNode == null) {
      rootObjects.add(o)
    }
    objectToNodeMap.put(o, node)
    return node
  }

  private fun checkWasNotAddedAlready(childNode:ObjectNode<T>, child:T) {
    var parent = childNode.parent
    while (parent != null) {
      if (parent!!.o == child) {
        //LOG.error(child + " was already added as a child of: " + parent)
        LOG.error(" was already added as a child of: " + parent)
      }
      parent = parent!!.parent
    }
  }

  private fun executeUnregistered(o:T, action:(o:T)->Unit) {
    executeActionWithRecursiveGuard(o, executedUnregisteredNodes, action)
  }

  public fun executeAll(o:T, disposeTree:Boolean, action:(o:T)->Unit, processUnregistered:Boolean):Boolean {
    val node = objectToNodeMap.get(o)
    if (node == null) {
      if (processUnregistered) {
        executeUnregistered(o, action)
        return true
      }
      else {
        return false
      }
    }
    node.execute(disposeTree, action)
    return true
  }
}

public fun newDisposable():Disposable = object : Disposable {
  public override fun dispose() {
  }
}

object disposer {
  private val tree = ObjectTree<Disposable>()
  private val disposeAction = {(o:Disposable) ->
    o.dispose()
  }

  // todo don't create object wrapper for dispose action
  public fun register(parent:Disposable, dispose:()->Unit) {
    tree.register(parent, object : Disposable {
      override public fun dispose() {
        dispose()
      }
    })
  }

  public fun register(parent:Disposable, child:Disposable) {
    if (parent == child) {
      throw IllegalStateException("Cannot register to itself")
    }
    tree.register(parent, child)
  }

  public fun dispose(disposable:Disposable, processUnregistered:Boolean = true) {
    tree.executeAll(disposable, true, disposeAction, processUnregistered);
  }
}

