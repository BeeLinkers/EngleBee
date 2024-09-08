package com.beelinkers.englebee.general.controller.page;

import com.beelinkers.englebee.auth.annotation.LoginMember;
import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.auth.domain.entity.Role;
import com.beelinkers.englebee.auth.oauth2.session.SessionMember;
import com.beelinkers.englebee.general.service.AccountService;
import com.beelinkers.englebee.student.dto.request.StudentAccountPageRequestDTO;
import com.beelinkers.englebee.student.dto.response.StudentAccountPageResponseDTO;
import com.beelinkers.englebee.student.dto.response.mapper.StudentAccountPageMapper;
import com.beelinkers.englebee.student.service.StudentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountPageController {

  private final AccountService accountService;
  private final StudentAccountService studentAccountService;
  private final StudentAccountPageMapper studentAccountPageMapper;

  @GetMapping
  public String getAccountPage(@LoginMember SessionMember sessionMember, Model model) {

    Long memberSeq = sessionMember.getSeq();
    Role memberRole = sessionMember.getRole();
    Member member = studentAccountService.getMemberInfo(memberSeq);
    //String nickname = accountService.getNickname(memberSeq);

    //   StudentAccountPageRequestDTO studentAccountPageRequestDTO = new StudentAccountPageRequestDTO(member.getNickname(),koreaGrade);
    model.addAttribute("nickname", member.getNickname());
    model.addAttribute("memberSeq", member.getSeq());

    //   model.addAttribute("studentAccountPageRequestDTO", studentAccountPageRequestDTO);
    if (memberRole.isStudent()) {
      String koreaGrade = member.getGrade().getKoreanGrade();
      model.addAttribute("grade", koreaGrade);
      model.addAttribute("isStudent", true);
      return "student/student-account";
    } else if (memberRole.isTeacher()) {
      model.addAttribute("isTeacher", true);
      return "teacher/teacher-account";
    }
    return "index";
  }

  // 수정된 학생 정보 계정 페이지
  @PostMapping("/update")
  public String updateAccount(@LoginMember SessionMember sessionMember,
      @ModelAttribute StudentAccountPageRequestDTO updateRequestDTO,
      Model model) {

    Long memberSeq = sessionMember.getSeq();
    Role memberRole = sessionMember.getRole();
    log.info(updateRequestDTO.getGrade());
    log.info(updateRequestDTO.getNickname());
    if (memberRole.isStudent()) {
      Member updatedMember = studentAccountService.updateStudentInfo(memberSeq, updateRequestDTO);
      StudentAccountPageResponseDTO responseDTO = studentAccountPageMapper.studentAccountResponseDTO(
          updatedMember);
      model.addAttribute("studentResponse", responseDTO);
    }

    // 수정된 정보를 모델에 다시 추가하여 페이지에 전달
    String updatedNickname = accountService.getNickname(memberSeq);
    model.addAttribute("nickname", updatedNickname);
    model.addAttribute("memberSeq", memberSeq);

    return "redirect:/account";
//    if (memberRole.isStudent()) {
//      return "student/student-account";
//    } else if (memberRole.isTeacher()) {
//      return "teacher/teacher-account";
//    }
//    return "redirect:/account";
  }
}
