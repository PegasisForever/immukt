package site.pegasis.immukt

import java.util.*

internal fun <K, V> Map<K, V>.toUnmodifiable(): Map<K, V> = Collections.unmodifiableMap(this)

internal fun <T> List<T>.toUnmodifiable(): List<T> = Collections.unmodifiableList(this)

internal inline fun <T, R> Collection<T>.mapToSet(transform: (T) -> R): Set<R> {
    val set = HashSet<R>(this.size)
    for (item in this) {
        set.add(transform(item))
    }
    return set
}