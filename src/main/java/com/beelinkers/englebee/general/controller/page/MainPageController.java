package com.beelinkers.englebee.general.controller.page;

import com.beelinkers.englebee.auth.annotation.LoginMember;
import com.beelinkers.englebee.auth.domain.entity.Role;
import com.beelinkers.englebee.auth.oauth2.session.SessionMember;
import com.beelinkers.englebee.general.domain.entity.LectureStatus;
import com.beelinkers.englebee.general.domain.repository.LectureRepository;
import com.beelinkers.englebee.general.service.MainPageService;
import com.beelinkers.englebee.student.dto.response.StudentMainPageLectureDTO;
import com.beelinkers.englebee.student.service.StudentMainService;
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
  private final StudentMainService studentMainService;
  private final LectureRepository lectureRepository;

  @GetMapping("/main")
  public String getMainPage(@LoginMember SessionMember sessionMember, Model model) {
    if (sessionMember == null) {
      return "index";
    }

    Long memberSeq = sessionMember.getSeq();
    List<StudentMainPageLectureDTO> lectures = studentMainService.getOngoingLectureInfo(
        memberSeq, null, LectureStatus.CREATED);

    // 강의가 존재하는 경우 첫 번째 강의 정보를 모델에 전달
    if (!lectures.isEmpty()) {
      Long lectureSeq = lectures.get(0).getLectureSeq(); // 첫 번째 강의의 lectureSeq 가져오기
      model.addAttribute("lectureSeq", lectureSeq); // lectureSeq를 모델에 추가
//      log.info("lectureSeq : {}", lectureSeq);
    } else {
      model.addAttribute("lectureSeq", null); // 강의가 없을 경우 null 전달
    }

    model.addAttribute("nickname", mainPageService.getNickname(memberSeq));
    model.addAttribute("memberSeq", memberSeq);
    model.addAttribute("lectures", lectures);

    Role sessionUserRole = sessionMember.getRole();
    if (sessionUserRole.isStudent()) {
      return "student/student-main";

    } else if (sessionUserRole.isTeacher()) {
      return "teacher/teacher-main";
    }
    return "admin/admin-dashboard";
  }

}
