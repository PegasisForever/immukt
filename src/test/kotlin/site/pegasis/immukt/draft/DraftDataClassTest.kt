package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.assertContentEquals
import site.pegasis.immukt.mapToDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DraftDataClassTest {
    data class Nested(val str: String) : DataClass

    data class Sample(
        val num: Int,
        val nested: Nested,
        val list: List<Int>,
        val dataList: List<Nested>,
        val map: Map<String, Int>,
        val dataMap: Map<String, Nested>,
    ) : DataClass

    private val data = Sample(
        10,
        Nested("awa"),
        listOf(1, 2, 3, 4, 5),
        listOf(Nested("a"), Nested("b"), Nested("c")),
        mapOf("a" to 1, "b" to 2, "c" to 3),
        mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
    )

    @Test
    fun `modify normal value`() {
        val finalData = Sample(
            0,
            Nested("awa"),
            listOf(1, 2, 3, 4, 5),
            listOf(Nested("a"), Nested("b"), Nested("c")),
            mapOf("a" to 1, "b" to 2, "c" to 3),
            mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
        )

        assertDraftData(finalData) {
            assertEquals(10, it[Sample::num])
            it[Sample::num] = 0
            assertEquals(0, it[Sample::num])
        }
    }

    @Test
    fun `modify nested data class`() {
        val finalData = Sample(
            10,
            Nested("test"),
            listOf(1, 2, 3, 4, 5),
            listOf(Nested("a"), Nested("b"), Nested("c")),
            mapOf("a" to 1, "b" to 2, "c" to 3),
            mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
        )

        assertDraftData(finalData) {
            assertEquals("awa", it[Sample::nested][Nested::str])
            it[Sample::nested][Nested::str] = "test"
            assertEquals("test", it[Sample::nested][Nested::str])
        }

        assertDraftData(finalData) {
            assertEquals("awa", it[Sample::nested][Nested::str])
            it[Sample::nested] = Nested("test")
            assertEquals("test", it[Sample::nested][Nested::str])
        }
    }

    @Test
    fun `modify nested value list`() {
        val finalData = Sample(
            10,
            Nested("awa"),
            listOf(1, 2, 3, 5, 6),
            listOf(Nested("a"), Nested("b"), Nested("c")),
            mapOf("a" to 1, "b" to 2, "c" to 3),
            mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
        )

        assertDraftData(finalData) {
            val list = it[Sample::list]
            assertEquals(listOf(1, 2, 3, 4, 5), list)
            list.remove(4)
            list.add(6)
            assertEquals(listOf(1, 2, 3, 5, 6), it[Sample::list])
        }

        assertDraftData(finalData) {
            it[Sample::list] = listOf(1, 2, 3, 5)
            it[Sample::list].add(6)
            assertEquals(listOf(1, 2, 3, 5, 6), it[Sample::list])
        }
    }

    @Test
    fun `modify nested data list`() {
        val finalData = Sample(
            10,
            Nested("awa"),
            listOf(1, 2, 3, 4, 5),
            listOf(Nested("z"), Nested("c"), Nested("d")),
            mapOf("a" to 1, "b" to 2, "c" to 3),
            mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
        )

        assertDraftData(finalData) {
            val list = it[Sample::dataList]
            assertEquals(listOf(Nested("a"), Nested("b"), Nested("c")).mapToDraft(), list)

            assertEquals("a", list.first()[Nested::str])
            list.first()[Nested::str] = "z"
            assertEquals("z", list.first()[Nested::str])

            list.remove(Nested("b"))
            list.add(Nested("d"))
            assertEquals(listOf(Nested("z"), Nested("c"), Nested("d")).mapToDraft(), it[Sample::dataList])
        }

        assertDraftData(finalData) {
            it[Sample::dataList] = listOf(Nested("z"), Nested("b"), Nested("c"))
            val list = it[Sample::dataList]

            list.remove(Nested("b"))
            list.add(Nested("d"))
            assertEquals(listOf(Nested("z"), Nested("c"), Nested("d")).mapToDraft(), it[Sample::dataList])
        }
    }

    @Test
    fun `modify nested value map`() {
        val finalData = Sample(
            10,
            Nested("awa"),
            listOf(1, 2, 3, 4, 5),
            listOf(Nested("a"), Nested("b"), Nested("c")),
            mapOf("a" to 0, "c" to 3, "d" to 4),
            mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu"))
        )

        assertDraftData(finalData) {
            val map = it[Sample::map]
            assertContentEquals(mapOf("a" to 1, "b" to 2, "c" to 3), map)

            map["a"] = 0
            map.remove("b")
            map["d"] = 4
            assertContentEquals(mapOf("a" to 0, "c" to 3, "d" to 4), it[Sample::map])
        }

        assertDraftData(finalData) {
            it[Sample::map] = mapOf("a" to 0, "b" to 2, "c" to 3)
            val map = it[Sample::map]

            map.remove("b")
            map["d"] = 4
            assertContentEquals(mapOf("a" to 0, "c" to 3, "d" to 4), it[Sample::map])
        }
    }

    @Test
    fun `modify nested data map`() {
        val finalValue = Sample(
            10,
            Nested("awa"),
            listOf(1, 2, 3, 4, 5),
            listOf(Nested("a"), Nested("b"), Nested("c")),
            mapOf("a" to 1, "b" to 2, "c" to 3),
            mapOf("a" to Nested("test"), "c" to Nested("uwu"), "e" to Nested("eve"))
        )

        assertDraftData(finalValue) {
            val map = it[Sample::dataMap]
            assertContentEquals(mapOf("a" to Nested("ovo"), "b" to Nested("qvq"), "c" to Nested("uwu")).mapToDraft(), map)

            map["a"]!![Nested::str] = "test"
            map.remove("b", Nested("qvq"))
            map["e"] = Nested("eve")
            assertContentEquals(mapOf("a" to Nested("test"), "c" to Nested("uwu"), "e" to Nested("eve")).mapToDraft(), it[Sample::dataMap])
        }

        assertDraftData(finalValue) {
            it[Sample::dataMap] = mapOf("a" to Nested("test"), "b" to Nested("qvq"), "c" to Nested("uwu"))
            val map = it[Sample::dataMap]

            map.remove("b", Nested("qvq"))
            map["e"] = Nested("eve")
            assertContentEquals(mapOf("a" to Nested("test"), "c" to Nested("uwu"), "e" to Nested("eve")).mapToDraft(), it[Sample::dataMap])
        }
    }

    @Test
    fun equals() {
        assertEquals(
            data.draft.also {
                it[Sample::list] = listOf(1, 2, 3)
            },
            data.draft.also {
                it[Sample::list].removeAll(listOf(4, 5))
            }
        )

        assertNotEquals(
            data.draft.also {
                it[Sample::list] = listOf(1, 2, 3, 4)
            },
            data.draft.also {
                it[Sample::list].removeAll(listOf(4, 5))
            }
        )

        assert(
            !data.draft.also {
                it[Sample::list] = listOf(1, 2, 3, 4)
            }.equals(1)
        )
    }

    @Test
    fun toStr() {
        assertEquals(
            "DraftDataClass(Sample(num=10, nested=Nested(str=awa), list=[1, 2, 3, 4, 5], dataList=[Nested(str=a), Nested(str=b), Nested(str=c)], map={a=1, b=2, c=3}, dataMap={a=Nested(str=ovo), b=Nested(str=qvq), c=Nested(str=uwu)}))",
            data.draft.toString()
        )
    }

    private inline fun assertDraftData(
        expectedData: Sample,
        recipe: (draftList: DraftDataClass<Sample>) -> Unit,
    ) {
        assertEquals(expectedData, data.produce(recipe))
    }
}