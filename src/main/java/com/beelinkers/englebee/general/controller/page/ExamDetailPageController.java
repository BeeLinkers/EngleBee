package com.beelinkers.englebee.general.controller.page;

import com.beelinkers.englebee.auth.annotation.LoginMember;
import com.beelinkers.englebee.auth.domain.entity.Role;
import com.beelinkers.englebee.auth.oauth2.session.SessionMember;
import com.beelinkers.englebee.general.domain.entity.ExamStatus;
import com.beelinkers.englebee.general.dto.response.ExamDetailPageDTO;
import com.beelinkers.englebee.general.service.ExamDetailPageService;
import com.beelinkers.englebee.general.service.GeneralExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ExamDetailPageController {

  private final GeneralExamService generalExamService;
  private final ExamDetailPageService examDetailPageService;

  // 클라이언트 측에서 시험 관련한 내용에서는 이 메서드의 단일 지점으로 요청하도록 처리
  @GetMapping("/exam/detail/{examSeq}")
  public String getExamDetailPage(@LoginMember SessionMember sessionMember,
      @PathVariable("examSeq") Long examSeq, Model model) {
    ExamStatus examStatus = generalExamService.getExamStatus(examSeq);
    Role role = sessionMember.getRole();

    if (role.isStudent()) {
      return handleExamDetailPageForRole(examStatus, examSeq, model, true);
    } else if (role.isTeacher()) {
      return handleExamDetailPageForRole(examStatus, examSeq, model, false);
    } else {
      log.error("지원하지 않는 Role입니다. role = {}", role);
      return "error/forbidden";
    }
  }

  private String handleExamDetailPageForRole(ExamStatus examStatus, Long examSeq, Model model,
      boolean isStudent) {
    return switch (examStatus) {
      case CREATED -> isStudent ? "error/forbidden" : "redirect:/exam/register/" + examSeq;
      case PREPARED -> isStudent ? "redirect:/exam/submit/" + examSeq
          : renderExamDetailPage(examSeq, model);
      case SUBMITTED -> isStudent ? renderExamDetailPage(examSeq, model)
          : "redirect:/exam/feedback/" + examSeq;
      case FEEDBACK_COMPLETED -> renderExamDetailPageWithFeedback(examSeq, model);
    };
  }

  private String renderExamDetailPage(Long examSeq, Model model) {
    ExamDetailPageDTO examDetailPageDTO = examDetailPageService.getExamDetailPageDTO(examSeq);
    model.addAttribute("examSeq", examSeq);
    model.addAttribute("examTitle", examDetailPageDTO.getExamTitle());
    model.addAttribute("teacherQuestions", examDetailPageDTO.getTeacherQuestionDetailDTOs());
    return "general/exam-detail";
  }

  private String renderExamDetailPageWithFeedback(Long examSeq, Model model) {
    ExamDetailPageDTO examDetailPageDTO = examDetailPageService.getExamDetailPageDTO(examSeq);
    model.addAttribute("examSeq", examSeq);
    model.addAttribute("examTitle", examDetailPageDTO.getExamTitle());
    model.addAttribute("teacherQuestions", examDetailPageDTO.getTeacherQuestionDetailDTOs());
    model.addAttribute("feedback", examDetailPageDTO.getFeedback());
    return "general/exam-detail";
  }
}
