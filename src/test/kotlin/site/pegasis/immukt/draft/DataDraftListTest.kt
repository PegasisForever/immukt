package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.mapToDraft
import kotlin.test.Test
import kotlin.test.assertEquals

class DataDraftListTest {
    data class Sample(val num: Int) : DataClass

    private val list = listOf(
        Sample(1),
        Sample(2),
        Sample(3),
        Sample(2),
    )

    @Test
    fun `do nothing produces identical list`() {
        assertDraftList(list) {}
    }

    @Test
    fun `produce with only list operations`() {
        val finalList = listOf(
            Sample(0),
            Sample(5),
            Sample(8),
            Sample(9),
            Sample(3),
            Sample(4),
        )

        assertDraftList(finalList) {
            it[0] = Sample(0)
            it.add(Sample(4))
            it.add(1, Sample(5))
            it.addAllData(2, listOf(Sample(6), Sample(7), Sample(8)))
            it.remove(Sample(7))
            it.removeAllData(listOf(Sample(2)))
            it.retainAllData(listOf(Sample(5), Sample(0), Sample(3), Sample(4), Sample(8)))
            it.subList(1, 3).add(Sample(9))
        }

        assertDraftList(finalList) {
            it[0] = Sample(0).draft
            it.add(Sample(4).draft)
            it.add(1, Sample(5).draft)
            it.addAll(2, listOf(Sample(6), Sample(7), Sample(8)).mapToDraft())
            it.remove(Sample(7).draft)
            it.removeAll(listOf(Sample(2)).mapToDraft())
            it.retainAll(listOf(Sample(5), Sample(0), Sample(3), Sample(4), Sample(8)).mapToDraft())
            it.subList(1, 3).add(Sample(9).draft)
        }
    }

    @Test
    fun `produce with data class operations`() {
        val finalList = listOf(
            Sample(1),
            Sample(4),
            Sample(0),
            Sample(3),
            Sample(5),
        )

        assertDraftList(finalList) {
            it[1][Sample::num] = 0
            it.add(1, Sample(4))
            it.last()[Sample::num] = 5
        }

        assertDraftList(finalList) {
            it[1][Sample::num] = 0
            it.add(1, Sample(4).draft)
            it.last()[Sample::num] = 5
        }
    }

    @Test
    fun contains() {
        val draftList = DataDraftList.from(list)
        assert(draftList.contains(Sample(1)))
        assert(draftList.contains(Sample(1).draft))
        assert(!draftList.contains(Sample(4)))
        assert(!draftList.contains(Sample(4).draft))

        draftList.add(Sample(4))
        assert(draftList.contains(Sample(4)))
        assert(draftList.contains(Sample(4).draft))
    }

    @Test
    fun containsAll() {
        val draftList = DataDraftList.from(list)
        assert(draftList.containsAllData(list))
        assert(draftList.containsAll(list.mapToDraft()))
        assert(!draftList.containsAllData(list + Sample(4)))
        assert(!draftList.containsAll((list + Sample(4)).mapToDraft()))

        draftList.add(Sample(4))
        assert(draftList.containsAllData(list + Sample(4)))
        assert(draftList.containsAll((list + Sample(4)).mapToDraft()))
    }

    @Test
    fun set() {
        val finalList = listOf(
            Sample(-1),
            Sample(2),
            Sample(3),
            Sample(2),
        )

        assertDraftList(finalList) {
            it[0] = Sample(-1)
        }

        assertDraftList(finalList) {
            it[0] = Sample(-1).draft
        }
    }

    @Test
    fun indexOf() {
        val draftList = DataDraftList.from(list)
        assertEquals(1, draftList.indexOf(Sample(2)))
        assertEquals(1, draftList.indexOf(Sample(2).draft))
        draftList.removeAt(1)
        assertEquals(2, draftList.indexOf(Sample(2)))
        assertEquals(2, draftList.indexOf(Sample(2).draft))

        assertEquals(-1, draftList.indexOf(Sample(4)))
        assertEquals(-1, draftList.indexOf(Sample(4).draft))
        draftList.add(Sample(4))
        assertEquals(3, draftList.indexOf(Sample(4)))
        assertEquals(3, draftList.indexOf(Sample(4).draft))
    }

