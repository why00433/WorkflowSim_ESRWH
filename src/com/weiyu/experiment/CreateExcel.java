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
            //**����������
            HSSFWorkbook wb = new HSSFWorkbook();
            //1������������
            HSSFSheet sheet = wb.createSheet("��Ʒ��Ϣ��");
            for(int i = 0; i < 3; i++){
                //�����п�
                sheet.setColumnWidth(i, 3000);
            }
            //������
            HSSFRow row = sheet.createRow(0);
            row.setHeightInPoints(30);//�����и�
            //������Ԫ��
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("��Ʒ��Ϣ");

            //2��������ʽ

            // ������Ԫ����ʽ
            HSSFCellStyle cellStyle = wb.createCellStyle();
            // ���õ�Ԫ��ı�����ɫΪgreen
            cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            // ���õ�Ԫ����ж���
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // ���õ�Ԫ��ֱ���ж���
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            // ������Ԫ��������ʾ����ʱ�Զ�����
            cellStyle.setWrapText(true);

            //3�����õ�Ԫ��������ʽ
            HSSFFont font = wb.createFont();
            // ��������Ӵ�
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            font.setFontName("����");
            font.setFontHeight((short) 200);
            cellStyle.setFont(font);
            // ���õ�Ԫ��߿�Ϊϸ����
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //���õ�Ԫ����ʽ
            cell.setCellStyle(cellStyle);
            //�ϲ���Ԫ��
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

            HSSFRow row1 = sheet.createRow(1);
            //������Ϣ
            String[] titles = {"��Ʒid","��Ʒ����","��Ʒ�۸�"};
            for(int i = 0; i < 3; i++){
                HSSFCell cell1 = row1.createCell(i);
                cell1.setCellValue(titles[i]);
                //���õ�Ԫ����ʽ
                cell1.setCellStyle(cellStyle);
            }

            //ģ������
            List<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"1","�ڴ���������֭������Ҷ��˾���720g ��͸����ʳ","11"});
            list.add(new String[]{"2","ȷ�� iphone7�����ֻ��轺�� ƻ��7 plus������7P��ˤ������4.7","22"});
            list.add(new String[]{"3","ë���̫����ԡ����۵������������ë���ԡ����ԡ���Ҽ���װ","33"});

            ///4��������ʽ
            // ������Ԫ����ʽ
            HSSFCellStyle cellStyle2 = wb.createCellStyle();
            // ���õ�Ԫ����ж���
            cellStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // ���õ�Ԫ��ֱ���ж���
            cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            // ������Ԫ��������ʾ����ʱ�Զ�����
            cellStyle2.setWrapText(true);

            // ���õ�Ԫ��������ʽ
            HSSFFont font2 = wb.createFont();
            // ��������Ӵ�
            font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            font2.setFontName("����");
            font2.setFontHeight((short) 200);
            cellStyle2.setFont(font2);
            // ���õ�Ԫ��߿�Ϊϸ����
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //ѭ����ֵ
            for(int i = 0; i < list.size(); i++){
                HSSFRow row2 = sheet.createRow(i+2);
                for(int j = 0; j < 3; j++){
                    HSSFCell cell1 = row2.createCell(j);
                    cell1.setCellValue(list.get(i)[j]);
                    //���õ�Ԫ����ʽ
                    cell1.setCellStyle(cellStyle2);
                }
            }
            File file = new File("E:\\test.xls");
            if(!file.exists()){
                file.createNewFile();
            }
            //����Excel�ļ�
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
        }
    }