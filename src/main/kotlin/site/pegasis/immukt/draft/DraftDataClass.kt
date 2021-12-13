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
    val data: T,
    private val path: List<String>,
    private val changeCache: HashMap<List<String>, Any?>, // todo don't initialize before first use
) : Producible<T> {
    constructor(data: T) : this(data, emptyList(), hashMapOf())

    init {
        assert(data::class.isData)
    }

    // get normal value
    operator fun <V> get(key: KProperty1<T, V>): V {
        val getPath = buildList { addAll(path); add(key.name) }
        return if (changeCache.containsKey(getPath)) {
            changeCache[getPath] as V
        } else {
            key.get(data)
        }
    }

    // get data class
    operator fun <V : DataClass> get(key: KProperty1<T, V>): DraftDataClass<V> {
        val getPath = buildList { addAll(path); add(key.name) }
        return if (changeCache.containsKey(getPath)) {
            DraftDataClass(changeCache[getPath] as V, getPath, changeCache)
        } else {
            DraftDataClass(key.get(data), getPath, changeCache)
        }
    }

    // get data list
    operator fun <V : List<I>, I : DataClass> get(key: KProperty1<T, V>) = getImpl(key, DataDraftList.Companion::from)

    // get value list
    operator fun <V : List<I>, I> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftList.Companion::from)

    // get data set
    operator fun <V : Set<I>, I : DataClass> get(key: KProperty1<T, V>) = getImpl(key, DataDraftSet.Companion::from)

    // get value set
    operator fun <V : Set<I>, I> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftSet.Companion::from)

    // get data map
    operator fun <V : Map<K, MV>, K, MV : DataClass> get(key: KProperty1<T, V>) = getImpl(key, DataDraftMap.Companion::from)

    // get value map
    operator fun <V : Map<K, MV>, K, MV> get(key: KProperty1<T, V>) = getImpl(key, ValueDraftMap.Companion::from)

    private inline fun <V, reified R> getImpl(key: KProperty1<T, V>, constructor: (v: V) -> R): R {
        val getPath = buildList { addAll(path); add(key.name) }
        val cached = changeCache[getPath]
        return when (cached) {
            null -> constructor(key.get(data))  // not cached
            is R -> cached                      // cached draft
            else -> constructor(cached as V)    // cached value
        }.also {
            changeCache[getPath] = it
        }
    }

    operator fun <V> set(key: KProperty1<T, V>, value: V) {
        val setPath = buildList { addAll(path); add(key.name) }
        changeCache[setPath] = value
    }

    // todo cache
    override fun produce(): T {
        val originalPropertyMap = data::class.declaredMemberProperties.associate {
            it.name to it.getter
        }

        fun getOriginalProperty(name: String): Any? {
            return originalPropertyMap[name]!!.call(data)
        }

        val constructor = data::class.primaryConstructor!!
        val params = constructor.parameters.map { param ->
            val paramPath = buildList { addAll(path); add(param.name!!) }
            val value = if (changeCache.containsKey(paramPath)) {
                changeCache[paramPath]
            } else {
                getOriginalProperty(param.name!!)
            }
            when (value) {
                is DataClass -> DraftDataClass(value, paramPath, changeCache).produce()
                is Producible<*> -> value.produce()
                else -> value
            }
        }
        return constructor.call(*params.toTypedArray())
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