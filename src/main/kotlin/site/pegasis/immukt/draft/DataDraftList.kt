package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.mapToSet
import site.pegasis.immukt.toUnmodifiable

// draft list for data classes
class DataDraftList<T : List<I>, I : DataClass>(list: T) :
    ArrayList<DraftDataClass<I>>(list.size),
    Producible<List<I>> {
    init {
        super.addAll(list.map { DraftDataClass(it) })
    }

    override fun produce(): List<I> {
        return map {
            it.produce()
        }.toUnmodifiable()
    }

    fun contains(element: I): Boolean {
        return super.contains(element.draft)
    }

    fun containsAllData(elements: Collection<I>): Boolean {
        return super.containsAll(elements.map { it.draft })
    }

    operator fun set(index: Int, element: I): I {
        super.set(index, element.draft)
        return element
    }

    fun indexOf(element: I): Int {
        return super.indexOf(element.draft)
    }

    fun lastIndexOf(element: I): Int {
        return super.lastIndexOf(element.draft)
    }

    fun add(element: I): Boolean {
        return super.add(element.draft)
    }

    fun add(index: Int, element: I) {
        super.add(index, element.draft)
    }

    fun addAllData(index: Int, elements: Collection<I>): Boolean {
        return super.addAll(index, elements.map { it.draft })
    }

    fun addAllData(elements: Collection<I>): Boolean {
        return super.addAll(elements.map { it.draft })
    }

    fun remove(element: I): Boolean {
        return super.remove(element.draft)
    }

    fun removeAllData(elements: Collection<I>): Boolean {
        return super.removeAll(elements.mapToSet { it.draft })
    }

    fun retainAllData(elements: Collection<I>): Boolean {
        return super.retainAll(elements.mapToSet { it.draft })
    }

    override fun subList(fromIndex: Int, toIndex: Int): DataDraftList<T, I> {
        TODO()
    }
}