import type { Route } from "./+types/home";
import { Link } from "react-router";
import { ThemeToggle } from "../theme";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "JYP Word" },
    { name: "description", content: "매일 20개의 영단어를 학습하세요." },
  ];
}

export default function Home() {
  return (
    <main className="study-home">
      <header className="study-home__header">
        <strong>JYP Word</strong>
        <div className="study-home__header-actions">
          <span>8월 30일 일요일</span>
          <ThemeToggle />
        </div>
      </header>

      <section className="study-home__intro" aria-labelledby="study-title">
        <p id="study-title">
          오늘도 한 걸음,
          <br />
          <em>20개의 단어</em>를 만나볼까요?
        </p>
      </section>

      <section className="study-progress" aria-label="오늘의 학습 진행도">
        <div>
          <span>오늘의 학습</span>
          <strong>0 / 20</strong>
        </div>
        <div className="study-progress__bar">
          <span />
        </div>
      </section>

      <section className="today-words" aria-labelledby="today-words-title">
        <span>TODAY&apos;S WORDS</span>
        <h1 id="today-words-title">오늘의 단어 20개</h1>
        <p>나에게 맞는 난이도와 관심 분야의 단어를 차분하게 학습해요.</p>
        <Link className="study-start-link" to="/word/study">
          오늘 학습 시작하기
        </Link>
      </section>

      <section className="study-settings" aria-labelledby="settings-title">
        <div>
          <h2 id="settings-title">오늘의 설정</h2>
          <button type="button">변경</button>
        </div>
        <p>
          <span>중급</span>
          <span>일반 영어</span>
        </p>
      </section>

      <nav className="bottom-nav" aria-label="주요 메뉴">
        <a className="bottom-nav__item bottom-nav__item--active" href="#home">
          <span aria-hidden="true">⌂</span>
          홈
        </a>
        <a className="bottom-nav__item" href="#review">
          <span aria-hidden="true">◷</span>
          복습
        </a>
        <a className="bottom-nav__item" href="#history">
          <span aria-hidden="true">◉</span>
          기록
        </a>
        <a className="bottom-nav__item" href="#settings">
          <span aria-hidden="true">⚙</span>
          설정
        </a>
      </nav>
    </main>
  );
}
