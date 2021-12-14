package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

inline fun <T : DataClass> T.produce(recipe: (draft: DraftDataClass<T>) -> Unit): T {
    return DraftDataClass(this).apply(recipe).produce()
}

inline val <T : DataClass> T.draft: DraftDataClass<T>
    get() = DraftDataClass(this)

class DraftDataClass<T : DataClass>(
    private val data: T,
) : Producible<T> {
    private val changeCache: HashMap<String, Any?> = hashMapOf()
    private var produceCache: T? = data

    init {
        assert(data::class.isData)
    }

    // get normal value
    operator fun <V> get(key: KProperty1<T, V>): V {
        val cached = changeCache[key.name]
        return if (cached != null) {
            cached as V
        } else {
            key.get(data)
        }
    }

    // get data class
    operator fun <V : DataClass> get(key: KProperty1<T, V>) = getImpl(key, ::DraftDataClass)

    // get data list
    operator fun <V : List<I>, I : DataClass> get(key: KProperty1<T, V>) = getImpl(key, DataDraftList.Companion::from)

    // get value list
    operator fun <V : List<I>, I> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftList.Companion::from)

    // get data set
    operator fun <V : Set<I>, I : DataClass> get(key: KProperty1<T, V>) = getImpl(key, DataDraftSet.Companion::from)

    // get value set
    operator fun <V : Set<I>, I> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftSet.Companion::from)

    // get data map
    operator fun <V : Map<K, MV>, K, MV : DataClass> get(key: KProperty1<T, V>) =
        getImpl(key, DataDraftMap.Companion::from)

    // get value map
    operator fun <V : Map<K, MV>, K, MV> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftMap.Companion::from)

    private inline fun <V, reified R> getImpl(key: KProperty1<T, V>, constructor: (v: V) -> R): R {
        return when (val cached = changeCache[key.name]) {
            null -> constructor(key.get(data)).also {  // not cached
                produceCache = null
                changeCache[key.name] = it
            }
            is R -> cached                             // cached draft
            else -> constructor(cached as V).also {    // cached value
                produceCache = null
                changeCache[key.name] = it
            }
        }
    }

    operator fun <V> set(key: KProperty1<T, V>, value: V) {
        produceCache = null
        changeCache[key.name] = value
    }

    override fun produce(): T {
        if (produceCache != null) return produceCache!!
        val klass = data::class
        val properties = klass.declaredMemberProperties.associate { it.name to it.getter }
        val constructor = klass.primaryConstructor!!
        val params = Array(constructor.parameters.size) { i ->
            val param = constructor.parameters[i]
            when (val cached = changeCache[param.name]) {
                null -> properties[param.name]!!.call(data)
                is Producible<*> -> cached.produce()
                else -> cached
            }
        }

        produceCache = constructor.call(*params)
        return produceCache!!
    }

    override fun toString(): String {
        return "DraftDataClass(${produce()})"
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Producible<*> -> this.produce() == other.produce()
            else -> false
        }
    }

    override fun hashCode(): Int {
        return produce().hashCode().inv()
    }
}