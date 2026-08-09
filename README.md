# Recipe Finder

- Spring Boot와 MongoDB Atlas Search를 기반으로 만든 개인 레시피 검색 백엔드 프로젝트
- 크롤링한 레시피 데이터를 MongoDB에 저장하고, MongoDB Atlas Search를 활용해 레시피명, 재료, 조리 과정 검색을 구현
- 비회원 사용자를 위해 UUID 쿠키 기반 좋아요/북마크 기능을 제공하고, 테스트 기반 REST Docs 문서화를 적용

### ✅ 기술 스택

**Backend / DB**

- Java 17, Spring Boot 3.5.6, Spring Data JPA, MySQL, MongoDB Atlas Search

**Cache / Infra / DevOps**

- Redis, Caffeine, Spring Cache, Docker, Docker Compose

### ✅ Architecture

![recipe architecture](https://res.cloudinary.com/dtrxriyea/image/upload/v1786173863/recipe-archi_poo30q.png)


### ✅ 배포 환경


- [온프레미스 서버 스펙](https://github.com/heojinn/spring-recipe-backend/wiki/2.-OnPremises-Server-Specifications)

### ✅ 주요 기능

▶ 레시피 조회

1. 단일 레시피 상세 및 전체 레시피 조회
2. MongoDB ObjectId 기반 커서형 페이징 적용

▶ [더 보기](https://github.com/heojinn/spring-recipe-backend/wiki/3.-Business-Rule)

### ✅ 주요 기술 구현 정리

#### [1. Apache Nori for Korean Search](https://github.com/heojinn/spring-recipe-backend/wiki/4.-Apache-Nori-for-Korean-Search)

#### [2. EdgeGram Autocomplete](https://github.com/heojinn/spring-recipe-backend/wiki/5.-EdgeGram-Autocomplete)

#### [3. Multi‐Level Caching with Caffeine & Redis](https://github.com/heojinn/spring-recipe-backend/wiki/6.-Multi%E2%80%90Level-Caching-with-Caffeine-&-Redis)

#### [4. Atlas Search Aggregation Pipeline](https://github.com/heojinn/spring-recipe-backend/wiki/7.-Atlas-Search-Aggregation-Pipeline)

#### [5. Guest Recipe Management with Cookie & Interceptor](https://github.com/heojinn/spring-recipe-backend/wiki/8.-Guest-Recipe-Management-with-Cookie-&-Interceptor)

### ✅ etc