package com.kelab.experiment.support.facade;


import com.kelab.info.usercenter.info.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "service-usercenter")
@RequestMapping("/usercenter")
public interface UserCenterServiceSender {

    /**
     * 从用户中心获取用户信息
     */
    @GetMapping("/inner/queryByIds")
    List<UserInfo> queryByUserIds(@RequestParam Map<String, Object> param);
}
