package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.dal.domain.*;
import com.kelab.experiment.dal.repo.*;
import com.kelab.experiment.service.ExperimentScoreDownLoadService;
import com.kelab.experiment.support.ContextLogger;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.experiment.util.ZipUtils;
import com.kelab.info.context.Context;
import com.kelab.info.problemcenter.info.ProblemUserMarkInnerInfo;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExperimentScoreDownLoadServiceImpl implements ExperimentScoreDownLoadService {

    private ExperimentHomeworkRepo experimentHomeworkRepo;

    private ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ExperimentClassRepo experimentClassRepo;

    private ExperimentContestRepo experimentContestRepo;

    private ProblemCenterService problemCenterService;

    private ContextLogger contextLogger;

    public ExperimentScoreDownLoadServiceImpl(ExperimentHomeworkRepo experimentHomeworkRepo,
                                              ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo,
                                              ExperimentStudentRepo experimentStudentRepo,
                                              ExperimentClassRepo experimentClassRepo,
                                              ExperimentContestRepo experimentContestRepo,
                                              ProblemCenterService problemCenterService,
                                              ContextLogger contextLogger) {
        this.experimentHomeworkRepo = experimentHomeworkRepo;
        this.experimentStudentHomeworkRepo = experimentStudentHomeworkRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.experimentClassRepo = experimentClassRepo;
        this.experimentContestRepo = experimentContestRepo;
        this.problemCenterService = problemCenterService;
        this.contextLogger = contextLogger;
    }

    @Override
    public ResponseEntity<byte[]> downloadClassContestSource(Context context, Integer classId) {
        ExperimentClassDomain classDomain = getExperimentClass(context, classId);
        List<ExperimentStudentDomain> allStudent = getAllStudent(context, classId);
        Map<Integer, ExperimentUserContestDomain> userContestMap = allStudent.stream().map(item -> {
            ExperimentUserContestDomain single = new ExperimentUserContestDomain();
            single.setUserId(item.getUserId());
            single.setStudentInfo(item);
            return single;
        }).collect(Collectors.toMap(ExperimentUserContestDomain::getUserId, obj -> obj, (v1, v2) -> v2));
        // 查询布置过的所有实验
        List<ExperimentContestDomain> contestList = experimentContestRepo.queryAllByClassId(context, classId, true);
        if (!CollectionUtils.isEmpty(contestList)) {
            // 所有题目的提交历史
            Map<Integer, Map<Integer, ProblemUserMarkInnerInfo>> userSubmitHistory = queryAllSubmitHistory(context, new ArrayList<>(userContestMap.keySet()), contestList, false);
            userContestMap.forEach((id, userContest) -> userContest.setSubmitHistory(userSubmitHistory.getOrDefault(id, Collections.emptyMap())));
            return writeZip(context, userContestMap, classDomain, contestList);
        }
        return null;
    }

    /**
     * 写入zip
     */
    private ResponseEntity<byte[]> writeZip(Context context,
                                            Map<Integer, ExperimentUserContestDomain> userContestMap,
                                            ExperimentClassDomain classDomain,
                                            List<ExperimentContestDomain> contestList) {
        // 文件目录
        String path = "/tmp/" + System.currentTimeMillis();
        String classDir = path + "/" + classDomain.getClassName() + "(" + classDomain.getTermName() + ")";
        // 对于每一个实验都进行创建文件夹
        contestList.forEach(contest -> {
            File contestDir = new File(classDir + "/" + contest.getTitle());
            Preconditions.checkState(contestDir.mkdirs());
            // 对于每个学生创建一个文件夹，存放当前实验所有的提交记录
            userContestMap.forEach((id, userContest) -> {
                File userDir = new File(contestDir,
                        userContest.getStudentInfo().getStudentInfo().getStudentId() + userContest.getStudentInfo().getStudentInfo().getRealName());
                Preconditions.checkState(userDir.mkdirs());
                contest.getProblemDomains().forEach(problem -> {
                    ProblemUserMarkInnerInfo submit = userContest.getSubmitHistory().get(problem.getProbId());
                    // 在实验截止时间之前有提交记录
                    if (submit != null && submit.getMarkTime() < contest.getEndTime()) {
                        File problemFile = new File(userDir, "(" + problem.getProbId() + ")" + problem.getTitle() + ".txt");
                        try {
                            Preconditions.checkState(problemFile.createNewFile());
                            FileUtils.write(problemFile, submit.getSubmitRef().getSource());
                        } catch (IOException e) {
                            contextLogger.error(context, "创建文件失败：%s" + e.getMessage());
                        }
                    }
                });
            });
        });
        String zipName = classDomain.getClassName() + classDomain.getTermName() + ".zip";
        zipName = new String(zipName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        ZipUtils.zip(classDir, path + "/" + zipName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(new File(path + "/" + zipName)), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (Exception e) {
                contextLogger.error(context, "删除文件失败：%s" + e.getMessage());
            }
        }
    }

    private ExperimentClassDomain getExperimentClass(Context context, Integer classId) {
        List<ExperimentClassDomain> classDomains = experimentClassRepo.queryByIds(context, Collections.singletonList(classId), true);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(classDomains), "班级不存在");
        return classDomains.get(0);
    }

    private List<ExperimentStudentDomain> getAllStudent(Context context, Integer classId) {
        List<ExperimentStudentDomain> allStudent = experimentStudentRepo.queryAllByClassId(context, classId, true);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(allStudent), "不能没有学生");
        return allStudent;
    }

    @Override
    public ResponseEntity<byte[]> downloadClassScore(Context context, Integer classId) {
        ExperimentClassDomain classDomain = getExperimentClass(context, classId);
        List<ExperimentStudentDomain> allStudent = getAllStudent(context, classId);
        Map<Integer, ExperimentUserScoreDomain> studentScoreMap = allStudent.stream().map(item -> {
            ExperimentUserScoreDomain single = new ExperimentUserScoreDomain();
            single.setUserId(item.getUserId());
            single.setStudentInfo(item);
            return single;
        }).collect(Collectors.toMap(ExperimentUserScoreDomain::getUserId, obj -> obj, (v1, v2) -> v2));
        fillHomeworkScore(studentScoreMap, classId);
        fillContestScore(context, studentScoreMap, classId);
        File xlsFile = new File("classScore.xls");
        this.writeToExcel(xlsFile, studentScoreMap);
        String fileName = new String((classDomain.getClassName() + ".xls").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(xlsFile), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            xlsFile.delete();
        }
    }

    /**
     * 将成绩写入excel
     */
    private void writeToExcel(File xlsFile, Map<Integer, ExperimentUserScoreDomain> studentScoreMap) {
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(xlsFile);
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            int row = 0;
            int col = 0;
            sheet.addCell(new Label(col++, row, "学号"));
            sheet.addCell(new Label(col++, row, "姓名"));
            sheet.addCell(new Label(col++, row, "班级"));
            ExperimentUserScoreDomain oneStudent = getOneStudent(studentScoreMap);
            assert oneStudent != null;
            if (!CollectionUtils.isEmpty(oneStudent.getContestMap())) {
                List<ExperimentContestDomain> contestList = new ArrayList<>(oneStudent.getContestMap().values());
                contestList.sort(Comparator.comparing(ExperimentContestDomain::getId));
                for (ExperimentContestDomain single : contestList) {
                    sheet.addCell(new Label(col++, row, single.getTitle() + "(实验)"));
                }
            }
            if (!CollectionUtils.isEmpty(oneStudent.getHomeworkMap())) {
                List<ExperimentHomeworkDomain> homeworkList = new ArrayList<>(oneStudent.getHomeworkMap().values());
                homeworkList.sort(Comparator.comparing(ExperimentHomeworkDomain::getId));
                for (ExperimentHomeworkDomain single : homeworkList) {
                    if (single.getType() == HomeWorkType.GROUP) {
                        sheet.addCell(new Label(col++, row, single.getTitle() + "(小组作业)"));
                    } else {
                        sheet.addCell(new Label(col++, row, single.getTitle() + "(个人作业)"));
                    }
                }
            }
            sheet.addCell(new Label(col, row, "综合成绩(所有实验成绩和作业成绩平均分)"));
            // 分数低于60标红
            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            WritableCellFormat redWcf = new WritableCellFormat(font);
            for (Map.Entry<Integer, ExperimentUserScoreDomain> entry : studentScoreMap.entrySet()) {
                ExperimentUserScoreDomain student = entry.getValue();
                col = 0;
                row++;
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getStudentId()));
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getRealName()));
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getStudentClass()));
                double totalScore = 0;
                int column = 0;
                if (!CollectionUtils.isEmpty(student.getContestMap())) {
                    List<ExperimentContestDomain> userContestList = new ArrayList<>(student.getContestMap().values());
                    userContestList.sort(Comparator.comparing(ExperimentContestDomain::getId));
                    for (ExperimentContestDomain single : userContestList) {
                        double score = single.getAcNum() / (single.getTotalNum() * 1.0) * 100.0;
                        if (score < 60) {
                            sheet.addCell(new Label(col++, row, String.format("%.2f", score), redWcf));
                        } else {
                            sheet.addCell(new Label(col++, row, String.format("%.2f", score)));
                        }
                        totalScore += score;
                        column++;
                    }
                }
                if (!CollectionUtils.isEmpty(student.getHomeworkMap())) {
                    List<ExperimentHomeworkDomain> userHomeworkList = new ArrayList<>(student.getHomeworkMap().values());
                    userHomeworkList.sort(Comparator.comparing(ExperimentHomeworkDomain::getId));
                    for (ExperimentHomeworkDomain single : userHomeworkList) {
                        if (single.getSubmitInfo().getScore() < 60) {
                            sheet.addCell(new Label(col++, row, String.format("%.2f", single.getSubmitInfo().getScore() * 1.0), redWcf));
                        } else {
                            sheet.addCell(new Label(col++, row, String.format("%.2f", single.getSubmitInfo().getScore() * 1.0)));
                        }
                        totalScore += single.getSubmitInfo().getScore();
                        column++;
                    }
                }
                double averageScore = totalScore / (column == 0 ? 1 : column);
                if (averageScore < 60) {
                    sheet.addCell(new Label(col, row, String.format("%.2f", averageScore), redWcf));
                } else {
                    sheet.addCell(new Label(col, row, String.format("%.2f", averageScore)));
                }
            }
            workbook.write();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert workbook != null;
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取一个人的信息
     * 用于获取表头信息
     */
    private ExperimentUserScoreDomain getOneStudent(Map<Integer, ExperimentUserScoreDomain> studentScoreMap) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(studentScoreMap));
        for (Map.Entry<Integer, ExperimentUserScoreDomain> entry : studentScoreMap.entrySet()) {
            return entry.getValue();
        }
        return null;
    }

    /**
     * 填充实验成绩
     */
    private void fillContestScore(Context context, Map<Integer, ExperimentUserScoreDomain> studentScoreMap, Integer classId) {
        // 查询布置过的所有实验
        List<ExperimentContestDomain> contestList = experimentContestRepo.queryAllByClassId(context, classId, false);
        if (!CollectionUtils.isEmpty(contestList)) {
            // 对每一个学生都初始化实验
            studentScoreMap.forEach((k, v) -> v.setContestMap(copyNewContestList(contestList)));
            Map<Integer, Map<Integer, ProblemUserMarkInnerInfo>> allUserAcMap = queryAllSubmitHistory(context, new ArrayList<>(studentScoreMap.keySet()), contestList, true);
            // 填充每个实验的完成情况
            studentScoreMap.forEach((userId, student) ->
                    student.getContestMap().forEach((contestId, contest) -> {
                        Map<Integer, ProblemUserMarkInnerInfo> userAc = allUserAcMap.getOrDefault(userId, Collections.emptyMap());
                        long acCount = contest.getProblemDomains().stream()
                                .filter(singleProblem -> userAc.containsKey(singleProblem.getProbId())
                                        && userAc.get(singleProblem.getProbId()).getMarkTime() < contest.getEndTime())
                                .count();
                        contest.setAcNum((int) acCount);
                    }));
        }
    }

    /**
     * 填充作业成绩
     */
    private void fillHomeworkScore(Map<Integer, ExperimentUserScoreDomain> studentScoreMap, Integer classId) {
        // 查询所有布置过的作业
        List<ExperimentHomeworkDomain> homeworkList = experimentHomeworkRepo.queryAllByClassId(classId);
        Map<Integer, ExperimentHomeworkDomain> homeworkMap = homeworkList.stream().collect(Collectors.toMap(ExperimentHomeworkDomain::getId, obj -> obj, (v1, v2) -> v2));
        if (!CollectionUtils.isEmpty(homeworkList)) {
            // 对每一个学生都初始化作业集
            studentScoreMap.forEach((k, v) -> v.setHomeworkMap(copyNewHomeworkList(homeworkList)));
            List<ExperimentStudentHomeworkDomain> submits = experimentStudentHomeworkRepo.queryByHomeworkIds(homeworkList.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()));
            // 提交列表
            submits.forEach(studentHomework -> {
                ExperimentHomeworkDomain homework = homeworkMap.get(studentHomework.getHomeworkId());
                switch (homework.getType()) {
                    case PERSON:
                        studentScoreMap.get(studentHomework.getTargetId())
                                .getHomeworkMap().get(homework.getId())
                                .setSubmitInfo(studentHomework);
                    case GROUP:
                        studentScoreMap.forEach((k, student) -> {
                            if (student.getStudentInfo().getGroupId().equals(studentHomework.getTargetId())) {
                                student.getHomeworkMap().get(homework.getId())
                                        .setSubmitInfo(studentHomework);
                            }
                        });
                        break;
                    default:
                }
            });
        }
    }

    /**
     * 查询历史提交记录
     */
    private Map<Integer, Map<Integer, ProblemUserMarkInnerInfo>> queryAllSubmitHistory(Context context,
                                                                                       List<Integer> userIds,
                                                                                       List<ExperimentContestDomain> contestList,
                                                                                       boolean onlyAc) {
        // 截止时间 endTime， 缩小搜索的范围
        long maxEndTime = 0L;
        for (ExperimentContestDomain single : contestList) {
            maxEndTime = maxEndTime < single.getEndTime() ? single.getEndTime() : maxEndTime;
        }
        // 合并所有的实验题目：
        List<Integer> probIds = new ArrayList<>();
        contestList.forEach(item -> probIds.addAll(item.getProblemDomains().stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList())));
        // 题目的 ac 记录
        if (onlyAc) {
            return problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context, userIds, probIds, maxEndTime)
                    .stream().collect(Collectors.groupingBy(ProblemUserMarkInnerInfo::getUserId, Collectors.toMap(ProblemUserMarkInnerInfo::getProblemId, obj -> obj, (v1, v2) -> v2)));
        } else {
            return problemCenterService.queryByUserIdsAndProbIdsAndEndTimeWithSubmitInfo(context, userIds, probIds, maxEndTime)
                    .stream().collect(Collectors.groupingBy(ProblemUserMarkInnerInfo::getUserId, Collectors.toMap(ProblemUserMarkInnerInfo::getProblemId, obj -> obj, (v1, v2) -> v2)));
        }
    }

    private Map<Integer, ExperimentHomeworkDomain> copyNewHomeworkList(List<ExperimentHomeworkDomain> source) {
        return source.stream().map(item -> {
            ExperimentHomeworkDomain single = new ExperimentHomeworkDomain();
            single.setId(item.getId());
            single.setTitle(item.getTitle());
            single.setType(item.getType());
            // 初始化每个人都是0分
            ExperimentStudentHomeworkDomain studentHomework = new ExperimentStudentHomeworkDomain();
            studentHomework.setScore(0);
            single.setSubmitInfo(studentHomework);
            return single;
        }).collect(Collectors.toMap(ExperimentHomeworkDomain::getId, obj -> obj, (v1, v2) -> v2));
    }

    private Map<Integer, ExperimentContestDomain> copyNewContestList(List<ExperimentContestDomain> source) {
        return source.stream().map(item -> {
            ExperimentContestDomain single = new ExperimentContestDomain();
            single.setId(item.getId());
            single.setTitle(item.getTitle());
            single.setProblemDomains(item.getProblemDomains());
            single.setEndTime(item.getEndTime());
            single.setAcNum(0);
            single.setTotalNum(item.getTotalNum());
            return single;
        }).collect(Collectors.toMap(ExperimentContestDomain::getId, obj -> obj, (v1, v2) -> v2));
    }
}
