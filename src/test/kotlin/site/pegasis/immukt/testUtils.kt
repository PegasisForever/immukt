package site.pegasis.immukt

import kotlin.test.assertEquals

fun <K, V> assertContentEquals(expected: Map<K, V>?, actual: Map<K, V>?, message: String? = null) {
    assertEquals(expected?.toList()?.toSet(), actual?.toList()?.toSet(), message)
}
