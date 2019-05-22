package com.weiyu.experiment.workflowGenerator;

import com.weiyu.experiment.simulation.app.Application;
import com.weiyu.experiment.simulation.app.CyberShake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by why on 2019/5/22.
 */
public class CyberShakeGenerator {
    public static void main(String[] arg) throws Exception {

        int[] taskNumbers = { 25, 50, 100, 150, 200};

//		int[] workflowNumbers = { 20, 40, 60, 80, 100 };
// 		int[] taskNumbers = { 20, 40, 60, 80};
//		int[] taskNames = {50, 100, 200, 500};

        for(int taskNumber:taskNumbers)
            for(int i=0;i<10;i++)
                generateWorkflows(taskNumber,i);


    }

    private static void generateWorkflows(int taskNumber, int index)
            throws Exception, FileNotFoundException {
        Random random = new Random();
        double runtime = 3 + random.nextInt(15000) / 1000;
        // String[] args = { "-a", "Montage", "-n", "40", "-f",
        // String.valueOf(runtime) };
        // String[] args = { "-a", "Montage", "-n", "40" };
        String[] args = { "-a", "CyberShake", "-n", String.valueOf(taskNumber) };
        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);

        Application app = new CyberShake();
        app.generateWorkflow(newArgs);
        String fileName = args[1] + "_"  + taskNumber + "_" + index + "_"  + ".xml";
        fileName = "F:/Experiment/CyberShake/" + fileName;
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
        }
        app.printWorkflow(new FileOutputStream(file));
    }
}
