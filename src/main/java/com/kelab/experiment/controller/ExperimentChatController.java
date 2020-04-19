package com.kelab.experiment.controller;

import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.experiment.convert.ExperimentChatConvert;
import com.kelab.experiment.service.ExperimentChatService;
import com.kelab.info.base.JsonAndModel;
import com.kelab.info.base.constant.StatusMsgConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChatInfo;
import com.kelab.info.experiment.query.ExperimentChatQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExperimentChatController {

    private ExperimentChatService experimentChatService;

    public ExperimentChatController(ExperimentChatService experimentChatService) {
        this.experimentChatService = experimentChatService;
    }

    /**
     * 查询讨论区
     */
    @GetMapping("/experiment/class/chat.do")
    @Verify(
            notNull = {"query.classId"},
            numberLimit = {"query.page [1, 10000]", "query.rows [1, 10000]"}
    )
    public JsonAndModel queryChatPage(Context context, ExperimentChatQuery query) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentChatService.queryPage(context, query))
                .build();
    }

    /**
     * 提交讨论
     */
    @PostMapping("/experiment/class/chat.do")
    @Verify(notNull = {"record.classId", "record.content", "record.parentId"})
    public JsonAndModel createChat(Context context, @RequestBody ExperimentChatInfo record) {
        experimentChatService.createChat(context, ExperimentChatConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }
}
