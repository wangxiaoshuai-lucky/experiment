package com.kelab.experiment.controller;

import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.service.ExperimentHomeworkService;
import com.kelab.info.base.JsonAndModel;
import com.kelab.info.base.constant.StatusMsgConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExperimentHomeworkController {

    private ExperimentHomeworkService experimentHomeworkService;

    public ExperimentHomeworkController(ExperimentHomeworkService experimentHomeworkService) {
        this.experimentHomeworkService = experimentHomeworkService;
    }

    /**
     * 查询作业列表
     */
    @GetMapping("/experiment/class/homework.do")
    @Verify(
            notNull = {"context.operatorId", "context.operatorRoleId", "query.classId"},
            numberLimit = {"query.page [1, 1000]", "query.rows [1, 10000]"}
    )
    public JsonAndModel queryHomeworkPage(Context context, ExperimentHomeworkQuery query) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentHomeworkService.queryHomeworkPage(context, query))
                .build();
    }

    /**
     * 新建作业
     */
    @PostMapping("/experiment/class/homework.do")
    @Verify(notNull = {"context.operatorId", "context.operatorRoleId",
            "record.classId", "record.title", "record.content", "record.type", "record.endTime"})
    public JsonAndModel createHomework(Context context, @RequestBody ExperimentHomeworkInfo record) {
        experimentHomeworkService.createHomework(context, ExperimentHomeworkConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 修改
     */
    @PutMapping("/experiment/class/homework.do")
    @Verify(notNull = {"context.operatorId", "context.operatorRoleId", "record.id"})
    public JsonAndModel updateHomework(Context context, @RequestBody ExperimentHomeworkInfo record) {
        experimentHomeworkService.updateHomework(context, ExperimentHomeworkConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/experiment/class/homework.do")
    @Verify(
            notNull = {"context.operatorId", "context.operatorRoleId"},
            sizeLimit = "ids [1, 10000]"
    )
    public JsonAndModel deleteHomework(Context context, @RequestParam("ids") List<Integer> ids) {
        experimentHomeworkService.deleteHomework(context, ids);
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }
}
