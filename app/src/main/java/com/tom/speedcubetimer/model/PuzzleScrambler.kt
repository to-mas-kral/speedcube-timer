package com.tom.speedcubetimer.model

import org.worldcubeassociation.tnoodle.puzzle.CubePuzzle
import org.worldcubeassociation.tnoodle.puzzle.FourByFourCubePuzzle
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle
import org.worldcubeassociation.tnoodle.puzzle.TwoByTwoCubePuzzle
import org.worldcubeassociation.tnoodle.scrambles.Puzzle
import org.worldcubeassociation.tnoodle.svglite.Color as svgliteColor

data class Scramble(
    val text: String,
    val imageSvg: String,
)

val colorScheme = HashMap<String, svgliteColor>()

class PuzzleScrambler {
    fun makeScramble(puzzleType: PuzzleType): Scramble {
        val scramble = when (puzzleType) {
            PuzzleType.TwoByTwo -> _2.generateScramble()
            PuzzleType.ThreeByThree -> _3.generateScramble()
            PuzzleType.FourByFour -> _4.generateScramble()
            PuzzleType.FiveByFive -> _5.generateScramble()
        }

        val image = when (puzzleType) {
            PuzzleType.TwoByTwo -> _2.drawScramble(scramble, colorScheme).toString()
            PuzzleType.ThreeByThree -> _3.drawScramble(scramble, colorScheme).toString()
            PuzzleType.FourByFour -> _4.drawScramble(scramble, colorScheme).toString()
            PuzzleType.FiveByFive -> _5.drawScramble(scramble, colorScheme).toString()
        }

        return Scramble(scramble, image)
    }

    // Puzzle objects might be expensive to create, so create them all at once and reuse
    private val _2: Puzzle = TwoByTwoCubePuzzle()
    private val _3: Puzzle = ThreeByThreeCubePuzzle()
    private val _4: Puzzle = FourByFourCubePuzzle()
    private val _5: Puzzle = CubePuzzle(5)
}
