'use strict';var Kotlin = Object.create(null, {modules:{value:Object.create(null)}, keys:{value:Object.keys}});
(function() {
  function g(b, c) {
    for(var a = null, h = 0, d = b.length;h < d;h++) {
      var k = b[h], e = k.proto;
      null === e || null === k.properties || (null === a ? a = Object.create(e, c || void 0) : Object.defineProperties(a, k.properties))
    }
    return a
  }
  function j(b, c, a, h, d) {
    var k;
    null === b ? (k = null, b = !d && null === a ? null : Object.create(null, a || void 0)) : Array.isArray(b) ? (k = b[0].initializer, b = g(b, a), null === b && d && (b = Object.create(null, a || void 0))) : (k = b.initializer, b = !d && null === a ? b.proto : Object.create(b.proto, a || void 0));
    var e = function l() {
      var a = Object.create(l.proto), b = l.initializer;
      null != b && (0 == b.length ? b.call(a) : b.apply(a, arguments));
      Object.seal(a);
      return a
    };
    Object.defineProperty(e, "proto", {value:b});
    Object.defineProperty(e, "properties", {value:a || null});
    d && (Object.defineProperty(e, "initializer", {value:c}), Object.defineProperty(c, "baseInitializer", {value:k}), Object.freeze(c));
    null !== h && void 0 !== h && Object.defineProperties(e, h);
    Object.freeze(e);
    return e
  }
  function f(b, c) {
    if(Array.isArray(b)) {
      for(var a = 0, h = b.length;a < h;a++) {
        b[a].call(c)
      }
    }else {
      b.call(c)
    }
  }
  function e(b, c) {
    return function() {
      if(null !== c) {
        var a = c;
        c = null;
        f(a, b);
        Object.seal(b)
      }
      return b
    }
  }
  Kotlin.isType = function(b, c) {
    return null === b || void 0 === b ? !1 : Object.getPrototypeOf(b) == c.proto ? !0 : !1
  };
  Kotlin.createTrait = function(b, c, a) {
    return j(b, null, c, a, !1)
  };
  Kotlin.createClass = function(b, c, a, h) {
    return j(b, null === c ? function() {
    } : c, a, h, !0)
  };
  Kotlin.createObject = function(b, c, a) {
    a = Object.create(null === b ? Object.prototype : Array.isArray(b) ? g(b, a) : b.proto, a || void 0);
    null !== c && (null !== b && Object.defineProperty(c, "baseInitializer", {value:Array.isArray(b) ? b[0].initializer : b.initializer}), c.call(a));
    Object.seal(a);
    return a
  };
  Kotlin.p = function(b, c, a, h) {
    var d = null === c ? b : b[c];
    void 0 === d ? (d = b.$packageNames$, null == d ? (d = [c], b.$packageNames$ = d) : d.push(c), b[c] = {$members$:Object.create(null, null === h ? void 0 : h), $initializers$:null === a ? null : a}) : (null !== h && Object.defineProperties(null === c ? b : d.$members$, h), null !== a && (b = d.$initializers$, null === b ? d.$initializers$ = a : Array.isArray(b) ? b.push(a) : d.$initializers$ = [b, a]))
  };
  Kotlin.finalize = function(b) {
    var c = b.$packageNames$;
    if(void 0 !== c) {
      for(var a = 0, h = c.length;a < h;a++) {
        var d = c[a], k = b[d];
        if("$initializers$" === d) {
          f(k, b)
        }else {
          var g = k.$initializers$;
          null == g ? b[d] = k.$members$ : (k = e(k.$members$, g), Object.freeze(k), Object.defineProperty(b, d, {get:k}))
        }
      }
      delete b.$packageNames$
    }
  };
  Kotlin.$new = function(b) {
    return b
  };
  Kotlin.$createClass = function(b, c) {
    null !== b && "function" != typeof b && (c = b, b = null);
    var a = null, h = c ? {} : null;
    if(null != h) {
      for(var d = Object.getOwnPropertyNames(c), e = 0, g = d.length;e < g;e++) {
        var f = d[e], j = c[f];
        "initialize" == f ? a = j : 0 === f.indexOf("get_") ? (h[f.substring(4)] = {get:j}, h[f] = {value:j}) : 0 === f.indexOf("set_") ? (h[f.substring(4)] = {set:j}, h[f] = {value:j}) : h[f] = {value:j, writable:!0}
      }
    }
    return Kotlin.createClass(b || null, a, h)
  };
  Kotlin.doDefineModule = function(b, c) {
    Kotlin.modules[b] = Object.freeze(c)
  }
})();
(function() {
  function g(a) {
    return function() {
      throw new TypeError(void 0 !== a ? "Function " + a + " is abstract" : "Function is abstract");
    }
  }
  function j(a, b) {
    if(0 > a || a >= b) {
      throw new RangeError("Index: " + a + ", Size: " + b);
    }
  }
  function f() {
    return null
  }
  Kotlin.equals = function(a, b) {
    return null === a || void 0 === a ? null === b || void 0 === b : Array.isArray(a) ? Kotlin.arrayEquals(a, b) : "object" == typeof a && "equals" in a ? a.equals(b) : a === b
  };
  Kotlin.stringify = function(a) {
    return null === a || void 0 === a ? "null" : Array.isArray(a) ? Kotlin.arrayToString(a) : a.toString()
  };
  Kotlin.arrayToString = function(a) {
    return"[" + a.join(", ") + "]"
  };
  Kotlin.intUpto = function(a, b) {
    return Kotlin.$new(Kotlin.NumberRange)(a, b)
  };
  Kotlin.intDownto = function(a, b) {
    return Kotlin.$new(Kotlin.NumberProgression)(a, b, -1)
  };
  Kotlin.throwNPE = function() {
    var a = new ReferenceError;
    a.name = "NullPointerException";
    throw a;
  };
  Kotlin.newException = function(a, b) {
    var d = Error(a);
    d.name = b;
    return d
  };
  Kotlin.Iterator = Kotlin.$createClass({initialize:function() {
  }, next:g("Iterator#next"), hasNext:g("Iterator#hasNext")});
  var e = Kotlin.$createClass(Kotlin.Iterator, {initialize:function(a) {
    this.array = a;
    this.size = a.length;
    this.index = 0
  }, next:function() {
    return this.array[this.index++]
  }, hasNext:function() {
    return this.index < this.size
  }});
  Kotlin.Collection = Kotlin.$createClass();
  Kotlin.AbstractCollection = Kotlin.$createClass(Kotlin.Collection, {size:function() {
    return this.$size
  }, addAll:function(a) {
    a = a.iterator();
    for(var b = this.size();0 < b--;) {
      this.add(a.next())
    }
  }, isEmpty:function() {
    return 0 === this.size()
  }, iterator:function() {
    return Kotlin.$new(e)(this.toArray())
  }, equals:function(a) {
    if(this.size() === a.size()) {
      var b = this.iterator();
      a = a.iterator();
      for(var d = this.size();0 < d--;) {
        if(!Kotlin.equals(b.next(), a.next())) {
          return!1
        }
      }
    }
    return!0
  }, toString:function() {
    for(var a = "[", b = this.iterator(), d = !0, c = this.$size;0 < c--;) {
      d ? d = !1 : a += ", ", a += b.next()
    }
    return a + "]"
  }, toJSON:function() {
    return this.toArray()
  }});
  Kotlin.Runnable = Kotlin.$createClass({initialize:function() {
  }, run:g("Runnable#run")});
  Kotlin.Comparable = Kotlin.$createClass({initialize:function() {
  }, compareTo:g("Comparable#compareTo")});
  Kotlin.Closeable = Kotlin.$createClass({initialize:function() {
  }, close:g("Closeable#close")});
  Kotlin.safeParseInt = function(a) {
    a = parseInt(a, 10);
    return isNaN(a) ? null : a
  };
  Kotlin.safeParseDouble = function(a) {
    a = parseFloat(a);
    return isNaN(a) ? null : a
  };
  Kotlin.collectionAdd = function(a, b) {
    return Array.isArray(a) ? a.push(b) : a.add(b)
  };
  Kotlin.collectionAddAll = function(a, b) {
    return Array.isArray(a) ? Kotlin.arrayAddAll(a, b) : a.addAll(b)
  };
  Kotlin.collectionRemove = function(a, b) {
    return Array.isArray(a) ? Kotlin.arrayRemove(a, b) : a.remove(b)
  };
  Kotlin.collectionClear = function(a) {
    Array.isArray(a) ? a.length = 0 : a.clear()
  };
  Kotlin.collectionIterator = function(a) {
    return Array.isArray(a) ? Kotlin.arrayIterator(a) : a.iterator()
  };
  Kotlin.collectionSize = function(a) {
    return Array.isArray(a) ? a.length : a.size()
  };
  Kotlin.collectionIsEmpty = function(a) {
    return Array.isArray(a) ? 0 === a.length : a.isEmpty()
  };
  Kotlin.collectionContains = function(a, b) {
    return Array.isArray(a) ? -1 !== Kotlin.arrayIndexOf(a, b) : a.contains(b)
  };
  Kotlin.arrayIndexOf = function(a, b) {
    for(var d = 0, c = a.length;d < c;d++) {
      if(Kotlin.equals(a[d], b)) {
        return d
      }
    }
    return-1
  };
  Kotlin.arrayLastIndexOf = function(a, b) {
    for(var d = a.length - 1;-1 < d;d--) {
      if(Kotlin.equals(a[d], b)) {
        return d
      }
    }
    return-1
  };
  Kotlin.arrayLastIndexOf = function(a, b) {
    for(var d = a.length - 1;-1 < d;d--) {
      if(Kotlin.equals(a[d], b)) {
        return d
      }
    }
    return-1
  };
  Kotlin.arrayAddAll = function(a, b) {
    var d, c;
    if(Array.isArray(b)) {
      var e = 0;
      d = a.length;
      for(c = b.length;0 < c--;) {
        a[d++] = b[e++]
      }
      return 0 < e
    }
    e = b.iterator();
    d = a.length;
    for(c = b.size();0 < c--;) {
      a[d++] = e.next()
    }
    return 0 != b.size()
  };
  Kotlin.arrayAddAt = function(a, b, d) {
    if(b > a.length || 0 > b) {
      throw new RangeError("Index: " + b + ", Size: " + a.length);
    }
    return a.splice(b, 0, d)
  };
  Kotlin.arrayGet = function(a, b) {
    j(b, a.length);
    return a[b]
  };
  Kotlin.arraySet = function(a, b, d) {
    j(b, a.length);
    a[b] = d;
    return!0
  };
  Kotlin.arrayRemoveAt = function(a, b) {
    j(b, a.length);
    return a.splice(b, 1)[0]
  };
  Kotlin.arrayRemove = function(a, b) {
    var d = Kotlin.arrayIndexOf(a, b);
    return-1 !== d ? (a.splice(d, 1), !0) : !1
  };
  Kotlin.arrayEquals = function(a, b) {
    if(a === b) {
      return!0
    }
    if(!Array.isArray(b) || a.length !== b.length) {
      return!1
    }
    for(var d = 0, c = a.length;d < c;d++) {
      if(!Kotlin.equals(a[d], b[d])) {
        return!1
      }
    }
    return!0
  };
  var b = "";
  Kotlin.System = {out:{print:function(a) {
    void 0 !== a && (b = null === a || "object" !== typeof a ? b + a : b + a.toString())
  }, println:function(a) {
    this.print(a);
    b += "\n"
  }}, output:function() {
    return b
  }, flush:function() {
    b = ""
  }};
  Kotlin.RangeIterator = Kotlin.$createClass(Kotlin.Iterator, {initialize:function(a, b, d) {
    this.$start = a;
    this.$end = b;
    this.$increment = d;
    this.$i = a
  }, get_start:function() {
    return this.$start
  }, get_end:function() {
    return this.$end
  }, get_i:function() {
    return this.$i
  }, set_i:function(a) {
    this.$i = a
  }, next:function() {
    var a = this.$i;
    this.set_i(this.$i + this.$increment);
    return a
  }, hasNext:function() {
    return 0 < this.$increment ? this.$next <= this.$end : this.$next >= this.$end
  }});
  Kotlin.NumberRange = Kotlin.$createClass(null, {initialize:function(a, b) {
    this.$start = a;
    this.$end = b
  }, get_start:function() {
    return this.$start
  }, get_end:function() {
    return this.$end
  }, get_increment:function() {
    return 1
  }, contains:function(a) {
    return this.$start <= a && a <= this.$end
  }, iterator:function() {
    return Kotlin.$new(Kotlin.RangeIterator)(this.get_start(), this.get_end(), this.get_increment())
  }});
  Kotlin.NumberProgression = Kotlin.$createClass(null, {initialize:function(a, b, d) {
    this.$start = a;
    this.$end = b;
    this.$increment = d
  }, get_start:function() {
    return this.$start
  }, get_end:function() {
    return this.$end
  }, get_increment:function() {
    return this.$increment
  }, iterator:function() {
    return Kotlin.$new(Kotlin.RangeIterator)(this.get_start(), this.get_end(), this.get_increment())
  }});
  Kotlin.Comparator = Kotlin.$createClass({initialize:function() {
  }, compare:g("Comparator#compare")});
  var c = Kotlin.$createClass(Kotlin.Comparator, {initialize:function(a) {
    this.compare = a
  }});
  Kotlin.comparator = function(a) {
    return Kotlin.$new(c)(a)
  };
  Kotlin.collectionsMax = function(a, b) {
    if(Kotlin.collectionIsEmpty(a)) {
      throw Error();
    }
    for(var d = Kotlin.collectionIterator(a), c = d.next();d.hasNext();) {
      var e = d.next();
      0 > b.compare(c, e) && (c = e)
    }
    return c
  };
  Kotlin.arrayOfNulls = function(a) {
    return Kotlin.arrayFromFun(a, f)
  };
  Kotlin.arrayFromFun = function(a, b) {
    for(var d = Array(a), c = 0;c < a;c++) {
      d[c] = b(c)
    }
    return d
  };
  Kotlin.arrayIndices = function(a) {
    return Kotlin.$new(Kotlin.NumberRange)(0, a.length - 1)
  };
  Kotlin.arrayIterator = function(a) {
    return Kotlin.$new(e)(a)
  };
  Kotlin.jsonFromTuples = function(a) {
    for(var b = a.length, d = {};0 < b;) {
      --b, d[a[b][0]] = a[b][1]
    }
    return d
  };
  Kotlin.jsonAddProperties = function(a, b) {
    for(var d in b) {
      b.hasOwnProperty(d) && (a[d] = b[d])
    }
    return a
  };
  Kotlin.defineModule = function(a, b, d) {
    if(a in Kotlin.modules) {
      throw Error("Module " + a + " is already defined");
    }
    Kotlin.doDefineModule(a, d())
  }
})();
Kotlin.assignOwner = function(g, j) {
  g.o = j;
  return g
};
(function() {
  function g(a) {
    if("string" == typeof a) {
      return a
    }
    if(typeof a.hashCode == d) {
      return a = a.hashCode(), "string" == typeof a ? a : g(a)
    }
    if(typeof a.toString == d) {
      return a.toString()
    }
    try {
      return String(a)
    }catch(b) {
      return Object.prototype.toString.call(a)
    }
  }
  function j(a, b) {
    return a.equals(b)
  }
  function f(a, b) {
    return typeof b.equals == d ? b.equals(a) : a === b
  }
  function e(a) {
    return function(b) {
      if(null === b) {
        throw Error("null is not a valid " + a);
      }
      if("undefined" == typeof b) {
        throw Error(a + " must not be undefined");
      }
    }
  }
  function b(a, b, d, c) {
    this[0] = a;
    this.entries = [];
    this.addEntry(b, d);
    null !== c && (this.getEqualityFunction = function() {
      return c
    })
  }
  function c(a) {
    return function(b) {
      for(var d = this.entries.length, c, e = this.getEqualityFunction(b);d--;) {
        if(c = this.entries[d], e(b, c[0])) {
          switch(a) {
            case l:
              return!0;
            case n:
              return c;
            case q:
              return[d, c[1]]
          }
        }
      }
      return!1
    }
  }
  function a(a) {
    return function(b) {
      for(var d = b.length, c = 0, e = this.entries.length;c < e;++c) {
        b[d + c] = this.entries[c][a]
      }
    }
  }
  function h(a, d) {
    var c = a[d];
    return c && c instanceof b ? c : null
  }
  var d = "function", k = typeof Array.prototype.splice == d ? function(a, b) {
    a.splice(b, 1)
  } : function(a, b) {
    var d, c, e;
    if(b === a.length - 1) {
      a.length = b
    }else {
      d = a.slice(b + 1);
      a.length = b;
      c = 0;
      for(e = d.length;c < e;++c) {
        a[b + c] = d[c]
      }
    }
  }, m = e("key"), r = e("value"), l = 0, n = 1, q = 2;
  b.prototype = {getEqualityFunction:function(a) {
    return typeof a.equals == d ? j : f
  }, getEntryForKey:c(n), getEntryAndIndexForKey:c(q), removeEntryForKey:function(a) {
    return(a = this.getEntryAndIndexForKey(a)) ? (k(this.entries, a[0]), a[1]) : null
  }, addEntry:function(a, b) {
    this.entries[this.entries.length] = [a, b]
  }, keys:a(0), values:a(1), getEntries:function(a) {
    for(var b = a.length, c = 0, d = this.entries.length;c < d;++c) {
      a[b + c] = this.entries[c].slice(0)
    }
  }, containsKey:c(l), containsValue:function(a) {
    for(var b = this.entries.length;b--;) {
      if(a === this.entries[b][1]) {
        return!0
      }
    }
    return!1
  }};
  var s = function(a, c) {
    var e = this, f = [], j = {}, l = typeof a == d ? a : g, n = typeof c == d ? c : null;
    this.put = function(a, c) {
      m(a);
      r(c);
      var d = l(a), e, g = null;
      (e = h(j, d)) ? (d = e.getEntryForKey(a)) ? (g = d[1], d[1] = c) : e.addEntry(a, c) : (e = new b(d, a, c, n), f[f.length] = e, j[d] = e);
      return g
    };
    this.get = function(a) {
      m(a);
      var b = l(a);
      if(b = h(j, b)) {
        if(a = b.getEntryForKey(a)) {
          return a[1]
        }
      }
      return null
    };
    this.containsKey = function(a) {
      m(a);
      var b = l(a);
      return(b = h(j, b)) ? b.containsKey(a) : !1
    };
    this.containsValue = function(a) {
      r(a);
      for(var b = f.length;b--;) {
        if(f[b].containsValue(a)) {
          return!0
        }
      }
      return!1
    };
    this.clear = function() {
      f.length = 0;
      j = {}
    };
    this.isEmpty = function() {
      return!f.length
    };
    var p = function(a) {
      return function() {
        for(var b = [], c = f.length;c--;) {
          f[c][a](b)
        }
        return b
      }
    };
    this._keys = p("keys");
    this._values = p("values");
    this._entries = p("getEntries");
    this.values = function() {
      for(var a = this._values(), b = a.length, c = Kotlin.$new(Kotlin.ArrayList)();--b;) {
        c.add(a[b])
      }
      return c
    };
    this.remove = function(a) {
      m(a);
      var b = l(a), c = null, d = h(j, b);
      if(d && (c = d.removeEntryForKey(a), null !== c && !d.entries.length)) {
        a: {
          for(a = f.length;a--;) {
            if(d = f[a], b === d[0]) {
              break a
            }
          }
          a = null
        }
        k(f, a);
        delete j[b]
      }
      return c
    };
    this.size = function() {
      for(var a = 0, b = f.length;b--;) {
        a += f[b].entries.length
      }
      return a
    };
    this.each = function(a) {
      for(var b = e.entries(), c = b.length, d;c--;) {
        d = b[c], a(d[0], d[1])
      }
    };
    this.putAll = function(a, b) {
      for(var c = a.entries(), f, h, g, j = c.length, k = typeof b == d;j--;) {
        f = c[j];
        h = f[0];
        f = f[1];
        if(k && (g = e.get(h))) {
          f = b(h, g, f)
        }
        e.put(h, f)
      }
    };
    this.clone = function() {
      var b = new s(a, c);
      b.putAll(e);
      return b
    };
    this.keySet = function() {
      for(var a = Kotlin.$new(Kotlin.ComplexHashSet)(), b = this._keys(), c = b.length;c--;) {
        a.add(b[c])
      }
      return a
    }
  };
  Kotlin.HashTable = s
})();
Kotlin.Map = Kotlin.$createClass();
Kotlin.HashMap = Kotlin.$createClass(Kotlin.Map, {initialize:function() {
  Kotlin.HashTable.call(this)
}});
Kotlin.ComplexHashMap = Kotlin.HashMap;
(function() {
  var g = Kotlin.$createClass(Kotlin.Iterator, {initialize:function(f, e) {
    this.map = f;
    this.keys = e;
    this.size = e.length;
    this.index = 0
  }, next:function() {
    return this.map[this.keys[this.index++]]
  }, hasNext:function() {
    return this.index < this.size
  }}), j = Kotlin.$createClass(Kotlin.Collection, {initialize:function(f) {
    this.map = f
  }, iterator:function() {
    return Kotlin.$new(g)(this.map.map, Kotlin.keys(this.map.map))
  }, isEmpty:function() {
    return 0 === this.map.$size
  }, contains:function(f) {
    return this.map.containsValue(f)
  }});
  Kotlin.PrimitiveHashMap = Kotlin.$createClass(Kotlin.Map, {initialize:function() {
    this.$size = 0;
    this.map = {}
  }, size:function() {
    return this.$size
  }, isEmpty:function() {
    return 0 === this.$size
  }, containsKey:function(f) {
    return void 0 !== this.map[f]
  }, containsValue:function(f) {
    var e = this.map, b;
    for(b in e) {
      if(e.hasOwnProperty(b) && e[b] === f) {
        return!0
      }
    }
    return!1
  }, get:function(f) {
    return this.map[f]
  }, put:function(f, e) {
    var b = this.map[f];
    this.map[f] = void 0 === e ? null : e;
    void 0 === b && this.$size++;
    return b
  }, remove:function(f) {
    var e = this.map[f];
    void 0 !== e && (delete this.map[f], this.$size--);
    return e
  }, clear:function() {
    this.$size = 0;
    this.map = {}
  }, putAll:function() {
    throw Kotlin.$new(Kotlin.UnsupportedOperationException)();
  }, keySet:function() {
    var f = Kotlin.$new(Kotlin.PrimitiveHashSet)(), e = this.map, b;
    for(b in e) {
      e.hasOwnProperty(b) && f.add(b)
    }
    return f
  }, values:function() {
    return Kotlin.$new(j)(this)
  }, toJSON:function() {
    return this.map
  }})
})();
Kotlin.Set = Kotlin.$createClass(Kotlin.Collection);
Kotlin.PrimitiveHashSet = Kotlin.$createClass(Kotlin.AbstractCollection, {initialize:function() {
  this.$size = 0;
  this.map = {}
}, contains:function(g) {
  return!0 === this.map[g]
}, add:function(g) {
  var j = this.map[g];
  this.map[g] = !0;
  if(!0 === j) {
    return!1
  }
  this.$size++;
  return!0
}, remove:function(g) {
  return!0 === this.map[g] ? (delete this.map[g], this.$size--, !0) : !1
}, clear:function() {
  this.$size = 0;
  this.map = {}
}, toArray:function() {
  return Kotlin.keys(this.map)
}});
(function() {
  function g(j, f) {
    var e = new Kotlin.HashTable(j, f);
    this.add = function(b) {
      e.put(b, !0)
    };
    this.addAll = function(b) {
      for(var c = b.length;c--;) {
        e.put(b[c], !0)
      }
    };
    this.values = function() {
      return e._keys()
    };
    this.iterator = function() {
      return Kotlin.arrayIterator(this.values())
    };
    this.remove = function(b) {
      return e.remove(b) ? b : null
    };
    this.contains = function(b) {
      return e.containsKey(b)
    };
    this.clear = function() {
      e.clear()
    };
    this.size = function() {
      return e.size()
    };
    this.isEmpty = function() {
      return e.isEmpty()
    };
    this.clone = function() {
      var b = new g(j, f);
      b.addAll(e.keys());
      return b
    };
    this.equals = function(b) {
      if(null === b || void 0 === b) {
        return!1
      }
      if(this.size() === b.size()) {
        var c = this.iterator();
        for(b = b.iterator();;) {
          var a = c.hasNext(), e = b.hasNext();
          if(a != e) {
            break
          }
          if(e) {
            if(a = c.next(), e = b.next(), !Kotlin.equals(a, e)) {
              break
            }
          }else {
            return!0
          }
        }
      }
      return!1
    };
    this.toString = function() {
      for(var b = "[", c = this.iterator(), a = !0;c.hasNext();) {
        a ? a = !1 : b += ", ", b += c.next()
      }
      return b + "]"
    };
    this.intersection = function(b) {
      var c = new g(j, f);
      b = b.values();
      for(var a = b.length, h;a--;) {
        h = b[a], e.containsKey(h) && c.add(h)
      }
      return c
    };
    this.union = function(b) {
      var c = this.clone();
      b = b.values();
      for(var a = b.length, f;a--;) {
        f = b[a], e.containsKey(f) || c.add(f)
      }
      return c
    };
    this.isSubsetOf = function(b) {
      for(var c = e.keys(), a = c.length;a--;) {
        if(!b.contains(c[a])) {
          return!1
        }
      }
      return!0
    }
  }
  Kotlin.ComplexHashSet = Kotlin.$createClass(Kotlin.Set, {initialize:function() {
    g.call(this)
  }})
})();
