package org.jetbrains.io.webSocket

import html.WebSocket
import org.jetbrains.util.Timer
import org.jetbrains.logging.getLogger
import org.jetbrains.util.setTimeout

private val LOG = getLogger("org.jetbrains.io.webSocket")

public class Socket<T>(private var uri:String? = null, private val reconnectTimeout:Int = 5000) {
  private var socket:WebSocket<T>? = null
  private var connecting = false
  private var reconnectTimer:Timer? = null;

  public var opened:(()->Unit)? = null
  public var closed:(()->Unit)? = null

  public var message:((data:T)->Any)? = null

  {
    if (uri != null) {
      connect()
    }
  }

  public fun send(data:String) {
    socket!!.send(data)
  }

  public fun disconnect() {
    val s = socket
    if (s != null) {
      socket = null
      s.close()
      LOG.info("WebSocket connection closed")
    }
  }

  public fun connect(uri:String) {
    this.uri = uri;
    connect()
  }

  public fun connect() {
    if (this.socket != null) {
      // todo kotlin assert
    }

    connecting = true
    val socket = WebSocket<T>(uri!!)
    this.socket = socket;

    socket.onopen = {
      connecting = false
      reconnectTimer = null

      socket.onmessage = {
        if (message != null) {
          message!!(it.data)
        }
      }

      if (opened != null) {
        opened!!()
      }
    }

    socket.onclose = {
      this.socket = null
      // closed if connect was failed, but we send closed event only if it was opened previously
      if (!connecting && closed != null) {
        try {
          closed!!()
        }
        catch (e:Exception) {
          LOG.error(e)
        }
      }

      if (reconnectTimeout > 0) {
        if (reconnectTimer == null) {
          reconnectTimer = setTimeout(reconnectTimeout) { connect() }
        }
        else {
          reconnectTimer!!.start()
        }
      }
    }

    socket.onerror = {
      if (reconnectTimeout == -1) {
        LOG.error("onerror $it")
      }
    }
  }
}