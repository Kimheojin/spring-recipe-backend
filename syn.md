### 동의어 관련

https://www.mongodb.com/ko-kr/docs/atlas/atlas-search/synonyms/?deployment-type=atlas&interface=driver&language=nodejs

- DB 단 변경만 필요
- batch job 설정
- collectionName: recipe_synonyms
- 원래 사용하던 meta 데이터 영향 가는 지 확인하기
  - search meta, hishlights mata?
- 데이터셋만 만들면 3분만에 할듯
- 

---
#### 방법 1

- 운영중 검색어 따로 mongo 에 저장
- 새벽마다 배치돌리기

#### 방법 2

- 전체 데이터 에서 주요도 순으로 뽑고
- batch 돌리기

---

- 둘 다 하는 게 best
- 1만 먼저 해보기