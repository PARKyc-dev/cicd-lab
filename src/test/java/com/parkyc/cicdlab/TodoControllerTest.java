package com.parkyc.cicdlab;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TodoControllerTest {

    @Test
    void showsTodoList() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        Todo todo = new Todo("Deploy from CI/CD domain");
        when(todoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(todo));
        TodoController controller = new TodoController(todoRepository);
        Model model = new ConcurrentModel();

        String viewName = controller.list(model);

        assertThat(viewName).isEqualTo("todos");
        assertThat(model.getAttribute("todos")).isEqualTo(List.of(todo));
        assertThat(model.getAttribute("newTodo")).isInstanceOf(Todo.class);
    }

    @Test
    void addsTodoWithTrimmedTitle() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoController controller = new TodoController(todoRepository);

        String redirect = controller.add("  Check Supabase connection  ");

        assertThat(redirect).isEqualTo("redirect:/todos");
        verify(todoRepository).save(new Todo("Check Supabase connection"));
    }

    @Test
    void togglesTodoCompletion() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        Todo todo = new Todo("Ship todo list");
        when(todoRepository.findById(7L)).thenReturn(Optional.of(todo));
        TodoController controller = new TodoController(todoRepository);

        String redirect = controller.toggle(7L);

        assertThat(redirect).isEqualTo("redirect:/todos");
        assertThat(todo.isCompleted()).isTrue();
        verify(todoRepository).save(todo);
    }

    @Test
    void deletesTodo() {
        TodoRepository todoRepository = mock(TodoRepository.class);
        TodoController controller = new TodoController(todoRepository);

        String redirect = controller.delete(3L);

        assertThat(redirect).isEqualTo("redirect:/todos");
        verify(todoRepository).deleteById(3L);
    }
}
