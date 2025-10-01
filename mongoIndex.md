## recipeNaame_autocomoplete_kr

{
    "mappings": {
        "dynamic": false,
        "fields": {
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

## ingredient_autocomplete_kr

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
            ]
        }
    }
}

## recipe_full_search_kr

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

### autocomplete 은 접두사 매핑하는 느낌, string 은 단어 단위
- 근데 토큰화 되는 거 생각해야 됨
  -  nori 로 예를들어 "해물된장찌개" -> 해물, 된장, 찌개로 분리되는 느낌
- token 별로 생각하기
- autocomplete 추가

-> autocomplete 으로 단어 매핑하고, 그 단어로 type String 인거 매핑한다고 생각하자

// 1단계: Autocomplete (입력 중)
사용자 입력 "된"
↓
Autocomplete 검색
↓
토큰의 접두사 매칭 → "된장" 토큰 발견
↓
자동완성 제안: "된장찌개", "된장국"

// 2단계: String (실제 검색)
사용자 선택/엔터 "된장"
↓
String 검색
↓
완전한 토큰 매칭 → "된장" 토큰 발견
↓
검색 결과: 모든 "된장" 포함 문서

## token order 관련해서도 정리하기(중요)
- 토큰화 방식 관련해서도 한번 정리하면 좋을듯

- string 타입의 경우 gram 형태 지정 X
- autocomplete 일 경우에만 지정