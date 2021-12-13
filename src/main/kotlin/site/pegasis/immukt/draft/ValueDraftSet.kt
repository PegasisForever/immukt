package site.pegasis.immukt.draft

import site.pegasis.immukt.Producible
import site.pegasis.immukt.toUnmodifiable

// draft set for normal value
class ValueDraftSet<I>(private val set: MutableSet<I>) :
    MutableSet<I> by set,
    Producible<Set<I>> {

    companion object {
        fun <I> from(fromSet: Set<I>): ValueDraftSet<I> {
            val set = HashSet<I>(fromSet.size)
            set.addAll(fromSet)
            return ValueDraftSet(set)
        }
    }

    override fun produce(): Set<I> {
        return set.toUnmodifiable()
    }
}