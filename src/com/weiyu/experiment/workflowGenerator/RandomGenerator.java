package com.weiyu.experiment.workflowGenerator;

import com.weiyu.experiment.simulation.app.Application;
import com.weiyu.experiment.simulation.app.CyberShake;
import com.weiyu.experiment.simulation.app.Montage;
import com.weiyu.experiment.simulation.app.SIPHT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Administrator on 2019/6/24.
 */

public class RandomGenerator {

    public static void main(String[] arg) throws Exception {

        int[] taskNumbers = { 50, 100, 150, 200, 250};

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

        int flag = random.nextInt(3);
        String[] args = { "-a", "CyberShake", "-n", String.valueOf(taskNumber) };

        Application app = new CyberShake();

        if(flag == 1){
            args = new String[]{"-a", "Montage", "-n", String.valueOf(taskNumber)};
            app = new Montage();


        }else if(flag == 2){
            args = new String[]{"-a", "SIPHT", "-n", String.valueOf(taskNumber)};
            app = new SIPHT();
        }


        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
        app.generateWorkflow(newArgs);
        String fileName = "Random" + "_"  + taskNumber + "_" + index + ".xml";
        fileName = "F:/Experiment/Random/" + fileName;
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
        }
        app.printWorkflow(new FileOutputStream(file));
    }
}

