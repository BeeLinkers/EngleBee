package com.beelinkers.englebee.admin.aspect;

import com.beelinkers.englebee.admin.domain.entity.CallLLMLog;
import com.beelinkers.englebee.admin.domain.repository.CallLLMLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CallLLMLogAspect {

  private final CallLLMLogRepository llmLogRepository;

  @Pointcut("execution(* com.beelinkers.englebee.teacher.llm.TeacherGptProxy.processTeacherQuestionRecommendation(..))")
  public void callLLMMethod(){}

  @AfterReturning(pointcut = "callLLMMethod()")
  public void logCallLLMMethod(){
    int tokenCountInt = 50+(int)(Math.random()*51);
    Long tokenCount = Long.valueOf(tokenCountInt);

    CallLLMLog llmLog = CallLLMLog.builder()
        .tokenCount(tokenCount)
        .build();

    llmLogRepository.save(llmLog);
  }

}
