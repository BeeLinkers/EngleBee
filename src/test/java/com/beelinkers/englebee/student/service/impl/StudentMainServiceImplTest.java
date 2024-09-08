package com.beelinkers.englebee.student.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.domain.repository.ExamRepository;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.general.dto.response.SubjectLevelCodeDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageLectureDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageNewExamDTO;
import com.beelinkers.englebee.student.dto.response.StudentMainPageSubmitExamDTO;
import com.beelinkers.englebee.student.dto.response.mapper.StudentMainPageMapper;
import com.beelinkers.englebee.student.service.StudentMainService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class StudentMainServiceImplTest {

  @Autowired
  private StudentMainService studentMainService;

  @MockBean
  private StudentMainPageMapper studentMainPageMapper;

  @MockBean
  private LectureRepository lectureRepository;

  @MockBean
  private ExamRepository examRepository;

  @Test
  @DisplayName("수강하는 수업을 조회할 수 있다")
  void 수강하는_수업을_조회할_수_있다() {
    // given
    Member teacher = Member.builder().nickname("user3").build();
    Member student = Member.builder().nickname("user2").build();

    Lecture lecture = Lecture.builder()
        .teacher(teacher)
        .student(student)
        .title("기초 문법 강의")
        .build();

    List<String> subjectCode = List.of("어법", "문법");
    List<String> levelCode = List.of("중", "고");

    List<SubjectLevelCodeDTO> lecturePage = List.of(
        new SubjectLevelCodeDTO());

    List<Lecture> lectures = List.of(lecture);

    StudentMainPageLectureDTO lectureDTO = new StudentMainPageLectureDTO(
        1L, "user3", "기초 문법 강의", "CREATED", LocalDateTime.now(), lecturePage.get(0)
    );

    when(lectureRepository.findByStudentSeqAndSeqAndStatus(2L, 1L,
        LectureStatus.CREATED)).thenReturn(lectures);
    when(studentMainPageMapper.mainPageLectureDto(lecture)).thenReturn(lectureDTO);

    // when
    List<StudentMainPageLectureDTO> resultLecture = studentMainService.getOngoingLectureInfo(2L, 1L,
        LectureStatus.CREATED);

    // then
    assertThat(resultLecture.size()).isEqualTo(1);
    assertThat(resultLecture.get(0).getTitle()).isEqualTo("기초 문법 강의");
    assertThat(resultLecture.get(0).getTeacherNickname()).isEqualTo("user3");
  }


  @Test
  @DisplayName("풀어야 할 시험 목록을 조회할 수 있다")
  void 풀어야_할_시험_목록을_조회할_수_있다() {
    // given
    Exam exam = Exam.builder()
        .lecture(Lecture.builder().teacher(Member.builder().nickname("user3").build()).build())
        .title("풀어야 할 시험1")
        .build();

    List<Exam> examPage = new ArrayList<>(List.of(exam));
    StudentMainPageNewExamDTO examDTO = new StudentMainPageNewExamDTO(
        1L, 1L, "PREPARE", "풀어야 할 시험1", "user3", LocalDateTime.now()
    );
    when(examRepository.findTop5ByLectureStudentSeqAndStatusOrderByCreatedAtDesc(1L,
        ExamStatus.PREPARED)).thenReturn(examPage);
    when(studentMainPageMapper.mainPageNewExamDTO(exam)).thenReturn(examDTO);

    // when
    List<StudentMainPageNewExamDTO> result = studentMainService.getPreparedExamInfo(1L,
        ExamStatus.PREPARED);

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getTitle()).isEqualTo("풀어야 할 시험1");
    assertThat(result.get(0).getTeacherNickname()).isEqualTo("user3");
  }

  @Test
  @DisplayName("제출한 시험 목록을 조회할 수 있다")
  void 제출한_시험_목록을_조회할_수_있다() {
    // given
    Exam exam = Exam.builder()
        .lecture(Lecture.builder().teacher(Member.builder().nickname("user3").build()).build())
        .title("풀었던 시험1")
        .build();

    List<Exam> examPage = new ArrayList<>(List.of(exam));
    StudentMainPageSubmitExamDTO examDTO = new StudentMainPageSubmitExamDTO(
        1L, 1L, "SUBMITTED", "풀었던 시험1", "user3", LocalDateTime.now()
    );
    when(examRepository.findTop5ByLectureStudentSeqAndStatusInOrderByCreatedAtDesc(1L,
        List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED))).thenReturn(
        examPage);
    when(studentMainPageMapper.mainPageSubmitExamDTO(exam)).thenReturn(examDTO);

    // when
    List<StudentMainPageSubmitExamDTO> result = studentMainService.getCompletedExamInfo(1L,
        List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED));

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getTitle()).isEqualTo("풀었던 시험1");
    assertThat(result.get(0).getTeacherNickname()).isEqualTo("user3");
  }
}
