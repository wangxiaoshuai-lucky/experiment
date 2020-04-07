package com.kelab.experiment.support.service;

import com.kelab.experiment.support.ParamBuilder;
import com.kelab.experiment.support.facade.ProblemCenterServiceSender;
import com.kelab.info.context.Context;
import com.kelab.info.problemcenter.info.ProblemInfo;
import com.kelab.info.problemcenter.info.ProblemUserMarkInnerInfo;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProblemCenterService {

    private ProblemCenterServiceSender problemCenterServiceSender;

    public ProblemCenterService(ProblemCenterServiceSender problemCenterServiceSender) {
        this.problemCenterServiceSender = problemCenterServiceSender;
    }

    public Map<Integer, ProblemInfo> queryByProIds(Context context, List<Integer> ids) {
        List<ProblemInfo> problemInfos = problemCenterServiceSender.queryByIds(
                ParamBuilder.buildParam(context)
                        .param("ids", Strings.collectionToCommaDelimitedString(ids)));
        if (CollectionUtils.isEmpty(problemInfos)) {
            return Collections.emptyMap();
        }
        return problemInfos.stream().collect((Collectors.toMap(ProblemInfo::getId, obj -> obj, (v1, v2) -> v2)));
    }

    /**
     * 查询用户在截止时间之前指定题目的ac记录
     */
    public List<ProblemUserMarkInnerInfo> queryByUserIdsAndProbIdsAndEndTime(Context context,
                                                                             List<Integer> userIds,
                                                                             List<Integer> probIds,
                                                                             Long endTime) {
        List<ProblemUserMarkInnerInfo> infos = problemCenterServiceSender.queryByUserIdsAndProbIdsAndEndTime(
                ParamBuilder.buildParam(context)
                        .param("userIds", Strings.collectionToCommaDelimitedString(userIds))
                        .param("probIds", Strings.collectionToCommaDelimitedString(probIds))
                        .param("endTime", endTime));
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos;
    }

    /**
     * 查询用户在截止时间之前指定题目的做题记录
     * 带有提交信息
     */
    public List<ProblemUserMarkInnerInfo> queryByUserIdsAndProbIdsAndEndTimeWithSubmitInfo(Context context,
                                                                                           List<Integer> userIds,
                                                                                           List<Integer> probIds,
                                                                                           Long endTime) {
        List<ProblemUserMarkInnerInfo> infos = problemCenterServiceSender.queryByUserIdsAndProbIdsAndEndTimeWithSubmitInfo(
                ParamBuilder.buildParam(context)
                        .param("userIds", Strings.collectionToCommaDelimitedString(userIds))
                        .param("probIds", Strings.collectionToCommaDelimitedString(probIds))
                        .param("endTime", endTime));
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos;
    }
}
