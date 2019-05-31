package com.weiyu.experiment.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.Job;
import org.workflowsim.Task;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.Parameters.ClassType;

public class Print {
	public static void printJobList(List<Job> list) {
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Job ID" + indent + "Task ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID"
				+ indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Depth");
		DecimalFormat dft = new DecimalFormat("###.##");
		for (Job job : list) {
			Log.print(indent + job.getCloudletId() + indent + indent);
			if (job.getClassType() == ClassType.STAGE_IN.value) {
				Log.print("Stage-in");
			}
			for (Task task : job.getTaskList()) {
				Log.print(task.getCloudletId() + ",");
			}
			Log.print(indent);

			if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");
				Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId() + indent
						+ indent + indent + dft.format(job.getActualCPUTime()) + indent + indent
						+ dft.format(job.getExecStartTime()) + indent + indent + indent
						+ dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
			} else if (job.getCloudletStatus() == Cloudlet.FAILED) {
				Log.print("FAILED");
				Log.printLine(indent + indent + job.getResourceId() + indent + indent + indent + job.getVmId() + indent
						+ indent + indent + dft.format(job.getActualCPUTime()) + indent + indent
						+ dft.format(job.getExecStartTime()) + indent + indent + indent
						+ dft.format(job.getFinishTime()) + indent + indent + indent + job.getDepth());
			}
		}
	}

	public static void exportToExcel(List<Job> list) throws IOException {
		// **创建工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("执行数据");
		for (int i = 0; i < 9; i++) {
			// 设置列宽
			sheet.setColumnWidth(i, 3000);
		}
		// 创建行
		HSSFRow row = sheet.createRow(0);
		row.setHeightInPoints(30);// 设置行高
		// 创建单元格
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("数据结果表");

		// 2、标题样式

		// 创建单元格样式
		HSSFCellStyle cellStyle = wb.createCellStyle();
		// 设置单元格的背景颜色为green
		cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置单元格居中对齐
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 设置单元格垂直居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle.setWrapText(true);

		// 3、设置单元格字体样式
		HSSFFont font = wb.createFont();
		// 设置字体加粗
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("宋体");
		font.setFontHeight((short) 200);
		cellStyle.setFont(font);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 设置单元格样式
		// cell.setCellStyle(cellStyle);
		// 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

		HSSFRow row1 = sheet.createRow(1);
		// 标题信息
		String[] titles = { "Job ID", "Task ID", "STATUS", "Data center ID", "VM ID", "Time", "Start Time",
				"Finish Time", "Depth" };
		for (int i = 0; i < 9; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
			// 设置单元格样式
			// cell1.setCellStyle(cellStyle);
		}

		/// 4、内容样式
		// 创建单元格样式
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		// 设置单元格居中对齐
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 设置单元格垂直居中对齐
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 创建单元格内容显示不下时自动换行
		cellStyle2.setWrapText(true);

		// 设置单元格字体样式
		HSSFFont font2 = wb.createFont();
		// 设置字体加粗
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font2.setFontName("宋体");
		font2.setFontHeight((short) 200);
		cellStyle2.setFont(font2);
		// 设置单元格边框为细线条
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// 循环赋值
		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < list.size(); i++) {
			HSSFRow row2 = sheet.createRow(i + 2);
			Job job = list.get(i);
			// for(int j = 0; j < 9; j++){
			HSSFCell cell0 = row2.createCell(0);
			cell0.setCellValue(job.getCloudletId());
			HSSFCell cell1 = row2.createCell(1);
			HSSFCell cell2 = row2.createCell(2);
			for (Task task : job.getTaskList()) {
				cell1.setCellValue(task.getCloudletId());
			}

			if (job.getClassType() == ClassType.STAGE_IN.value) {
				cell1.setCellValue("Stage-in");
			}

			HSSFCell cell3 = row2.createCell(3);
			HSSFCell cell4 = row2.createCell(4);
			HSSFCell cell5 = row2.createCell(5);
			HSSFCell cell6 = row2.createCell(6);
			HSSFCell cell7 = row2.createCell(7);
			HSSFCell cell8 = row2.createCell(8);

			if (job.getCloudletStatus() == Cloudlet.SUCCESS) {
				cell2.setCellValue("SUCCESS");

			} else if (job.getCloudletStatus() == Cloudlet.FAILED) {
				cell2.setCellValue("FAILED");
			}
			cell3.setCellValue(job.getResourceId());
			cell4.setCellValue(job.getVmId());
			cell5.setCellValue(dft.format(job.getActualCPUTime()));
			cell6.setCellValue(dft.format(job.getExecStartTime()));
			cell7.setCellValue(dft.format(job.getFinishTime()));
			cell8.setCellValue(job.getDepth());
			// 设置单元格样式
			// cell1.setCellStyle(cellStyle2);
		}
		// }

		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		String fileName = format.format(new Date());
		File file = new File("E://实验数据/" + fileName + ".xls");
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
	}

	/**
	 * 保存虚拟机
	 * 
	 * @param list
	 * @throws IOException
	 */
	public static void exportVMsToExcel(List<CondorVM> vmList) throws IOException {
		// **创建工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("VM数据表");
		// for (int i = 0; i < 9; i++) {
		// // 设置列宽
		// sheet.setColumnWidth(i, 3000);
		// }

		HSSFRow row1 = sheet.createRow(0);
		// 标题信息
		String[] titles = { "VM ID", "userId", "mips", "numberOfPes", "ram", "bw", "size", "maxpower", "vmm" };
		for (int i = 0; i < 9; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
		}

		HSSFRow row = null;
		// 创建行数
		for (int i = 0; i < vmList.size(); i++) {
			CondorVM condorVM = vmList.get(i);
			row = sheet.createRow(i + 1);
			HSSFCell cell1 = row.createCell(0);
			HSSFCell cell2 = row.createCell(1);
			HSSFCell cell3 = row.createCell(2);
			HSSFCell cell4 = row.createCell(3);
			HSSFCell cell5 = row.createCell(4);
			HSSFCell cell6 = row.createCell(5);
			HSSFCell cell7 = row.createCell(6);
			HSSFCell cell8 = row.createCell(7);
			HSSFCell cell9 = row.createCell(8);

			cell1.setCellValue(condorVM.getId());
			cell2.setCellValue(condorVM.getUserId());
			cell3.setCellValue(condorVM.getMips());
			cell4.setCellValue(condorVM.getNumberOfPes());
			cell5.setCellValue(condorVM.getRam());
			cell6.setCellValue(condorVM.getBw());
			cell7.setCellValue(condorVM.getSize());
			cell8.setCellValue(condorVM.getPower());
			cell9.setCellValue(condorVM.getVmm());
		}

		File file = new File("F:/Experiment/VM_20190530.xls");
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
	}

	public static void exportVMsToExcel(List<CondorVM> vmList, String filePath) throws IOException {
		// **创建工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 1、创建工作表
		HSSFSheet sheet = wb.createSheet("VM数据表");
		// for (int i = 0; i < 9; i++) {
		// // 设置列宽
		// sheet.setColumnWidth(i, 3000);
		// }

		HSSFRow row1 = sheet.createRow(0);
		// 标题信息
		String[] titles = { "VM ID", "userId", "mips", "numberOfPes", "ram", "bw", "size", "maxpower", "vmm" };
		for (int i = 0; i < 9; i++) {
			HSSFCell cell1 = row1.createCell(i);
			cell1.setCellValue(titles[i]);
		}

		HSSFRow row = null;
		// 创建行数
		for (int i = 0; i < vmList.size(); i++) {
			CondorVM condorVM = vmList.get(i);
			row = sheet.createRow(i + 1);
			HSSFCell cell1 = row.createCell(0);
			HSSFCell cell2 = row.createCell(1);
			HSSFCell cell3 = row.createCell(2);
			HSSFCell cell4 = row.createCell(3);
			HSSFCell cell5 = row.createCell(4);
			HSSFCell cell6 = row.createCell(5);
			HSSFCell cell7 = row.createCell(6);
			HSSFCell cell8 = row.createCell(7);
			HSSFCell cell9 = row.createCell(8);

			cell1.setCellValue(condorVM.getId());
			cell2.setCellValue(condorVM.getUserId());
			cell3.setCellValue(condorVM.getMips());
			cell4.setCellValue(condorVM.getNumberOfPes());
			cell5.setCellValue(condorVM.getRam());
			cell6.setCellValue(condorVM.getBw());
			cell7.setCellValue(condorVM.getSize());
			cell8.setCellValue(condorVM.getPower());
			cell9.setCellValue(condorVM.getVmm());
		}

		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存Excel文件
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
	}

	
	public static List<CondorVM> readVMListFromExcel(String filePath) {


		// 判断是否为excel类型文件
		if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
			System.out.println("文件不是excel类型");
		}

		FileInputStream fis = null;
		HSSFWorkbook wookbook = null;
		try {
			// 获取一个绝对地址的流
			fis = new FileInputStream(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// 2003版本的excel，用.xls结尾
			wookbook = new HSSFWorkbook(fis);// 得到工作簿

		} catch (Exception ex) {
			// ex.printStackTrace();
			try {
				// 2007版本的excel，用.xlsx结尾
				wookbook = new HSSFWorkbook(fis);// 得到工作簿
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 得到一个工作表
		HSSFSheet sheet = wookbook.getSheetAt(0);

		// 获得表头
//		HSSFRow rowHead = sheet.getRow(0);

//		// 判断表头是否正确
//		if (rowHead.getPhysicalNumberOfCells() != 3) {
//			System.out.println("表头的数量不对!");
//		}

		// 获得数据的总行数
		int totalRowNum = sheet.getLastRowNum();

		List<CondorVM> vmList = new ArrayList<CondorVM>();
		
		CondorVM fastestVm = null;
		double fastestMips = 0.0;
		// 获得所有数据
		for (int i = 1; i <= totalRowNum; i++) {
			
			// 获得第i行对象
			HSSFRow row = sheet.getRow(i);
			
			HSSFCell cell1 = row.getCell(0);
			HSSFCell cell2 = row.getCell(1);
			HSSFCell cell3 = row.getCell(2);
			HSSFCell cell4 = row.getCell(3);
			HSSFCell cell5 = row.getCell(4);
			HSSFCell cell6 = row.getCell(5);
			HSSFCell cell7 = row.getCell(6);
			HSSFCell cell8 = row.getCell(7);
			HSSFCell cell9 = row.getCell(8);
			int vmId = (int) cell1.getNumericCellValue();
			int userId = (int) cell2.getNumericCellValue();
			int mips = (int) cell3.getNumericCellValue();
			int numberOfPes = (int) cell4.getNumericCellValue();
			int ram = (int) cell5.getNumericCellValue();
			int bw = (int) cell6.getNumericCellValue();
			int size = (int) cell7.getNumericCellValue();
			int power = (int) cell8.getNumericCellValue();
			String vmm = cell9.getStringCellValue();
			
			CondorVM condorVM = new CondorVM(vmId, userId, mips, numberOfPes, ram, bw, size, power, vmm, new CloudletSchedulerSpaceShared());
			if(mips > fastestMips){
				fastestMips = mips;
				fastestVm = condorVM;
			}
			vmList.add(condorVM);
		}
		
		Parameters.setFastestVM(fastestVm);
		return vmList;
	}
}
