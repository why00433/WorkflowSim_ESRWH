package com.weiyu.experiment.simulation.data;

import java.util.Arrays;

import com.weiyu.experiment.simulation.util.Misc;

/**
 * @author Shishir Bharathi
 */
public class BiasedRandom implements DataFactor {
    private int totalFiles;

    public BiasedRandom(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public boolean needNumFiles() {
        return false;
    }

    public int[] getNumberOfStageInFiles(int numFiles, int[] widths) {
        int[] inFiles = Misc.randomSet(widths.length, totalFiles);
        Arrays.sort(inFiles);
        Misc.reverse(inFiles, 0, inFiles.length);

        return inFiles;
    }

    public int[] getNumberOfStageOutFiles(int numFiles, int[] widths) {
        int[] outFiles = Misc.randomSet(widths.length, totalFiles);
        Arrays.sort(outFiles);

        return outFiles;
    }
}
