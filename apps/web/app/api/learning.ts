const MEMBER_STORAGE_KEY = "jyp-word-member";

export type TodayWord = {
  word: string;
  accent: string | null;
  meanings: {
    type: string;
    meaning: string;
    displayOrder: number;
  }[];
  sentences: {
    sentence: string;
    translation: string;
    displayOrder: number;
  }[];
};

export type TodayLearnResponse = {
  member: string;
  learningDate: string;
  wordBookId: number;
  wordBookName: string;
  cursor: number;
  words: TodayWord[];
};

export async function fetchTodayWords(signal?: AbortSignal) {
  const member = window.localStorage.getItem(MEMBER_STORAGE_KEY);
  const searchParams = new URLSearchParams();
  if (member) searchParams.set("member", member);

  const query = searchParams.size ? `?${searchParams.toString()}` : "";
  const response = await fetch(`/api/word/learn/today${query}`, { signal });

  if (!response.ok) {
    throw new Error(`오늘의 단어를 가져오지 못했습니다. (${response.status})`);
  }
  if (response.status === 204) {
    throw new Error("학습할 수 있는 단어가 없습니다.");
  }

  const data = (await response.json()) as TodayLearnResponse;
  window.localStorage.setItem(MEMBER_STORAGE_KEY, data.member);
  return data;
}
