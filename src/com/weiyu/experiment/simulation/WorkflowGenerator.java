package com.weiyu.experiment.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

import com.weiyu.experiment.simulation.app.AppFactory;
import com.weiyu.experiment.simulation.app.Application;
import com.weiyu.experiment.simulation.app.Montage;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * 生成一定数量的工作流应用
 * 
 * @author Wei Yu
 *
 */
public class WorkflowGenerator {

	public static void main(String[] arg) throws Exception {
		int[] workflowNumbers = { 20, 40, 60, 80, 100 };
		int[] taskNumbers = { 20, 40, 60, 80};
		int[] taskNames = {50, 100, 200, 500};
		for (int i = 0; i < workflowNumbers.length; i++) {
			for (int s = 0; s < workflowNumbers[i]; s++)
				for (int j = 0; j < taskNumbers.length; j++) {
					for (int k = 0; k < 10; k++) {
						generateWorkflows(workflowNumbers[i], taskNames[j], s, k);
					}
				}
		}

	}

	private static void generateWorkflows(int workflowNumber, int taskNumber, int s, int index)
			throws Exception, FileNotFoundException {
		Random random = new Random();
		double runtime = 3 + random.nextInt(15000) / 1000;
		// String[] args = { "-a", "Montage", "-n", "40", "-f",
		// String.valueOf(runtime) };
		// String[] args = { "-a", "Montage", "-n", "40" };
		String[] args = { "-a", "Montage", "-n", String.valueOf(taskNumber) };
		String[] newArgs = Arrays.copyOfRange(args, 2, args.length);

		Application app = new Montage();
		app.generateWorkflow(newArgs);
		String fileName = args[1] + "_" + workflowNumber + "_" + taskNumber + "_" + index + "_" + s + ".xml";
		fileName = "E:/实验数据/RandomDAG/" + fileName;
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		app.printWorkflow(new FileOutputStream(file));
	}
}
