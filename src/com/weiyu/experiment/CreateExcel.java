package com.weiyu.experiment;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxx on 2016/10/28.
 * poi-3.15.jar
 */
public class CreateExcel {
        public static void main(String[] args) throws IOException {
            //**创建工作簿
            HSSFWorkbook wb = new HSSFWorkbook();
            //1、创建工作表
            HSSFSheet sheet = wb.createSheet("商品信息表");
            for(int i = 0; i < 3; i++){
                //设置列宽
                sheet.setColumnWidth(i, 3000);
            }
            //创建行
            HSSFRow row = sheet.createRow(0);
            row.setHeightInPoints(30);//设置行高
            //创建单元格
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("商品信息");

            //2、标题样式

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

            //3、设置单元格字体样式
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
            //设置单元格样式
            cell.setCellStyle(cellStyle);
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            HSSFRow row1 = sheet.createRow(1);
            //标题信息
            String[] titles = {"商品id","商品名称","商品价格"};
            for(int i = 0; i < 3; i++){
                HSSFCell cell1 = row1.createCell(i);
                cell1.setCellValue(titles[i]);
                //设置单元格样式
                cell1.setCellStyle(cellStyle);
            }

            //模拟数据
            List<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"1","冠达妙乐滋青汁大麦若叶吐司面包720g 早餐糕点零食","11"});
            list.add(new String[]{"2","确悦 iphone7超薄手机硅胶套 苹果7 plus软壳外壳7P防摔保护套4.7","22"});
            list.add(new String[]{"3","毛巾架太空铝浴巾架折叠卫生间置物架毛巾杆浴室卫浴五金挂件套装","33"});

            ///4、内容样式
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
            //循环赋值
            for(int i = 0; i < list.size(); i++){
                HSSFRow row2 = sheet.createRow(i+2);
                for(int j = 0; j < 3; j++){
                    HSSFCell cell1 = row2.createCell(j);
                    cell1.setCellValue(list.get(i)[j]);
                    //设置单元格样式
                    cell1.setCellStyle(cellStyle2);
                }
            }
            File file = new File("E:\\test.xls");
            if(!file.exists()){
                file.createNewFile();
            }
            //保存Excel文件
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
        }
    }