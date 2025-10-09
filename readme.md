# Recipe Search Engine

- MVP 버전
- MongoDB를 활용한 레시피 검색 엔진
- 한국어 전용 분석기(Nori)를 사용하여 효율적인 검색 및 자동완성 기능을 제공

## MongoDB 인덱스 구성

- 무료 티어 제한으로 인해 인덱스는 최대 3개까지 사용 가능
  - 변동 가능성 존재 

- **자동완성 인덱스**: type = autocomplete
- **통합검색 인덱스**: type = String

- 분석기는 한국어 전용 Lucene Nori를 사용
- 본 프로젝트 루트 폴더에 mongoIndex.md 에 참고용 존재

## API 문서

Spring REST Docs를 통해 API 문서가 자동 생성

## 배포 환경

- 자체 온프레미스 서버 + Vercel (프론트)