// @vitest-environment node

import { renderToString } from "react-dom/server";
import { describe, expect, it } from "vitest";

import PoeLens from "./poe";
import PoePreview from "./poe-preview";

describe("PoE SSR routes", () => {
  it("renders the analysis entry route without browser globals", () => {
    expect(() => renderToString(<PoeLens />)).not.toThrow();
  });

  it("renders the preview route without browser globals", () => {
    expect(() => renderToString(<PoePreview />)).not.toThrow();
  });
});
