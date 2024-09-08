package com.beelinkers.englebee.student.dto.response.mapper;

import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageLectureDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageNewExamDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageSubmitExamDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StudentMainPageMapper {

  // main > lecture
  public StudentMainPageLectureDTO mainPageLectureDto(Lecture lecture) {
    List<String> subjectCode = lecture.getSubjectLevels().stream()
        .map(lectureSubjectLevel -> lectureSubjectLevel.getSubjectLevel().getSubjectCode()
            .getKoreanCode())
        .collect(Collectors.toList());

    List<String> levelCode = lecture.getSubjectLevels().stream()
        .map(lectureSubjectLevel -> lectureSubjectLevel.getSubjectLevel().getLevelCode()
            .getKoreanCode())
        .collect(Collectors.toList());

    SubjectLevelCodeDTO subjectLevelCode = new SubjectLevelCodeDTO();

    return new StudentMainPageLectureDTO(
        lecture.getSeq(),
        lecture.getTeacher().getNickname(),
        lecture.getTitle(),
        lecture.getStatus().name(),
        lecture.getCreatedAt(),
        subjectLevelCode
    );
  }

  // main > newExamList
  public StudentMainPageNewExamDTO mainPageNewExamDTO(Exam exam) {
    Lecture lecture = exam.getLecture();
    return new StudentMainPageNewExamDTO(
        exam.getSeq(),
        lecture.getSeq(),
        exam.getStatus().name(),
        exam.getTitle(),
        lecture.getTeacher().getNickname(),
        lecture.getCreatedAt()
    );
  }

  // main > submitExamList
  public StudentMainPageSubmitExamDTO mainPageSubmitExamDTO(Exam exam) {
    Lecture lecture = exam.getLecture();
    return new StudentMainPageSubmitExamDTO(
        exam.getSeq(),
        lecture.getSeq(),
        exam.getStatus().name(),
        exam.getTitle(),
        lecture.getTeacher().getNickname(),
        lecture.getCreatedAt()
    );
  }
}
