package com.weiyu.experiment.simulation;

import java.io.File;
import java.io.FileOutputStream;

import com.weiyu.experiment.simulation.app.Application;
import com.weiyu.experiment.simulation.app.CyberShake;
import com.weiyu.experiment.simulation.app.Genome;
import com.weiyu.experiment.simulation.app.LIGO;
import com.weiyu.experiment.simulation.app.Montage;
import com.weiyu.experiment.simulation.app.SIPHT;

/**
 * Generate several workflows for each application.
 * 
 * @author Gideon Juve <juve@usc.edu>
 */
public class RunAll {
    public static void run(Application app, File outfile, String... args) throws Exception {
        app.generateWorkflow(args);
        app.printWorkflow(new FileOutputStream(outfile));
    }
    
    public static void main(String[] args) throws Exception {
//    	Montage [-h] [options]
//    			--data | -d Approximate size of input data.
//    			--factor | -f Avg. runtime to execute an mProject job.
//    			--help | -h Print help message.
//    			--inputs | -i Number of inputs.
//    			--numjobs | -n Number of jobs.
//    			--overlap-probability | -p Probability any two inputs overlap.
//    			--square | -s Square degree of workflow.
//
//    			One of the following combinations is required:
//    			-d or
//    			-s, -p -i or
//    			-n
        run(new CyberShake(), new File("CyberShake_30.xml"), "-n", "30");
        run(new CyberShake(), new File("CyberShake_50.xml"), "-n", "50");
        run(new CyberShake(), new File("CyberShake_100.xml"), "-n", "100");
        run(new CyberShake(), new File("CyberShake_1000.xml"), "-n", "1000");
        
        run(new Montage(), new File("Montage_25.xml"), "-n", "25");
        run(new Montage(), new File("Montage_50.xml"), "-n", "25");
        run(new Montage(), new File("Montage_100.xml"), "-n", "25");
        run(new Montage(), new File("Montage_1000.xml"), "-n", "25");
        
        run(new Genome(), new File("Epigenomics_24.xml"), "-n", "24");
        run(new Genome(), new File("Epigenomics_46.xml"), "-n", "46");
        run(new Genome(), new File("Epigenomics_100.xml"), "-n", "100");
        run(new Genome(), new File("Epigenomics_997.xml"), "-n", "997");
        
        run(new LIGO(), new File("Inspiral_30.xml"), "-n", "30");
        run(new LIGO(), new File("Inspiral_50.xml"), "-n", "50");
        run(new LIGO(), new File("Inspiral_100.xml"), "-n", "100");
        run(new LIGO(), new File("Inspiral_1000.xml"), "-n", "1000");
        
        run(new SIPHT(), new File("Sipht_30.xml"), "-n", "30");
        run(new SIPHT(), new File("Sipht_60.xml"), "-n", "60");
        run(new SIPHT(), new File("Sipht_100.xml"), "-n", "100");
        run(new SIPHT(), new File("Sipht_1000.xml"), "-n", "1000");
        
        
        run(new CyberShake(), new File("CyberShake_30_1.xml"), "-n", "30");
        run(new CyberShake(), new File("CyberShake_50_1.xml"), "-n", "50");
        run(new CyberShake(), new File("CyberShake_100_1.xml"), "-n", "100");
        run(new CyberShake(), new File("CyberShake_1000_1.xml"), "-n", "1000");
        
        run(new Montage(), new File("Montage_25_1.xml"), "-n", "25");
        run(new Montage(), new File("Montage_50_1.xml"), "-n", "25");
        run(new Montage(), new File("Montage_100_1.xml"), "-n", "25");
        run(new Montage(), new File("Montage_1000_1.xml"), "-n", "25");
        
        run(new Genome(), new File("Epigenomics_24_1.xml"), "-n", "24");
        run(new Genome(), new File("Epigenomics_46_1.xml"), "-n", "46");
        run(new Genome(), new File("Epigenomics_100_1.xml"), "-n", "100");
        run(new Genome(), new File("Epigenomics_997_1.xml"), "-n", "997");
        
        run(new LIGO(), new File("Inspiral_30_1.xml"), "-n", "30");
        run(new LIGO(), new File("Inspiral_50_1.xml"), "-n", "50");
        run(new LIGO(), new File("Inspiral_100_1.xml"), "-n", "100");
        run(new LIGO(), new File("Inspiral_1000_1.xml"), "-n", "1000");
        
        run(new SIPHT(), new File("Sipht_30_1.xml"), "-n", "30");
        run(new SIPHT(), new File("Sipht_60_1.xml"), "-n", "60");
        run(new SIPHT(), new File("Sipht_100_1.xml"), "-n", "100");
        run(new SIPHT(), new File("Sipht_1000_1.xml"), "-n", "1000");
    }
}
