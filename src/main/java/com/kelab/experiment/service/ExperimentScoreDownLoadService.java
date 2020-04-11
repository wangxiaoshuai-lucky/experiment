package com.kelab.experiment.service;

import com.kelab.info.context.Context;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ExperimentScoreDownLoadService {
    /**
     * 下载班级的成绩
     */
    ResponseEntity<byte[]> downloadClassScore(Context context, Integer classId);


    /**
     * 下载班级下所有的用户源代码
     */
    ResponseEntity<byte[]> downloadClassContestSource(Context context, Integer classId);
}
