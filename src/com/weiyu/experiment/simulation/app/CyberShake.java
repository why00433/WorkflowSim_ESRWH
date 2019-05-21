package com.weiyu.experiment.simulation.app;

import java.util.Arrays;
import java.util.Set;

import com.weiyu.experiment.simulation.util.Distribution;
import com.weiyu.experiment.simulation.util.Misc;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * @author Shishir Bharathi
 */
public class CyberShake extends AbstractApplication {

    public static final int MAX_RUPTURES = 30;
    public static final int MAX_VARIATIONS = 30;
    public static final double BIAS = 1.0 / 20;
    private static final int MIN_INPUTS = 1;
    private static final double EXTRACT_SGT_FACTOR = 0.0081;
//    public static final double DEFAULT_FACTOR = SeismogramSynthesis.MEAN_RUNTIME;
    private double runtimeFactor = 1;

    public enum SITE {
        CCP, DLA, FFI, LADT, LBP, PAS, SABD, SBSM, SMCA, USC, WNGC
    };
    
    public static final String NAMESPACE = "CyberShake";
    private SITE site;
    private int[] counts;
    private int numExtractSGT;

    private void usage(int exitCode) {
        String msg = "CyberShake [-h] [options]." +
                "\n--data | -d Approximate size of input dataset." +
                "\n--factor | -f Avg. runtime to execute an seismogram_synthesis job." +
                "\n--help | -h Print help message." +
                "\n--numjobs | -n Number of jobs." +
                "\n--ruptures | -r Number of ruptures." +
                "\n--site | -s Generate workflow for specified site." +
                "\n--variations | -m Maximum number of variations for any rupture." +
                "\n\nOne of the following combinations is required:" +
                "\n-d or" +
                "\n-r,-v or" +
                "\n-n.";

        System.out.println(msg);
        System.exit(exitCode);
    }

    public double getRuntimeFactor() {
        return this.runtimeFactor;
    }

