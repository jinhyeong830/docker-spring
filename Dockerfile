#./gradlew build 로 jar 파일을 먼저 만들어야 해요.
# 이미지 정의
FROM openjdk:17-alpine

# 작업공강
WORKDIR /app

# build 된 파일을 app.jar 로 복사
COPY build/libs/*.jar app.jar

# 포트 열기
EXPOSE 8080

# 이미지가 실행될 때 앱을 구동할 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]