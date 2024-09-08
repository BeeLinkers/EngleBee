package com.beelinkers.englebee.teacher.service.impl;

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
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import com.beelinkers.englebee.teacher.dto.response.mapper.TeacherMainPageMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class TeacherMainServiceImplTest {

  @InjectMocks
  private TeacherMainServiceImpl teacherMainService;

  @Mock
  private TeacherMainPageMapper teacherMainPageMapper;

  @Mock
  private LectureRepository lectureRepository;

  @Mock
  private ExamRepository examRepository;


  @Test
  @DisplayName("진행하는_수업을 조회할 수 있다")
  void 진행하는_수업을_조회할_수_있다() {
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
        new SubjectLevelCodeDTO()
    );
    List<Lecture> lectureList = List.of(lecture);

    TeacherMainPageLectureDTO lectureDTO = new TeacherMainPageLectureDTO(
        1L, "student1", "기초문법강의", "CREATED", LocalDateTime.now(), lecturePage.get(0)
    );
    when(lectureRepository.findByTeacherSeqAndStatus(
        3L, LectureStatus.CREATED
    )).thenReturn(lectureList);
    when(teacherMainPageMapper.teacherMainPageLectureDto(lecture)).thenReturn(lectureDTO);

    // when
    List<TeacherMainPageLectureDTO> resultLecture = teacherMainService.getOngoingLectureInfo(
        3L, LectureStatus.CREATED
    );
    // then
    assertThat(resultLecture.size()).isEqualTo(1);
    assertThat(resultLecture.get(0).getTitle()).isEqualTo("기초문법강의");
    assertThat(resultLecture.get(0).getStudentNickname()).isEqualTo("student1");
  }

  @Test
  @DisplayName("출제할 시험 목록을 조회할 수 있다. ")
  void 출제할_시험_목록을_조회할_수_있다() {
    // given
    Exam exam = Exam.builder()
        .lecture(Lecture.builder().student(Member.builder().nickname("user2").build()).build())
        .title("출제할 시험1")
        .build();
    List<Exam> examPage = new ArrayList<>(List.of(exam));
    TeacherMainPagePendingExamDTO pendingExamDTO = new TeacherMainPagePendingExamDTO(
        1L, 1L, "출제할 시험1", "CREATED", "user2", LocalDateTime.now()
    );
    when(examRepository.findTop5ByLectureTeacherSeqAndStatusOrderByCreatedAtDesc(3L,
        ExamStatus.CREATED)).thenReturn(examPage);
    when(teacherMainPageMapper.teacherMainPagePendingExamDto(exam)).thenReturn(pendingExamDTO);

    // when
    List<TeacherMainPagePendingExamDTO> result = teacherMainService.getPendingExamInfo(3L,
        ExamStatus.CREATED);
    System.out.println("Result: " + result.toString());

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getTitle()).isEqualTo("출제할 시험1");
    assertThat(result.get(0).getStudentNickname()).isEqualTo("user2");

  }

  @Test
  @DisplayName("출제한 시험을 조회할 수 있다.")
  void 출제한_시험을_조회할_수_있다() {
    // given
    Exam exam = Exam.builder()
        .lecture(Lecture.builder().student(Member.builder().nickname("user2").build()).build())
        .title("출제한 시험1")
        .build();
    List<Exam> examPage = new ArrayList<>(List.of(exam));
//    List<ExamStatus> status = List.of(ExamStatus.PREPARED, ExamStatus.SUBMITTED,
//        ExamStatus.FEEDBACK_COMPLETED);
    TeacherMainPageExamHistoryDTO historyExamPreparedDTO = new TeacherMainPageExamHistoryDTO(
        1L, 1L, "출제한 시험1", ExamStatus.PREPARED.name(), "user2", LocalDateTime.now()
    );
    when(examRepository.findTop5ByLectureTeacherSeqAndStatusInOrderByCreatedAtDesc(3L,
        List.of(ExamStatus.PREPARED, ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED)))
        .thenReturn(examPage);
    when(teacherMainPageMapper.teacherMainPageExamHistoryDTO(exam))
        .thenReturn(historyExamPreparedDTO);

    // when
    List<TeacherMainPageExamHistoryDTO> result = teacherMainService.getExamHistoryInfo(
        3L, List.of(ExamStatus.PREPARED, ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED)
    );

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getTitle()).isEqualTo("출제한 시험1");
    assertThat(result.get(0).getStudentNickname()).isEqualTo("user2");

  }
}
