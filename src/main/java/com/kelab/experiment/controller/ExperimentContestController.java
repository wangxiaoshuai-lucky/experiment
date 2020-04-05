package com.kelab.experiment.controller;

import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.service.ExperimentContestService;
import com.kelab.info.base.JsonAndModel;
import com.kelab.info.base.constant.StatusMsgConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import com.kelab.info.experiment.query.ExperimentContestQuery;
import com.kelab.info.experiment.query.ExperimentProblemQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExperimentContestController {

    private ExperimentContestService experimentContestService;

    public ExperimentContestController(ExperimentContestService experimentContestService) {
        this.experimentContestService = experimentContestService;
    }

    /**
     * 查询实验
     */
    @GetMapping("/experiment/class/contest.do")
    public JsonAndModel queryContest(Context context, ExperimentContestQuery query) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentContestService.queryContest(context, query))
                .build();
    }

    /**
     * 创建实验
     */
    @PostMapping("/experiment/class/contest.do")
    @Verify(
            notNull = {"record.title", "record.classId", "record.endTime", "context.operatorId", "context.operatorRoleId"},
            sizeLimit = "record.problemInfos [1, 1000]"
    )
    public JsonAndModel saveContest(Context context, @RequestBody ExperimentContestInfo record) {
        experimentContestService.saveContest(context, ExperimentContestConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 更新实验
     */
    @PutMapping("/experiment/class/contest.do")
    @Verify(
            notNull = {"record.id", "context.operatorId", "context.operatorRoleId"},
            sizeLimit = "record.problemInfos [1, 1000]"
    )
    public JsonAndModel updateContest(Context context, @RequestBody ExperimentContestInfo record) {
        experimentContestService.updateContest(context, ExperimentContestConvert.infoToDomain(record));
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 删除实验
     */
    @DeleteMapping("/experiment/class/contest.do")
    @Verify(sizeLimit = "ids [1, 200]")
    public JsonAndModel deleteContest(Context context, @RequestParam("ids") List<Integer> ids) {
        experimentContestService.deleteContest(context, ids);
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS).build();
    }

    /**
     * 查询实验题目
     */
    @GetMapping("/experiment/contest/problems.do")
    @Verify(
            notNull = {"context.operatorId", "context.operatorRoleId", "query.contestId"},
            numberLimit = {"query.page [1, 100000]", "query.rows [1, 100000]"}
    )
    public JsonAndModel queryByContestIdPage(Context context, ExperimentProblemQuery query) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentContestService.queryByContestIdPage(context, query))
                .build();
    }

    /**
     * 查询实验排行
     */
    @GetMapping("/experiment/contest/rank.do")
    @Verify(notNull = {"context.operatorId", "context.operatorRoleId", "contestId"})
    public JsonAndModel queryRankByContestId(Context context, Integer contestId) {
        return JsonAndModel.builder(StatusMsgConstant.SUCCESS)
                .data(experimentContestService.queryRankByContestId(context, contestId))
                .build();
    }
}
