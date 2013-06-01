package org.jetbrains.util.concurrency

import java.util.ArrayList
import org.jetbrains.logging.getLogger

private val LOG = getLogger("org.jetbrains.util.concurrency")

public class QueueProcessor<T> (private val processor:(item:T, done:()->Unit)->Unit) {
  private val queue = ArrayList<T>()
  private var processing = false

  private val done = {
    if (!processing) {
        throw Exception("processing must be true")
    }
    queue.remove(0)
    if (queue.isEmpty()) {
      processing = false
    }
    else {
      process(queue.get(0))
    }
  }

  public fun add(item:T) {
    queue.add(item)
    if (!processing) {
      processing = true
      process(item)
    }
  }

  private fun process(item:T) {
    processor(item, done)
  }
}