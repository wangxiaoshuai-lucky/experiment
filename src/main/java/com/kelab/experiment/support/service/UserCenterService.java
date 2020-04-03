package com.kelab.experiment.support.service;

import com.kelab.experiment.support.ParamBuilder;
import com.kelab.experiment.support.facade.UserCenterServiceSender;
import com.kelab.info.context.Context;
import com.kelab.info.usercenter.info.UserInfo;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCenterService {

    private UserCenterServiceSender userCenterServiceSender;

    public UserCenterService(UserCenterServiceSender userCenterServiceSender) {
        this.userCenterServiceSender = userCenterServiceSender;
    }

    public List<UserInfo> queryByUserIds(Context context, List<Integer> ids) {
        Map<String, Object> param = new HashMap<>();
        param.put("ids", Strings.collectionToCommaDelimitedString(ids));
        List<UserInfo> userInfos = userCenterServiceSender.queryByUserIds(ParamBuilder.buildParam(context, param));
        if (CollectionUtils.isEmpty(userInfos)) {
            return Collections.emptyList();
        }
        return userInfos;
    }
}
