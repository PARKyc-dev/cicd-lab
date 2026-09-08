import { describe, expect, it } from "vitest";

import { meta as homeMeta } from "./index";
import { meta as poeMeta } from "./poe";
import { meta as todoMeta } from "../todo/routes/todo";
import { meta as wordMeta } from "./home";

function titleOf(entries: ReturnType<typeof homeMeta>) {
  return entries.find((entry) => "title" in entry)?.title;
}

describe("route titles", () => {
  it.each([
    ["home", homeMeta, "PARKYC.COM"],
    ["poe", poeMeta, "PoE Lens"],
    ["todo", todoMeta, "Todo"],
    ["word", wordMeta, "JYP Word"],
  ])("uses the correct %s title", (_route, meta, expectedTitle) => {
    expect(titleOf(meta({} as never))).toBe(expectedTitle);
  });
});
