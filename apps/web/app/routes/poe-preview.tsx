import type { Route } from "./+types/poe-preview";
import { AnalysisPreviewApp } from "../poe/App";

export function meta({}: Route.MetaArgs) {
  return [{ title: "PoE Lens · 분석 미리보기" }];
}

export default AnalysisPreviewApp;
