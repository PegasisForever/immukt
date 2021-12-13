package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft list for data classes
class DataDraftList<T : List<I>, I : DataClass>(list: T) : ArrayList<DraftDataClass<I>>(), Producible<List<I>> {
    init {
        super.addAll(list.map { DraftDataClass(it) })
    }

    override fun produce(): List<I> {
        return map {
            it.produce()
        }.toUnmodifiable()
    }

    fun contains(element: I): Boolean {
        TODO("Not yet implemented")
    }

    fun containsAllList(elements: Collection<I>): Boolean {
        TODO("Not yet implemented")
    }

    operator fun set(index: Int, element: I): I {
        super.set(index, DraftDataClass(element))
        return element
    }

    override operator fun set(index: Int, element: DraftDataClass<I>): DraftDataClass<I> {
        super.set(index, DraftDataClass(element.data))
        return element
    }

    fun indexOf(element: I): Int {
        TODO("Not yet implemented")
    }

    fun lastIndexOf(element: I): Int {
        TODO("Not yet implemented")
    }

    fun add(element: I): Boolean {
        return super.add(DraftDataClass(element))
    }

    override fun add(element: DraftDataClass<I>): Boolean {
        return super.add(DraftDataClass(element.data))
    }

    fun add(index: Int, element: I) {
        super.add(index, DraftDataClass(element))
    }

    override fun add(index: Int, element: DraftDataClass<I>) {
        super.add(index, DraftDataClass(element.data))
    }

    fun addAllList(index: Int, elements: Collection<I>): Boolean {
        TODO("Not yet implemented")
    }

    fun addAllList(elements: Collection<I>): Boolean {
        TODO("Not yet implemented")
    }

    fun remove(element: I): Boolean {
        TODO("Not yet implemented")
    }

    fun removeAllList(elements: Collection<I>): Boolean {
        TODO("Not yet implemented")
    }

    fun retainAllList(elements: Collection<I>): Boolean {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<DraftDataClass<I>> {
        TODO("Not yet implemented")
    }
}