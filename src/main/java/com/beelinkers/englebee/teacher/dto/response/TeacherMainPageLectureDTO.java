package com.beelinkers.englebee.teacher.dto.response;

import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherMainPageLectureDTO {

  private Long id;
  private Long lectureSeq;
  private String studentNickname;
  private String title;
  private String status;
  private LocalDateTime createdAt;
  private SubjectLevelCodeDTO subjectLevelCode;
}
