package com.microcyber.cloud.history.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microcyber.cloud.history.service.DataItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microcyber.cloud.history.model.InfluxQueryParams;
import com.microcyber.cloud.history.model.ReturnResult;
import com.microcyber.cloud.history.service.HistoryService;

import java.util.*;

/**
 * 历史查询接口
 * 
 * @author zhaohongwei
 *
 */
@RequestMapping(value = "/history")
@RestController
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private HistoryService historyService;
    @Autowired
    private DataItemService dataItemService;

    /**
     * 外部查询历史数据接口
     * 
     * @param
     * @param
     */
    @RequestMapping(value = "/query")
    public ReturnResult queryhistory(@RequestBody InfluxQueryParams iqp) {
        ReturnResult returnResult = historyService.getQueryList(iqp);
        return returnResult;
    }

    /**
     * 外部查询最新数据接口
     * 
     * @param
     * @param
     */
    @RequestMapping(value = "/last/query")
    public ReturnResult queryLast(@RequestBody InfluxQueryParams iqp) {
        ReturnResult returnResult = historyService.getQueryLast(iqp);
        return returnResult;
    }

    /**
     * 外部查询最新数据接口,所有的数据项
     * 
     * @param
     * @param
     */
    @RequestMapping(value = "/all/query")
    public ReturnResult queryAllLast(@RequestBody InfluxQueryParams iqp) {
        ReturnResult returnResult = historyService.getQueryAllLast(iqp);
        return returnResult;
    }

    /**
     * 分组查询
     * 
     * @param iqp
     * @return
     */
    @RequestMapping(value = "/group/query")
    public ReturnResult groupQuery(@RequestBody InfluxQueryParams iqp) {
        ReturnResult returnResult = historyService.getGroupQueryList(iqp);
        return returnResult;
    }

    /**
     * 分组查询
     * 
     * @param
     * @return limit 分页的条数 page 当前页数 sort 排序规则 默认asc
     */
    @RequestMapping(value = "/queryByPage")
    public ReturnResult groupQuery(String beginDate, String endDate, @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "2000") String limit, String deviceId, String dataItemId, String dataType,
            @RequestParam(defaultValue = "asc") String sort) {
        ReturnResult returnResult = historyService.getQueryListByPage(beginDate, endDate, page, limit, deviceId,
                dataItemId, dataType, sort);
        return returnResult;
    }
}
