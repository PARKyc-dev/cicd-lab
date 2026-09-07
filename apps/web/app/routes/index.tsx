import type { Route } from "./+types/index";

const services = [
  {
    title: "PoE Lens",
    eyebrow: "BUILD ANALYSIS",
    description: "Path of Exile 빌드를 분석하고, 복잡한 데이터를 한눈에 확인합니다.",
    href: "/poe",
    accent: "ember",
  },
  {
    title: "Todo",
    eyebrow: "DAILY FOCUS",
    description: "오늘 해야 할 일을 가볍게 기록하고 흐름을 놓치지 않습니다.",
    href: "/todo",
    accent: "cobalt",
  },
  {
    title: "JYP Word",
    eyebrow: "WORD PRACTICE",
    description: "나만의 단어장을 만들고 반복 학습으로 어휘를 익힙니다.",
    href: "/word",
    accent: "sage",
  },
] as const;

export function meta({}: Route.MetaArgs) {
  return [
    { title: "PARKYC.COM · Project Hub" },
    {
      name: "description",
      content: "PARKYC가 만들고 운영하는 서비스들을 한곳에서 만나보세요.",
    },
  ];
}

export default function Index() {
  return (
    <main className="hub-page">
      <header className="hub-header">
        <a className="hub-brand" href="/" aria-label="PARKYC.COM 홈">
          <span aria-hidden="true">P</span>
          PARKYC.COM
        </a>
        <div className="hub-header__actions">
          <span className="hub-status"><i />3 SERVICES ONLINE</span>
          <a
            className="hub-github"
            href="https://github.com/PARKyc-dev"
            target="_blank"
            rel="noreferrer"
          >
            GitHub <span aria-hidden="true">↗</span>
          </a>
        </div>
      </header>

      <section className="hub-hero">
        <div>
          <p className="hub-kicker">PERSONAL LAB · BUILT IN SEOUL</p>
          <h1 aria-label="작은 아이디어를 실제 서비스로 만듭니다.">
            작은 아이디어를<br />
            <em>실제 서비스</em>로 만듭니다.
          </h1>
          <p className="hub-intro">
            필요해서 만들고, 직접 쓰며 다듬는 개인 프로젝트 공간입니다.
            아래에서 원하는 서비스를 선택해 시작해 보세요.
          </p>
        </div>
        <aside className="hub-about">
          <span>ABOUT THIS PLACE</span>
          <p>배우고 실험한 것을 작동하는 제품으로 기록합니다.</p>
          <strong>03 <small>ACTIVE PROJECTS</small></strong>
        </aside>
      </section>

      <section className="hub-services" aria-labelledby="services-title">
        <div className="hub-section-title">
          <p>EXPLORE THE LAB</p>
          <h2 id="services-title">어디로 이동할까요?</h2>
        </div>
        <div className="hub-service-grid">
          {services.map((service, index) => (
            <a
              className={`hub-service-card hub-service-card--${service.accent}`}
              href={service.href}
              key={service.href}
            >
              <span className="hub-service-card__number">0{index + 1}</span>
              <div>
                <p>{service.eyebrow}</p>
                <h3>{service.title}</h3>
                <span>{service.description}</span>
              </div>
              <strong>{service.title} 시작하기 <i aria-hidden="true">→</i></strong>
            </a>
          ))}
        </div>
      </section>

      <footer className="hub-footer">
        <span>DESIGNED &amp; BUILT BY PARKYC</span>
        <span>SEOUL, KR · 2026</span>
      </footer>
    </main>
  );
}
