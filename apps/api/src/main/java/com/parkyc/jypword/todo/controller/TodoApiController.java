package com.parkyc.jypword.todo.controller;

import com.parkyc.jypword.todo.domain.entity.Todo;
import com.parkyc.jypword.todo.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoApiController {

    private final TodoRepository todoRepository;

    public TodoApiController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<TodoResponse> list() {
        return todoRepository.findAllByOrderByIdDesc()
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@RequestParam String title) {
        String trimmedTitle = title.trim();
        if (trimmedTitle.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Todo todo = todoRepository.save(new Todo(trimmedTitle));
        return ResponseEntity.status(HttpStatus.CREATED).body(TodoResponse.from(todo));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<TodoResponse> toggle(@PathVariable Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.toggleCompleted();
                    return ResponseEntity.ok(TodoResponse.from(todoRepository.save(todo)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record TodoResponse(Long id, String title, boolean completed) {

        private static TodoResponse from(Todo todo) {
            return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted());
        }
    }
}
