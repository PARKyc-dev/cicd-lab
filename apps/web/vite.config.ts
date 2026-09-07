import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vitest/config";

export default defineConfig(({ mode }) => ({
  plugins: mode === "test" ? [] : [tailwindcss(), reactRouter()],
  resolve: {
    tsconfigPaths: true,
  },
  server: {
    proxy: {
      "/api": "http://localhost:8080",
    },
  },
  test: {
    environment: "jsdom",
    exclude: ["node_modules/**", "build/**", "**/buildFacts.integration.test.ts"],
    setupFiles: "./app/poe/test/setup.ts",
  },
}));
