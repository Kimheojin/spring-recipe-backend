# Spring-Mongo Atlas Search Engine

- Spring + Mongo Atlas Search 기반 구현
- 서비스 링크 : [Recipe Finder](https://recipefinder-hj.vercel.app/)

## 프로젝트 배포 구조 요약

![레시피 프로젝트 도식도](https://res.cloudinary.com/dtrxriyea/image/upload/v1774006165/etc/pwkwr9lalmwrzrrnzdx3.avif)

## 구현 내용

### Apache Nori 형태소 분석기를 이용한 한글 형태소 분석 인덱스 구성

#### 구현 목적

- 한국어의 특성인 복합 명사 (ex 갓김치) 와 조사/어미 분리 필요성 대두
- 일반적인 공백 분석기를 통한 한국어 검색 품질 한계를 극복 필요 

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

- \$search 연산자를 통한 토큰화 확인

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

### EdgeGram 기반 자동 완성(Autocomplete) 기능 구현

#### 구현 목적

- 사용자가 한 글자만 입력해도 실시간으로 관련 레시피와 재료를 제안하는 빠른 UX 제공 목적
- 오타나 미완성 단어에 대해서도 유연한 접두사 매칭 수행

#### 구현 내용

- `edgeGram` 토큰화 방식 적용 (`minGrams: 1`, `maxGrams: 15`)
- Atlas Search의 `autocomplete` 타입을 활용하여 실시간 인덱스 조회 최적화

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
 
- 입력 후 재료명 및 레시피명이 제안되어 검색 편의성 개선
- 한국어 음절 단위의 실시간 매칭으로 자연스러운 자동 완성 경험 제공 예상

### Caffeine & Redis를 활용한 이중 캐싱 아키텍처 구축

#### 구현 목적

- 타이핑마다 발생하는 자동 완성 API의 높은 트래픽 부하 분산
- 동일/인기 검색어에 대한 응답 속도를 극대화하여 서버 리소스 및 DB 비용 절감

#### 구현 내용

- **L1 Cache (Local)**: `Caffeine`을 사용하여 Controller 단에서 1분간 응답 처리

```java
@GetMapping("/autocomplete/ingredient")
@Cacheable(cacheNames = "autocomplete:controller:ingredient", key = "#term", cacheManager = "caffeineCacheManager")
public ResponseEntity<ListAutocompleteIngredientDto> IngredientAutocomplete(@RequestParam("term") String term) {
    return ResponseEntity.ok(autocompleteService.getIngredientAutocomplete(term));
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
  - Caffeine을 통한 추가 54.7% 단축 (429ms -> 194ms)
- MongoDB Atlas Search 호출 횟수 감소로 인한 네트워크 오버헤드 제거 

### Atlas Search Aggregation Pipeline 기반의 복합 검색 엔진 구축

#### 구현 목표

- 단순 regex 대비 한글 형태소 기반의 고도화된 레시피 검색 기능 구현
- 검색어와 가장 연관성 높은 레시피를 상단에 배치하는 정교한 쿼리 로직 필요

#### 구현 내용
- 검색 결과와 메타데이터 조회를 독립적인 파이프라인으로 구성, 대용량 데이터셋에서 페이징 로직 구현

- 검색 대상(레시피명, 재료, 조리법 등)에 따라 런타임에 쿼리를 동적으로 생성하는 Aggregation Pipeline 설계 및 확장성 확보

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

- 런타임 영향 없는 테스트 기반의 정확한 API 명세서 구현

## 🛠 Tech Stack
- **Framework**: Spring Boot 3.5.6 / Java 17
- **Database**: MongoDB Atlas Search / MySQL
- **Caching**: Multi-level Caching (Caffeine L1 & Redis L2)
- **Documentation**: Spring RestDocs (Asciidoc)
- **Build & DevOps**: Gradle / Docker
- **Testing**: JUnit5 / MockMvc / Embedded Mongo

 