package com.beelinkers.englebee.student.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beelinkers.englebee.student.dto.response.StudentMainPageLectureDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageNewExamDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageQuestionDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageSubmitExamDTO;
import com.beelinkers.englebee.student.service.StudentMainService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentMainApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentMainService studentMainService;

  @BeforeEach
  void setUp() {
    // given
    Page<StudentMainPageLectureDTO> lecturePage = new PageImpl<>(
        List.of(new StudentMainPageLectureDTO(
            1L, "teacher1", "기초문법강의", "CREATED",
            LocalDateTime.now(), List.of(List.of("어법", "중"), List.of("문장", "중"))
        )));
    Page<StudentMainPageQuestionDTO> questionPage = new PageImpl<>(
        List.of(new StudentMainPageQuestionDTO(
            1L, "user2", "REST API 모범 사례", LocalDateTime.now()
        )));
    Page<StudentMainPageNewExamDTO> newExamPage = new PageImpl<>(
        List.of(new StudentMainPageNewExamDTO(
            1L, 1L, "PREPARED", "풀어야 할 시험", "user3", LocalDateTime.now()
        )));
    Page<StudentMainPageSubmitExamDTO> submitExamPage = new PageImpl<>(List.of(
        new StudentMainPageSubmitExamDTO(1L, 1L, "SUBMITTED", "제출된 시험", "user3",
            LocalDateTime.now()),
        new StudentMainPageSubmitExamDTO(1L, 1L, "FEEDBACK_COMPLETED", "피드백 받은 시험", "user3",
            LocalDateTime.now())
    ));

    Mockito.when(studentMainService.getLectureList(anyLong(), any(Pageable.class)))
        .thenReturn(lecturePage);
    Mockito.when(studentMainService.getQuestionList(any(Pageable.class)))
        .thenReturn(questionPage);
    Mockito.when(studentMainService.getNewExamList(anyLong(), any(Pageable.class)))
        .thenReturn(newExamPage);
    Mockito.when(studentMainService.getSubmitExamList(anyLong(), any(Pageable.class)))
        .thenReturn(submitExamPage);

  }

  @Test
  @DisplayName("수강 중인 강의 목록 조회 API 테스트")
  void 수강_중인_강의_목록_조회_API_테스트() throws Exception {
    // when
    mockMvc.perform(get("/api/student/main/lecture")
            .param("memberSeq", "2")
            .param("page", "1")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].title").value("기초문법강의"))
        .andExpect(jsonPath("$.content[0].status").value("CREATED"))
        .andExpect(jsonPath("$.content[0].createdAt").exists())
        .andExpect(jsonPath("$.content[0].subjectLevelCode[0][0]").value("어법"))
        .andExpect(jsonPath("$.content[0].subjectLevelCode[0][1]").value("중"))
        .andExpect(jsonPath("$.pagination.totalPages").value(1))
        .andExpect(jsonPath("$.pagination.totalElements").value(1))
        .andExpect(jsonPath("$.pagination.currentPage").value(0));
  }

  @Test
  @DisplayName("질문 목록 조회 API 테스트")
  void 질문_목록_조회_API_테스트() throws Exception {
    // when
    mockMvc.perform(get("/api/student/main/question")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].memberNickname").value("user2"))
        .andExpect(jsonPath("$.content[0].title").value("REST API 모범 사례"))
        .andExpect(jsonPath("$.content[0].createdAt").exists())
        .andExpect(jsonPath("$.pagination.totalPages").value(1))
        .andExpect(jsonPath("$.pagination.totalElements").value(1))
        .andExpect(jsonPath("$.pagination.currentPage").value(0));
  }

  @Test
  @DisplayName("풀어야 할 시험 목록 조회 API 테스트")
  void 풀어야_할_시험_목록_조회_API_테스트() throws Exception {
    // when
    mockMvc.perform(get("/api/student/main/new-exams")
            .param("memberSeq", "2")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].teacherNickname").value("user3"))
        .andExpect(jsonPath("$.content[0].title").value("풀어야 할 시험"))
        .andExpect(jsonPath("$.content[0].status").value("PREPARED"))
        .andExpect(jsonPath("$.pagination.totalPages").value(1))
        .andExpect(jsonPath("$.pagination.totalElements").value(1))
        .andExpect(jsonPath("$.pagination.currentPage").value(0));
  }

  @Test
  @DisplayName("제출된 시험 목록 조회 API 테스트")
  void 제출된_시험_목록_조회_API_테스트() throws Exception {
    // when
    mockMvc.perform(get("/api/student/main/submit-exams")
            .param("memberSeq", "1")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].teacherNickname").value("user3"))
        .andExpect(jsonPath("$.content[0].title").value("제출된 시험"))
        .andExpect(jsonPath("$.content[0].status").value("SUBMITTED"))
        .andExpect(jsonPath("$.content[1].teacherNickname").value("user3"))
        .andExpect(jsonPath("$.content[1].title").value("피드백 받은 시험"))
        .andExpect(jsonPath("$.content[1].status").value("FEEDBACK_COMPLETED"))
        .andExpect(jsonPath("$.pagination.totalPages").value(1))
        .andExpect(jsonPath("$.pagination.totalElements").value(2))
        .andExpect(jsonPath("$.pagination.currentPage").value(0));
  }
}
