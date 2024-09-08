package com.beelinkers.englebee.teacher.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRegisterLectureRequestDTO {

  private String studentNickname;
  private String lectureTitle;
  private List<TeacherRegisterLectureSubjectLevelDTO> subjectLevels;

}
