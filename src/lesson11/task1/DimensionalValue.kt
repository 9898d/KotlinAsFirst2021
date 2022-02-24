@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

/**
 * Класс "Величина с размерностью".
 *
 * Предназначен для представления величин вроде "6 метров" или "3 килограмма"
 * Общая сложность задания - средняя, общая ценность в баллах -- 18
 * Величины с размерностью можно складывать, вычитать, делить, менять им знак.
 * Их также можно умножать и делить на число.
 *
 * В конструктор передаётся вещественное значение и строковая размерность.
 * Строковая размерность может:
 * - либо строго соответствовать одной из abbreviation класса Dimension (m, g)
 * - либо соответствовать одной из приставок, к которой приписана сама размерность (Km, Kg, mm, mg)
 * - во всех остальных случаях следует бросить IllegalArgumentException
 */
fun main() {
    val a = DimensionalValue(1.0, "g")
    println(a.value)
    println(a.dimension)
    val b = DimensionalValue("0.5 Kg")
    println(b.value)
    println(b.dimension)
    println(a.plus(b).value)
    println(a.plus(b).dimension)
    println(a.unaryMinus().value)
    println(a.minus(b).value)
    println(a.times(8.0).value)
    println(b.div(100.0).value)
    val c = DimensionalValue(10.0, "mg")
    println(b.div(c))
    val d = DimensionalValue("1 Kg")
    val e = DimensionalValue("1 Kg")
    println(d == e)
    println(d.hashCode())
    println(e.hashCode())
}

class DimensionalValue(value: Double, dimension: String) : Comparable<DimensionalValue> {
    /**
     * Величина с БАЗОВОЙ размерностью (например для 1.0Kg следует вернуть результат в граммах -- 1000.0)
     */
    private val _value = value
    private val _dimension = if (dimension.length > 1) dimension[1].toString() else dimension
    private val _dimensionPrefix = if (dimension.length > 1) dimension[0].toString() else ""

    val value: Double
        get() {
            return when (_dimensionPrefix) {
                "K" -> _value * 1000
                "m" -> _value / 1000
                else -> _value
            }
        }

    /**
     * БАЗОВАЯ размерность (опять-таки для 1.0Kg следует вернуть GRAM)
     */
    val dimension: Dimension
        get() {
            return if (_dimension == "g") Dimension.GRAM
            else Dimension.METER
        }

    /**
     * Конструктор из строки. Формат строки: значение пробел размерность (1 Kg, 3 mm, 100 g и так далее).
     */
    constructor(s: String) : this(s.substringBefore(' ').toDouble(), s.substringAfter(' '))

    /**
     * Сложение с другой величиной. Если базовая размерность разная, бросить IllegalArgumentException
     * (нельзя складывать метры и килограммы)
     */
    operator fun plus(other: DimensionalValue): DimensionalValue {
        if (_dimension != other._dimension) throw IllegalArgumentException()
        else return DimensionalValue(value + other.value, _dimension)
    }

    /**
     * Смена знака величины
     */
    operator fun unaryMinus(): DimensionalValue = DimensionalValue(-value, _dimension)

    /**
     * Вычитание другой величины. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun minus(other: DimensionalValue): DimensionalValue {
        if (_dimension != other._dimension) throw IllegalArgumentException()
        else return DimensionalValue(value - other.value, _dimension)
    }

    /**
     * Умножение на число
     */
    operator fun times(other: Double): DimensionalValue = DimensionalValue(other * value, _dimension)

    /**
     * Деление на число
     */
    operator fun div(other: Double): DimensionalValue = DimensionalValue(value / other, _dimension)

    /**
     * Деление на другую величину. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun div(other: DimensionalValue): Double {
        if (_dimension != other._dimension) throw IllegalArgumentException()
        else return value / other.value
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean =
        ((other is DimensionalValue) && (other.value == value) && (other.dimension == dimension))

    /**
     * Сравнение на больше/меньше. Если базовая размерность разная, бросить IllegalArgumentException
     */
    override fun compareTo(other: DimensionalValue): Int {
        if (_dimension != other._dimension) throw IllegalArgumentException()
        else {
            return when {
                value > other.value -> 1
                value == other.value -> 0
                else -> -1
            }
        }
    }
}

/**
 * Размерность. В этот класс можно добавлять новые варианты (секунды, амперы, прочие), но нельзя убирать
 */
enum class Dimension(val abbreviation: String) {
    METER("m"),
    GRAM("g");
}

/**
 * Приставка размерности. Опять-таки можно добавить новые варианты (деци-, санти-, мега-, ...), но нельзя убирать
 */
enum class DimensionPrefix(val abbreviation: String, val multiplier: Double) {
    KILO("K", 1000.0),
    MILLI("m", 0.001);
}