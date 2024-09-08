package com.beelinkers.englebee.teacher.dto.response.mapper;

import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureSubjectLevel;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import org.springframework.stereotype.Component;

@Component
public class TeacherMainPageMapper {

  // main > lecture
  public TeacherMainPageLectureDTO teacherMainPageLectureDto(Lecture lecture) {
    SubjectLevelCodeDTO subjectLevelCodeDTO = null;

    if (lecture.getSubjectLevels() != null && !lecture.getSubjectLevels().isEmpty()) {
      LectureSubjectLevel firstSubjectLevel = lecture.getSubjectLevels().get(0);
      subjectLevelCodeDTO = new SubjectLevelCodeDTO(
          firstSubjectLevel.getSubjectLevel().getSubjectKoreanCode(),
          firstSubjectLevel.getSubjectLevel().getLevelKoreanCode()
      );
    }
    return new TeacherMainPageLectureDTO(
        lecture.getSeq(),
        lecture.getTeacher().getNickname(),
        lecture.getTitle(),
        lecture.getStatus().name(),
        lecture.getCreatedAt(),
        subjectLevelCodeDTO
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
