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

    private inner class ProducedList(private val list: List<DraftDataClass<I>>) : List<I> {
        override val size: Int
            get() = list.size

        override fun contains(element: I) = list.contains(element.draft)

        override fun containsAll(elements: Collection<I>): Boolean {
            for (element in elements) {
                if (!list.contains(element.draft)) return false
            }
            return true
        }

        override operator fun get(index: Int) = list[index].produce()

        override fun indexOf(element: I) = list.indexOf(element.draft)

        override fun isEmpty() = list.isEmpty()

        override fun lastIndexOf(element: I) = list.lastIndexOf(element.draft)

        override fun subList(fromIndex: Int, toIndex: Int) = ProducedList(list.subList(fromIndex, toIndex))

        override fun iterator() = object : Iterator<I> {
            private val it = list.iterator()

            override fun hasNext() = it.hasNext()

            override fun next() = it.next().produce()
        }

        private inner class ProducedListIterator(private val it: ListIterator<DraftDataClass<I>>) : ListIterator<I> {
            override fun hasNext() = it.hasNext()

            override fun hasPrevious() = it.hasPrevious()

            override fun next() = it.next().produce()

            override fun nextIndex() = it.nextIndex()

            override fun previous() = it.previous().produce()

            override fun previousIndex() = it.previousIndex()
        }

        override fun listIterator() = ProducedListIterator(list.listIterator())

        override fun listIterator(index: Int) = ProducedListIterator(list.listIterator(index))
    }

    override fun produce(lazy: Boolean): List<I> = if (lazy) {
        ProducedList(list)
    } else {
        list.map {
            it.produce(lazy)
        }.toUnmodifiable()
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