# CI/CD Lab

PoE Lens, JYP Word, Todo를 하나의 React 프론트엔드와 하나의 Spring Boot 백엔드로 통합한 프로젝트입니다.

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

`main` 대상 push와 pull request에서 통합 백엔드 테스트, 프론트엔드 타입 검사와 프로덕션 빌드를 수행합니다. `main` push의 검사가 성공하면 다음 이미지를 GHCR에 발행합니다.

- `ghcr.io/parkyc-dev/cicd-lab-api`
- `ghcr.io/parkyc-dev/cicd-lab-web`
