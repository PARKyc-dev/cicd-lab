package com.parkyc.jypword.todo.repository;

import com.parkyc.jypword.todo.domain.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByOrderByIdDesc();
}
