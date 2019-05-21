/**
 * Copyright 2012-2013 University Of Southern California
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weiyu.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.weiyu.experiment.domain.SimulationResult;

/**
 * ���������ĳ����¶๤����ʵ��
 * 
 * @author Wei Yu
 *
 */
public class RPDCalculation {
	
	
	public static void main(String[] args) throws IOException {
		importFromTxt();
	}

	/**
	 * ���õ��Ľ��д�뵽TXT�ĵ���
	 * 
	 * @param results
	 * @throws IOException
	 */
	private static void exportToTxt(List<SimulationResult> results) throws IOException {
		String filePath = "E:/ʵ������/Montage_results.txt";
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();// �������򴴽�
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));// true,��׷��д��text�ı�
		for (SimulationResult result : results) {
			writer.write(result.toString());
			writer.write("\r\n");// ����
		}

		writer.flush();
		if (null != writer) {
			writer.close();
		}
	}
	
	
	private static void importFromTxt() throws IOException {
		String filePath = "E:/ʵ������/Montage_results.txt";
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
		String line = null;
		List<SimulationResult> results = new ArrayList<>();
		while((line = reader.readLine()) != null){
			SimulationResult result = new SimulationResult();
			String[] attrs = line.split("\t");
			int workflowNum = Integer.parseInt(attrs[0]);
			int taskNum = Integer.parseInt(attrs[1]);
			int instanceNum = Integer.parseInt(attrs[2]);
			String workflowName = attrs[3];
			String taskName = attrs[4];
			double electricCost = Double.parseDouble(attrs[5]);
			long runtime = Long.parseLong(attrs[6]);
			double rpd = Double.parseDouble(attrs[7]);
			result.setWorkflowNumber(workflowNum);
			result.setTaskNumber(taskNum);
			result.setInstanceNumber(instanceNum);
			result.setWorkflowMethod(workflowName);
			result.setRankMethod(taskName);
			result.setElecCostForAllWorkflowsBeforeVND(electricCost);
			result.setRuntime(runtime);
			result.setRpdBeforeVND(rpd);
			results.add(result);		
		}
		if(null != reader){
			reader.close();
		}
		
		List<Double> minimalCosts = new ArrayList<>();
		double minimalCost = Double.MAX_VALUE;
		for(int i = 0;i < results.size();i++){
			SimulationResult result = results.get(i);
			int index = i % 9;
			if(index <= 8){
				if(result.getElecCostForAllWorkflowsBeforeVND() < minimalCost)
					minimalCost = result.getElecCostForAllWorkflowsBeforeVND();
				if(8 == index){
					minimalCosts.add(minimalCost);
					minimalCost = Double.MAX_VALUE;
				}
			}
		}
		
		for(int i = 0;i < results.size();i++){
			SimulationResult result = results.get(i);
			int item = i / 9;
			minimalCost = minimalCosts.get(item);
			double rpd = (result.getElecCostForAllWorkflowsBeforeVND() - minimalCost) / minimalCost * 100;
			result.setRpdBeforeVND(rpd);
		}
		
		filePath = "E:/ʵ������/results_new.txt";
		file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();// �������򴴽�
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));// true,��׷��д��text�ı�
		for (SimulationResult result : results) {
			writer.write(result.toString());
			writer.write("\r\n");// ����
		}

		writer.flush();
		if (null != writer) {
			writer.close();
		}
	}
	
	

	/**
	 * ��Լ���õ��Ľ������ý����RPDֵ
	 * 
	 * @param results
	 */
	private static void calculateRPD(List<SimulationResult> results) {
		double minimalElecCostBeforeVND = Double.MAX_VALUE;
		double minimalElecCostAfterVND = Double.MAX_VALUE;

		for (SimulationResult result : results) {
			if (result.getElecCostForAllWorkflowsBeforeVND() < minimalElecCostBeforeVND)
				minimalElecCostBeforeVND = result.getElecCostForAllWorkflowsBeforeVND();

			if (result.getElecCostForAllWorkflowsAfterVND() < minimalElecCostAfterVND)
				minimalElecCostAfterVND = result.getElecCostForAllWorkflowsAfterVND();
		}

		for (SimulationResult result : results) {
			double elecCostForAllWorkflowsBeforeVND = result.getElecCostForAllWorkflowsBeforeVND();
			double elecCostForAllWorkflowsAfterVND = result.getElecCostForAllWorkflowsAfterVND();
			result.setRpdBeforeVND(
					(elecCostForAllWorkflowsBeforeVND - minimalElecCostBeforeVND) / minimalElecCostBeforeVND);
			result.setRpdAfterVND(
					(elecCostForAllWorkflowsAfterVND - minimalElecCostAfterVND) / minimalElecCostAfterVND);
		}
	}

}
