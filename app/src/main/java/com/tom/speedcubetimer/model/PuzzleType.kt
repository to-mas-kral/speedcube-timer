package com.tom.speedcubetimer.model

enum class PuzzleType {
    TwoByTwo, ThreeByThree, FourByFour, FiveByFive;

    override fun toString() = run {
        when (this) {
            TwoByTwo -> "2x2"
            ThreeByThree -> "3x3"
            FourByFour -> "4x4"
            FiveByFive -> "5x5"
        }
    }
}
