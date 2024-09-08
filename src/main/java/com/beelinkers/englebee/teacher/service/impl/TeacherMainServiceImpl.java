package com.beelinkers.englebee.teacher.service.impl;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.domain.repository.ExamRepository;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import com.beelinkers.englebee.teacher.dto.response.mapper.TeacherMainPageMapper;
import com.beelinkers.englebee.teacher.service.TeacherMainService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherMainServiceImpl implements TeacherMainService {

  private final TeacherMainPageMapper teacherMainPageMapper;
  private final LectureRepository lectureRepository;
  private final ExamRepository examRepository;

  // lecture
  @Override
  public List<TeacherMainPageLectureDTO> getOngoingLectureInfo(Long memberSeq,
      LectureStatus lectureStatus) {
    return lectureRepository.findByTeacherSeqAndStatus(memberSeq, lectureStatus)
        .stream()
        .map(teacherMainPageMapper::teacherMainPageLectureDto)
        .collect(Collectors.toList());
  }

  // pending-exam
  @Override
  public List<TeacherMainPagePendingExamDTO> getPendingExamInfo(Long memberSeq,
      ExamStatus status) {
    return examRepository.findTop5ByLectureTeacherSeqAndStatusOrderByCreatedAtDesc(memberSeq,
            status)
        .stream().map(teacherMainPageMapper::teacherMainPagePendingExamDto).toList();
  }

  // exam-history
  @Override
  public List<TeacherMainPageExamHistoryDTO> getExamHistoryInfo(Long memberSeq,
      List<ExamStatus> status) {
    List<TeacherMainPageExamHistoryDTO> examHistoryList = new ArrayList<>();
    examRepository.findTop5ByLectureTeacherSeqAndStatusInOrderByCreatedAtDesc(memberSeq,
            status)
        .forEach(exam -> {
          TeacherMainPageExamHistoryDTO dto = teacherMainPageMapper.teacherMainPageExamHistoryDTO(
              exam);
          if (dto != null) {
            examHistoryList.add(dto);
          }
        });
    return examHistoryList;

  }

}
