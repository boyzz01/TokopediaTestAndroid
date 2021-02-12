package com.tokopedia.climbingstairs

object Solution {
    fun climbStairs(n: Int): Long {
        // TODO, return in how many distinct ways can you climb to the top. Each time you can either climb 1 or 2 steps.
        // 1 <= n < 90
        return count(n + 1);
    }

    fun count(n: Int): Long {
        return if (n <= 1) n.toLong() else count(n - 1) + count(n - 2)
    }
}
