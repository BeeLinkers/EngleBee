package com.beelinkers.englebee.teacher.controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import com.beelinkers.englebee.teacher.service.TeacherMainService;
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
public class TeacherMainApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TeacherMainService teacherMainService;

  @Autowired
  private WebApplicationContext context;
  @Autowired
  private TeacherMainApiController teacherMainApiController;


  @BeforeEach
  public void setupSecurity() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  @WithMockUser(roles = {"TEACHER"})
  @DisplayName("강의 조회 API 테스트")
  void 강의_조회_API_테스트() throws Exception {
    // given
    Long lectureSeq = 1L;
    Long memberSeq = 3L;
    LectureStatus lectureStatus = LectureStatus.CREATED;
    List<String> subjectCodes = List.of("어법", "문법");
    List<String> levelCodes = List.of("중", "고");

    SubjectLevelCodeDTO subjectLevelCode = new SubjectLevelCodeDTO();
    TeacherMainPageLectureDTO lecturePageList = new TeacherMainPageLectureDTO(
        1L, 1L, "user1", "기초어법강의", "CREATED", LocalDateTime.now(), subjectLevelCode
    );
    List<TeacherMainPageLectureDTO> lectureList = List.of(lecturePageList);

    when(teacherMainService.getOngoingLectureInfo(memberSeq, lectureStatus)).thenReturn(
        lectureList);

    //when
    ResponseEntity<List<TeacherMainPageLectureDTO>> response = teacherMainApiController.getOngoingLecture(
        memberSeq, "CREATED"
    );

    //then
    assertEquals(lectureList, response.getBody());
    assertEquals(200, response.getStatusCode().value());

  }

  @Test
  @WithMockUser(roles = {"TEACHER"})
  @DisplayName("출제 할 문제 목록 API 테스트")
  void 출제_할_문제_목록_API_테스트() throws Exception {
    // given
    TeacherMainPagePendingExamDTO pendingExamDTO = new TeacherMainPagePendingExamDTO(
        2L, 1L, ExamStatus.CREATED.name(), "기초어법강의", "user2", LocalDateTime.now()
    );
    List<TeacherMainPagePendingExamDTO> pendingExamList = List.of(pendingExamDTO);
    when(teacherMainService.getPendingExamInfo(3L, ExamStatus.CREATED))
        .thenReturn(List.of(pendingExamDTO));

    //when
    ResponseEntity<List<TeacherMainPagePendingExamDTO>> response = teacherMainApiController.getPendingExam(
        3L);

    //then
    assertEquals(pendingExamList, response.getBody());
    assertEquals(200, response.getStatusCode().value());

  }

  @Test
  @WithMockUser(roles = {"TEACHER"})
  @DisplayName("출제 한 목록 조회 API 테스트")
  void 출제_한_목록_조회_API_테스트() throws Exception {
    // given
    Long memberSeq = 3L;
    List<ExamStatus> status = List.of(ExamStatus.PREPARED, ExamStatus.SUBMITTED,
        ExamStatus.FEEDBACK_COMPLETED);

    TeacherMainPageExamHistoryDTO examHistoryPreparedDTO = new TeacherMainPageExamHistoryDTO(
        1L, 1L, ExamStatus.PREPARED.name(), "기초문장강의", "user2", LocalDateTime.now()
    );
    TeacherMainPageExamHistoryDTO examHistorySubmittedDTO = new TeacherMainPageExamHistoryDTO(
        2L, 2L, ExamStatus.SUBMITTED.name(), "기초문법강의", "user2", LocalDateTime.now()
    );
    TeacherMainPageExamHistoryDTO examHistoryFeedbackDTO = new TeacherMainPageExamHistoryDTO(
        3L, 3L, ExamStatus.FEEDBACK_COMPLETED.name(), "기초어법강의", "user2", LocalDateTime.now()
    );
    List<TeacherMainPageExamHistoryDTO> examHistoryList = List.of(examHistoryPreparedDTO,
        examHistorySubmittedDTO, examHistoryFeedbackDTO);
    when(teacherMainService.getExamHistoryInfo(3L, status)).thenReturn(examHistoryList);

    //when
    ResponseEntity<List<TeacherMainPageExamHistoryDTO>> response = teacherMainApiController.getExamHistory(
        memberSeq, status
    );

    //then
    assertEquals(examHistoryList, response.getBody());
    assertEquals(200, response.getStatusCode().value());

  }
}
