package com.beelinkers.englebee.teacher.controller.api;

import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import com.beelinkers.englebee.teacher.service.TeacherMainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teacher/main")
public class TeacherMainApiController {

  private final TeacherMainService teacherMainService;

  @GetMapping("/lecture")
  public ResponseEntity<List<TeacherMainPageLectureDTO>> getOngoingLecture(
      @RequestParam("memberSeq") Long memberSeq,
      @RequestParam("lectureSeq") Long lectureSeq,
      @RequestParam("lectureStatus") String lectureStatus) {
    LectureStatus status = LectureStatus.valueOf(lectureStatus.toUpperCase());
    List<TeacherMainPageLectureDTO> lectureList = teacherMainService.getOngoingLectureInfo(
        memberSeq, lectureSeq, status
    );
    return ResponseEntity.ok(lectureList);
  }

  @GetMapping("/pending-exam")
  public ResponseEntity<List<TeacherMainPagePendingExamDTO>> getPendingExam(
      @RequestParam("memberSeq") Long memberSeq
  ) {
    ExamStatus status = ExamStatus.CREATED;
    List<TeacherMainPagePendingExamDTO> pendingExamList = teacherMainService.getPendingExamInfo(
        memberSeq, status
    );
    return ResponseEntity.ok(pendingExamList);
  }

  @GetMapping("/exam-history")
  public ResponseEntity<List<TeacherMainPageExamHistoryDTO>> getExamHistory(
      @RequestParam("memberSeq") Long memberSeq,
      @RequestParam(value = "status", required = false) List<ExamStatus> status
  ) {
    if (status == null || status.isEmpty()) {
      status = List.of(ExamStatus.PREPARED, ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED);
    }
    List<TeacherMainPageExamHistoryDTO> examHistoryList = teacherMainService.getExamHistoryInfo(
        memberSeq, status
    );
    return ResponseEntity.ok(examHistoryList);
  }
}
