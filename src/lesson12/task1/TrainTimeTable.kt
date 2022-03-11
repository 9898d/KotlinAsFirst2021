@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

/**
 * Класс "расписание поездов".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса хранит расписание поездов для определённой станции отправления.
 * Для каждого поезда хранится конечная станция и список промежуточных.
 * Поддерживаемые методы:
 * добавить новый поезд, удалить поезд,
 * добавить / удалить промежуточную станцию существующему поезду,
 * поиск поездов по времени.
 *
 * В конструктор передаётся название станции отправления для данного расписания.
 */

fun main() {
    val listOfTrains = mutableListOf<String>("А", "Б")
    val listOfTimes = mutableListOf<Time>(Time(2, 2), Time(1, 1)).map { it.toString() }
    val b = listOfTimes.sorted()
    val newListOfTrains = mutableListOf<String>()
    for (string in b) {
        newListOfTrains.add(listOfTrains[listOfTimes.indexOf(string)])
    }
    println(b)
    println(newListOfTrains)
    val train1 = Train("N1", Stop("Пушкин", Time(7, 4)))
    val train2 = Train("N2", Stop("Пушкин", Time(7, 3)))
    val mmap = mutableMapOf<Train, Int>(train1 to 1, train2 to 2)
    println(mmap)
    mmap.remove(Train(train1.name, Stop("Пушкин", Time(7, 4))))
    println(mmap)
    val time1 = Time(5, 6)
    val time2 = Time(6, 5)
    println(time1.compareTo(time2))
    val stop1 = Stop("a", Time(2, 2))
    val stop2 = Stop("b", Time(1, 1))
    val listOfStops = mutableListOf<Stop>(stop1, stop2).sortedBy { it.time }
    println(listOfStops)
    val stop3 = Stop("c", Time(3, 3))
    val set1 = setOf(1, 2, 3)
    val set2 = setOf(1, 3, 2)
    println(set1 == set2)
}

class TrainTimeTable(val baseStationName: String) {
    private val map = mutableMapOf<Train, Time>()

    /**
     * Добавить новый поезд.
     *
     * Если поезд с таким именем уже есть, следует вернуть false и ничего не изменять в таблице
     *
     *
     * @param train название поезда
     * @param depart время отправления с baseStationName
     * @param destination конечная станция
     * @return true, если поезд успешно добавлен, false, если такой поезд уже есть
     */
    fun addTrain(train: String, depart: Time, destination: Stop): Boolean {
        return if (map.keys.map { it.name }.contains(train))
            false
        else {
            map[Train(train, Stop(baseStationName, depart), destination)] = depart
            true
        }
    }

    /**
     * Удалить существующий поезд.
     *
     * Если поезда с таким именем нет, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @return true, если поезд успешно удалён, false, если такой поезд не существует
     */
    fun removeTrain(train: String): Boolean {
        return if (!map.keys.map { it.name }.contains(train)) false
        else {
            var stops = listOf<Stop>()
            for ((key, value) in map) {
                if (key.name == train) {
                    stops = key.stops
                }
            }
            map.remove(Train(train, stops))
            true
        }
    }

