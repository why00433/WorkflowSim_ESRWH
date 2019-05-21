package com.weiyu.experiment.simulation.shape;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Shishir Bharathi
 */
public class Conquer implements Shape {
    public int[] setupWidths(int numJobs, int depth) {
        Random random = new Random();
        int avgWidth = numJobs / depth;

        /*
         * Choose number of jobs at each level randomly.
         * Total number of jobs is set to be average width * depth.
         */
        int[] widths = new int[depth];
        int sum = 0;

        for (int i = 0; i < (depth - 1); i++) {
            widths[i] = 1 + random.nextInt(avgWidth);

            sum += widths[i];
        }

        widths[depth - 1] = numJobs - sum;
        Arrays.sort(widths);

        for (int i = 0, j = depth - 1; i < j; i++, j--) {
            int temp = widths[i];
            widths[i] = widths[j];
            widths[j] = temp;
        }

        return widths;
    }
}
