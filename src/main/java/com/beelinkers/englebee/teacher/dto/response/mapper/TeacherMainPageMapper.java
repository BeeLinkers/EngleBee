package com.beelinkers.englebee.teacher.dto.response.mapper;

import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TeacherMainPageMapper {

  // main > lecture
  public TeacherMainPageLectureDTO teacherMainPageLectureDto(Lecture lecture) {
    List<String> subjectCode = lecture.getSubjectLevels().stream()
        .map(lectureSubjectLevel -> lectureSubjectLevel.getSubjectLevel().getSubjectCode()
            .getKoreanCode())
        .collect(Collectors.toList());

    List<String> levelCode = lecture.getSubjectLevels().stream()
        .map(lectureSubjectLevel -> lectureSubjectLevel.getSubjectLevel().getLevelCode()
            .getKoreanCode())
        .collect(Collectors.toList());

    SubjectLevelCodeDTO subjectLevelCode = new SubjectLevelCodeDTO(subjectCode.get(0),
        levelCode.get(0));

    return new TeacherMainPageLectureDTO(
        lecture.getSeq(),
        lecture.getSeq(),
        lecture.getStudent().getNickname(),
        lecture.getTitle(),
        lecture.getStatus().name(),
        lecture.getCreatedAt(),
        subjectLevelCode
    );
  }

  // main > pending-exam
  public TeacherMainPagePendingExamDTO teacherMainPagePendingExamDto(Exam exam) {
    Lecture lecture = exam.getLecture();
    return new TeacherMainPagePendingExamDTO(
        exam.getSeq(),
        lecture.getSeq(),
        exam.getTitle(),
        exam.getStatus().name(),
        lecture.getStudent().getNickname(),
        exam.getCreatedAt()
    );
  }

  // main > exam-history
  public TeacherMainPageExamHistoryDTO teacherMainPageExamHistoryDTO(Exam exam) {
    Lecture lecture = exam.getLecture();
    return new TeacherMainPageExamHistoryDTO(
        exam.getSeq(),
        lecture.getSeq(),
        exam.getTitle(),
        exam.getStatus().name(),
        lecture.getStudent().getNickname(),
        lecture.getCreatedAt()
    );
  }

}
