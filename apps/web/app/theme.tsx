import { createContext, useContext, useEffect, useState } from "react";

type Theme = "warm" | "cobalt";

const ThemeContext = createContext<{
  theme: Theme;
  setTheme: (theme: Theme) => void;
} | null>(null);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<Theme>("warm");

  useEffect(() => {
    if (window.localStorage.getItem("jyp-word-theme") === "cobalt") {
      setTheme("cobalt");
    }
  }, []);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    window.localStorage.setItem("jyp-word-theme", theme);
  }, [theme]);

  return <ThemeContext.Provider value={{ theme, setTheme }}>{children}</ThemeContext.Provider>;
}

export function ThemeToggle() {
  const context = useContext(ThemeContext);
  if (!context) throw new Error("ThemeToggle must be used within ThemeProvider.");

  const isWarm = context.theme === "warm";

  return (
    <button
      className="theme-toggle"
      type="button"
      onClick={() => context.setTheme(isWarm ? "cobalt" : "warm")}
    >
      {isWarm ? "코발트" : "리딩룸"}
    </button>
  );
}
