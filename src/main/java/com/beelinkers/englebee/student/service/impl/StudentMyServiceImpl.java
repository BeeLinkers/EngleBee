package com.beelinkers.englebee.student.service.impl;

import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.repository.ExamRepository;
import com.beelinkers.englebee.student.dto.response.StudentMyPageCreatedExamDTO;
import com.beelinkers.englebee.student.dto.response.mapper.StudentMyPageMapper;
import com.beelinkers.englebee.student.service.StudentMyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentMyServiceImpl implements StudentMyService {

  private final ExamRepository examRepository;
  private final StudentMyPageMapper studentMyPageMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<StudentMyPageCreatedExamDTO> getStudentMyCreatedExamInfo(Long memberSeq,
      Pageable pageable) {
    Page<Exam> createdExamList = examRepository.findByLectureStudentSeqAndStatus(memberSeq,
        ExamStatus.PREPARED, pageable);
    return createdExamList.map(studentMyPageMapper::studentMyPageCreatedExam);
  }


}