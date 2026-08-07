# Recipe Finder

- Spring Boot와 MongoDB Atlas Search를 기반으로 만든 개인 레시피 검색 백엔드 프로젝트
- 크롤링한 레시피 데이터를 MongoDB에 저장하고, MongoDB Atlas Search를 활용해 레시피명, 재료, 조리 과정 검색을 구현
- 비회원 사용자를 위해 UUID 쿠키 기반 좋아요/북마크 기능을 제공하고, 테스트 기반 REST Docs 문서화를 적용

## ✅ 프로젝트 개요

![레시피 프로젝트 도식도](./docs/overview.png)

## ✅ 주요 기능

| 기능 | 설명 |
| --- | --- |
| 기본 레시피 조회 | 단일 레시피 조회, 전체 레시피 페이징 조회, 전체 개수 조회 |
| 통합 검색 | 레시피명, 재료, 조리 과정 기준의 MongoDB Atlas Search 검색 |
| 자동완성 | 레시피명과 재료명에 대한 실시간 자동완성 |
| 비회원 상태 관리 | UUID 쿠키 기반 좋아요/북마크 토글 및 목록 조회 |
| API 문서화 | Spring REST Docs와 Asciidoc 기반 문서 자동 생성 |
| 테스트 독립성 | Embedded MongoDB와 H2를 활용한 외부 인프라 독립 테스트 |

## ✅ 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.5.6 |
| Database | MongoDB Atlas Search, MySQL |
| Cache | Caffeine, Redis |
| Persistence | Spring Data MongoDB, Spring Data JPA |
| Documentation | Spring REST Docs, Asciidoc |
| Test | JUnit 5, MockMvc, Embedded MongoDB, H2 |
| Build & Deploy | Gradle, Docker |

## ✅ 실행 방법

### 테스트 실행

테스트 환경은 `test` 프로필을 사용하며, MongoDB는 Embedded MongoDB로, RDB는 H2로 대체됩니다.

```bash
./gradlew test
```

REST Docs 문서까지 생성하려면 다음 명령을 실행합니다.

```bash
./gradlew asciidoctor
```

생성된 문서는 빌드 결과물의 `static/docs` 또는 Asciidoctor 출력 디렉터리에서 확인할 수 있습니다.

### 애플리케이션 실행

실제 애플리케이션 실행에는 MongoDB Atlas, MySQL, Redis 설정이 필요합니다. 현재 기본 프로필은 `local`입니다.

필수 설정 예시는 다음과 같습니다.

```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://...
      database: recipe
    redis:
      host: localhost
      port: 6379
  datasource:
    url: jdbc:mysql://localhost:3306/recipe
    username: root
    password: password

mongo:
  collectionName: recipes

custom:
  guest:
    cookie:
      name: GUEST_UUID
```

로컬에서 실행합니다.

```bash
./gradlew bootRun
```

Docker 이미지를 빌드해 실행할 수도 있습니다.

```bash
docker compose up --build
```

단, 현재 `docker-compose.yml`은 외부 Docker 네트워크를 사용하므로 실행 환경에 맞게 네트워크와 인프라 설정을 먼저 준비해야 합니다.

## API 요약

기본 경로는 `/seo`입니다.

### 기본 조회

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/basic/recipe?objectId={id}` | 단일 레시피 조회 |
| GET | `/basic/recipes?page=0&pageSize=10&objectId=` | 레시피 목록 페이징 조회 |
| GET | `/basic/recipescount` | 전체 레시피 개수 조회 |

### 검색

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/search/ingredient?page=0&pageSize=10&term=토마토` | 재료명 기반 검색 |
| GET | `/search/recipename?page=0&pageSize=10&term=김치찌개` | 레시피명 기반 검색 |
| GET | `/search/cookingorder?page=0&pageSize=10&term=볶기` | 조리 과정 기반 검색 |

### 자동완성

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/autocomplete/ingredient?term=토` | 재료명 자동완성 |
| GET | `/autocomplete/recipename?term=김` | 레시피명 자동완성 |

### 비회원 좋아요/북마크

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/recipe/like` | 레시피 좋아요 토글 |
| POST | `/recipe/bookmark` | 레시피 북마크 토글 |
| GET | `/recipe/status?recipeId={id1}&recipeId={id2}` | 여러 레시피의 좋아요/북마크 상태 조회 |
| GET | `/recipe/likes` | 비회원 좋아요 목록 조회 |
| GET | `/recipe/bookmark` | 비회원 북마크 목록 조회 |

`/seo/**` 요청에는 인터셉터가 적용되어 `GUEST_UUID` 쿠키가 없으면 UUID 기반 쿠키를 발급합니다.

## 기술 하이라이트

### 한국어 검색 품질 개선

MongoDB Atlas Search의 `lucene.nori` 분석기를 사용해 한국어 복합 명사와 조사/어미 분리 문제를 완화했습니다. 일반적인 공백 기반 검색보다 레시피명, 재료명, 조리 과정 검색에 적합한 토큰화를 적용했습니다.

검색 인덱스의 상세 설정은 [mongoIndex.md](./mongoIndex.md)에 정리되어 있습니다.

### EdgeGram 기반 자동완성

자동완성 인덱스에 `edgeGram` 토큰화를 적용해 사용자가 한 글자만 입력해도 레시피명과 재료명을 제안할 수 있도록 구성했습니다. 레시피명은 최대 15글자, 재료명은 최대 10글자 기준으로 접두사 매칭을 수행합니다.

### Caffeine + Redis 이중 캐싱

자동완성 API는 호출 빈도가 높기 때문에 Controller 단에는 Caffeine 로컬 캐시를, Service 단에는 Redis 캐시를 적용했습니다.

- L1 Cache: Caffeine, 1분 TTL, 최대 100개
- L2 Cache: Redis, 10분 TTL
- 기존 측정 기준 MongoDB Atlas Search 대비 응답 시간 약 77% 개선

### 비회원 개인화

로그인 없이도 좋아요와 북마크를 사용할 수 있도록 `GUEST_UUID` 쿠키를 발급합니다. 이후 요청에서는 쿠키 값을 기준으로 MySQL에 저장된 좋아요/북마크 상태를 조회하거나 토글합니다.

### 테스트와 문서의 일관성

Spring REST Docs를 사용해 테스트가 통과한 API 스펙만 문서로 생성합니다. 테스트 환경에서는 Embedded MongoDB와 H2를 사용하므로 외부 MongoDB Atlas, MySQL에 의존하지 않고 핵심 로직을 검증할 수 있습니다.

## 프로젝트 구조

```text
src/main/java/com/HeoJin/RecipeSearchEngine
├── IntegratedSearch   # Atlas Search 기반 통합 검색
├── autocomplete       # 레시피명/재료명 자동완성
├── basicSearch        # 기본 레시피 조회
├── guest              # 비회원 좋아요/북마크
└── global             # 공통 설정, 예외, 엔티티
```

## 참고 문서

- [MongoDB Atlas Search Index Settings](./mongoIndex.md)
- [REST Docs Asciidoc 진입점](./src/docs/asciidoc/index.adoc)