    @Test
    fun lastIndexOf() {
        val draftList = DataDraftList.from(list)
        assertEquals(3, draftList.lastIndexOf(Sample(2)))
        assertEquals(3, draftList.lastIndexOf(Sample(2).draft))
        draftList.removeAt(3)
        assertEquals(1, draftList.lastIndexOf(Sample(2)))
        assertEquals(1, draftList.lastIndexOf(Sample(2).draft))

        assertEquals(-1, draftList.lastIndexOf(Sample(4)))
        assertEquals(-1, draftList.lastIndexOf(Sample(4).draft))
        draftList.add(Sample(4))
        draftList.add(Sample(4).draft)
        assertEquals(4, draftList.lastIndexOf(Sample(4)))
        assertEquals(4, draftList.lastIndexOf(Sample(4).draft))
    }

    @Test
    fun add() {
        val finalList = listOf(
            Sample(1),
            Sample(5),
            Sample(2),
            Sample(3),
            Sample(2),
            Sample(4),
        )

        assertDraftList(finalList) {
            it.add(Sample(4))
            it.add(1, Sample(5))
        }

        assertDraftList(finalList) {
            it.add(Sample(4).draft)
            it.add(1, Sample(5).draft)
        }
    }

    @Test
    fun addAll() {
        val finalList = listOf(
            Sample(1),
            Sample(6),
            Sample(7),
            Sample(2),
            Sample(3),
            Sample(2),
            Sample(4),
            Sample(5),
        )

        assertDraftList(finalList) {
            it.addAllData(listOf(Sample(4), Sample(5)))
            it.addAllData(1, listOf(Sample(6), Sample(7)))
        }

        assertDraftList(finalList) {
            it.addAll(listOf(Sample(4).draft, Sample(5).draft))
            it.addAll(1, listOf(Sample(6).draft, Sample(7).draft))
        }
    }

    @Test
    fun remove() {
        val finalList = listOf(
            Sample(3),
            Sample(2),
        )

        assertDraftList(finalList) {
            it.remove(Sample(2))
            it.remove(Sample(1))
        }

        assertDraftList(finalList) {
            it.remove(Sample(2).draft)
            it.remove(Sample(1).draft)
        }
    }

    @Test
    fun removeAll() {
        val finalList = listOf(
            Sample(3),
        )

        assertDraftList(finalList) {
            it.removeAllData(listOf(Sample(2), Sample(1)))
        }

        assertDraftList(finalList) {
            it.removeAll(listOf(Sample(2).draft, Sample(1).draft))
        }
    }

    @Test
    fun retainAll() {
        val finalList = listOf(
            Sample(2),
            Sample(3),
            Sample(2),
        )

        assertDraftList(finalList) {
            it.retainAllData(listOf(Sample(2), Sample(3)))
        }

        assertDraftList(finalList) {
            it.retainAll(listOf(Sample(2).draft, Sample(3).draft))
        }
    }

    @Test
    fun subList() {
        val finalList = listOf(
            Sample(2),
            Sample(3),
        )

        val draftList = DataDraftList.from(list).subList(1, 3)
        assertEquals(finalList.mapToDraft(), draftList)
        assertEquals(finalList, draftList.produce())
    }

    private inline fun assertDraftList(
        expectedList: List<Sample>,
        recipe: (draftList: DataDraftList<Sample>) -> Unit,
    ) {
        val draftList = DataDraftList.from(list).apply(recipe)
        assertEquals(expectedList.mapToDraft(), draftList)
        assertEquals(expectedList, draftList.produce())
    }
}