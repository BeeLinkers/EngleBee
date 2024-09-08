package com.beelinkers.englebee.teacher.service;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import java.util.List;

public interface TeacherMainService {

  List<TeacherMainPageLectureDTO> getOngoingLectureInfo(Long memberSeq,
      LectureStatus lectureStatus);

  List<TeacherMainPagePendingExamDTO> getPendingExamInfo(Long memberSeq, ExamStatus status);

  List<TeacherMainPageExamHistoryDTO> getExamHistoryInfo(Long memberSeq,
      List<ExamStatus> status);
}