    @Override
    protected void processArgs(String[] args) {
        int c;
        LongOpt[] longopts = new LongOpt[7];

        longopts[0] = new LongOpt("data", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[1] = new LongOpt("factor", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[2] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');

        longopts[3] = new LongOpt("num-jobs", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longopts[4] = new LongOpt("ruptures", LongOpt.REQUIRED_ARGUMENT, null, 'r');
        longopts[5] = new LongOpt("site", LongOpt.REQUIRED_ARGUMENT, null, 's');
        longopts[6] = new LongOpt("variations", LongOpt.REQUIRED_ARGUMENT,
                null, 'v');

        Getopt g = new Getopt("CyberShake", args, "d:f:hn:r:s:v:", longopts);
        g.setOpterr(false);

        int numJobs = 0;
        int variations = MAX_VARIATIONS;
        int ruptures = MAX_RUPTURES;
        SITE site = null;
        long data = 0;

        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'd':
                    data = Long.parseLong(g.getOptarg());
                    break;
                case 'f':
                    this.runtimeFactor = Double.parseDouble(g.getOptarg());
                    break;
                case 'h':
                    usage(0);
                    break;
                case 'm':
                    variations = Integer.parseInt(g.getOptarg());
                    break;
                case 'n':
                    numJobs = Integer.parseInt(g.getOptarg());
                    break;
                case 'r':
                    ruptures = Integer.parseInt(g.getOptarg());
                    break;
                case 's':
                    site = SITE.valueOf(g.getOptarg());
                    break;

                default:
                    usage(1);
            }
        }

        if (site == null) {
            site = SITE.FFI;
        }


        int[] counts = null;
        int numExtractSGT = 0;
        int numSeismogramSynthesis = 0;

        if (data > 0) {
            /*
             * Reverse engineer everything from data size.
             */
            long singleInputSize = this.distributions.get("SGT_MEAN").getLong();
            if (data < singleInputSize * MIN_INPUTS) {
                throw new RuntimeException("Not enough data: " + data +
                        "\nMinimum required: " + singleInputSize * MIN_INPUTS);
            }
            numExtractSGT = (int) Math.ceil(data / this.distributions.get("SGT_MEAN").getLong());
            numJobs = numExtractSGT + Misc.randomInt((int) (numExtractSGT * 5), 0.25) + 2;
            numSeismogramSynthesis = (numJobs - 2 - numExtractSGT) / 2;
            counts = Misc.closeNonZeroRandoms(numExtractSGT, numSeismogramSynthesis, 0.25);
            Arrays.sort(counts);
            
        } else if (numJobs > 0) {
            int remaining = numJobs - 2;
            if (remaining < 3) {
                throw new RuntimeException("Cannot generate workflow with numJobs=" + numJobs);
            }

            numExtractSGT = Misc.randomInt((int) (remaining * EXTRACT_SGT_FACTOR), 0.5);
            if (numExtractSGT < 2) {
                numExtractSGT = 2;
            }
            if ((remaining - numExtractSGT) % 2 != 0) {
                if (numExtractSGT > 1) {
                    numExtractSGT--;
                } else {
                    numExtractSGT++;
                }
            }
            numSeismogramSynthesis = (remaining - numExtractSGT) / 2;

            counts = Misc.closeNonZeroRandoms(numExtractSGT, numSeismogramSynthesis, 0.25);
            Arrays.sort(counts);

        } else if (ruptures > 0 && variations > 0) {
            int total = Misc.randomInt(ruptures * variations * 6, 0.1);
            counts = Misc.closeNonZeroRandoms(ruptures * variations, total, 0.25);
            numExtractSGT = ruptures * variations;
        } else {
            usage(1);
        }
        this.site = site;
        this.numExtractSGT = numExtractSGT;
        this.counts = counts;
    }

    public void constructWorkflow() {
        int rupture = 0, variation = 0;

        ZipPSA zipPSA = new ZipPSA(this, "ZipPSA", "1.0", getNewJobID());
        ZipSeis zipSeis = new ZipSeis(this, "ZipSeis", "1.0", getNewJobID());
        for (int i = 0; i < numExtractSGT; i++) {
            if (Misc.randomToss(BIAS)) {
                rupture++;
                variation = 0;
            } else {
                variation++;
            }

            String prefix = site + "_" + rupture + "_" + variation;
            ExtractSGT e = new ExtractSGT(this, "ExtractSGT", "1.0", getNewJobID(), prefix);
            for (int j = 0; j < counts[i]; j++) {
                SeismogramSynthesis s = new SeismogramSynthesis(this, "SeismogramSynthesis", "1.0", getNewJobID(), prefix);
                e.addChild(s);
                s.addChild(zipSeis);

                PeakValCalcOkaya p = new PeakValCalcOkaya(this, "PeakValCalcOkaya", "1.0", getNewJobID());
                s.addChild(p);
                p.addChild(zipPSA);
            }
            e.finish();
        }

        zipPSA.finish();
        zipSeis.finish();
    }

    @Override
    protected void populateDistributions() {
        /*
         * File size distributions.
         */
        this.distributions.put("SGT", Distribution.getTruncatedNormalDistribution(19958666972.0, 93654683371233792.0));
        this.distributions.put("SGT_MEAN", Distribution.getConstantDistribution(19958666972.0));
        this.distributions.put("SUB_SGT", Distribution.getTruncatedNormalDistribution(231720131.58, 27081652820787388.00));
        this.distributions.put("SLIP", Distribution.getUniformDistribution(0, 10000));
        this.distributions.put("HIPO", Distribution.getUniformDistribution(0, 10000));
        this.distributions.put("VARIATION", Distribution.getTruncatedNormalDistribution(3708598.53, 10160641539133.56));
        this.distributions.put("GRM", Distribution.getConstantDistribution(24000));
        this.distributions.put("BSA", Distribution.getConstantDistribution(216));
        this.distributions.put("ZipSeis_factor", Distribution.getConstantDistribution(6));
        this.distributions.put("ZipPSA_factor", Distribution.getConstantDistribution(6));

        /*
         * Runtime distributions.
         */
        this.distributions.put("ExtractSGT", Distribution.getTruncatedNormalDistribution(137.45, 42681.27));
        this.distributions.put("SeismogramSynthesis", Distribution.getTruncatedNormalDistribution(43.40, 984.36));
        this.distributions.put("PeakValCalcOkaya", Distribution.getTruncatedNormalDistribution(1.09, 3.71));
        this.distributions.put("ZipSeis_rate", Distribution.getConstantDistribution(228180.52));
        this.distributions.put("ZipPSA_rate", Distribution.getConstantDistribution(2782.00));
    }
}

class ExtractSGT extends AppJob {

