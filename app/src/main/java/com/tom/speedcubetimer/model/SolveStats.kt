package com.tom.speedcubetimer.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun calculateStats(
    timeRecords: List<TimeRecord>, requestedSizes: List<AverageSize> = arrayListOf(
        AverageSize(5),
        AverageSize(12),
        AverageSize(100),
        AverageSize(1000),
    )
): Stats {
    val statRecords = HashMap<PuzzleType, List<StatRecord>>()

    // proof-of-concept... very inefficient
    for (puzzleType in PuzzleType.entries) {
        val stats = ArrayList<StatRecord>()
        val puzzleRecords = timeRecords.filter { it.puzzleType == puzzleType }

        for (size in requestedSizes) {
            if (puzzleRecords.size < size.size) {
                break
            }

            // TODO: calculate the average, not the mean
            val movingAverages = puzzleRecords.map { it.time.inWholeMilliseconds }
                .windowed(size.size, 1) { it.average() }.map { it.milliseconds }

            stats.add(StatRecord(size.size, movingAverages.first(), movingAverages.min()))
        }

        statRecords[puzzleType] = stats
    }


    return Stats(statRecords)
}

class Stats(val records: Map<PuzzleType, List<StatRecord>> = HashMap())

data class StatRecord(
    val size: Int,
    val current: Duration,
    val best: Duration,
)

data class AverageSize(val size: Int)
