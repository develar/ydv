package org.jetbrains.extensions

import java.util.HashMap
import java.util.ArrayList

public class ExtensionPointName<T>(val name:String) {
  val extensions:List<T>
    get() = Extensions.getExtensions<T>(this)

  val extension:T
    get () {
      return extensions.get(0)
    }
}

native
fun <T> ff():Array<T> = noImpl

public val ANY:LoadingOrder = LoadingOrder("any")
public val FIRST:LoadingOrder = LoadingOrder("first")

// KT-1907
trait Orderable {
  val order:LoadingOrder
}

class LoadingOrder(val name:String) {
}

abstract class ExtensionComponentAdapter : Orderable {

}

public class ExtensionPoint<T>(val name:String) {
  public val extensions:List<T>
    get() = instances

  private val instances = ArrayList<T>()

  private class ObjectComponentAdapter(override val order:LoadingOrder) : ExtensionComponentAdapter() {
  }

  public fun registerExtension(order:LoadingOrder = ANY, extension:T) {
    registerExtension(extension, order)
  }

  public fun registerExtension(extension:T, order:LoadingOrder) {
    if (order == FIRST) {
      registerExtension(extension, 0)
    }
    else {
      registerExtension(extension, -1)
    }
  }

  private fun registerExtension(extension:T, index:Int) {
    if (instances.contains(extension)) {
      throw IllegalArgumentException("Extension was already added: $extension")
    }

    if (index == -1) {
      instances.add(extension)
    }
    else {
      instances.add(index, extension)
    }
  }
}

public class ExtensionsArea {
  val extensionPoints = HashMap<String, ExtensionPoint<Any>>()

  public fun <T> registerExtensionPoint(name:ExtensionPointName<T>):ExtensionPoint<T> {
    val extensionPoint = ExtensionPoint<T>(name.name)
    extensionPoints.put(name.name, extensionPoint as ExtensionPoint<Any>)
    return extensionPoint
  }

  public fun <T> getExtensionPoint(name:ExtensionPointName<T>):ExtensionPoint<T> {
    return extensionPoints.get(name.name) as ExtensionPoint<T>;
  }
}

public object Extensions {
  public val rootArea:ExtensionsArea = ExtensionsArea()

  // todo kotlin bug
  public fun <T> getExtensions(name:ExtensionPointName<T>):List<T> {
    return getExtensions(name, rootArea)
  }

  public fun <T> getExtensions(name:ExtensionPointName<T>, area:ExtensionsArea):List<T> {
    return area.getExtensionPoint(name).extensions
  }
}