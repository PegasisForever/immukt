package site.pegasis.immukt.draft

import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft list for normal value
class ValueDraftList<T : List<I>, I>(list: T) : ArrayList<I>(list.size), Producible<List<I>> {
    init {
        super.addAll(list)
    }

    override fun produce(): List<I> {
        return this.toUnmodifiable()
    }
}