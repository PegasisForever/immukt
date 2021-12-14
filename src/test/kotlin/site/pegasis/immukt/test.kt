package site.pegasis.immukt

import site.pegasis.immukt.draft.produceWith

data class Person(val name: String, val age: Int) : DataClass

data class Party(val time: Int, val people: List<Person>) : DataClass

fun main() {
    val data = Party(
        10,
        listOf(
            Person("Pega", 18),
            Person("Alex", 16),
        )
    )

    val newList = ArrayList<Person>()
    newList.addAll(data.people)
    newList[0] = Person(
        newList[0].name,
        19,
    )
    val newData1 = Party(
        data.time,
        newList,
    )

    val newData2 = data.produceWith {
        it[Party::people][0][Person::name] = "Awa"
    }

    println("-----")
    println(newData2.people[1])
}