package com.tokopedia.oilreservoir

/**
 * Created by fwidjaja on 2019-09-24.
 */
object Solution {
    fun collectOil(height: IntArray): Int {
        // TODO, return the amount of oil blocks that could be collected
        // below is stub

        var result = 0
        var leftMax = 0
        var rightMax = 0
        var lo = 0
        var hi: Int = height.size-1

        while (lo <= hi) {
            if (height[lo] < height[hi]) {

                if (height[lo] > leftMax){
                    leftMax = height[lo]
                } else
                    result += leftMax - height[lo]
                lo++

            } else {
                if (height[hi] > rightMax){
                    rightMax = height[hi]
                } else
                    result += rightMax - height[hi]
                hi--
            }
        }
        return result;
    }
}
