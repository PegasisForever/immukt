package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.mapToSet
import site.pegasis.immukt.toUnmodifiable

// draft list for data classes
class DataDraftList<I : DataClass>(private val list: MutableList<DraftDataClass<I>>) :
    MutableList<DraftDataClass<I>> by list,
    Producible<List<I>> {

    companion object {
        fun <I : DataClass> from(fromList: List<I>): DataDraftList<I> {
            val list = ArrayList<DraftDataClass<I>>(fromList.size)
            for (item in fromList) {
                list.add(item.draft)
            }
            return DataDraftList(list)
        }
    }

    override fun produce(): List<I> {
        return map {
            it.produce()
        }.toUnmodifiable()
    }

    fun contains(element: I): Boolean {
        return contains(element.draft)
    }

    fun containsAllData(elements: Collection<I>): Boolean {
        return containsAll(elements.map { it.draft })
    }

    operator fun set(index: Int, element: I): I {
        set(index, element.draft)
        return element
    }

    fun indexOf(element: I): Int {
        return indexOf(element.draft)
    }

    fun lastIndexOf(element: I): Int {
        return lastIndexOf(element.draft)
    }

    fun add(element: I): Boolean {
        return add(element.draft)
    }

    fun add(index: Int, element: I) {
        add(index, element.draft)
    }

    fun addAllData(index: Int, elements: Collection<I>): Boolean {
        return addAll(index, elements.map { it.draft })
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

    override fun subList(fromIndex: Int, toIndex: Int): DataDraftList<I> {
        return DataDraftList(list.subList(fromIndex, toIndex))
    }
}