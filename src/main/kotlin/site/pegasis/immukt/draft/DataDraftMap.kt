package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft map K -> data class
class DataDraftMap<T : Map<K, V>, K, V : DataClass>(map: T) : HashMap<K, DraftDataClass<V>>(), Producible<Map<K, V>> {
    init {
        super.putAll(map.mapValues { (_, value) -> DraftDataClass(value) })
    }

    override fun produce(): Map<K, V> {
        return mapValues { (_, draft) ->
            draft.produce()
        }.toUnmodifiable()
    }

    fun containsValue(value: V): Boolean {
        return containsValue(DraftDataClass(value))
    }

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    fun put(key: K, value: V): V? {
        val prev = put(key, DraftDataClass(value))
        return prev?.produce()
    }

    fun putAllMap(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }
}