    private String prefix;
    private SeismogramSynthesis lastChild;

    public ExtractSGT(CyberShake cybershake, String name, String version, String jobID, String prefix) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.prefix = prefix;

        long size = cybershake.generateLong("SGT");
        input(prefix + "_fx.sgt", size);
        input(prefix + "_fy.sgt", size);

        double runtime = cybershake.generateDouble("ExtractSGT") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    public void addChild(AppJob child) {
        long subSize = ((CyberShake) getApp()).generateLong("SUB_SGT");
        addLink(child, prefix + "_subfx.sgt", subSize);
        addLink(child, prefix + "_subfy.sgt", subSize);
        lastChild = (SeismogramSynthesis) child;
    }

    @Override
    public void finish() {
        Set<AppFilename> inputs = lastChild.getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().contains("variation")) {
                input(input);
            }
        }
    }
}


class SeismogramSynthesis extends AppJob {

    private String prefix;
    private String jobID;

    public SeismogramSynthesis(CyberShake cybershake, String name, String version, String jobID, String prefix) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
        this.prefix = prefix;
        this.jobID = jobID;

        int slip = cybershake.generateInt("SLIP");
        int hipo = cybershake.generateInt("HIPO");
        String inputVariation = prefix + "_txt.variation-s" +
                String.format("%05d", slip) + "-h" + String.format("%05d", hipo);

        long size = cybershake.generateLong("VARIATION");
        input(inputVariation, size);

        double runtime = cybershake.generateDouble("SeismogramSynthesis") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime));
    }

    @Override
    public void addChild(AppJob child) {
        addLink(child, "Seismogram_" + prefix + "_" + jobID + ".grm", ((CyberShake) getApp()).generateLong("GRM"));
    }
}
class PeakValCalcOkaya extends AppJob {

    public PeakValCalcOkaya(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);

        double runtime = cybershake.generateDouble("PeakValCalcOkaya") * cybershake.getRuntimeFactor();
        addAnnotation("runtime", String.format("%.2f", runtime * cybershake.getRuntimeFactor()));
    }

    @Override
    public void addChild(AppJob child) {
        Set<AppFilename> inputs = getInputs();
        for (AppFilename input : inputs) {
            if (input.getFilename().startsWith("Seismogram")) {
                String temp = input.getFilename();
                temp = temp.replace("Seismogram", "PeakVals");
                temp = temp.replace("grm", "bsa");
                addLink(child, temp, ((CyberShake) getApp()).generateLong("BSA"));
                break;
            }
        }
    }
}

class ZipSeis extends AppJob {

    public ZipSeis(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);

    }

    @Override
    public void finish() {
        /*
         * Hack.
         */
        Set<AppFilename> inputs = getInputs();
        long zipSize = Misc.randomLong((long) (inputs.size() * ((CyberShake) getApp()).generateLong("GRM") / ((CyberShake) getApp()).generateDouble("ZipSeis_factor")), 0.25);

        output("Cybershake_Seismograms.zip", zipSize);
        double runtime = zipSize * ((CyberShake) getApp()).getRuntimeFactor() / ((CyberShake) getApp()).generateDouble("ZipSeis_rate");
        addAnnotation("runtime", String.format("%.2f", runtime));
    }
}

class ZipPSA extends AppJob {

    public ZipPSA(CyberShake cybershake, String name, String version, String jobID) {
        super(cybershake, CyberShake.NAMESPACE, name, version, jobID);
    }

    public void finish() {
        /*
         * Hack.
         */
        Set<AppFilename> inputs = getInputs();
        long zipSize = Misc.randomLong((long) (inputs.size() * ((CyberShake) getApp()).generateLong("BSA") / ((CyberShake) getApp()).generateDouble("ZipSeis_factor")), 0.25);

        output("Cybershake_PSA.zip", zipSize);

        double runtime = zipSize * ((CyberShake) getApp()).getRuntimeFactor() / ((CyberShake) getApp()).generateDouble("ZipPSA_rate");
        addAnnotation("runtime", String.format("%.2f", runtime));
    }
}
