package com.beelinkers.englebee.teacher.service.impl;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureSubjectLevel;
import com.beelinkers.englebee.general.domain.entity.MemberSubjectLevel;
import com.beelinkers.englebee.general.domain.entity.SubjectLevel;
import com.beelinkers.englebee.general.domain.repository.LectureSubjectLevelRepository;
import com.beelinkers.englebee.general.domain.repository.MemberSubjectLevelRepository;
import com.beelinkers.englebee.general.service.GeneralExamValidationService;
import com.beelinkers.englebee.teacher.domain.entity.TeacherQuestion;
import com.beelinkers.englebee.teacher.domain.repository.TeacherQuestionRepository;
import com.beelinkers.englebee.teacher.dto.response.TeacherExamFeedbackPageDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherExamRegisterPageDTO;
import com.beelinkers.englebee.teacher.dto.response.mapper.TeacherExamFeedbackPageMapper;
import com.beelinkers.englebee.teacher.dto.response.mapper.TeacherExamRegisterPageMapper;
import com.beelinkers.englebee.teacher.service.TeacherExamPageService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeacherExamPageServiceImpl implements TeacherExamPageService {

  private final GeneralExamValidationService teacherExamValidationService;
  private final MemberSubjectLevelRepository memberSubjectLevelRepository;
  private final LectureSubjectLevelRepository lectureSubjectLevelRepository;
  private final TeacherQuestionRepository teacherQuestionRepository;
  private final TeacherExamRegisterPageMapper teacherExamRegisterPageMapper;
  private final TeacherExamFeedbackPageMapper teacherExamFeedbackPageMapper;

  @Override
  @Transactional(readOnly = true)
  public TeacherExamRegisterPageDTO getTeacherExamRegisterPageInfo(Long teacherSeq, Long examSeq) {
    Member teacher = validateAndGetTeacher(teacherSeq);
    Exam exam = validateExamAndCheckIsPreparedForRegistration(teacher, examSeq);
    Lecture lecture = exam.getLecture();
    Member student = lecture.getStudent();
    Map<String, String> lectureSubjectLevels = extractLectureSubjectLevels(lecture);
    Map<String, String> studentSubjectLevels = extractStudentSubjectLevels(student);
    String studentGrade = student.getGrade().getKoreanGrade();
    return mapToTeacherExamRegisterPageDTO(studentGrade, lectureSubjectLevels,
        studentSubjectLevels);
  }

  @Override
  @Transactional(readOnly = true)
  public TeacherExamFeedbackPageDTO getTeacherExamFeedbackPageInfo(Long teacherSeq, Long examSeq) {
    Member teacher = validateAndGetTeacher(teacherSeq);
    Exam exam = validateExamAndCheckIsPreparedForFeedback(teacher, examSeq);
    return mapToTeacherExamFeedbackPageDTO(exam);
  }

  private Member validateAndGetTeacher(Long teacherSeq) {
    return teacherExamValidationService.validateAndGetTeacher(teacherSeq);
  }

  private Exam validateExamAndCheckIsPreparedForRegistration(Member teacher, Long examSeq) {
    Exam exam = teacherExamValidationService.validateAndGetExam(examSeq);
    teacherExamValidationService.validateTeacherAccessToExam(teacher, exam);
    teacherExamValidationService.validateExamIsReadyToBeRegistered(exam);
    return exam;
  }

  private Map<String, String> extractLectureSubjectLevels(Lecture lecture) {
    List<LectureSubjectLevel> lectureSubjectLevels =
        lectureSubjectLevelRepository.findByLectureWithSubjectLevel(lecture);
    Map<String, String> lectureSubjectLevelsMap = new HashMap<>();
    lectureSubjectLevels.forEach(
        lectureSubjectLevel -> {
          SubjectLevel subjectLevel = lectureSubjectLevel.getSubjectLevel();
          lectureSubjectLevelsMap.put(
              subjectLevel.getSubjectCode().getKoreanCode(),
              subjectLevel.getLevelCode().getKoreanCode()
          );
        }
    );
    return lectureSubjectLevelsMap;
  }

  private Map<String, String> extractStudentSubjectLevels(Member student) {
    List<MemberSubjectLevel> studentSubjectLevels = memberSubjectLevelRepository.findByStudentWithSubjectLevel(
        student);
    Map<String, String> studentSubjectLevelsMap = new HashMap<>();
    studentSubjectLevels.forEach(
        memberSubjectLevel -> {
          SubjectLevel subjectLevel = memberSubjectLevel.getSubjectLevel();
          studentSubjectLevelsMap.put(
              subjectLevel.getSubjectCode().getKoreanCode(),
              subjectLevel.getLevelCode().getKoreanCode()
          );
        }
    );
    return studentSubjectLevelsMap;
  }

  private TeacherExamRegisterPageDTO mapToTeacherExamRegisterPageDTO(String studentGrade,
      Map<String, String> lectureSubjectLevels, Map<String, String> studentSubjectLevels) {
    return teacherExamRegisterPageMapper.toExamRegisterPageDTO(studentGrade, lectureSubjectLevels,
        studentSubjectLevels);
  }

  private Exam validateExamAndCheckIsPreparedForFeedback(Member teacher, Long examSeq) {
    Exam exam = teacherExamValidationService.validateAndGetExam(examSeq);
    teacherExamValidationService.validateTeacherAccessToExam(teacher, exam);
    teacherExamValidationService.validateExamIsReadyToBeFeedBacked(exam);
    return exam;
  }

  private TeacherExamFeedbackPageDTO mapToTeacherExamFeedbackPageDTO(Exam exam) {
    List<TeacherQuestion> teacherQuestions = teacherQuestionRepository.findByExam(exam);
    return teacherExamFeedbackPageMapper.toExamFeedbackPageDTO(exam.getTitle(), teacherQuestions);
  }

}
