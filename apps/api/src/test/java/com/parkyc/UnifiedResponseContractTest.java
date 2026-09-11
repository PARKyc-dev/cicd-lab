package com.parkyc;

import com.parkyc.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnifiedResponseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoRepository todoRepository;

    @Test
    void returnsTodoListAsAnArrayWithoutPoeResponseWrapping() throws Exception {
        when(todoRepository.findAllByOrderByIdDesc()).thenReturn(List.of());

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
