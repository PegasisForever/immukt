package site.pegasis.immukt.draft

import site.pegasis.immukt.DataClass
import site.pegasis.immukt.Producible
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

inline fun <T : DataClass> T.produceWith(lazy: Boolean = true, recipe: (draft: DraftDataClass<T>) -> Unit): T {
    return DraftDataClass(this).apply(recipe).produce(lazy)
}

inline fun <T, D : Producible<T>> D.produceWith(lazy: Boolean = true, recipe: (draft: D) -> Unit): T {
    return this.apply(recipe).produce(lazy)
}

inline val <T : DataClass> T.draft: DraftDataClass<T>
    get() = DraftDataClass(this)

class DraftDataClass<T : DataClass>(
    private val data: T,
) : Producible<T> {
    private val changeCache: HashMap<String, Any?> = hashMapOf()
    private var produceCache: T? = data

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
        val cached = changeCache[key.name]
        return if (cached is R) {                 // cached draft
            cached
        } else {
            if (cached == null) {
                constructor(key.get(data))        // not cached
            } else {
                constructor(cached as V)          // cached value
            }.also {
                produceCache = null
                changeCache[key.name] = it
            }
        }
    }

    operator fun <V> set(key: KProperty1<T, V>, value: V) {
        produceCache = null
        changeCache[key.name] = value
    }

    override fun produce(lazy: Boolean): T {
        if (produceCache != null) return produceCache!!

        val (propertyGetters, paramNames, constructorFun) = getClassRefl(data::class)
        val params = Array(paramNames.size) { i ->
            val paramName = paramNames[i]
            when (val cached = changeCache[paramName]) {
                null -> propertyGetters[paramName]!!(data)
                is Producible<*> -> cached.produce(lazy)
                else -> cached
            }
        }

        produceCache = constructorFun(params) as T
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

    companion object {
        internal data class ClassRefl(
            val propertyGetters: Map<String, (instance: DataClass) -> Any?>,
            val constructorParamNames: List<String>,
            val constructorFun: (params: Array<Any?>) -> DataClass,
        )

        private val classPropertiesCache = hashMapOf<KClass<out DataClass>, ClassRefl>()

        private fun <T : DataClass> getClassRefl(kClass: KClass<out T>): ClassRefl {
            val cached = classPropertiesCache[kClass]
            return if (cached != null) {
                cached
            } else {
                val classRefl = ClassRefl(
                    kClass.members.associate {
                        it.name to { instance -> it.call(instance) }
                    },
                    kClass.constructors.first().parameters.map { it.name!! },
                    { params -> kClass.constructors.first().call(*params) }
                )
                classPropertiesCache[kClass] = classRefl
                classRefl
            }
        }
    }
}