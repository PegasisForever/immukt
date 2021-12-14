package site.pegasis.immukt

interface DataClass

interface Producible<T> {
    fun produce(lazy: Boolean = true): T // todo add tests for lazy = false
}