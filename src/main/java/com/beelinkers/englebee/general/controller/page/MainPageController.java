package com.beelinkers.englebee.general.controller.page;

import com.beelinkers.englebee.auth.annotation.LoginMember;
import com.beelinkers.englebee.auth.domain.entity.Role;
import com.beelinkers.englebee.auth.oauth2.session.SessionMember;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.service.MainPageService;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageExamHistoryDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPagePendingExamDTO;
import com.beelinkers.englebee.teacher.service.TeacherMainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainPageController {

  private final MainPageService mainPageService;
  private final TeacherMainService teacherMainService;

  @GetMapping("/main")
  public String getMainPage(@LoginMember SessionMember sessionMember, Model model) {
    if (sessionMember == null) {
      return "index";
    }

    Long memberSeq = sessionMember.getSeq();
    model.addAttribute("nickname", mainPageService.getNickname(memberSeq));
    model.addAttribute("memberSeq", memberSeq);

    Role sessionUserRole = sessionMember.getRole();
    if (sessionUserRole.isStudent()) {

      return "student/student-main";

    } else if (sessionUserRole.isTeacher()) {
      List<TeacherMainPageLectureDTO> ongoingLectures = teacherMainService.getOngoingLectureInfo(
          memberSeq, LectureStatus.CREATED);

      if (ongoingLectures != null && !ongoingLectures.isEmpty()) {
//        Long lectureSeq = ongoingLectures.get(0).getId();
        TeacherMainPageLectureDTO firstLecture = ongoingLectures.get(0);
        Long lectureSeq = firstLecture.getId();
        log.info("lectureSeq 값 설정: {}", lectureSeq);

        if (firstLecture.getSubjectLevelCode() != null) {
          model.addAttribute("subjectLevelCode", firstLecture.getSubjectLevelCode());
          log.info("subjectLevelCode 값 설정: {}", firstLecture.getSubjectLevelCode());
        } else {
          log.warn("subjectLevelCode 값이 null 입니다.");
          model.addAttribute("subjectLevelCode", null);
        }

        model.addAttribute("lectureSeq", lectureSeq);

      } else {
        log.warn("No ongoing lectures found for teacher with ID: {}", memberSeq);
        model.addAttribute("lectureSeq", null);  // 강의가 없을 경우 null 설정
      }
      log.info("ongoingLectures 값 : {}", ongoingLectures);
      model.addAttribute("ongoingLectures", ongoingLectures);

      // Pending Exams 처리
      List<TeacherMainPagePendingExamDTO> pendingExams = teacherMainService.getPendingExamInfo(
          memberSeq, ExamStatus.CREATED);
      if (pendingExams == null) {
        pendingExams = List.of();
      }
      model.addAttribute("pendingExams", pendingExams);

      // Completed Exams 처리
      List<TeacherMainPageExamHistoryDTO> completedExams = teacherMainService.getExamHistoryInfo(
          memberSeq,
          List.of(ExamStatus.SUBMITTED, ExamStatus.FEEDBACK_COMPLETED));
      if (completedExams == null) {
        completedExams = List.of();
      }
      model.addAttribute("completedExams", completedExams);

      return "teacher/teacher-main";
    }
    return "admin/admin-dashboard";
  }

}
