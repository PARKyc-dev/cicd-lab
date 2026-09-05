import { useEffect, useRef, useState } from "react";
import { Link } from "react-router";

import type { Route } from "./+types/study";
import { fetchTodayWords, type TodayWord } from "../api/learning";
import { ThemeToggle } from "../theme";

const meanTypeLabels: Record<string, string> = {
  noun: "명사",
  verb: "동사",
  adjective: "형용사",
  adverb: "부사",
  pronoun: "대명사",
  preposition: "전치사",
  conjunction: "접속사",
  interjection: "감탄사",
  article: "관사",
};

export function meta({}: Route.MetaArgs) {
  return [{ title: "오늘의 학습 | JYP Word" }];
}

export default function Study() {
  const [studyWords, setStudyWords] = useState<TodayWord[]>([]);
  const [testWords, setTestWords] = useState<TodayWord[]>([]);
  const [totalWords, setTotalWords] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isRevealed, setIsRevealed] = useState(false);
  const [isTransitioning, setIsTransitioning] = useState(false);
  const [drag, setDrag] = useState({ x: 0, y: 0, isDragging: false });
  const pointerStart = useRef<{ x: number; y: number } | null>(null);
  const currentWord = studyWords[0];

  useEffect(() => {
    const controller = new AbortController();

    fetchTodayWords(controller.signal)
      .then((data) => {
        setStudyWords(data.words);
        setTotalWords(data.words.length);
      })
      .catch((requestError: unknown) => {
        if (requestError instanceof DOMException && requestError.name === "AbortError") return;
        setError(
          requestError instanceof Error
            ? requestError.message
            : "오늘의 단어를 가져오지 못했습니다.",
        );
      })
      .finally(() => setIsLoading(false));

    return () => controller.abort();
  }, []);

  const cardStyle = {
    opacity: 1 - Math.min(0.35, Math.max(Math.abs(drag.x), Math.abs(drag.y)) / 900),
    transform: `translate3d(${drag.x}px, ${drag.y}px, 0) rotate(${drag.x * 0.035}deg)`,
    transition: drag.isDragging ? "none" : "transform 180ms ease, opacity 180ms ease",
  };

  function showNext(addToTest = false) {
    if (!currentWord) return;

    setStudyWords((words) => {
      const [word, ...remainingWords] = words;
      return addToTest || !word ? remainingWords : [...remainingWords, word];
    });
    if (addToTest) setTestWords((words) => [...words, currentWord]);
    setIsRevealed(false);
  }

  function handlePointerDown(event: React.PointerEvent<HTMLElement>) {
    if (isTransitioning) return;
    pointerStart.current = { x: event.clientX, y: event.clientY };
    event.currentTarget.setPointerCapture(event.pointerId);
  }

  function handlePointerMove(event: React.PointerEvent<HTMLElement>) {
    const start = pointerStart.current;
    if (!start || isTransitioning) return;

    setDrag({
      x: event.clientX - start.x,
      y: event.clientY - start.y,
      isDragging: true,
    });
  }

  function handlePointerUp(event: React.PointerEvent<HTMLElement>) {
    const start = pointerStart.current;
    pointerStart.current = null;
    if (!start || isTransitioning) return;

    const horizontalDistance = event.clientX - start.x;
    const verticalDistance = event.clientY - start.y;
    if (Math.max(Math.abs(horizontalDistance), Math.abs(verticalDistance)) < 50) {
      setDrag({ x: 0, y: 0, isDragging: false });
      setIsRevealed((revealed) => !revealed);
      return;
    }

    const addToTest =
      verticalDistance > 0 && Math.abs(verticalDistance) > Math.abs(horizontalDistance);
    const isVertical = Math.abs(verticalDistance) > Math.abs(horizontalDistance);
    const distance = 620;

    setIsTransitioning(true);
    setDrag({
      x: isVertical ? 0 : horizontalDistance > 0 ? distance : -distance,
      y: isVertical ? (verticalDistance > 0 ? distance : -distance) : 0,
      isDragging: false,
    });
    window.setTimeout(() => {
      showNext(addToTest);
      setDrag({ x: 0, y: 0, isDragging: false });
      setIsTransitioning(false);
    }, 180);
  }

  return (
    <main className="study-screen">
      <header className="study-screen__header">
        <Link to="/word" aria-label="홈으로 돌아가기">
          ‹
        </Link>
        <div>
          <span>오늘의 학습</span>
          <strong>{currentWord ? totalWords - studyWords.length + 1 : totalWords} / {totalWords || 20}</strong>
        </div>
        <ThemeToggle />
      </header>

      {isLoading ? (
        <section className="study-status" aria-live="polite">
          <h1>오늘의 단어를 불러오는 중이에요.</h1>
        </section>
      ) : error ? (
        <section className="study-status study-status--error" role="alert">
          <h1>단어를 불러오지 못했어요.</h1>
          <p>{error}</p>
          <Link to="/word">홈으로 돌아가기</Link>
        </section>
      ) : currentWord ? (
        <section className="word-deck" aria-label="단어 카드">
          {studyWords.slice(1, 3).map((word, index) => (
            <div className={`word-card word-card--stack-${index + 1}`} key={word.word} />
          ))}
          <article
            className={`word-card word-card--current${isRevealed ? " word-card--revealed" : ""}`}
            style={cardStyle}
            onPointerDown={handlePointerDown}
            onPointerMove={handlePointerMove}
            onPointerUp={handlePointerUp}
            onPointerCancel={() => setDrag({ x: 0, y: 0, isDragging: false })}
            role="button"
            tabIndex={0}
          >
            <h1>{currentWord.word}</h1>
            {isRevealed && (
              <div className="word-card__details">
                <p className="word-card__pronunciation">
                  {currentWord.accent || "발음 정보가 없어요."}
                </p>
                <p className="word-card__meaning">
                  {currentWord.meanings.length
                    ? currentWord.meanings
                        .map(
                          (meaning) =>
                            `[${meanTypeLabels[meaning.type] || meaning.type}] ${meaning.meaning}`,
                        )
                        .join(" · ")
                    : "뜻 정보가 없어요."}
                </p>
                {currentWord.sentences.length > 0 && <hr />}
                {currentWord.sentences.map((sentence) => (
                  <div className="word-card__example" key={sentence.displayOrder}>
                    <p className="word-card__sentence">
                      {sentence.sentence || "영문 예문 정보가 없어요."}
                    </p>
                    <p className="word-card__translation">{sentence.translation}</p>
                  </div>
                ))}
              </div>
            )}
            {drag.y > 48 && <span className="word-card__test-hint">테스트 보기에 추가</span>}
          </article>
        </section>
      ) : (
        <section className="study-complete">
          <span>오늘의 학습 완료</span>
          <h1>수고했어요!</h1>
          <p>테스트 보기로 옮긴 단어는 아래 목록에서 확인할 수 있어요.</p>
        </section>
      )}

      <p className="swipe-guide">좌·우·위로 넘기기 · 아래로 내리면 테스트 보기에 추가</p>

      <section className="word-lists" aria-label="학습 목록">
        <div>
          <h2>학습 중</h2>
          <ul>
            {studyWords.slice(0, 3).map((word) => (
              <li key={word.word}>{word.word}</li>
            ))}
          </ul>
        </div>
        <div>
          <h2>테스트 보기</h2>
          <ul>
            {testWords.length ? (
              testWords.map((word) => <li key={word.word}>{word.word}</li>)
            ) : (
              <li className="word-lists__empty">아래로 내려보세요</li>
            )}
          </ul>
        </div>
      </section>
    </main>
  );
}
