package site.pegasis.immukt.performance

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.draft.produceWith
import site.pegasis.immukt.measureAvgTimeNs
import java.text.DecimalFormat
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
    fun `change pega's age to 19`() {
        compareTime(
            {
                val newList = ArrayList<Person>()
                newList.addAll(data.people)
                newList[0] = Person(
                    newList[0].name,
                    19,
                )
                Party(
                    data.time,
                    newList,
                )
            },
            {
                data.produceWith {
                    it[Party::people][0][Person::age] = 19
                }
            }
        )
    }

    @Test
    fun `change party time to 100`() {
        compareTime(
            {
                Party(
                    100,
                    data.people,
                )
            },
            {
                data.produceWith {
                    it[Party::time] = 100
                }
            }
        )
    }

    private val decimalFormat = DecimalFormat("#.##")

    private inline fun compareTime(manual: () -> Party, immuKt: () -> Party) {
        val manualTime = measureAvgTimeNs { manual() }
        val immuKtTime = measureAvgTimeNs { immuKt() }
        println("manual: ${decimalFormat.format(manualTime)}ns")
        println("immuKt: ${decimalFormat.format(immuKtTime)}ns")
        println("(${decimalFormat.format(immuKtTime / manualTime)}x slower)")
    }
}