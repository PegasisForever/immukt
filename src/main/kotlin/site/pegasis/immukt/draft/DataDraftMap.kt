package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft map K -> data class
class DataDraftMap<K, V : DataClass>(private val map: MutableMap<K, DraftDataClass<V>>) :
    MutableMap<K, DraftDataClass<V>> by map,
    Producible<Map<K, V>> {

    companion object {
        fun <K, V : DataClass> from(fromMap: Map<K, V>): DataDraftMap<K, V> {
            val map = HashMap<K, DraftDataClass<V>>(fromMap.size)
            for ((key, value) in fromMap) {
                map[key] = value.draft
            }
            return DataDraftMap(map)
        }
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
        val prev = put(key, value.draft)
        return prev?.produce()
    }

    fun putAllData(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    fun remove(key: K, value: V): Boolean {
        return remove(key, value.draft)
    }
}