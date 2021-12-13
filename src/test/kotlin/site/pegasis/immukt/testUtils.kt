package site.pegasis.immukt

import site.pegasis.immukt.draft.draft
import kotlin.test.assertEquals

fun <K, V> assertContentEquals(expected: Map<K, V>?, actual: Map<K, V>?, message: String? = null) {
    assertEquals(expected?.toList()?.toSet(), actual?.toList()?.toSet(), message)
}

fun <V : DataClass> List<V>.mapToDraft() = this.map { it.draft }

fun <K, V : DataClass> Map<K, V>.mapToDraft() = this.mapValues { it.value.draft }