package com.kelab.experiment.controller;

import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.experiment.convert.ExperimentGroupConvert;
import com.kelab.experiment.service.ExperimentGroupService;
import com.kelab.info.base.JsonAndModel;
import com.kelab.info.base.constant.StatusMsgConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChangeGroupInfo;
import com.kelab.info.experiment.info.ExperimentGroupInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExperimentGroupController {

    private ExperimentGroupService experimentGroupService;

    public ExperimentGroupController(ExperimentGroupService experimentGroupService) {
        this.experimentGroupService = experimentGroupService;
    }

    /**
     * 查询班级所有的分组
     */
    @GetMapping("/experiment/class/group.do")
    @Verify(notNull = "*")
    public JsonAndModel queryAllGroup(Context context, Integer classId) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentGroupService.queryAllGroup(context, classId))
                .build();
    }

    /**
     * 创建分组
     */
    @PostMapping("/experiment/class/group.do")
    @Verify(notNull = {"record.name", "record.classId"})
    public JsonAndModel createGroup(Context context, @RequestBody ExperimentGroupInfo record) {
        experimentGroupService.createGroup(context, ExperimentGroupConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 修改分组名字
     */
    @PutMapping("/experiment/class/group.do")
    @Verify(notNull = {"record.name", "record.id"})
    public JsonAndModel updateGroup(Context context, @RequestBody ExperimentGroupInfo record) {
        experimentGroupService.updateGroup(context, ExperimentGroupConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/experiment/class/group.do")
    @Verify(sizeLimit = "ids [1, 10000]")
    public JsonAndModel deleteGroup(Context context, @RequestParam("ids") List<Integer> ids) {
        experimentGroupService.deleteGroup(context, ids);
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 切换分组
     */
    @PutMapping("/experiment/class/changeGroup.do")
    @Verify(notNull = {"record.groupId", "record.experimentStudentId"})
    public JsonAndModel changeStudentGroup(Context context, @RequestBody ExperimentChangeGroupInfo record) {
        experimentGroupService.changeStudentGroup(context, record);
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }
}
