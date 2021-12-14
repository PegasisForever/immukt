package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible

// draft set for data classes
class DataDraftSet<I : DataClass>(private val set: MutableSet<DraftDataClass<I>>) :
    MutableSet<DraftDataClass<I>> by set,
    Producible<Set<I>> {

    companion object {
        fun <I : DataClass> from(fromSet: Set<I>): DataDraftSet<I> {
            val set = HashSet<DraftDataClass<I>>(fromSet.size)
            for (item in fromSet) {
                set.add(item.draft)
            }
            return DataDraftSet(set)
        }
    }

    private inner class ProducedSet(private val set: Set<DraftDataClass<I>>) : Set<I> {
        override val size: Int
            get() = set.size

        override fun contains(element: I) = set.contains(element.draft)

        override fun containsAll(elements: Collection<I>): Boolean {
            for (element in elements) {
                if (!set.contains(element.draft)) return false
            }
            return true
        }

        override fun isEmpty() = set.isEmpty()

        override fun iterator(): Iterator<I> = object : Iterator<I> {
            private val it = set.iterator()

            override fun hasNext() = it.hasNext()

            override fun next() = it.next().produce()
        }
    }

    override fun produce(): Set<I> = ProducedSet(set)

    fun add(element: I): Boolean {
        return add(element.draft)
    }

    fun addAllData(elements: Collection<I>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element.draft)) modified = true
        }
        return modified
    }

    fun remove(element: I): Boolean {
        return remove(element.draft)
    }

    fun removeAllData(elements: Collection<I>): Boolean {
        var modified = false
        val it = iterator()
        while (it.hasNext()) {
            if (elements.contains(it.next().produce())) {
                it.remove()
                modified = true
            }
        }

        return modified
    }

    fun retainAllData(elements: Collection<I>): Boolean {
        var modified = false
        val it = iterator()
        while (it.hasNext()) {
            if (!elements.contains(it.next().produce())) {
                it.remove()
                modified = true
            }
        }

        return modified
    }

    fun contains(element: I): Boolean {
        return contains(element.draft)
    }

    fun containsAllData(elements: Collection<I>): Boolean {
        for (element in elements) {
            if (!contains(element.draft)) return false
        }
        return true
    }
}