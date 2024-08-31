package com.beelinkers.englebee.teacher.service.impl;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.teacher.domain.repository.TeacherQuestionRepository;
import com.beelinkers.englebee.teacher.dto.request.TeacherExamRegisterRequestDTO;
import com.beelinkers.englebee.teacher.dto.request.TeacherQuestionCreateRequestDTO;
import com.beelinkers.englebee.teacher.dto.request.mapper.TeacherQuestionRequestMapper;
import com.beelinkers.englebee.teacher.service.TeacherExamService;
import com.beelinkers.englebee.teacher.service.TeacherExamValidationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeacherExamServiceImpl implements TeacherExamService {

  private final TeacherExamValidationService validationService;
  private final TeacherQuestionRepository teacherQuestionRepository;
  private final TeacherQuestionRequestMapper teacherQuestionRequestMapper;

  @Override
  public void registerExam(Long teacherSeq, Long examSeq,
      TeacherExamRegisterRequestDTO teacherExamRegisterRequestDTO) {
    Member teacher = validationService.validateAndGetTeacher(teacherSeq);
    Exam exam = validationService.validateAndGetExam(examSeq);
    validationService.validateTeacherAccessToExam(teacher, exam);
    validationService.validateExamStatus(exam, ExamStatus.CREATED);

    exam.prepare();
    List<TeacherQuestionCreateRequestDTO> teacherQuestionCreateRequestDTOs = teacherExamRegisterRequestDTO.getTeacherQuestionCreateRequestDTOs();
    teacherQuestionCreateRequestDTOs.stream()
        .map(teacherQuestionRequestMapper::toTeacherQuestion)
        .forEach(teacherQuestion -> {
          teacherQuestion.registerToExam(exam);
          teacherQuestionRepository.save(teacherQuestion);
        });
  }
}
