package com.parkyc.cicdlab;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoController {

    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping({"/", "/todos"})
    public String list(Model model) {
        model.addAttribute("todos", todoRepository.findAllByOrderByIdDesc());
        model.addAttribute("newTodo", new Todo(""));
        return "todos";
    }

    @PostMapping("/todos")
    public String add(@RequestParam String title) {
        String trimmedTitle = title.trim();
        if (!trimmedTitle.isEmpty()) {
            todoRepository.save(new Todo(trimmedTitle));
        }
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        todoRepository.findById(id).ifPresent(todo -> {
            todo.toggleCompleted();
            todoRepository.save(todo);
        });
        return "redirect:/todos";
    }

    @PostMapping("/todos/{id}/delete")
    public String delete(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/todos";
    }
}
