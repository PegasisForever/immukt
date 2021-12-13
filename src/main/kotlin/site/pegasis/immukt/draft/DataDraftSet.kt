package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.mapToSet
import site.pegasis.immukt.toUnmodifiable

// todo optimize xxxAll don't create new lists

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

    override fun produce(): Set<I> {
        return mapToSet {
            it.produce()
        }.toUnmodifiable()
    }

    fun add(element: I): Boolean {
        return add(element.draft)
    }

    fun addAllData(elements: Collection<I>): Boolean {
        return addAll(elements.map { it.draft })
    }

    fun remove(element: I): Boolean {
        return remove(element.draft)
    }

    fun removeAllData(elements: Collection<I>): Boolean {
        return removeAll(elements.mapToSet { it.draft })
    }

    fun retainAllData(elements: Collection<I>): Boolean {
        return retainAll(elements.mapToSet { it.draft })
    }

    fun contains(element: I): Boolean {
        return contains(element.draft)
    }

    fun containsAllData(elements: Collection<I>): Boolean {
        return containsAll(elements.mapToSet { it.draft })
    }
}