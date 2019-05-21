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
 * Generate a workflow for a specific application and write it to stdout.
 * 
 * @author Shishir Bharathi
 */
public class AppGenerator {

	public static void main(String[] arg) throws Exception {
		for (int i = 0; i < 5; i++) {
			generateWorkflows(i);
		}
	}

	private static void generateWorkflows(int length) throws Exception, FileNotFoundException {

		Random random = new Random();
		double runtime = 3 + random.nextInt(15000) / 1000;
//		String[] args = { "-a", "Montage", "-n", "40", "-f", String.valueOf(runtime) };
		String[] args = { "-a", "Montage", "-n", "40" };
		String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
		
		Application app = new Montage();
		app.generateWorkflow(newArgs);
		String fileName = args[1] + "_" + args[3] + "_" + length + ".xml";
		app.printWorkflow(new FileOutputStream(new File(fileName)));
	}
}
