package com.beelinkers.englebee.teacher.controller.api;

import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageLectureDTO;
import com.beelinkers.englebee.teacher.dto.response.TeacherMainPageQuestionDTO;
import com.beelinkers.englebee.teacher.service.TeacherMainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class TeacherMainApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherMainService teacherMainService;

    @BeforeEach
    void setUp() {
        Page<TeacherMainPageLectureDTO> lecturePage = new PageImpl<>(List.of(new TeacherMainPageLectureDTO(
                1L, "user3", "기초문법강의", "CREATED",
                LocalDateTime.now(), List.of(List.of("어법", "중"), List.of("문장", "중"))
        ))
        );
        Page<TeacherMainPageQuestionDTO> questionPage = new PageImpl<>(List.of(new TeacherMainPageQuestionDTO(
                1L, "user2", "REST API 모범 사례", LocalDateTime.now()
        ))
        );

        Mockito.when(teacherMainService.getLectureList(anyLong(), any(Pageable.class)))
                .thenReturn(lecturePage);
        Mockito.when(teacherMainService.getQuestionList(any(Pageable.class)))
                .thenReturn(questionPage);
    }

    @Test
    void testGetLectureList() throws Exception {
        mockMvc.perform(get("/api/teacher/main/lecture")
            .param("memberSeq", "3")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("기초문법강의"))
            .andExpect(jsonPath("$.content[0].status").value("CREATED"))
            .andExpect(jsonPath("$.content[0].createdAt").exists())
            .andExpect(jsonPath("$.content[0].subjectLevelCode[0][0]").value("어법"))
            .andExpect(jsonPath("$.content[0].subjectLevelCode[0][1]").value("중"))
            .andExpect(jsonPath("$.pagination").exists())
            .andExpect(jsonPath("$.pagination.totalPages").value(1))
            .andExpect(jsonPath("$.pagination.totalElements").value(1))
            .andExpect(jsonPath("$.pagination.currentPage").value(1)
        );
    }

    @Test
    void testGetQuestionList() throws Exception {
        mockMvc.perform(get("/api/teacher/main/question")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].memberNickname").value("user2"))
            .andExpect(jsonPath("$.content[0].title").value("REST API 모범 사례"))
            .andExpect(jsonPath("$.content[0].createdAt").exists())
            .andExpect(jsonPath("$.pagination.currentPage").value(1)
        );
    }
}
