data class Coord(val x: Int, val y: Int) {
    operator fun plus(increment: Coord): Coord {
        return Coord(x + increment.x, y + increment.y)
    }

    operator fun minus(decrement: Coord): Coord {
        return Coord(x - decrement.x, y - decrement.y)
    }

    fun north(): Coord {
        return Coord(x, y - 1)
    }

    fun south(): Coord {
        return Coord(x, y + 1)
    }

    fun east(): Coord {
        return Coord(x + 1, y)
    }

    fun west(): Coord {
        return Coord(x - 1, y)
    }
}