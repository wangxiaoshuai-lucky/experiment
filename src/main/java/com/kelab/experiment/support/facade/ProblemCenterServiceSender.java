package com.kelab.experiment.support.facade;


import cn.wzy.verifyUtils.annotation.Verify;
import com.kelab.info.context.Context;
import com.kelab.info.problemcenter.info.ProblemInfo;
import com.kelab.info.problemcenter.info.ProblemUserMarkInfo;
import com.kelab.info.problemcenter.info.ProblemUserMarkInnerInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "service-problemcenter")
@RequestMapping("/problemcenter")
public interface ProblemCenterServiceSender {


    /**
     * 通过ids查询题目
     */
    @GetMapping("/inner/queryByIds")
    List<ProblemInfo> queryByIds(@RequestParam Map<String, Object> param);

    /**
     * 查询用户在截止时间之前指定题目的ac记录
     */
    @GetMapping("/inner/queryUserProbAc")
    List<ProblemUserMarkInnerInfo> queryByUserIdsAndProbIdsAndEndTime(@RequestParam Map<String, Object> param);

    /**
     * 查询用户在截止时间之前指定题目的做题记录
     * 带有提交信息
     */
    @GetMapping("/inner/queryUserProbWithSubmitInfo")
    List<ProblemUserMarkInnerInfo> queryByUserIdsAndProbIdsAndEndTimeWithSubmitInfo(@RequestParam Map<String, Object> param);
}
