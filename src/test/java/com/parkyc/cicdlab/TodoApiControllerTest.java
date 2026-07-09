package com.parkyc.cicdlab;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TodoApiControllerTest {

    @Test
    void listsTodos() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        Todo todo = new Todo("Render without full reload");
        when(todoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(todo));
        TodoApiController controller = new TodoApiController(todoRepository);

        List<TodoApiController.TodoResponse> todos = controller.list();

        assertThat(todos).hasSize(1);
        assertThat(todos.getFirst().title()).isEqualTo("Render without full reload");
        assertThat(todos.getFirst().completed()).isFalse();
    }

    @Test
    void createsTodo() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TodoApiController controller = new TodoApiController(todoRepository);

        ResponseEntity<TodoApiController.TodoResponse> response = controller.create("  Add with fetch  ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Add with fetch");
        assertThat(response.getBody().completed()).isFalse();
        verify(todoRepository).save(new Todo("Add with fetch"));
    }

    @Test
    void togglesTodo() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        Todo todo = new Todo("Toggle with fetch");
        when(todoRepository.findById(3L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);
        TodoApiController controller = new TodoApiController(todoRepository);

        ResponseEntity<TodoApiController.TodoResponse> response = controller.toggle(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().completed()).isTrue();
        verify(todoRepository).save(todo);
    }

    @Test
    void deletesTodo() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoApiController controller = new TodoApiController(todoRepository);

        ResponseEntity<Void> response = controller.delete(9L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(todoRepository).deleteById(9L);
    }
}