    /**
     * Добавить/изменить начальную, промежуточную или конечную остановку поезду.
     *
     * Если у поезда ещё нет остановки с названием stop, добавить её и вернуть true.
     * Если stop.name совпадает с baseStationName, изменить время отправления с этой станции и вернуть false.
     * Если stop совпадает с destination данного поезда, изменить время прибытия на неё и вернуть false.
     * Если stop совпадает с одной из промежуточных остановок, изменить время прибытия на неё и вернуть false.
     *
     * Функция должна сохранять инвариант: время прибытия на любую из промежуточных станций
     * должно находиться в интервале между временем отправления с baseStation и временем прибытия в destination,
     * иначе следует бросить исключение IllegalArgumentException.
     * Также, время прибытия на любую из промежуточных станций не должно совпадать с временем прибытия на другую
     * станцию или с временем отправления с baseStation, иначе бросить то же исключение.
     *
     * @param train название поезда
     * @param stop начальная, промежуточная или конечная станция
     * @return true, если поезду была добавлена новая остановка, false, если было изменено время остановки на старой
     */
    fun addStop(train: String, stop: Stop): Boolean {
        val stops: MutableList<Stop> = mutableListOf()
        var dep = Time(1, 1)
        var destination = Stop("", Time(0, 0))
        for ((key, value) in map) {
            if (key.name == train) {
                stops += (key.stops)
                stops.sortedBy { it.time }
                dep = value
                destination = stops.last()
            }
        }
        val listOfNamesOfStops = mutableListOf<String>()
        for (st in stops) {
            listOfNamesOfStops.add(st.name)
        }
        if (!listOfNamesOfStops.contains(stop.name)) {
            for (st in stops) {
                if ((stop.time == st.time) || (stop.time < stops[0].time) || (stop.time > stops.last().time))
                    throw IllegalArgumentException()
            }
            stops.add(stop)
            stops.sortBy { it.time }
            removeTrain(train)
            map[Train(train, stops)] = dep
            return true
        } else {
            when (stop.name) {
                baseStationName -> {
                    if (stop.time >= stops[1].time)
                        throw IllegalArgumentException()
                    stops.removeFirst()
                    stops.add(0, stop)
                    dep = stop.time
                    removeTrain(train)
                    map[Train(train, stops)] = dep
                    return false
                }
                stops.last().name -> {
                    if (stop.time <= stops[stops.size - 2].time)
                        throw IllegalArgumentException()
                    stops.removeLast()
                    stops.add(stop)
                    removeTrain(train)
                    map[Train(train, stops)] = dep
                    return false
                }
                else -> {
                    var index = 0
                    for (st in stops) {
                        if (st.name == stop.name) {
                            index = stops.indexOf(st)
                        }
                    }
                    if ((stop.time <= stops[index - 1].time) || (stop.time >= stops[index + 1].time))
                        throw IllegalArgumentException()
                    stops.removeAt(index)
                    stops.add(index, stop)
                    removeTrain(train)
                    map[Train(train, stops)] = dep
                    return false
                }
            }
        }
    }

    /**
     * Удалить одну из промежуточных остановок.
     *
     * Если stopName совпадает с именем одной из промежуточных остановок, удалить её и вернуть true.
     * Если у поезда нет такой остановки, или stopName совпадает с начальной или конечной остановкой, вернуть false.
     *
     * @param train название поезда
     * @param stopName название промежуточной остановки
     * @return true, если удаление успешно
     */
    fun removeStop(train: String, stopName: String): Boolean = TODO()

    /**
     * Вернуть список всех поездов, упорядоченный по времени отправления с baseStationName
     */
    fun trains(): List<Train> {
        val listOfTrains = map.keys.toList()
        val listOfTimes = map.values.toList().map { it.toString() }
        val sort = listOfTimes.sorted()
        val newListOfTrains = mutableListOf<Train>()
        for (string in sort) {
            newListOfTrains.add(listOfTrains[listOfTimes.indexOf(string)])
        }
        return newListOfTrains
    }

    /**
     * Вернуть список всех поездов, отправляющихся не ранее currentTime
     * и имеющих остановку (начальную, промежуточную или конечную) на станции destinationName.
     * Список должен быть упорядочен по времени прибытия на станцию destinationName
     */
    fun trains(currentTime: Time, destinationName: String): List<Train> {
        val currentList = mutableListOf<Train>()
        for ((key, value) in map) {
            if ((value >= currentTime) && (key.stops.map { it.name }.contains(destinationName))) currentList.add(key)
        }
        return currentList
    }

    /**
     * Сравнение на равенство.
     * Расписания считаются одинаковыми, если содержат одинаковый набор поездов,
     * и поезда с тем же именем останавливаются на одинаковых станциях в одинаковое время.
     */
    override fun equals(other: Any?): Boolean = TODO()
}

/**
 * Время (часы, минуты)
 */
data class Time(val hour: Int, val minute: Int) : Comparable<Time> {
    /**
     * Сравнение времён на больше/меньше (согласно контракту compareTo)
     */
    override fun compareTo(other: Time): Int {
        return when {
            hour > other.hour -> 1
            hour < other.hour -> -1
            else -> {
                when {
                    minute > other.minute -> 1
                    minute < other.minute -> -1
                    else -> 0
                }
            }
        }
    }
}

/**
 * Остановка (название, время прибытия)
 */
data class Stop(val name: String, val time: Time)

/**
 * Поезд (имя, список остановок, упорядоченный по времени).
 * Первой идёт начальная остановка, последней конечная.
 */
data class Train(val name: String, val stops: List<Stop>) {
    constructor(name: String, vararg stops: Stop) : this(name, stops.asList())
}