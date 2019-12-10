data class Coord(val x: Int, val y: Int) {
    operator fun plus(increment: Coord): Coord {
        return Coord(x + increment.x, y + increment.y)
    }

    operator fun minus(decrement: Coord): Coord {
        return Coord(x - decrement.x, y - decrement.y)
    }
}