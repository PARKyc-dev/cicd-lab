# CI/CD Lab

[PoE Lens](https://github.com/PARKyc-dev/poe-build-lens)와 [JYP Word](https://github.com/PARKyc-dev/jyp-word)를 하나의 이미지로 빌드하고 배포하기 위해 병합한 프로젝트입니다. 두 프로젝트의 React 프론트엔드와 Spring Boot 백엔드를 각각 하나로 통합했으며, Todo 기능도 함께 제공합니다.

## 구조

```text
.
├── apps/
│   ├── api/       # 통합 Spring Boot 백엔드
│   ├── web/       # 통합 React 프론트엔드
│   └── data/      # JYP Word 데이터 적재 자료
├── compose.yaml
└── .env.example
```

프론트엔드 경로:

- PoE Lens: `/poe`
- JYP Word: `/word`
- Todo: `/todo`

백엔드 경로:

- PoE Lens API: `/api/poe/**`
- JYP Word API: `/api/word/**`
- Todo API: `/api/todo/**`

## 로컬 원본 동기화

이 저장소의 기능 원본은 Git 원격이 아니라 다음 로컬 프로젝트다.

- PoE Lens: `/Users/parkyc/Desktop/Code/poe-lens`
- JYP Word: `/Users/parkyc/Desktop/Code/JYP-Word`

동기화할 때 두 원본은 읽기 전용으로 사용하고 변경은 이 통합 저장소에만 적용한다. PoE Lens는 `apps/api/src/main/java/com/parkyc/poelens`와 `apps/web/app/poe`로, JYP Word는 `apps/api/src/main/java/com/parkyc/jypword`와 `apps/web/app`의 공통 라우트 영역으로 반영된다.

통합본은 원본과 달리 API 접두사(`/api/poe/**`, `/api/word/**`, `/api/todo/**`), 웹 라우트(`/poe`, `/word`, `/todo`), 단일 Spring Boot 진입점과 통합 빌드 설정을 사용한다. 따라서 원본 디렉터리를 통째로 덮어쓰지 말고 체크섬으로 변경 파일을 확인한 뒤 통합 전용 차이를 보존해야 한다. 구체적인 파일 매핑과 검증 절차는 [`AGENTS.md`](./AGENTS.md)에 기록되어 있다.

## 로컬 실행

Docker Desktop을 실행하고 환경변수 파일을 준비합니다.

```bash
cp .env.example .env
```

`.env`에 Supabase 접속 정보와 필요한 경우 OpenAI API 키를 입력합니다.

```dotenv
IMAGE_TAG=latest
WEB_PORT=8081
OPENAI_API_KEY=
DB_URL=jdbc:postgresql://<SUPABASE_HOST>:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=<SUPABASE_PASSWORD>
```

통합 프론트엔드와 백엔드를 실행합니다.

```bash
docker compose up -d --build
```

접속 주소:

- `http://localhost:8081/poe`
- `http://localhost:8081/word`
- `http://localhost:8081/todo`

상태와 로그를 확인합니다.

```bash
docker compose ps
docker compose logs -f
```

종료합니다.

```bash
docker compose down
```

데이터베이스는 Compose에서 실행하지 않으며 외부 Supabase PostgreSQL을 사용합니다.

## CI/CD

`main` 대상 push와 pull request에서 통합 백엔드 테스트, 프론트엔드 단위 테스트·타입 검사와 프로덕션 빌드를 수행합니다. `main` push의 검사가 성공하면 다음 이미지를 GHCR에 발행합니다.

- `ghcr.io/parkyc-dev/cicd-lab-api`
- `ghcr.io/parkyc-dev/cicd-lab-web`
