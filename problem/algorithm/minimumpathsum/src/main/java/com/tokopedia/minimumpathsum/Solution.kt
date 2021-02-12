package com.tokopedia.minimumpathsum

import java.util.ArrayList

object Solution {
    fun minimumPathSum(matrix: Array<IntArray>): Int {
        // TODO, find a path from top left to bottom right which minimizes the sum of all numbers along its path, and return the sum
        // below is stub

        if (matrix.isEmpty() || matrix[0].isEmpty()) return 0

        val temp = Array(matrix.size) { IntArray(matrix[0].size) }

        temp[0][0] = matrix[0][0]

        for (i in 1 until matrix.size) {
            temp[i][0] = temp[i - 1][0] + matrix[i][0]
        }
        for (j in 1 until matrix[0].size) {
            temp[0][j] = temp[0][j - 1] + matrix[0][j]
        }
        for (i in 1 until matrix.size) {
            for (j in 1 until matrix[0].size) {
                temp[i][j] = Math.min(temp[i - 1][j], temp[i][j - 1]) + matrix[i][j]
            }
        }
        return temp[matrix.size - 1][matrix[0].size - 1]

    }

}
