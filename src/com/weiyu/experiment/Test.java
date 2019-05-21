package com.weiyu.experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.weiyu.experiment.domain.SimulationResult;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Hello\tWorld");
		List<SimulationResult> results = new ArrayList<>();
		SimulationResult result1 = new SimulationResult(20, 50, 1, "EDF", "Upward Rank", "LEVEL1", 0.2, 34.57, 31.33, 30, 1.2, 1.2);
		SimulationResult result2 = new SimulationResult(20, 50, 1, "EDF", "Upward Rank", "LEVEL1", 0.2, 34.57, 31.33, 30, 1.2, 1.2);
		SimulationResult result3 = new SimulationResult(20, 50, 1, "EDF", "Upward Rank", "LEVEL1", 0.2, 34.57, 31.33, 30, 1.2, 1.2);
		SimulationResult result4 = new SimulationResult(20, 50, 1, "EDF", "Upward Rank", "LEVEL1", 0.2, 34.57, 31.33, 30, 1.2, 1.2);
		results.add(result1);
		results.add(result2);
		results.add(result3);
		results.add(result4);
		exportToTxt(results);
	}
	
	private static void exportToTxt(List<SimulationResult> results) throws IOException {
		String filePath = "F:\\��ҵ����!\\Experiment\\Montage_results.txt";
		File file = new File(filePath);
		if (!file.exists()) {    
			file.createNewFile();// �������򴴽�    
        }
		BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));//true,��׷��д��text�ı� 
		for(SimulationResult result : results){
			writer.write(result.toString());
			writer.write("\r\n");//����
		}
		
		writer.flush();
		if(null != writer){
			writer.close();
		}
	}


}
