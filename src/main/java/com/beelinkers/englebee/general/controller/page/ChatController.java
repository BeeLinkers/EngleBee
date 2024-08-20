package com.beelinkers.englebee.general.controller.page;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ChatController {
  @GetMapping("/chat")
  public String goToSocketPage() {
    return "general/socket";
  }
}
