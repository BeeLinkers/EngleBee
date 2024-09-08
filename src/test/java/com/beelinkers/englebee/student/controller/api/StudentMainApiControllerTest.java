package com.beelinkers.englebee.student.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageLectureDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageNewExamDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageSubmitExamDTO;
import com.beelinkers.englebee.student.service.StudentMainService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentMainApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentMainService studentMainService;

  @Autowired
  private WebApplicationContext context;
  @Autowired
  private StudentMainApiController studentMainApiController;

  @BeforeEach
  public void setupSecurity() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  @WithMockUser(roles = {"STUDENT"})
  @DisplayName("수강 중인 강의 목록 조회 API 테스트")
  void 수강_중인_강의_목록_조회_API_테스트() throws Exception {
    // given
    Long lectureSeq = 1L;
    Long memberSeq = 2L;
    LectureStatus lectureStatus = LectureStatus.CREATED;
    List<String> subjectCodes = List.of("어법", "문법");
    List<String> levelCodes = List.of("중", "고");

    SubjectLevelCodeDTO subjectLevelCode = new SubjectLevelCodeDTO();
    StudentMainPageLectureDTO lecturePageList = new StudentMainPageLectureDTO(
        1L, "teacher", "기초어법강의", "CREATED", LocalDateTime.now(), subjectLevelCode
    );
    List<StudentMainPageLectureDTO> lectureList = List.of(lecturePageList);

    when(studentMainService.getOngoingLectureInfo(memberSeq, lectureSeq, lectureStatus)).thenReturn(
        lectureList);

    // when
    ResponseEntity<List<StudentMainPageLectureDTO>> response = studentMainApiController.getOngoingLecture(
        memberSeq, lectureSeq, "CREATED");

    // then
    assertEquals(lectureList, response.getBody());
    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  @WithMockUser(roles = {"STUDENT"})
  @DisplayName("풀어야 할 시험 목록 조회 API 테스트")
  void 풀어야_할_시험_목록_조회_API_테스트() throws Exception {
    // given
    StudentMainPageNewExamDTO newExamDTO = new StudentMainPageNewExamDTO(
        2L, 1L, ExamStatus.PREPARED.name(), "기초문법강의", "teacher", LocalDateTime.now()
    );
    List<StudentMainPageNewExamDTO> newExamList = List.of(newExamDTO);
    when(studentMainService.getPreparedExamInfo(2L, ExamStatus.PREPARED)).thenReturn(
        newExamList);

    // when
    ResponseEntity<List<StudentMainPageNewExamDTO>> response = studentMainApiController.getPreparedExam(
        2L);

    // then
    assertEquals(newExamList, response.getBody());
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  @WithMockUser(roles = {"STUDENT"})
  @DisplayName("제출된 시험 목록 조회 API 테스트")
  void 제출된_시험_목록_조회_API_테스트() throws Exception {
    // given
    Long memberSeq = 2L;
    List<ExamStatus> status = List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED);

    StudentMainPageSubmitExamDTO submitExamDTO = new StudentMainPageSubmitExamDTO(
        1L, 1L, ExamStatus.SUBMITTED.name(), "기본어법강의", "teacher", LocalDateTime.now()
    );
    StudentMainPageSubmitExamDTO submitExamFeedbackDTO = new StudentMainPageSubmitExamDTO(
        2L, 2L, ExamStatus.FEEDBACK_COMPLETED.name(), "기본문법강의", "teacher1", LocalDateTime.now()
    );
    List<StudentMainPageSubmitExamDTO> submitExamList = List.of(submitExamDTO,
        submitExamFeedbackDTO);
    when(studentMainService.getCompletedExamInfo(2L,
        List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED)))
        .thenReturn(submitExamList);

    // when
    ResponseEntity<List<StudentMainPageSubmitExamDTO>> response = studentMainApiController.getCompletedExam(
        memberSeq, status);

    // then
    assertEquals(submitExamList, response.getBody());
    assertEquals(200, response.getStatusCode().value());

  }
}
