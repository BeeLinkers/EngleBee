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
import com.beelinkers.englebee.general.dto.response.QnaDetailPageResponseDTO;
import com.beelinkers.englebee.general.dto.response.QnaPageResponseDTO;
import com.beelinkers.englebee.general.dto.response.ReplyResponseDTO;
import com.beelinkers.englebee.general.dto.response.mapper.QnaDetailPageResponseMapper;
import com.beelinkers.englebee.general.dto.response.mapper.QnaPageResponseMapper;
import com.beelinkers.englebee.general.dto.response.mapper.ReplyResponseMapper;
import com.beelinkers.englebee.general.exception.ExamNotFoundException;
import com.beelinkers.englebee.general.exception.MemberNotFoundException;
import com.beelinkers.englebee.general.llm.GptQnaProxy;
import com.beelinkers.englebee.general.service.QnaService;
import java.util.List;
import java.util.stream.Collectors;
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

  private final GptQnaProxy qnaProxy;
  private final QuestionRepository questionRepository;
  private final QnaPageResponseMapper qnaPageMapper;
  private final MemberRepository memberRepository;
  private final ReplyRepository replyRepository;
  private final QnaPageRequestMapper qnaPageRequestMapper;
  private final ReplyRequestMapper replyRequestMapper;
  private final ReplyResponseMapper replyResponseMapper;
  private final QnaDetailPageResponseMapper qnaDetailPageResponseMapper;

  @Override
  public void registerQnaInfo(QnaPageRequestDTO qnaRequestDTO, Long memberSeq) {
    Member member = memberRepository.findById(memberSeq)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    Question questionRegister = qnaPageRequestMapper.registerQnaDTO(qnaRequestDTO, member);
    questionRepository.save(questionRegister);

    // EngleBee 유저로 댓글 생성
    Member engleBee = memberRepository.findByNickname("EngleBee")
        .orElseThrow(() -> new MemberNotFoundException("해당하는 멤버가 존재하지 않습니다."));
    String gptReplyContent = qnaProxy.makeAutoReply(questionRegister.getContent());
    Reply firstReply = Reply.builder()
        .question(questionRegister)
        .author(engleBee)
        .content(gptReplyContent)
        .build();
    replyRepository.save(firstReply);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QnaPageResponseDTO> getQnaListInfo(Pageable pageable) {
    return questionRepository.findAllByOrderByCreatedAtDesc(pageable)
        .map(qnaPageMapper::qnaPageResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public QnaDetailPageResponseDTO getQnaDetailInfo(Long questionSeq) {
    Question question = questionRepository.findById(questionSeq)
        .orElseThrow(() -> new ExamNotFoundException("해당 질문을 찾을 수 없습니다."));
    return qnaDetailPageResponseMapper.qnaDetailResponseDTO(question);
  }

  @Override
  @Transactional
  public ReplyResponseDTO registerReplyInfo(ReplyRequestDTO replyRequestDTO) {
    Question question = questionRepository.findById(replyRequestDTO.getQuestionSeq())
        .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
    Member member = memberRepository.findById(replyRequestDTO.getMemberSeq())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    Reply reply = replyRequestMapper.toReply(replyRequestDTO, question, member);
    Reply saveReply = replyRepository.save(reply);

    return replyResponseMapper.replyResponseDTO(saveReply);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReplyResponseDTO> getReplyListInfo(Long questionSeq) {
    List<Reply> replies = replyRepository.findByQuestionSeqOrderByCreatedAtDesc(questionSeq);
    return replies.stream().map(replyResponseMapper::replyResponseDTO)
        .collect(Collectors.toList());
  }

}
