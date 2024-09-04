package com.beelinkers.englebee.general.service.impl;

import com.beelinkers.englebee.auth.domain.entity.Member;
import com.beelinkers.englebee.auth.domain.repository.MemberRepository;
import com.beelinkers.englebee.general.domain.entity.Question;
import com.beelinkers.englebee.general.domain.entity.Reply;
import com.beelinkers.englebee.general.domain.repository.QuestionRepository;
import com.beelinkers.englebee.general.domain.repository.ReplyRepository;
import com.beelinkers.englebee.general.dto.request.QnaPageRequestDTO;
import com.beelinkers.englebee.general.dto.request.ReplyRequestDTO;
import com.beelinkers.englebee.general.dto.request.mapper.QnaPageRequestMapper;
import com.beelinkers.englebee.general.dto.request.mapper.ReplyRequestMapper;
import com.beelinkers.englebee.general.dto.response.QnaPageResponseDTO;
import com.beelinkers.englebee.general.dto.response.ReplyResponseDTO;
import com.beelinkers.englebee.general.dto.response.mapper.QnaPageResponseMapper;
import com.beelinkers.englebee.general.dto.response.mapper.ReplyResponseMapper;
import com.beelinkers.englebee.general.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QnaServiceImpl implements QnaService {

  private final QuestionRepository questionRepository;
  private final QnaPageResponseMapper qnaPageMapper;
  private final MemberRepository memberRepository;
  private final ReplyRepository replyRepository;
  private final QnaPageRequestMapper qnaPageRequestMapper;
  private final ReplyRequestMapper replyRequestMapper;
  private final ReplyResponseMapper replyResponseMapper;

  @Override
  @Transactional
  public void registerQnaInfo(QnaPageRequestDTO qnaRequestDTO, Long memberSeq) {
    Member member = memberRepository.findById(memberSeq)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    Question questionRegister = qnaPageRequestMapper.registerQnaDTO(qnaRequestDTO, member);
    questionRepository.save(questionRegister);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QnaPageResponseDTO> getQnaListInfo(Pageable pageable) {
    return questionRepository.findAllByOrderByCreatedAtDesc(pageable)
        .map(qnaPageMapper::qnaPageResponseDTO);
  }

  @Override
  @Transactional
  public ReplyResponseDTO registerReplyInfo(ReplyRequestDTO replyRequestDTO) {
    Question question = questionRepository.findById(replyRequestDTO.getQuestionSeq())
        .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
    Member member = memberRepository.findById(replyRequestDTO.getMemberSeq())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    Reply reply = replyRequestMapper.registerReply(replyRequestDTO, question, member);
    Reply saveReply = replyRepository.save(reply);

    return replyResponseMapper.replyResponseDTO(saveReply);
  }

}
