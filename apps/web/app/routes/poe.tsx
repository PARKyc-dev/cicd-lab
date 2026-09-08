import type { Route } from "./+types/poe";
import PoeLens from "../poe/App";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "PoE Lens" },
    { name: "description", content: "Path of Exile 빌드를 분석하세요." },
  ];
}

export default PoeLens;
