package com.beelinkers.englebee.general.domain.repository;

import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

  // 학생 수강 목록 조회
  @EntityGraph(attributePaths = {"subjectLevels.subjectLevel", "teacher"})
  List<Lecture> findByStudentSeqAndSeqAndStatus(Long studentSeq, Long lectureSeq,
      LectureStatus lectureStatus);

  // 선생님 강의 목록 조회
  @EntityGraph(attributePaths = {"subjectLevels.subjectLevel", "student"})
  List<Lecture> findByTeacherSeqAndStatus(Long teacherSeq,
      LectureStatus status);
}

