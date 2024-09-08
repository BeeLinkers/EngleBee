package com.beelinkers.englebee.general.domain.repository;

import com.beelinkers.englebee.general.domain.entity.Lecture;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

  // 학생 수강 목록 조회
  @EntityGraph(attributePaths = {"subjectLevels.subjectLevel", "teacher"})
  @Query("SELECT l FROM Lecture l WHERE l.student.seq = :studentSeq AND (:lectureSeq IS NULL OR l.seq = :lectureSeq) AND l.status = :lectureStatus")
  List<Lecture> findByStudentSeqAndSeqAndStatus(Long studentSeq, Long lectureSeq,
      LectureStatus lectureStatus);

  // 선생님 강의 목록 조회
  @EntityGraph(attributePaths = {"subjectLevels.subjectLevel", "student"})
  @Query("SELECT l FROM Lecture l WHERE l.teacher.seq = :teacherSeq AND (:lectureSeq IS NULL OR l.seq = :lectureSeq) AND l.status = :status")
  List<Lecture> findByTeacherSeqAndSeqAndStatus(Long teacherSeq, Long lectureSeq,
      LectureStatus status);
}

