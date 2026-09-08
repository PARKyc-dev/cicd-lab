import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/index.tsx"),
  route("poe", "routes/poe.tsx"),
  route("word", "routes/home.tsx"),
  route("word/study", "routes/study.tsx"),
  route("todo", "todo/routes/todo.tsx"),
] satisfies RouteConfig;
