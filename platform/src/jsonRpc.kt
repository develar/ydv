package org.jetbrains.io.jsonRpc

import java.util.HashMap
import org.jetbrains.io.kt_invoke
import org.jetbrains.io.webSocket.Socket
import org.jetbrains.logging.getLogger

private val LOG = getLogger("org.jetbrains.io.jsonRpc")

public trait Message {
  public val method:String

  public fun toString():String
}

public fun stringifyNullable(s:String?):String = if (s == null) "null" else JSON.stringify(s)

public class JsonRpcServer(private val socket:Socket<String>) {
  private var messageIdCounter = 0;
  private val callbacks = HashMap<Int, (result:Any?)->Unit>()

  private val domains = HashMap<String, Any>();

  {
    socket.message = {message ->
      if (LOG.debugEnabled) {
        LOG.debug("IN $message")
      }

      val data = JSON.parse<Array<Any>>(message)
      if (data.size == 1 || (data.size == 2 && !(data.get(1) is String))) {
        val f = callbacks.get(data.get(0))!!
        val singletonArray = safeGet(data, 1)
        if (singletonArray == null) (f as ()->Unit)() else f(singletonArray.get(0))
      }
      else {
        val id:Int
        val offset:Int
        if (data.get(0) is String) {
          id = -1
          offset = 0
        }
        else {
          id = data.get(0) as Int
          offset = 1
        }

        var args = safeGet(data, offset + 2)
        val errorCallback:((message:String)->Unit)?
        if (id != -1) {
          val resultCallback = {(result:Any) ->
            socket.send("[$id, \"r\", " + JSON.stringify(result) + "]")
          }

          errorCallback = {
            socket.send("[$id, \"e\", " + JSON.stringify(it) + "]")
          }

          if (args == null) {
            args = array(resultCallback, errorCallback!!)
          }
          else {
            val regularArgs = args!!
            args = Array<Any?>(regularArgs.size + 2) {
              if (it < regularArgs.size) {
                regularArgs[it]
              }
              else if (it == regularArgs.size) {
                resultCallback
              }
              else {
                errorCallback
              }
            }
          }
        }
        else {
          errorCallback = null
        }

        try {
          kt_invoke(domains.get(data.get(offset))!!, data.get(offset + 1) as String, args)
        }
        catch (e:Exception) {
          LOG.error(e)

          if (errorCallback != null) {
            errorCallback(e.getMessage()!!)
          }
        }
      }
    }
  }

  private fun safeGet(a:Array<Any>, index:Int):Array<Any?>? {
    return if (index < a.size) a.get(index) as Array<Any?> else null
  }

  public fun registerDomain(name:String, commands:Any) {
    if (domains.containsKey(name)) {
      throw Exception()
    }

    domains.put(name, commands)
  }

  public fun send(domain:String, command:String, encodedMessage:String? = null) {
    send<Any>(domain, command, encodedMessage, null)
  }

  public fun send<T>(domain:String, command:String, encodedMessage:String? = null, callback:((result:T)->Unit)? = null) {
    var message = "["
    if (callback != null) {
      val id = messageIdCounter++;
      callbacks.put(id, callback as (result:Any?)->Unit)
      message += "$id, "
    }
    message += "\"$domain\", \"$command\""
    if (encodedMessage != null) {
      message += ", ${encodedMessage}"
    }
    message += "]"
    socket.send(message)
  }

  public fun send(domain:String, message:Message) {
    send<Any>(domain, message.method, message.toString(), null)
  }

  public fun send<T>(domain:String, message:Message, callback:((result:T)->Unit)? = null) {
    send(domain, message.method, message.toString(), callback)
  }
}