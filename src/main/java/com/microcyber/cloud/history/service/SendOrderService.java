package com.microcyber.cloud.history.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
@FeignClient(value = "MC-COLLECTION-Z")
@RequestMapping("/send")
@Service
public interface SendOrderService {
    /**
     * type指令的类型，目前只有fanyi的，type=5,同步数据项的指令
     * device_id,设备id
     * gateway_id网关id
     * cmd 指令内容，目前只有fanyi，同步的指令
     * driver 驱动的类型，繁易的驱动类型，sys_dict表种driver对应的key_name
     *
     * */
    @RequestMapping(value="/order")
    public Map sendOrder(@RequestParam(defaultValue = "0")int type, @RequestParam(defaultValue = "") String device_id, @RequestParam(defaultValue = "") String gateway_id,@RequestParam(defaultValue = "") String cmd,@RequestParam(defaultValue = "") String driver);
    /**下发指令配置
     * type指令的类型，目前只有fanyi的，繁易不支持下发指令，type=0即可
     * device_id,设备id
     * gateway_id网关id
     * cmd 指令内容，目前只有fanyi，同步的指令，空即可
     * driver 驱动的类型，繁易的驱动类型，sys_dict表种driver对应的key_name
     *
     * */
    @RequestMapping(value="/device/order")
    public Map sendDeviceOrder(@RequestParam(defaultValue = "0")int type, @RequestParam(defaultValue = "") String device_id, @RequestParam(defaultValue = "") String gateway_id, @RequestParam(defaultValue = "") String cmd, @RequestParam(defaultValue = "") String driver);
}
