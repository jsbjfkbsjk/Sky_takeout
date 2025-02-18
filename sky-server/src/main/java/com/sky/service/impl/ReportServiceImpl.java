package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
