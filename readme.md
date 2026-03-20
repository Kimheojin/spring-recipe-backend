# Spring-Mongo Atlas Search Engine

- Spring + Mongo Atlas Search 기반 구현
- 서비스 링크 : [Recipe Finder](https://recipefinder-hj.vercel.app/)

## 프로젝트 배포 구조 요약

![레시피 프로젝트 도식도](https://res.cloudinary.com/dtrxriyea/image/upload/v1774006165/etc/pwkwr9lalmwrzrrnzdx3.avif)

## 구현 내용

### Apache Nori 형태소 분석기를 이용한 한글 형태소 분석 인덱스 구성
#### 구현 목적

- 일반 형태소 분석기 사용 시 검색 품질 저하 문제 
- 
## 구현 내용

### Apache Nori를 이용한 한글 형태소 분석 인덱스 구성
#### 구현 목적
- 한국어의 특성인 복합 명사("해물된장찌개")와 조사/어미 분리 필요성 대두
- 일반 공백 분석기의 검색 품질 한계를 극복하고 정교한 키워드 매칭 구현
#### 구현 내용
- MongoDB Atlas Search의 `lucene.nori` 분석기를 사용하여 한글 형태소 단위 인덱싱
- `recipeName`, `ingredientList`, `instruction` 등 주요 검색 필드에 Nori 분석기 적용
#### 구현 결과
- "된장" 검색 시 "해물된장찌개"가 포함된 레시피를 정확히 탐색 가능
- 불용어 제거 및 어근 추출을 통해 검색 의도에 부합하는 높은 정확도의 결과 반환

### EdgeGram 기반 고성능 자동 완성(Autocomplete) 기능 구현
#### 구현 목적
- 사용자가 한 글자만 입력해도 실시간으로 관련 레시피와 재료를 제안하는 빠른 UX 제공
- 오타나 미완성 단어에 대해서도 유연한 접두사 매칭 수행
#### 구현 내용
- `edgeGram` 토큰화 방식 적용 (`minGrams: 1`, `maxGrams: 15`)
- Atlas Search의 `autocomplete` 타입을 활용하여 실시간 인덱스 조회 최적화
#### 구현 결과
- 입력 즉시 재료명 및 레시피명이 제안되어 검색 편의성 극대화
- 한국어 음절 단위의 실시간 매칭으로 자연스러운 자동 완성 경험 제공

### Caffeine & Redis를 활용한 이중 캐싱 아키텍처 구축
#### 구현 목적
- 타이핑마다 발생하는 자동 완성 API의 높은 트래픽 부하 분산
- 동일/인기 검색어에 대한 응답 속도를 극대화하여 서버 리소스 및 DB 비용 절감
#### 구현 내용
- **L1 Cache (Local)**: `Caffeine`을 사용하여 Controller 단에서 1분간 초고속 응답 처리
- **L2 Cache (Global)**: `Redis`를 사용하여 Service 단에서 10분간 클러스터 전체 데이터 공유
#### 구현 결과
- 반복 요청 시 DB I/O 없이 10ms 이내의 응답 속도 달성
- MongoDB Atlas Search 호출 횟수를 90% 이상 절감하여 안정적인 서비스 운영 가능

### Atlas Search Aggregation Pipeline 기반의 복합 검색 엔진 구축
#### 구현 목적
- 단순 쿼리를 넘어 대량의 데이터에서 효율적인 검색, 페이징, 카운트를 동시에 수행
- 검색어와 가장 연관성 높은 레시피를 상단에 배치하는 정교한 쿼리 로직 필요
#### 구현 내용
- `$search` 및 `$searchMeta` 스테이지를 활용하여 검색 결과와 총 검색 건수(totalCount)를 동시에 처리
- `MongoTemplate`을 이용한 동적 Aggregation Pipeline 구성 및 커스텀 스코어링 적용
#### 구현 결과
- 수만 건의 레시피 데이터에서도 0.1초 내외의 빠른 페이징 검색 성능 확보
- 재료, 이름, 조리법 등 다양한 조건이 조합된 복합 검색 기능 구현

### Cookie 및 Interceptor를 활용한 비회원 레시피 관리 시스템
#### 구현 목적
- 로그인 없이도 레시피 북마크 및 '좋아요' 기능을 제공하여 사용자 진입 장벽 완화
- 서비스 이탈을 방지하고 개인화된 사용자 경험(UX) 초기 단계 구축
#### 구현 내용
- `HandlerInterceptor`를 통해 HTTP Cookie에서 Guest ID(UUID)를 자동 추출 및 발급
- 추출된 Guest ID를 기반으로 MongoDB에 북마크 및 좋아요 정보를 영속적으로 저장
#### 구현 결과
- 별도의 회원 가입 절차 없이도 사용자의 관심 레시피가 브라우저 세션에 유지
- 사용자별 맞춤형 레시피 탐색 경험 제공

### Spring RestDocs 기반의 신뢰성 있는 API 문서 자동화
#### 구현 목적
- 코드와 문서 간의 불일치 문제를 해결하고 신뢰할 수 있는 API 가이드 제공
- 테스트 통과 시에만 문서가 생성되도록 하여 API의 안정성 보장
#### 구현 내용
- JUnit5 및 MockMvc 테스트와 연동하여 실제 API 호출 결과로 Snippet 자동 생성
- Asciidoc 템플릿을 사용하여 정적인 HTML API 문서 빌드 및 배포
#### 구현 결과
- 런타임 영향 없이 100% 테스트 기반의 정확한 API 명세서 확보
- 프론트엔드 협업 시 명확한 인터페이스 정의로 개발 생산성 향상

 