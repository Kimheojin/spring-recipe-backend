# 졸리다
# 멀티 스테이지로 할듯
# 컴파일 전용 이미지는 없나
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew build -x test ## 테스트 제외
# 빌드 자주 안할꺼라 캐시는 안할듯

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 3777
ENTRYPOINT ["java", "-jar", "app.jar"]