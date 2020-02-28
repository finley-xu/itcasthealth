package com.itheima.health.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @ClassName TestPOI
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2020/2/19 10:52
 * @Version V1.0
 */
public class TestPOI {

    // （1）从Excel文件读取数据（简化）
    /**
     * XSSFWorkBook：工作簿
     * XSSFSheet：工作表（从0开始）
     * XSSFRow：行对象（从0开始）
     * XSSFCell：单元格对象（从0开始）
     */
    @Test
    public void readExcel() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:/hello.xlsx");
        // 2：获得工作表对象
//        XSSFSheet sheet = workbook.getSheet("预约设置模板");
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 3：遍历工作表对象 获得行对象
        for (Row row : sheet) { // 出现空格（空格表示多了一些无谓的行）
            // 4：遍历行对象 获得单元格（列）对象
            for (Cell cell : row) {
                // 5：获得数据（获取字符串）
                String value = cell.getStringCellValue();
                System.out.println(value);
            }
        }
        // 6：关闭
        workbook.close();
    }

    // （2）从Excel文件读取数据（简化）
    @Test
    public void readExcel_2() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:/hello.xlsx");
        // 2：获得工作表对象
//        XSSFSheet sheet = workbook.getSheet("预约设置模板");
        XSSFSheet sheet = workbook.getSheetAt(0);
        // 3：遍历工作表对象 获得行对象
        // 获取Sheet对象对应的最后一个行的索引
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(lastRowNum);
        for(int i=0;i<=lastRowNum;i++){
            XSSFRow row = sheet.getRow(i);
            // 获取行对象对应的最后一个单元格的索引
            short cellNum = row.getLastCellNum();
            System.out.println(cellNum);
            for(int j=0;j<cellNum;j++){
                XSSFCell cell = row.getCell(j);
                String value = cell.getStringCellValue();
                System.out.println(value);
            }
        }
        // 6：关闭
        workbook.close();
    }

    // 使用POI可以在内存中创建一个Excel文件并将数据写入到这个文件，最后通过输出流将内存中的Excel文件下载到磁盘
    @Test
    public void writeExcel() throws IOException {
        // 1：创建工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 2：创建工作表对象
        XSSFSheet sheet = workbook.createSheet("用户信息");
        // 3：创建行对象
        XSSFRow row1 = sheet.createRow(0);
        // 4：创建单元格对象，创建数据
        row1.createCell(0).setCellValue("姓名");
        row1.createCell(1).setCellValue("年龄");
        row1.createCell(2).setCellValue("地址");

        XSSFRow row2 = sheet.createRow(1);
        // 4：创建单元格对象，创建数据
        row2.createCell(0).setCellValue("张无忌");
        row2.createCell(1).setCellValue("25");
        row2.createCell(2).setCellValue("武当山");

        // 4：创建单元格对象，创建数据
        XSSFRow row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("灭绝师太");
        row3.createCell(1).setCellValue("40");
        row3.createCell(2).setCellValue("峨眉");

        // 5：输出excel
        OutputStream out = new FileOutputStream(new File("D:/user.xlsx"));
        workbook.write(out);
        out.flush();
        out.close();
        // 6：关闭
        workbook.close();
    }
}
