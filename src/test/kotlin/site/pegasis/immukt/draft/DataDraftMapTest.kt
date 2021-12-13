package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.assertContentEquals
import site.pegasis.immukt.mapToDraft
import kotlin.test.Test
import kotlin.test.assertEquals

class DataDraftMapTest {
    data class Sample(val num: Int) : DataClass

    private val map = mapOf(
        "a" to Sample(1),
        "b" to Sample(2),
        "c" to Sample(3),
    )

    @Test
    fun `do nothing produces identical map`() {
        assertDraftMap(map) {}
    }

    @Test
    fun `produce with only map operations`() {
        val finalMap = mapOf(
            "a" to Sample(1),
            "b" to Sample(0),
            "c" to Sample(4),
            "e" to Sample(5),
            "g" to Sample(7),
        )

        assertDraftMap(finalMap) {
            it["c"] = Sample(4)
            it["e"] = Sample(5)
            it.putAllData(mapOf("b" to Sample(0), "f" to Sample(6), "g" to Sample(7)))
            it.remove("f", Sample(6))
        }

        assertDraftMap(finalMap) {
            it["c"] = Sample(4).draft
            it["e"] = Sample(5).draft
            it.putAll(mapOf("b" to Sample(0), "f" to Sample(6), "g" to Sample(7)).mapToDraft())
            it.remove("f", Sample(6).draft)
        }
    }

    @Test
    fun `produce with data class operations`() {
        val finalMap = mapOf(
            "a" to Sample(1),
            "b" to Sample(0),
            "c" to Sample(4),
        )

        assertDraftMap(finalMap) {
            it["b"]!![Sample::num] = 0
            it["c"]!![Sample::num] = 4
        }
    }

    @Test
    fun containsValue() {
        val draftMap = DataDraftMap.from(map)
        assert(draftMap.containsValue(Sample(2)))
        assert(draftMap.containsValue(Sample(2).draft))

        assert(!draftMap.containsValue(Sample(4)))
        assert(!draftMap.containsValue(Sample(4).draft))

        draftMap["d"] = Sample(4)
        assert(draftMap.containsValue(Sample(4)))
        assert(draftMap.containsValue(Sample(4).draft))
    }

    @Test
    fun put() {
        val finalMap = mapOf(
            "a" to Sample(0),
            "b" to Sample(2),
            "c" to Sample(3),
            "d" to Sample(4),
        )

        assertDraftMap(finalMap) {
            assertEquals(Sample(1), it.put("a", Sample(0)))
            assertEquals(null, it.put("d", Sample(4)))
        }

        assertDraftMap(finalMap) {
            assertEquals(Sample(1).draft, it.put("a", Sample(0).draft))
            assertEquals(null, it.put("d", Sample(4).draft))
        }
    }

    @Test
    fun putAll() {
        val finalMap = mapOf(
            "a" to Sample(1),
            "b" to Sample(2),
            "c" to Sample(4),
            "d" to Sample(5),
            "e" to Sample(6),
        )
        val putMap = mapOf(
            "c" to Sample(4),
            "d" to Sample(5),
            "e" to Sample(6),
        )

        assertDraftMap(finalMap) {
            it.putAllData(putMap)
        }

        assertDraftMap(finalMap) {
            it.putAll(putMap.mapToDraft())
        }
    }

    @Test
    fun remove() {
        val finalMap = mapOf(
            "a" to Sample(1),
            "c" to Sample(3),
        )

        assertDraftMap(finalMap) {
            assert(it.remove("b", Sample(2)))
            assert(!it.remove("c", Sample(2)))
        }

        assertDraftMap(finalMap) {
            assert(it.remove("b", Sample(2).draft))
            assert(!it.remove("c", Sample(2).draft))
        }
    }

    private inline fun assertDraftMap(
        expectedMap: Map<String, Sample>,
        recipe: (draftMap: DataDraftMap<String, Sample>) -> Unit,
    ) {
        val draftMap = DataDraftMap.from(map).apply(recipe)
        assertContentEquals(expectedMap.mapToDraft(), draftMap)
        assertContentEquals(expectedMap, draftMap.produce())
    }
}