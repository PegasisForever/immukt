package site.pegasis.immukt.draft

import site.pegasis.immukt.assertContentEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class ValueDraftMapTest {
    @Test
    fun produce() {
        val map = mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3,
        )
        val finalMap = mapOf(
            "b" to 2,
            "c" to 4,
            "d" to 5,
        )

        ValueDraftMap(map).also {
            assertContentEquals(map, it)

            it["c"] = 4
            assertEquals(it["d"], null)

            it.remove("a")
            it["d"] = 5
            assertContentEquals(finalMap, it)
            assertContentEquals(finalMap, it.produce())
        }
    }
}

