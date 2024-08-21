package com.beelinkers.englebee.student.service.impl;

import com.beelinkers.englebee.general.domain.entity.Exam;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.Question;
import com.beelinkers.englebee.general.domain.repository.ExamRepository;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.general.domain.repository.QuestionRepository;
import com.beelinkers.englebee.student.dto.response.*;
import com.beelinkers.englebee.student.dto.response.mapper.StudentMainPageMapper;
import com.beelinkers.englebee.student.service.StudentMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.beelinkers.englebee.general.dto.response.PaginationResponseDTO;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentMainServiceImpl implements StudentMainService {

    private final LectureRepository lectureRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final StudentMainPageMapper studentMainPageMapper;

    @Autowired
    public StudentMainServiceImpl(
            LectureRepository lectureRepository,
            QuestionRepository questionRepository,
            ExamRepository examRepository,
            StudentMainPageMapper studentMainPageMapper) {
        this.lectureRepository = lectureRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.studentMainPageMapper = studentMainPageMapper;
    }

    @Override
    public StudentMainPageDTO getStudentMainPage(Long memberSeq, String code, Pageable pageable) {
        List<MainPageLectureDTO> lectureList;
        List<MainPageQuestionDTO> questionList;
        List<MainPageNewExamDTO> newExamList;
        List<MainPageSubmitExamDTO> submitExamList;
        PaginationResponseDTO questionPagination;

        if("STUDENT".equals(code)) {
            List<Lecture> lectures = lectureRepository.findByStudentSeq(memberSeq);
            lectureList = lectures.stream()
                    .map(studentMainPageMapper::mainPageLectureDto)
                    .collect(Collectors.toList());

            Page<Question> questionPageData = questionRepository.findAll(pageable);
            questionList = questionPageData.getContent().stream()
                    .map(studentMainPageMapper::mainPageQuestionDTO)
                    .collect(Collectors.toList());
            // pagination
            questionPagination = new PaginationResponseDTO(
                questionPageData.getNumber(),
                questionPageData.getSize(),
                questionPageData.getTotalPages(),
                questionPageData.getTotalElements(),
                questionPageData.hasPrevious(),
                questionPageData.hasNext()
            );

            List<Exam> newExams = examRepository.findByLectureStudentSeq(memberSeq);
            newExamList = newExams.stream()
                    .filter(exam -> exam.getStatus() == ExamStatus.PREPARED)
                    .map(studentMainPageMapper::mainPageNewExamDTO)
                    .collect(Collectors.toList());

            List<Exam> submitExams = examRepository.findByLectureStudentSeq(memberSeq);
            submitExamList = submitExams.stream()
                    .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED || exam.getStatus() == ExamStatus.FEEDBACK_COMPLETED)
                    .map(studentMainPageMapper::mainPageSubmitExamDTO)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("접속하신 코드가 일치하지 않습니다. 해당코드 : "+code);
        }
        return new StudentMainPageDTO(lectureList, questionList, newExamList, submitExamList, questionPagination );
    }


}
