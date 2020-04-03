package com.kelab.experiment.controller;

import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.experiment.convert.ExperimentClassConvert;
import com.kelab.experiment.service.ExperimentClassService;
import com.kelab.info.base.JsonAndModel;
import com.kelab.info.base.constant.StatusMsgConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExperimentClassController {

    private ExperimentClassService experimentClassService;

    public ExperimentClassController(ExperimentClassService experimentClassService) {
        this.experimentClassService = experimentClassService;
    }

    /**
     * 查询所有班级
     */
    @GetMapping("/experiment/class.do")
    @Verify(
            notNull = {"context.operatorId", "context.operatorRoleId"},
            numberLimit = {"query.page [1, 100000]", "query.rows [1, 100000]"}
    )
    public JsonAndModel queryPage(Context context, ExperimentClassQuery query) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentClassService.queryPage(context, query))
                .build();
    }

    /**
     * 开设班级
     */
    @PostMapping("/experiment/class.do")
    @Verify(notNull = {"context.operatorId", "context.operatorRoleId", "record.className", "record.termName"})
    public JsonAndModel createExperimentClass(Context context, @RequestBody ExperimentClassInfo record) {
        experimentClassService.createExperimentClass(context, ExperimentClassConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 更新班级信息
     */
    @PutMapping("/experiment/class.do")
    @Verify(notNull = "record.id")
    public JsonAndModel updateExperimentClass(Context context, @RequestBody ExperimentClassInfo record) {
        experimentClassService.updateExperimentClass(context, ExperimentClassConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/experiment/class.do")
    @Verify(sizeLimit = "ids [1, 200]")
    public JsonAndModel deleteExperimentClass(Context context, @RequestParam("ids") List<Integer> ids) {
        experimentClassService.deleteExperimentClass(context, ids);
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }
}
