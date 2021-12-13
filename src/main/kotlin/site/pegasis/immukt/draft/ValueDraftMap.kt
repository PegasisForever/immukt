package site.pegasis.immukt.draft

import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft map K -> value
class ValueDraftMap<K, V>(private val map: MutableMap<K, V>) :
    MutableMap<K, V> by map,
    Producible<Map<K, V>> {

    companion object {
        fun <K, V> from(fromMap: Map<K, V>): ValueDraftMap<K, V> {
            val map = HashMap<K, V>(fromMap.size)
            map.putAll(fromMap)
            return ValueDraftMap(map)
        }
    }

    override fun produce(): Map<K, V> {
        return map.toUnmodifiable()
    }
}