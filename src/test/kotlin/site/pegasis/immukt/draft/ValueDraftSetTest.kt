package site.pegasis.immukt.draft

import kotlin.test.Test
import kotlin.test.assertEquals

class ValueDraftSetTest {
    @Test
    fun produce() {
        val set = setOf(1, 2, 3, 4)
        val finalSet = setOf(2, 4, 5)

        ValueDraftSet.from(set).also {
            assertEquals(set, it)

            it.remove(1)
            it.removeAll(listOf(1, 3))
            it.add(5)

            assertEquals(finalSet, it)
            assertEquals(finalSet, it.produce())
        }
    }
}