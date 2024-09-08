package com.beelinkers.englebee.general.service.impl;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.auth.domain.repository.MemberRepository;
import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureSubjectLevel;
import com.beelinkers.englebee.general.domain.entity.LevelCode;
import com.beelinkers.englebee.general.domain.entity.SubjectCode;
import com.beelinkers.englebee.general.domain.entity.SubjectLevel;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.general.domain.repository.LectureSubjectLevelRepository;
import com.beelinkers.englebee.general.domain.repository.SubjectLevelRepository;
import com.beelinkers.englebee.general.exception.InvalidSubjectLevelCodeException;
import com.beelinkers.englebee.general.exception.MemberNotFoundException;
import com.beelinkers.englebee.general.exception.NoSubjectLevelException;
import com.beelinkers.englebee.general.service.LectureService;
import com.beelinkers.englebee.teacher.dto.request.TeacherRegisterLectureRequestDTO;
import com.beelinkers.englebee.teacher.dto.request.TeacherRegisterLectureSubjectLevelDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final MemberRepository memberRepository;
  private final SubjectLevelRepository subjectLevelRepository;
  private final LectureRepository lectureRepository;
  private final LectureSubjectLevelRepository lectureSubjectLevelRepository;

  @Override
  public void createLecture(Long teacherSeq,
      TeacherRegisterLectureRequestDTO teacherRegisterLectureRequestDTO) {
    Member teacher = memberRepository.findById(teacherSeq)
        .orElseThrow(() -> new MemberNotFoundException("해당 선생님이 존재하지 않습니다."));
    Member student = memberRepository.findByNickname(
            teacherRegisterLectureRequestDTO.getStudentNickname())
        .orElseThrow(() -> new MemberNotFoundException("해당 학생이 존재하지 않습니다."));

    Lecture lecture = Lecture.builder()
        .teacher(teacher)
        .student(student)
        .title(teacherRegisterLectureRequestDTO.getLectureTitle())
        .build();
    lectureRepository.save(lecture);

    List<LevelCode> levels = new ArrayList<>();
    List<SubjectCode> subjects = new ArrayList<>();

    boolean levelChecked = false;

    for (TeacherRegisterLectureSubjectLevelDTO subjectLevel : teacherRegisterLectureRequestDTO.getSubjectLevels()) {
      String level = subjectLevel.getLevel();
      String subject = subjectLevel.getSubject();

      if (level.equals(" ")) {
        continue;
      }
      levels.add(LevelCode.fromKoreanCode(level));
      subjects.add(SubjectCode.fromKoreanCode(subject));
      levelChecked = true;
    }
    if (!levelChecked) {
      throw new NoSubjectLevelException("과목 레벨을 선택해주세요");
    }

    List<SubjectLevel> subjectLevels = new ArrayList<>();

    for (int i = 0; i < levels.size(); i++) {
      subjectLevels.add(
          subjectLevelRepository.findBySubjectCodeAndLevelCode(subjects.get(i), levels.get(i))
              .orElseThrow(() -> new InvalidSubjectLevelCodeException("해당하는 과목 레벨 수준이 존재하지 않습니다")));
    }

    for (SubjectLevel sl : subjectLevels) {
      LectureSubjectLevel lectureSubjectLevel = LectureSubjectLevel.builder()
          .lecture(lecture)
          .subjectLevel(sl)
          .build();
      lectureSubjectLevelRepository.save(lectureSubjectLevel);
    }
  }


}
