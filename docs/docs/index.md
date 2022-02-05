# Introduction to ImmuKT

ImmuKT is heavily inspired by [Immer](https://immerjs.github.io/immer/). Similar to Immer, ImmuKT allows you to work with nested immutable state safely and conveniently.

Assuming we have the following data:

```kotlin
import site.pegasis.immukt.DataClass

data class Person(val name: String, val age: Int) : DataClass // (1)
data class Party(val time: Int, val people: List<Person>) : DataClass

val party = Party(
    10,
    listOf(
        Person("Pega", 18),
        Person("Alex", 16),
    )
)
```

1. `DataClass` is just an empty marker interface from ImmuKT to make the generics work.

What would you do if you want to change Pega's age from 18 to 19?

Without ImmuKT, you need to manually create a new list and copy every data like this:

```kotlin
val newList = ArrayList<Person>()
newList.addAll(party.people)
newList[0] = Person(
    newList[0].name,
    19,
)
val newParty = Party(
    party.time,
    newList,
)
```

With ImmuKT, the operation become much easier:

```kotlin
import site.pegasis.immukt.draft.produceWith

val newParty = party.produceWith {
    it[Party::people][0][Person::age] = 19
}
```

## How does it work?

ImmuKT keep tracks your changes in a temporary draft, after all the mutation is completed, ImmuKT produces the next state based on mutations to the draft.

## What's the cost?

### Performance

ImmuKT heavily utilizes reflection, it is usually 3x-10x slower than manual copying. However, it is still very fast for normal use cases. <u>In the party example above, manual copying takes 37 nanoseconds and ImmuKT takes 123 nanoseconds.</u> For benchmarks and optimization details, see [Performance](/1.performance/).

### Multiplatform

- Kotlin/Native and Kotlin/JS don't have full reflection support
- I don't use kotlin multiplatform in any of my projects

It is technically possible to include multiplatform support with the current state of kotlin, see [Multiplatform Support](/2.multiplatform_support/).
