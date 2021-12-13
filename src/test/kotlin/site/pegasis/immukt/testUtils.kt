package site.pegasis.immukt

import site.pegasis.immukt.draft.draft

fun <V : DataClass> List<V>.mapToDraft() = this.map { it.draft }

fun <K, V : DataClass> Map<K, V>.mapToDraft() = this.mapValues { it.value.draft }

fun <V : DataClass> Set<V>.mapToDraft() = this.mapToSet { it.draft }