package site.pegasis.immukt

import java.util.*

internal fun <K, V> Map<K, V>.toUnmodifiable(): Map<K, V> = Collections.unmodifiableMap(this)

internal fun <T> List<T>.toUnmodifiable(): List<T> = Collections.unmodifiableList(this)