package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.mapToDraft
import kotlin.test.Test
import kotlin.test.assertEquals

class DataDraftSetTest {
    data class Sample(val num: Int) : DataClass

    private val set = setOf(
        Sample(1),
        Sample(2),
        Sample(3),
    )

    @Test
    fun `do nothing produces identical set`() {
        assertDraftSet(set) {}
    }

    @Test
    fun `produce with only set operations`() {
        val finalSet = setOf(
            Sample(3),
            Sample(4),
            Sample(7),
        )

        assertDraftSet(finalSet) {
            it.add(Sample(4))
            it.addAllData(listOf(Sample(5), Sample(6), Sample(7)))
            it.remove(Sample(1))
            it.removeAllData(listOf(Sample(2), Sample(6)))
            it.retainAllData(listOf(Sample(3), Sample(4), Sample(7)))
        }

        assertDraftSet(finalSet) {
            it.add(Sample(4).draft)
            it.addAll(listOf(Sample(5), Sample(6), Sample(7)).mapToDraft())
            it.remove(Sample(1).draft)
            it.removeAll(setOf(Sample(2), Sample(6)).mapToDraft())
            it.retainAll(setOf(Sample(3), Sample(4), Sample(7)).mapToDraft())
        }
    }

    @Test
    fun add() {
        val finalSet = setOf(
            Sample(1),
            Sample(2),
            Sample(3),
            Sample(4),
        )

        assertDraftSet(finalSet) {
            it.add(Sample(4))
            it.add(Sample(4))
        }

        assertDraftSet(finalSet) {
            it.add(Sample(4).draft)
            it.add(Sample(4).draft)
        }
    }

    @Test
    fun addAll() {
        val finalSet = setOf(
            Sample(1),
            Sample(2),
            Sample(3),
            Sample(4),
            Sample(5),
        )

        assertDraftSet(finalSet) {
            it.addAllData(listOf(Sample(3), Sample(4), Sample(5)))
        }

        assertDraftSet(finalSet) {
            it.addAll(listOf(Sample(3), Sample(4), Sample(5)).mapToDraft())
        }
    }

    @Test
    fun remove() {
        val finalSet = setOf(
            Sample(1),
        )

        assertDraftSet(finalSet) {
            it.remove(Sample(2))
            it.remove(Sample(2))
            it.remove(Sample(3))
        }

        assertDraftSet(finalSet) {
            it.remove(Sample(2).draft)
            it.remove(Sample(2).draft)
            it.remove(Sample(3).draft)
        }
    }

    @Test
    fun removeAll() {
        val finalSet = setOf(
            Sample(1),
        )

        assertDraftSet(finalSet) {
            it.removeAllData(setOf(Sample(2), Sample(3), Sample(4)))
        }

        assertDraftSet(finalSet) {
            it.removeAll(setOf(Sample(2), Sample(3), Sample(4)).mapToDraft())
        }
    }

    @Test
    fun retainAll() {
        val finalSet = setOf(
            Sample(1),
            Sample(2),
        )

        assertDraftSet(finalSet) {
            it.retainAllData(setOf(Sample(1), Sample(2)))
        }

        assertDraftSet(finalSet) {
            it.retainAll(setOf(Sample(1), Sample(2)).mapToDraft())
        }
    }

    @Test
    fun contains() {
        val draftSet = DataDraftSet.from(set)
        assert(draftSet.contains(Sample(1)))
        assert(draftSet.contains(Sample(1).draft))
        assert(!draftSet.contains(Sample(4)))
        assert(!draftSet.contains(Sample(4).draft))

        draftSet.add(Sample(4))
        assert(draftSet.contains(Sample(4)))
        assert(draftSet.contains(Sample(4).draft))
    }

    @Test
    fun containsAll() {
        val draftSet = DataDraftSet.from(set)
        assert(draftSet.containsAllData(set))
        assert(draftSet.containsAll(set.mapToDraft()))
        assert(!draftSet.containsAllData(set + Sample(4)))
        assert(!draftSet.containsAll((set + Sample(4)).mapToDraft()))

        draftSet.add(Sample(4))
        assert(draftSet.containsAllData(set + Sample(4)))
        assert(draftSet.containsAll((set + Sample(4)).mapToDraft()))
    }

    private inline fun assertDraftSet(
        expectedSet: Set<Sample>,
        recipe: (draftSet: DataDraftSet<Sample>) -> Unit
    ) {
        val draftSet = DataDraftSet.from(set).apply(recipe)
        assertEquals(expectedSet.mapToDraft(), draftSet)
        assertEquals(expectedSet, draftSet.produce())
    }
}