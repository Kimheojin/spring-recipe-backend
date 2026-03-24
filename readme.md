# Spring-Mongo Atlas Search Engine

- Spring + Mongo Atlas Search 기반 구현
- 서비스 링크 : [Recipe Finder](https://recipefinder-hj.vercel.app/)

## 프로젝트 배포 구조 요약

![레시피 프로젝트 도식도](https://res.cloudinary.com/dtrxriyea/image/upload/v1774006165/etc/pwkwr9lalmwrzrrnzdx3.avif)

## 구현 내용

### Apache Nori 형태소 분석기를 이용한 한글 형태소 분석 인덱스 구성

#### 구현 목적

- 한국어의 특성인 복합 명사 (ex 갓김치) 와 조사/어미 분리 필요성 대두
- 일반적인 공백 분석기를 통한 한국어 검색 품질 한계 극복 필요 

#### 구현 내용

- MongoDB Atlas Search의 `lucene.nori` 분석기를 사용하여 한글 형태소 단위 인덱싱

```json
{
  "mappings": {
    "dynamic": false,
    "fields": {
      "recipeName": {
        "type": "string",
        "analyzer": "lucene.nori" 
      },
```

#### 구현 결과

- \$search 연산자를 통한 한글 기반 토큰화 확인

```json
{
  "_id": "68caa46cc2e635f0fa552434",
  "recipeName": "갓김치의 아삭함이 살아있는 갓돈찌개",
  "highlights": [
    {
      "score": 2.747103452682495,
      "path": "recipeName",
      "texts": [
        {
          "value": "갓",
          "type": "text"
        },
        {
          "value": "김치",
          "type": "hit"
        },
        {
          "value": "의 아삭함이 살아있는 갓돈",
          "type": "text"
        },
        {
          "value": "찌개",
          "type": "hit"
        }
      ]
    }
  ]
}
```

### EdgeGram 기반 자동 완성 기능 구현

#### 구현 목적

- 사용자가 한 글자만 입력해도 실시간으로 관련 레시피와 재료를 제안하는 빠른 UX 제공 목적
- 오타나 미완성 단어에 대해서도 유연한 접두사 매칭 수행

#### 구현 내용

- `edgeGram` 토큰화 방식 적용 (`minGrams: 1`, `maxGrams: 10`)
- Atlas Search의 `autocomplete` 타입을 활용, 실시간 인덱스 조회 최적화

```json 
{
  "ingredientList": [ // 인덱스 중 일부 
    {
      "type": "autocomplete",
      "analyzer": "lucene.nori",
      "tokenizer": "edge_ngram", 
      "min_gram": 1,             
      "max_gram": 10
    }
  ]
}
```

#### 구현 결과
 
- 입력 후 재료명 및 레시피명이 제안, 검색 편의성 개선
- 한국어 음절 단위의 매칭으로 자연스러운 자동 완성 경험 제공 예상

### Caffeine & Redis를 활용한 이중 캐싱 아키텍처 구축

#### 구현 목적

- 타이핑마다 발생하는 자동 완성 API의 높은 트래픽 부하 분산
- 동일/인기 검색어에 대한 응답 속도를 극대화하여 서버 리소스 및 DB 비용 절감

#### 구현 내용

- **L1 Cache (Local)**: `Caffeine`을 사용하여 Controller 단에서 1분간 응답 처리

```java
@RestController
@RequestMapping("/seo")
public class AutocompleteController {
    @GetMapping("/autocomplete/ingredient")
    @Cacheable(cacheNames = "autocomplete:controller:ingredient", key = "#term", cacheManager = "caffeineCacheManager")
    public ResponseEntity<ListAutocompleteIngredientDto> IngredientAutocomplete(@RequestParam("term") String term) {
        return ResponseEntity.ok(autocompleteService.getIngredientAutocomplete(term));
    }
}
```
- **L2 Cache (Global)**: `Redis`를 사용하여 Service 단에서 10분간 클러스터 전체 데이터 공유

```java
@Cacheable(cacheNames = "autocomplete:service:ingredient", key = "#term", cacheManager = "redisCacheManager")
public ListAutocompleteIngredientDto getIngredientAutocomplete(String term) {
    return new ListAutocompleteIngredientDto(autocompleteRepository.getResultAboutIngredient(term));
}
```

#### 구현 결과

- Mongo Atlas Search 대비 77% 향상
  - Redis를 통한 49.8% 단축 (856ms -> 429ms)
  - Caffeine을 통한 Redis 대비 54.7% 단축 (429ms -> 194ms)
- MongoDB Atlas Search 호출 횟수 감소로 인한 네트워크 오버헤드 제거 

### Atlas Search Aggregation Pipeline 기반 복합 검색 엔진 구축

#### 구현 목표

- 단순 regex 대비 한글 형태소 기반 고도화된 레시피 검색 기능 구현 목적
- 검색어와 가장 연관성 높은 레시피를 상단에 배치하는 정교한 쿼리 로직 필요

#### 구현 내용

- 검색 결과와 메타데이터 조회를 독립적인 파이프라인으로 구성, 대용량 데이터셋에서 페이징 로직 구현

```java
FacetOperation facetOperation = Aggregation.facet(
    Aggregation.skip((long) pageable.getOffset()),
    Aggregation.limit(pageable.getPageSize())
).as("paginatedResults").and(Aggregation.count().as("count")).as("totalCount");
```

- 검색 대상(레시피명, 재료, 조리법 등)에 따라 런타임에 쿼리를 동적으로 생성하는 Aggregation Pipeline 설계 및 확장성 확보

```java
List<Document> mustList = new ArrayList<>();
mustList.add(new Document("text", new Document("query", term)
    .append("path", Arrays.asList("recipeName", "ingredientList", "cookingOrderList.description"))));
Document searchStage = new Document("$search", new Document("compound", new Document("must", mustList)));
```

#### 구현 결과

- Full Scan 방식인 Regex 대비, 역색인을 활용한 대용량 데이터 검색 효율 최적화
- 재료, 이름, 조리법 등 다양한 조건이 조합된 복합 검색 기능 제공


### Cookie 및 Interceptor를 활용한 비회원 레시피 관리 시스템

#### 구현 목적
- 로그인 없는 북마크/좋아요 제공으로 사용자 접근성 향상
- 개인화 UX 구축을 통한 서비스 이탈 방지

#### 구현 내용

- `HandlerInterceptor` 기반 Guest ID(UUID) 쿠키 자동 발급 (30일간 유지)

```java
if (getGuestIdFromCookie(request) == null) {
    String guestId = UUID.randomUUID().toString();
    ResponseCookie cookie = ResponseCookie.from("guestId", guestId)
            .path("/").maxAge(2592000).sameSite("None").secure(true).build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
}
```

- 추출된 Guest ID 기반 **MySQL 영속화** (JPA를 활용한 토글 방식)

```java
public void toggleBookmark(String guestUuid, String recipeId) {
    Guest guest = guestRepository.findByGuestUuid(guestUuid)
            .orElseGet(() -> guestRepository.save(new Guest(guestUuid)));
    guestRecipeBookmarkRepository.save(GuestRecipeBookmark.builder()
            .guestId(guest.getId()).recipeId(recipeId).build());
}
```

#### 구현 결과

- 별도의 회원 가입 절차 없이도 사용자의 관심 레시피가 쿠키를 통해 30일간 브라우저에 유지
- 사용자별 맞춤형 레시피 탐색 경험 제공

### 로컬 환경 기반의 테스트 독립성 확보

#### 구현 목적
- 외부 데이터베이스(Mongo Atlas, MySQL) 의존성 없이 로컬 및 CI 환경에서 독립적인 테스트 수행
- 테스트 실행 시마다 스키마 및 데이터를 초기화하여 테스트 간 격리성 보장 및 신뢰도 향상 복적

#### 구현 내용

- `de.flapdoodle.embed.mongo`를 활용하여 테스트 런타임에 인메모리 NoSQL 환경 구축

```yaml
# application-test.yml 설정 예시
spring:
  data:
    mongodb:
      port: 0  # 가용 포트 자동 할당
de:
  flapdoodle:
    mongodb:
      embedded:
        version: 6.0.5
```

- MySQL(JPA) 환경을 대체하는 인메모리 RDB를 사용하여 비회원 상태 관리 로직(Guest/Bookmark) 검증

#### 구현 결과

- 외부 인프라 의존 없이 프로젝트 클론 후 즉시 `gradle test` 및 전체 빌드 가능
- 운영 데이터 오염 및 네트워크 오버헤드 없는 독립적인 테스트 실행 환경 구축

### Spring RestDocs 기반 API 문서 자동화 구현 

#### 구현 목적

- 코드와 문서 간의 불일치 문제를 해결하고 신뢰할 수 있는 API 가이드 제공
- 테스트 통과 시에만 문서가 생성되도록 하여 API의 안정성 보장

#### 구현 내용
- JUnit5 + MockMvc 연동을 통한 테스트 기반 API Snippet 자동 생성

```java
mockMvc.perform(get("/seo/autocomplete/ingredient").queryParam("term", "토마토"))
    .andExpect(status().isOk())
    .andDo(document("autocomplete-ingredient",
        queryParameters(parameterWithName("term").description("자동완성 검색어"))
    ));
```

- Asciidoc 템플릿을 활용한 정적 HTML API 문서 빌드 자동화

```adoc
==== HTTP 요청
include::{snippets}/{operation}/http-request.adoc[]
==== HTTP 응답
include::{snippets}/{operation}/http-response.adoc[]
```

#### 구현 결과
- 런타임 영향 없는 100% 테스트 기반의 정확한 API 명세서 구현

## Tech Stack
- **Framework**: Spring Boot 3.5.6 / Java 17
- **Database**: 
  - **MongoDB Atlas Search** (레시피 데이터 및 고도화된 검색 엔진)
  - **MySQL** (사용자 북마크/좋아요 등 상태 관리)
- **Caching**: Multi-level Caching (Caffeine L1 & Redis L2)
- **Documentation**: Spring RestDocs (Asciidoc)
- **Build & DevOps**: Gradle / Docker
- **Testing**: JUnit5 / MockMvc / Embedded Mongo

 