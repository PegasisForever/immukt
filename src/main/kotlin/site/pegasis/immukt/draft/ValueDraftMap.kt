package site.pegasis.immukt.draft

import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft map K -> value
class ValueDraftMap<K, V>(map: Map<K, V>) : HashMap<K, V>(map.size), Producible<Map<K, V>> {
    init {
        super.putAll(map)
    }

    override fun produce(): Map<K, V> {
        return this.toUnmodifiable()
    }
}