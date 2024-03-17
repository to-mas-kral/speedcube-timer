package com.tom.speedcubetimer.model

enum class PuzzleType {
    _2, _3, _4, _5;

    override fun toString() = run {
        when (this) {
            _2 -> "2x2"
            _3 -> "3x3"
            _4 -> "4x4"
            _5 -> "5x5"
        }
    }
}
