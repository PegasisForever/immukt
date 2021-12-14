package site.pegasis.immukt.performance

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.draft.produce
import site.pegasis.immukt.measureAvgTimeNs
import kotlin.test.Test

class PerformanceTest {
    data class Person(val name: String, val age: Int) : DataClass

    data class Party(val time: Int, val people: List<Person>) : DataClass

    private val data = Party(
        10,
        listOf(
            Person("Pega", 18),
            Person("Alex", 16),
        )
    )

    @Test
    fun `change Pega's age to 19`() {
        measureAvgTimeNs {
            val newList = ArrayList<Person>()
            newList.addAll(data.people)
            newList[0] = Person(
                newList[0].name,
                19,
            )
            val newData = Party(
                data.time,
                newList,
            )
        }.also { println("manual: ${it}Ns") }

        measureAvgTimeNs {
            val newData = data.produce {
                it[Party::people][0][Person::age] = 19
            }
        }.also { println("immuKt: ${it}Ns") }
    }
}