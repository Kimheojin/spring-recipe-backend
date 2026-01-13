# MongoDB Atlas Search Index Settings

- 바뀌면 수정하기

## 1. recipe_full_search_kr (통합 검색용)

- 이거 사실 2번이랑 통합해서 한개로 처리해도 상관 없을 듯
- 일단 남겨두기

```json
{
  "mappings": {
    "dynamic": false,
    "fields": {
      "recipeName": {
        "type": "string",
        "analyzer": "lucene.nori"
      },
      "ingredientList": {
        "type": "string",
        "analyzer": "lucene.nori"
      },
      "cookingOrderList": {
        "type": "document",
        "fields": {
          "instruction": {
            "type": "string",
            "analyzer": "lucene.nori"
          }
        }
      }
    }
  }
}
```

## 2. autocomplete_kr (자동 완성용)

```json
{
  "mappings": {
    "dynamic": false,
    "fields": {
      "ingredientList": [
        {
          "type": "autocomplete",
          "analyzer": "lucene.nori",
          "tokenization": "edgeGram",
          "minGrams": 1,
          "maxGrams": 10
        },
        {
          "type": "string",
          "analyzer": "lucene.nori"
        }
      ],
      "recipeName": [
        {
          "type": "autocomplete",
          "analyzer": "lucene.nori",
          "tokenization": "edgeGram",
          "minGrams": 1,
          "maxGrams": 15
        },
        {
          "type": "string",
          "analyzer": "lucene.nori"
        }
      ]
    }
  }
}
```

---

### 검색 원리 및 전략

#### Autocomplete, String
- **Autocomplete (edgeGram)**
  - 사용자 입력 중인 단어의 앞부분(접두사)을 매칭
  - (예: "된장" 입력 시 "된장찌개" 매칭)
- **String (lucene.nori)**
  - 형태소 분석을 통해 분리된 단어 단위로 매칭합니다.
  - (예: "해물된장찌개" -> "해물", "된장", "찌개")
- Autocomplete 필드로 단어 후보를 매핑하고, 그 단어의 완성본을 String 필드로 매핑하여 정확도를 높입니다.

#### 검색 단계 (Workflow)
1. **1단계: 자동 완성 (입력 중)**
   - 사용자 입력: "된"
   - autocomplete 필드 검색 -> 토큰의 접두사 매칭 -> "된장" 토큰 발견
   - 결과 제안: "된장찌개", "된장국" 등
2. **2단계: 전문 검색 (확정 후)**
   - 사용자 선택/엔터: "된장"
   - string 필드 검색 -> 완전한 토큰 매칭 -> "된장" 토큰 발견
   - 결과 반환: "된장"이 포함된 모든 레시피 문서

#### 기타 설정 참고
- string 타입
  - 별도의 Gram 형태를 지정하지 않음
  - 사실 이거로 지정하면 분석기(nori)가 본체인 듯 
- autocomplete 타입
  - 반드시 minGrams, maxGrams 설정을 포함 해야함
  - 사실 안해도 디폴트 값
- tokenOrder
  - ㅁㄴㅇㄹ
