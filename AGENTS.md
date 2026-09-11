# 통합 프로젝트 작업 지침

## 프로젝트 성격

이 저장소는 아래 두 로컬 원본 프로젝트를 하나의 배포 단위로 통합한다.

- PoE Lens 원본: `/Users/parkyc/Desktop/Code/poe-lens`
- JYP Word 원본: `/Users/parkyc/Desktop/Code/JYP-Word`
- 수정 대상 통합본: `/Users/parkyc/Desktop/Code/cicd-lab`

사용자가 원본 프로젝트 자체의 수정을 명시하지 않는 한 두 원본은 항상 읽기 전용 기준으로 취급한다. 동기화 요청에서는 Git 원격이나 `origin/main`을 기준으로 삼지 않고 위 로컬 디렉터리의 현재 파일을 기준으로 비교한다.

## 동기화 경로

- PoE Lens `api/src/main/java/com/parkyc/poelens/` → `apps/api/src/main/java/com/parkyc/poelens/`
- PoE Lens `api/src/test/java/com/parkyc/poelens/` → `apps/api/src/test/java/com/parkyc/poelens/`
- PoE Lens `web/src/` → `apps/web/app/poe/`
- PoE Lens `web/public/pob/` → `apps/web/public/pob/`
- JYP Word `api/src/main/java/com/parkyc/jypword/` → `apps/api/src/main/java/com/parkyc/jypword/`
- JYP Word `api/src/test/java/com/parkyc/jypword/` → `apps/api/src/test/java/com/parkyc/jypword/`
- JYP Word `web/app/` → `apps/web/app/` (`apps/web/app/poe/` 제외)
- JYP Word `data/` → `apps/data/`

## 반드시 유지할 통합 차이

원본을 그대로 덮어쓰면 안 되는 통합 전용 계약이다.

- Spring Boot 진입점은 `apps/api/src/main/java/com/parkyc/UnifiedApplication.java` 하나를 사용한다. 원본의 `PoeLensApplication.java`와 `JypWordApplication.java`를 복사하지 않는다.
- PoE Lens API는 `/api/poe/**`, JYP Word API는 `/api/word/**`, Todo API는 `/api/todo/**`를 사용한다.
- JYP Word 원본의 `jypword.health`와 `jypword.todo`는 통합본에서 각각 `com.parkyc.health`와 `com.parkyc.todo` 패키지로 이동되어 있다. JYP Word 동기화 시 원래 패키지 위치로 되돌리지 않는다.
- 웹 라우트는 `/poe`, `/word`, `/word/study`, `/todo`를 유지한다.
- PoE Lens 웹 API 클라이언트와 테스트의 주소도 `/api/poe/**`를 사용한다.
- `apps/web/app/poe/pob/pob.worker.ts`의 Wasmoon 경로는 통합 폴더 깊이에 맞춰 `../../../node_modules/wasmoon/dist/glue.wasm`을 사용한다.
- `apps/api/build.gradle`, `apps/api/src/main/resources/application.yaml`, `apps/web/package.json`, `apps/web/vite.config.ts`, `apps/web/server.mjs`는 두 프로젝트 요구사항을 합친 통합 설정이므로 한쪽 원본으로 덮어쓰지 않는다.

## 작업 절차

1. 시작 전에 통합본과 두 원본의 상태를 확인하고 기존 사용자 변경을 보존한다.
2. `rsync -ainc` 또는 체크섬 비교로 내용 차이를 먼저 확인한다. 원본에는 쓰기 명령을 실행하지 않는다.
3. 변경 파일을 통합본에 반영한 뒤 위 통합 차이를 다시 적용한다.
4. 두 원본의 상태가 작업 전후 동일한지 확인한다.
5. 완료 전에 아래 검증을 모두 실행한다.

```bash
cd apps/api && ./gradlew test --no-daemon && ./gradlew bootJar --no-daemon
cd apps/web && npm test -- --run && npm run typecheck && npm run build
docker compose config
docker compose build
```

PoE Lens의 `buildFacts.integration.test.ts`는 원본에서도 장시간 대기하는 별도 통합 테스트이므로 기본 Vitest에서는 제외한다. 해당 테스트를 수정하거나 PoB 엔진을 검증하는 작업에서는 별도로 실행하고 결과를 명시한다.

## Git 및 생성 파일

- 사용자가 요청하지 않으면 커밋하거나 푸시하지 않는다.
- 테스트가 변경한 `apps/logs/` 파일과 빌드 산출물은 소스 변경에 포함하지 않는다.
- 기존의 미추적 파일이나 사용자 변경은 임의로 삭제하지 않는다.
