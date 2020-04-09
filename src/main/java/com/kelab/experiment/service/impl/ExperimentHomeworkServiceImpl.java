package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.convert.ExperimentStudentHomeworkConvert;
import com.kelab.experiment.dal.domain.*;
import com.kelab.experiment.dal.repo.*;
import com.kelab.experiment.service.ExperimentHomeworkService;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.info.ExperimentStudentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
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
public class ExperimentHomeworkServiceImpl implements ExperimentHomeworkService {

    private ExperimentHomeworkRepo experimentHomeworkRepo;

    private ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ExperimentGroupRepo experimentGroupRepo;

    private ExperimentClassRepo experimentClassRepo;

    private ExperimentContestRepo experimentContestRepo;


    private ProblemCenterService problemCenterService;


    public ExperimentHomeworkServiceImpl(ExperimentHomeworkRepo experimentHomeworkRepo,
                                         ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo,
                                         ExperimentStudentRepo experimentStudentRepo,
                                         ExperimentGroupRepo experimentGroupRepo,
                                         ExperimentClassRepo experimentClassRepo,
                                         ExperimentContestRepo experimentContestRepo,
                                         ProblemCenterService problemCenterService) {
        this.experimentHomeworkRepo = experimentHomeworkRepo;
        this.experimentStudentHomeworkRepo = experimentStudentHomeworkRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.experimentGroupRepo = experimentGroupRepo;
        this.experimentClassRepo = experimentClassRepo;
        this.experimentContestRepo = experimentContestRepo;
        this.problemCenterService = problemCenterService;
    }

    @Override
    public PaginationResult<ExperimentHomeworkInfo> queryHomeworkPage(Context context, ExperimentHomeworkQuery query) {
        PaginationResult<ExperimentHomeworkInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (CollectionUtils.isEmpty(totalIds)) {
            List<ExperimentHomeworkInfo> infos = convertToInfo(context, experimentHomeworkRepo.queryPage(query));
            result.setPagingList(infos);
            result.setTotal(experimentHomeworkRepo.queryTotal(query));
        } else {
            List<ExperimentHomeworkInfo> infos = convertToInfo(context, experimentHomeworkRepo.queryByIds(totalIds));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        }
        return result;
    }

    @Override
    public void createHomework(Context context, ExperimentHomeworkDomain record) {
        checkGroup(record);
        experimentHomeworkRepo.save(record);
    }

