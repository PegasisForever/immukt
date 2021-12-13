package site.pegasis.immukt.draft

import kotlin.test.Test
import kotlin.test.assertContentEquals

internal class ValueDraftListTest {
    @Test
    fun produce() {
        val list = listOf(1, 2, 3)
        val finalList = listOf(2, 3, 4)
        ValueDraftList(list).also {
            assertContentEquals(list, it)

            it.add(4)
            it.removeAt(0)
            assertContentEquals(finalList, it)

            assertContentEquals(finalList, it.produce())
        }
    }
}