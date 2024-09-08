package com.beelinkers.englebee.student.controller.api;

import com.beelinkers.englebee.auth.annotation.LoginMember;
import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.auth.oauth2.session.SessionMember;
import com.beelinkers.englebee.student.dto.request.StudentAccountPageRequestDTO;
import com.beelinkers.englebee.student.service.StudentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/account")
public class StudentAccountApiController {

  private final StudentAccountService studentAccountService;

  // 정보 조회
  @GetMapping("/info")
  public ResponseEntity<Member> getMemberInfo(@RequestParam("memberSeq") Long memberSeq) {
    try {
      Member member = studentAccountService.getMemberInfo(memberSeq);
      return ResponseEntity.ok(member);
    } catch (Exception e) {
      log.error("회원정보를 불러올 수 없습니다.");
      return ResponseEntity.badRequest().build();
    }
  }

  // nickname 중복 검사(general)
  @PostMapping("/check-nickname")
  public ResponseEntity<Boolean> checkNicknameDuplicated(@RequestParam String nickname) {
    studentAccountService.checkNicknameDuplicated(nickname);
    return ResponseEntity.ok().build();
  }

  // 학생 계정 정보 수정
  @PutMapping("/update")
  public ResponseEntity<Member> updateStudentAccount(
      @LoginMember SessionMember sessionMember,
      @RequestBody StudentAccountPageRequestDTO updateRequestDTO) {
    Member updateMember = studentAccountService.updateStudentInfo(
        sessionMember.getSeq(), updateRequestDTO
    );
    return ResponseEntity.ok(updateMember);
  }

  // 회원탈퇴(계정 비활성화)
  @PutMapping("/deactivate")
  public ResponseEntity<Void> deleteStudentAccount(@RequestParam("memberSeq") Long memberSeq) {
    studentAccountService.deleteStudentAccountInfo(memberSeq);
    return ResponseEntity.noContent().build();
  }

}