    @Override
    public void updateHomework(Context context, ExperimentHomeworkDomain record) {
        checkGroup(record);
        List<ExperimentHomeworkDomain> old = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(old), "作业不存在");
        // 如果更改了作业的类型，那么删除之前所有的提交作业
        if (record.getType() != null && record.getType() != old.get(0).getType()) {
            experimentStudentHomeworkRepo.deleteByHomeworkId(old.get(0).getId());
        }
        experimentHomeworkRepo.update(record);
    }

    private void checkGroup(ExperimentHomeworkDomain record) {
        if (record.getType() == HomeWorkType.GROUP) {
            List<ExperimentHomeworkDomain> old = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getId()));
            Preconditions.checkArgument(!CollectionUtils.isEmpty(old), "作业不存在");
            List<ExperimentGroupDomain> groupDomains = experimentGroupRepo.queryAllByClassId(null, old.get(0).getClassId(), false);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(groupDomains), "班级没有分组，不可以布置分组作业");
        }
    }

    @Override
    public void deleteHomework(Context context, List<Integer> ids) {
        experimentHomeworkRepo.delete(ids);
    }

    @Override
    public PaginationResult<ExperimentStudentHomeworkInfo> queryStudentHomeworkPage(Context context, ExperimentStudentHomeworkQuery query) {
        PaginationResult<ExperimentStudentHomeworkInfo> result = new PaginationResult<>();
        // 获取作业的布置信息
        List<ExperimentHomeworkDomain> homework = experimentHomeworkRepo.queryByIds(Collections.singletonList(query.getHomeworkId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(homework), "作业不存在");
        List<Integer> ids = CommonService.totalIds(query);
        if (CollectionUtils.isEmpty(ids)) {
            List<ExperimentStudentHomeworkInfo> infos = convertToStudentHomeworkInfo(context, experimentStudentHomeworkRepo.queryPage(context, query, true));
            result.setPagingList(infos);
            result.setTotal(experimentStudentHomeworkRepo.queryTotal(query));
        } else {
            List<ExperimentStudentHomeworkInfo> infos = convertToStudentHomeworkInfo(context, experimentStudentHomeworkRepo.queryByIds(context, ids, true));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        }
        return result;
    }

    @Override
    public void submitHomework(Context context, ExperimentStudentHomeworkDomain record) {
        List<ExperimentHomeworkDomain> homeworkList = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getHomeworkId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(homeworkList), "作业不存在");
        ExperimentHomeworkDomain homework = homeworkList.get(0);
        // 已经到了截至时间
        Preconditions.checkArgument(homework.getEndTime() > System.currentTimeMillis(), "到达截至时间");
        // 分组作业
        if (homework.getType() == HomeWorkType.GROUP) {
            ExperimentStudentDomain student = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), homework.getClassId());
            Preconditions.checkArgument(student.getGroupId() != 0, "你没有加组");
            record.setTargetId(student.getGroupId());
        } else {
            record.setTargetId(context.getOperatorId());
        }
        // 教师未批改的状态
        record.setScore(0);
        record.setComment(null);
        record.setCommentTime(null);
        record.setPostTime(System.currentTimeMillis());
        // 查找之前的提交情况, 如果有就替换
        List<ExperimentStudentHomeworkDomain> oldSubmit = experimentStudentHomeworkRepo.queryAllByHomeworkIdsAndTargetIds(context,
                Collections.singletonList(homework.getId()),
                Collections.singletonList(record.getTargetId()),
                false);
        if (!CollectionUtils.isEmpty(oldSubmit)) {
            record.setId(oldSubmit.get(0).getId());
            experimentStudentHomeworkRepo.update(record);
        } else {
            experimentStudentHomeworkRepo.save(record);
        }
    }

    @Override
    public void reviewHomework(Context context, ExperimentStudentHomeworkDomain record) {
        Preconditions.checkArgument(record.getScore() <= 0, "不能低于1分哦");
        Preconditions.checkArgument(record.getScore() > 100, "不能超过100分");
        record.setAttachName(null);
        record.setAttachUrl(null);
        record.setPostTime(null);
        record.setCommentTime(System.currentTimeMillis());
        experimentStudentHomeworkRepo.update(record);
    }

    @Override
    public ResponseEntity<byte[]> downloadClassScore(Context context, Integer classId) {
        // todo 下载班级成绩表
        List<ExperimentClassDomain> classDomains = experimentClassRepo.queryByIds(context, Collections.singletonList(classId), true);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(classDomains), "班级不存在");
        // 1 学生的作业成绩
        List<ExperimentStudentDomain> allStudent = experimentStudentRepo.queryAllByClassId(context, classId, true);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(allStudent), "不能没有学生");
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
        String fileName = new String((classDomains.get(0).getClassName() + ".xls").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(xlsFile), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            xlsFile.delete();
        }
        return null;
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
            List<ExperimentContestDomain> contestList = new ArrayList<>(oneStudent.getContestMap().values());
            contestList.sort(Comparator.comparing(ExperimentContestDomain::getId));
            for (ExperimentContestDomain single : contestList) {
                sheet.addCell(new Label(col++, row, single.getTitle() + "(实验)"));
            }
            // 分数低于60标红
            WritableFont font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
            WritableCellFormat redWcf = new WritableCellFormat(font);
            List<ExperimentHomeworkDomain> homeworkList = new ArrayList<>(oneStudent.getHomeworkMap().values());
            homeworkList.sort(Comparator.comparing(ExperimentHomeworkDomain::getId));
            for (ExperimentHomeworkDomain single : homeworkList) {
                if (single.getType() == HomeWorkType.GROUP) {
                    sheet.addCell(new Label(col++, row, single.getTitle() + "(小组作业)"));
                } else {
                    sheet.addCell(new Label(col++, row, single.getTitle() + "(个人作业)"));
                }
            }
            sheet.addCell(new Label(col, row, "综合成绩(所有实验成绩和作业成绩平均分)"));
            for (Map.Entry<Integer, ExperimentUserScoreDomain> entry : studentScoreMap.entrySet()) {
                ExperimentUserScoreDomain student = entry.getValue();
                col = 0;
                row++;
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getStudentId()));
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getRealName()));
                sheet.addCell(new Label(col++, row, student.getStudentInfo().getStudentInfo().getStudentClass()));
                List<ExperimentContestDomain> userContestList = new ArrayList<>(student.getContestMap().values());
                userContestList.sort(Comparator.comparing(ExperimentContestDomain::getId));
                double totalScore = 0;
                for (ExperimentContestDomain single : userContestList) {
                    double score = single.getAcNum() / (single.getTotalNum() * 1.0) * 100.0;
                    if (score < 60) {
                        sheet.addCell(new Label(col++, row, String.format("%.2f", score), redWcf));
                    } else {
                        sheet.addCell(new Label(col++, row, String.format("%.2f", score)));
                    }
                    totalScore += score;
                }
                List<ExperimentHomeworkDomain> userHomeworkList = new ArrayList<>(student.getHomeworkMap().values());
                userHomeworkList.sort(Comparator.comparing(ExperimentHomeworkDomain::getId));
                for (ExperimentHomeworkDomain single : userHomeworkList) {
                    if (single.getSubmitInfo().getScore() < 60) {
                        sheet.addCell(new Label(col++, row, single.getSubmitInfo().getScore().toString(), redWcf));
                    } else {
                        sheet.addCell(new Label(col++, row, single.getSubmitInfo().getScore().toString()));
                    }
                    totalScore += single.getSubmitInfo().getScore();
                }
                double averageScore = totalScore / (userContestList.size() + userHomeworkList.size());
                if (averageScore < 60) {
                    sheet.addCell(new Label(col, row, String.format("%.2f", averageScore), redWcf));
                }else {
                    sheet.addCell(new Label(col, row, String.format("%.2f", averageScore)));
                }
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
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
            // 截止时间 endTime， 缩小搜索的范围
            long maxEndTime = 0L;
            for (ExperimentContestDomain single : contestList) {
                maxEndTime = maxEndTime < single.getEndTime() ? single.getEndTime() : maxEndTime;
            }
            // 合并所有的实验题目：
            List<Integer> probIds = new ArrayList<>();
            contestList.forEach(item -> probIds.addAll(item.getProblemDomains().stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList())));
            // 题目的 ac 记录
            Map<Integer, Map<Integer, ProblemUserMarkInnerInfo>> allUserAcMap = problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context, new ArrayList<>(studentScoreMap.keySet()), probIds, maxEndTime)
                    .stream().collect(Collectors.groupingBy(ProblemUserMarkInnerInfo::getUserId, Collectors.toMap(ProblemUserMarkInnerInfo::getProblemId, obj -> obj, (v1, v2) -> v2)));
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

    private List<ExperimentStudentHomeworkInfo> convertToStudentHomeworkInfo(Context context, List<ExperimentStudentHomeworkDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentStudentHomeworkConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<ExperimentHomeworkInfo> convertToInfo(Context context, List<ExperimentHomeworkDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        // 填充自己的作业信息
        // 查询自己所在分组信息，查询分组作业
        ExperimentStudentDomain studentDomain = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), domains.get(0).getClassId());
        List<ExperimentStudentHomeworkDomain> studentHomework = experimentStudentHomeworkRepo.queryAllByHomeworkIdsAndTargetIds(context,
                domains.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()),
                Arrays.asList(context.getOperatorId(), studentDomain.getGroupId()),
                true);
        // Map<作业 ID::提交人 ID, 学生作业> 这里的 targetId 有可能是用户 id 也有可能是分组 id
        Map<String, ExperimentStudentHomeworkDomain> studentHomeworkMap = studentHomework.stream().collect(
                Collectors.toMap(item -> item.getHomeworkId() + "::" + item.getTargetId(), obj -> obj, (v1, v2) -> v2));
        domains.forEach(item -> {
            switch (item.getType()) {
                case PERSON:
                    item.setSubmitInfo(studentHomeworkMap.get(item.getId() + "::" + context.getOperatorId()));
                    break;
                case GROUP:
                    item.setSubmitInfo(studentHomeworkMap.get(item.getId() + "::" + studentDomain.getGroupId()));
                    break;
                default:
            }
        });
        fillSubmitNumberAndTotal(context, domains);
        return domains.stream().map(ExperimentHomeworkConvert::domainToInfo).collect(Collectors.toList());
    }

    private void fillSubmitNumberAndTotal(Context context, List<ExperimentHomeworkDomain> domains) {
        // 班级的所有分组
        List<ExperimentGroupDomain> groupDomains = experimentGroupRepo.queryAllByClassId(context, domains.get(0).getClassId(), false);
        // 所有的学生
        ExperimentStudentQuery query = new ExperimentStudentQuery();
        query.setStatus(ApplyClassStatus.ALLOWED.value());
        query.setClassId(domains.get(0).getClassId());
        Integer totalUser = experimentStudentRepo.queryTotal(query);
        Map<Integer, Integer> submitTotal = experimentStudentHomeworkRepo.queryTotalByHomeworkIds(
                domains.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(HomeworkSubmitDomain::getHomeworkId, HomeworkSubmitDomain::getTotalNum));
        domains.forEach(item -> {
            switch (item.getType()) {
                case PERSON:
                    item.setTotalNum(totalUser);
                    item.setPostNum(submitTotal.getOrDefault(item.getId(), 0));
                    break;
                case GROUP:
                    item.setTotalNum(groupDomains.size());
                    item.setPostNum(submitTotal.getOrDefault(item.getId(), 0));
                    break;
                default:
            }
        });
    }
}
