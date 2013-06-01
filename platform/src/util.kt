package org.jetbrains.util

import js.parseInt

public object ThreeState {
  public val YES:Int = 1
  public val NO:Int = -1
  public val UNSURE:Int = 0
}

public fun String.compareVersionNumber(otherVersion:String?):Int = compareVersionNumbers(this, otherVersion)

public fun compareVersionNumbers(v1:String?, v2:String?):Int {
  if (v1 == null && v2 == null) {
    return 0
  }
  if (v1 == null) {
    return -1
  }
  if (v2 == null) {
    return 1
  }

  val part1 = v1.split("[\\.\\_\\-]")
  val part2 = v2.split("[\\.\\_\\-]")

  var index = 0
  while (index < part1.size && index < part2.size) {
    val p1 = part1[index]
    val p2 = part2[index]

    var cmp:Int
    if (p1.matches("\\d+") && p2.matches("\\d+")) {
      cmp = parseInt(p1) - parseInt(p2)
      if (cmp < -1) {
        cmp = -1
      }
      else if (cmp > 1) {
        cmp = 1
      }
    }
    else {
      cmp = part1[index].compareTo(part2[index]);
    }
    if (cmp != 0) {
      return cmp
    }

    index++
  }

  if (part1.size == part2.size) {
    return 0
  }
  else if (part1.size > index) {
    return 1
  }
  else {
    return -1
  }
}