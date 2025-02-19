package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;



    @Override
    public TurnoverReportVO getStatistics(LocalDate start, LocalDate end) {
        List<LocalDate>list = new ArrayList();
        while(!start.isAfter(end)) {
            list.add(start);
            start = start.plusDays(1);
        }
        String s= StringUtils.join(list, ",");
        log.info("日期区间{}",s);
        List<Double>turnoverList = new ArrayList<>();
        for(LocalDate date:list) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            log.info("beginTime,endTime:{},{}",begintime,endtime);
            Map map = new HashMap();
            map.put("begin", begintime);
            map.put("end", endtime);
            map.put("status", Orders.COMPLETED);
            Double sum=reportMapper.sumByTime(map);
            if(sum==null) {
                sum=new Double(0.0);
            }
            log.info("单日sum:{}",sum);
            turnoverList.add(sum);
        }
        String s1 = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO.builder().dateList(s).turnoverList(s1).build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate start, LocalDate end) {

        List<LocalDate>list = new ArrayList();
        while(!start.isAfter(end)) {
            list.add(start);
            start = start.plusDays(1);
        }
        String s= StringUtils.join(list, ",");
        List<Integer>newUsers=new ArrayList<>();
        List<Integer>totalUsers=new ArrayList<>();
        for(LocalDate date:list) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endtime);
            totalUsers.add(reportMapper.countByMap(map)==null?0:reportMapper.countByMap(map));
            map.put("begin", begintime);
            newUsers.add(reportMapper.countByMap(map)==null?0:reportMapper.countByMap(map));


        }

        return UserReportVO.builder().dateList(s)
                .totalUserList(StringUtils.join(totalUsers,","))
                .newUserList(StringUtils.join(newUsers,","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate start, LocalDate end) {
        List<LocalDate>list = new ArrayList();
        while(!start.isAfter(end)) {
            list.add(start);
            start = start.plusDays(1);
        }
        String s= StringUtils.join(list, ",");
        List<Integer>orders=new ArrayList<>();//总订单
        List<Integer>trueOrders=new ArrayList<>();//有效订单
        for(LocalDate date:list) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endtime);
            map.put("begin", begintime);
            orders.add(reportMapper.countOrder(map)==null?0:reportMapper.countOrder(map));
            map.put("status", Orders.COMPLETED);
            trueOrders.add(reportMapper.countOrder(map)==null?0:reportMapper.countOrder(map));
        }
        Integer totalOrder=orders.stream().reduce(0,Integer::sum);
        Integer validOrder=trueOrders.stream().reduce(0,Integer::sum);
        Double rate;
        if(totalOrder!=0) {
          rate = new Double(validOrder/totalOrder);
        }
        else{
            rate=new Double(0);
        }

        return OrderReportVO.builder()
                .dateList(s)
                .orderCountList(StringUtils.join(orders,","))
                .totalOrderCount(totalOrder)
                .validOrderCount(validOrder)
                .validOrderCountList(StringUtils.join(trueOrders,","))
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime begintime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = reportMapper.getSalesTop10(begintime, endtime);
        List<String> collects = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        //StringUtils.join(collects,",");
        List<Integer>numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        //StringUtils.join(numbers,",");

        return SalesTop10ReportVO.builder().nameList(StringUtils.join(collects,",")).numberList(StringUtils.join(numbers,",")).build();
    }

    @Override
    public void exportData(HttpServletResponse response)  {
        LocalDate end = LocalDate.now();
        LocalDate begin = LocalDate.now().minusDays(30);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try{
             XSSFWorkbook excel = new XSSFWorkbook(in);
             //填充数据
            XSSFSheet sheet = excel.getSheet("sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间："+begin+"至 "+end);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            XSSFRow row1 = sheet.getRow(4);
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            row1.getCell(4).setCellValue(businessData.getUnitPrice());
            for(int i=0;i<30;i++){
                LocalDate date =begin.plusDays(i);
                BusinessDataVO DayBusinessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row2 = sheet.getRow(7+i);
                row2.getCell(1).setCellValue(String.valueOf(date));
                row2.getCell(2).setCellValue(DayBusinessData.getTurnover());
                row2.getCell(3).setCellValue(DayBusinessData.getValidOrderCount());
                row2.getCell(4).setCellValue(DayBusinessData.getOrderCompletionRate());
                row2.getCell(5).setCellValue(DayBusinessData.getUnitPrice());
                row2.getCell(6).setCellValue(DayBusinessData.getNewUsers());

            }

            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            excel.close();


        }
        catch(Exception e){}



    }
}
