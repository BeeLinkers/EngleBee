package com.beelinkers.englebee.teacher.dto.response.mapper;

import com.beelinkers.englebee.teacher.dto.response.TeacherExamRegisterPageDTO;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TeacherExamRegisterPageMapper {

  public TeacherExamRegisterPageDTO toExamRegisterPageDTO(Map<String, String> lectureSubjectLevels,
      Map<String, String> studentSubjectLevels) {
    return new TeacherExamRegisterPageDTO(studentSubjectLevels, lectureSubjectLevels);
  }
}
