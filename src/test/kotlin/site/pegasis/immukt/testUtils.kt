package site.pegasis.immukt

import site.pegasis.immukt.draft.draft

fun <V : DataClass> List<V>.mapToDraft() = this.map { it.draft }

fun <K, V : DataClass> Map<K, V>.mapToDraft() = this.mapValues { it.value.draft }

fun <V : DataClass> Set<V>.mapToDraft() = this.mapToSet { it.draft }

inline fun measureAvgTimeNs(count: Int = 20000000, operation: () -> Unit): Double {
    // warm up
    for (i in 1..100000) operation()

    val start = System.nanoTime()
    for (i in 1..count) operation()
    val finish = System.nanoTime()
    return (finish - start).toDouble() / count
}