import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import type { Route } from "./+types/todo";
import "../todo.css";

type Todo = {
  id: number;
  title: string;
  completed: boolean;
};

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Todo" },
    { name: "description", content: "할 일 목록" },
  ];
}

export default function TodoPage() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [title, setTitle] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    void loadTodos();
  }, []);

  async function loadTodos() {
    try {
      const response = await fetch("/api/todo");
      if (!response.ok) {
        throw new Error();
      }
      setTodos(await response.json());
    } catch {
      setError("할 일 목록을 불러오지 못했습니다.");
    } finally {
      setIsLoading(false);
    }
  }

  async function addTodo(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const trimmedTitle = title.trim();
    if (!trimmedTitle) {
      return;
    }

    setError("");
    const response = await fetch("/api/todo", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ title: trimmedTitle }),
    });

    if (!response.ok) {
      setError("할 일을 추가하지 못했습니다.");
      return;
    }

    const todo: Todo = await response.json();
    setTodos((currentTodos) => [todo, ...currentTodos]);
    setTitle("");
  }

  async function toggleTodo(todo: Todo) {
    setError("");
    const response = await fetch(`/api/todo/${todo.id}/toggle`, {
      method: "POST",
    });

    if (!response.ok) {
      setError("상태를 변경하지 못했습니다.");
      return;
    }

    const updatedTodo: Todo = await response.json();
    setTodos((currentTodos) =>
      currentTodos.map((currentTodo) =>
        currentTodo.id === updatedTodo.id ? updatedTodo : currentTodo,
      ),
    );
  }

  async function deleteTodo(id: number) {
    setError("");
    const response = await fetch(`/api/todo/${id}`, { method: "DELETE" });

    if (!response.ok) {
      setError("할 일을 삭제하지 못했습니다.");
      return;
    }

    setTodos((currentTodos) =>
      currentTodos.filter((todo) => todo.id !== id),
    );
  }

  return (
    <main className="todo-page">
      <h1>Todo List</h1>
      <p className="todo-summary">
        CI/CD 도메인에서 Supabase PostgreSQL 저장을 확인하는 간단한 목록입니다.
      </p>

      <form className="todo-add-form" onSubmit={addTodo}>
        <input
          type="text"
          maxLength={200}
          placeholder="할 일을 입력하세요"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          required
        />
        <button type="submit">추가</button>
      </form>
      <p className="todo-error" aria-live="polite">
        {error}
      </p>

      {isLoading ? (
        <div className="todo-empty">목록을 불러오는 중입니다.</div>
      ) : todos.length === 0 ? (
        <div className="todo-empty">아직 등록된 할 일이 없습니다.</div>
      ) : (
        <ul className="todo-list">
          {todos.map((todo) => (
            <li className="todo-item" key={todo.id}>
              <span className={todo.completed ? "todo-title completed" : "todo-title"}>
                {todo.title}
              </span>
              <button
                className="secondary"
                type="button"
                onClick={() => void toggleTodo(todo)}
              >
                {todo.completed ? "되돌리기" : "완료"}
              </button>
              <button
                className="danger"
                type="button"
                onClick={() => void deleteTodo(todo.id)}
              >
                삭제
              </button>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
