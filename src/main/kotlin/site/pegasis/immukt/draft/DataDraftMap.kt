package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.mapToSet
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

    private inner class ProducedMap(private val map: Map<K, DraftDataClass<V>>) : Map<K, V> {

        private inner class ProducedCollection(private val collection: Collection<DraftDataClass<V>>) : Collection<V> {
            override val size: Int
                get() = collection.size

            override fun contains(element: V) = collection.contains(element.draft)

            override fun containsAll(elements: Collection<V>): Boolean {
                for (element in elements) {
                    if (!collection.contains(element.draft)) return false
                }
                return true
            }

            override fun isEmpty() = collection.isEmpty()

            override fun iterator() = object : Iterator<V> {
                private val it = collection.iterator()

                override fun hasNext() = it.hasNext()

                override fun next() = it.next().produce()
            }
        }

        private inner class ProducedEntries(private val entries: Set<Map.Entry<K, DraftDataClass<V>>>) :
            Set<Map.Entry<K, V>> {

            private inner class ProducedEntry(override val key: K, val draft: DraftDataClass<V>) : Map.Entry<K, V> {
                override val value: V
                    get() = draft.produce()

                override fun equals(other: Any?): Boolean {
                    return if (other is DataDraftMap<*, *>.ProducedMap.ProducedEntries.ProducedEntry) {
                        key == other.key && draft == other.draft
                    } else {
                        false
                    }
                }

                override fun hashCode(): Int {
                    var result = key?.hashCode() ?: 0
                    result = 31 * result + draft.hashCode()
                    return result
                }
            }

            private fun Map.Entry<K, DraftDataClass<V>>.produce() = ProducedEntry(key, value)

            override val size: Int
                get() = entries.size

            // todo optimize these two functions
            override fun contains(element: Map.Entry<K, V>) =
                entries.mapToSet { it.produce() }.contains(ProducedEntry(element.key, element.value.draft))

            override fun containsAll(elements: Collection<Map.Entry<K, V>>) =
                entries.mapToSet { it.produce() }
                    .containsAll(elements.mapToSet { ProducedEntry(it.key, it.value.draft) })

            override fun isEmpty() = entries.isEmpty()

            override fun iterator() = object : Iterator<Map.Entry<K, V>> {
                private val it = entries.iterator()

                override fun hasNext() = it.hasNext()

                override fun next() = it.next().produce()
            }
        }

        override val entries: Set<Map.Entry<K, V>>
            get() = ProducedEntries(map.entries)
        override val keys: Set<K>
            get() = map.keys
        override val size: Int
            get() = map.size
        override val values: Collection<V>
            get() = ProducedCollection(map.values)

        override fun containsKey(key: K) = map.containsKey(key)

        override fun containsValue(value: V) = map.containsValue(value.draft)

        override operator fun get(key: K): V? = map[key]?.produce()

        override fun isEmpty() = map.isEmpty()
    }

    override fun produce(lazy: Boolean): Map<K, V> = if (lazy) {
        ProducedMap(map)
    } else {
        map.mapValues {
            it.value.produce(lazy)
        }.toUnmodifiable()
    }

    fun containsValue(value: V): Boolean {
        return containsValue(value.draft)
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
            put(key, value.draft)
        }
    }

    fun remove(key: K, value: V): Boolean {
        return remove(key, value.draft)
    }
}