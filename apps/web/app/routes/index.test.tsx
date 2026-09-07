import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import Index from "./index";

describe("service hub", () => {
  it("introduces parkyc.com and links to every service and GitHub", () => {
    render(<Index />);

    expect(screen.getByRole("heading", { name: "작은 아이디어를 실제 서비스로 만듭니다." })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /PoE Lens 시작하기/ })).toHaveAttribute("href", "/poe");
    expect(screen.getByRole("link", { name: /Todo 시작하기/ })).toHaveAttribute("href", "/todo");
    expect(screen.getByRole("link", { name: /JYP Word 시작하기/ })).toHaveAttribute("href", "/word");
    expect(screen.getByRole("link", { name: /GitHub/ })).toHaveAttribute("href", "https://github.com/PARKyc-dev");
  });
});
