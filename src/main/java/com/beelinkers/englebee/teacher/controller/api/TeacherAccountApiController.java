package com.beelinkers.englebee.teacher.controller.api;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.teacher.dto.request.TeacherAccountPageRequestDTO;
import com.beelinkers.englebee.teacher.service.TeacherAccountService;
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
@RequestMapping("/api/teacher/account")
public class TeacherAccountApiController {

  private final TeacherAccountService teacherAccountService;

  // 회원 정보 조회
  @GetMapping("/info")
  public ResponseEntity<Member> getMemberInfo(@RequestParam("memberSeq") Long memberSeq) {
    try {
      Member member = teacherAccountService.getMemberInfo(memberSeq);
      return ResponseEntity.ok(member);
    } catch (Exception e) {
      log.error("회원정보를 불러올 수 없습니다.");
      return ResponseEntity.badRequest().build();
    }
  }

  // nickname 중복 검사
  @PostMapping("/check-nickname")
  public ResponseEntity<Boolean> checkNicknameDuplicated(@RequestParam String nickname) {
    boolean isDuplicated = teacherAccountService.checkNicknameDuplicate(nickname);
    return ResponseEntity.ok(isDuplicated);
  }

  // 선생님 계정 정보 수정
  @PutMapping("/update")
  public ResponseEntity<Member> updateTeacherAccount(@RequestParam("memberSeq") Long memberSeq,
      @RequestBody
      TeacherAccountPageRequestDTO updateRequestDTO) {
    Member updateMember = teacherAccountService.updateTeacherInfo(memberSeq, updateRequestDTO);
    return ResponseEntity.ok(updateMember);
  }

  // 회원 탈퇴 (계정 비활성화)
  @PutMapping("/deactivate")
  public ResponseEntity<Void> deleteTeacherAccount(@RequestParam("memberSeq") Long memberSeq) {
    teacherAccountService.deleteTeacherAccountInfo(memberSeq);
    return ResponseEntity.noContent().build();
  }

}
