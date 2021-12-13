package site.pegasis.immukt.draft

import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft list for normal value
class ValueDraftList<I>(private val list: MutableList<I>) :
    MutableList<I> by list,
    Producible<List<I>> {

    companion object {
        fun <I> from(fromList: List<I>): ValueDraftList<I> {
            val list = ArrayList<I>(fromList.size)
            list.addAll(fromList)
            return ValueDraftList(list)
        }
    }

    override fun produce(): List<I> {
        return list.toUnmodifiable()
    }
}