package com.beelinkers.englebee.student.service.impl;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.domain.repository.ExamRepository;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.general.domain.repository.QuestionRepository;
import com.beelinkers.englebee.student.dto.response.*;
import com.beelinkers.englebee.student.dto.response.mapper.StudentMainPageMapper;
import com.beelinkers.englebee.student.service.StudentMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentMainServiceImpl implements StudentMainService {

    private final LectureRepository lectureRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final StudentMainPageMapper studentMainPageMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMainPageLectureDTO> getLectureList(Long memberSeq, Pageable pageable) {
        return lectureRepository.findByStudentSeqAndStatus(memberSeq, LectureStatus.CREATED, pageable)
                .map(studentMainPageMapper::mainPageLectureDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMainPageQuestionDTO> getQuestionList(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(studentMainPageMapper::mainPageQuestionDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMainPageNewExamDTO> getNewExamList(Long memberSeq, Pageable pageable) {
        return examRepository.findByLectureStudentSeqAndStatus(memberSeq,ExamStatus.PREPARED, pageable)
                .map(studentMainPageMapper::mainPageNewExamDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentMainPageSubmitExamDTO> getSubmitExamList(Long memberSeq, Pageable pageable) {
       return examRepository.findByLectureStudentSeqAndStatusIn(
                    memberSeq, List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED), pageable
               ).map(studentMainPageMapper::mainPageSubmitExamDTO);
    }

}
