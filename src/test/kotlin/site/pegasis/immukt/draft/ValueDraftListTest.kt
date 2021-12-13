package site.pegasis.immukt.draft

import kotlin.test.Test
import kotlin.test.assertEquals

class ValueDraftListTest {
    @Test
    fun produce() {
        val list = listOf(1, 2, 3)
        val finalList = listOf(2, 3, 4)
        ValueDraftList(list).also {
            assertEquals(list, it)

            it.add(4)
            it.removeAt(0)

            assertEquals(finalList, it)
            assertEquals(finalList, it.produce())
        }
    }
}