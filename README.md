# WebFlux SFTP upload and download / WebFlux SFTP 업로드·다운로드

[ENG](#eng) | [KR](#kr)

## ENG

### Project structure

```text
pom.xml
src/main/java/io/github/parkwoong/sftp/Application.java
src/main/java/io/github/parkwoong/sftp/sftp/config/JschSessionHolder.java
src/main/java/io/github/parkwoong/sftp/sftp/config/SftpApiRouterConfig.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/download/DownloadRequest.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/download/DownloadResponse.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/upload/UploadRequest.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/upload/UploadResponse.java
src/main/java/io/github/parkwoong/sftp/sftp/exception/InvalidRequestData.java
src/main/java/io/github/parkwoong/sftp/sftp/handler/DownloadHandler.java
src/main/java/io/github/parkwoong/sftp/sftp/handler/UploadHandler.java
src/main/java/io/github/parkwoong/sftp/sftp/properties/SftpProperties.java
src/main/java/io/github/parkwoong/sftp/sftp/util/SftpPath.java
src/main/java/io/github/parkwoong/sftp/sftp/util/StringUtils.java
src/main/java/io/github/parkwoong/sftp/webflux/config/WebfluxConfig.java
src/main/java/io/github/parkwoong/sftp/webflux/properties/WebfluxProperties.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/sftp/SftpPathTest.java
```

### Code explanation

`SftpApiRouterConfig` maps POST `/sftp/upload` and `/sftp/download` to the original `UploadHandler` and `DownloadHandler` flow. The handlers decode or encode Base64 and transfer data with JSch. `JschSessionHolder` creates a session for a request, verifies the server through known_hosts, and closes the channel and session. `SftpPath` accepts a file name and joins it with the configured remote directory. The handlers move blocking SFTP work to a boundedElastic worker. `WebfluxConfig` limits the request body to 1 MiB.

### Run and test

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

Use JDK 8 and Maven 3.6.3+. Set `SFTP_HOST`, `SFTP_USER`, `SFTP_PRIVATE_KEY_PATH`, `SFTP_KNOWN_HOSTS_PATH`, and `SFTP_REMOTE_DIR` in your environment before making a transfer. Do not put key files in this repository. `mvn clean verify` checks filename confinement without an SFTP server. `mvn spring-boot:run` starts the local API on 127.0.0.1:8080. A live upload/download requires your own test SFTP server and valid credentials.

## KR

### 프로젝트 구조

```text
pom.xml
src/main/java/io/github/parkwoong/sftp/Application.java
src/main/java/io/github/parkwoong/sftp/sftp/config/JschSessionHolder.java
src/main/java/io/github/parkwoong/sftp/sftp/config/SftpApiRouterConfig.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/download/DownloadRequest.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/download/DownloadResponse.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/upload/UploadRequest.java
src/main/java/io/github/parkwoong/sftp/sftp/dto/upload/UploadResponse.java
src/main/java/io/github/parkwoong/sftp/sftp/exception/InvalidRequestData.java
src/main/java/io/github/parkwoong/sftp/sftp/handler/DownloadHandler.java
src/main/java/io/github/parkwoong/sftp/sftp/handler/UploadHandler.java
src/main/java/io/github/parkwoong/sftp/sftp/properties/SftpProperties.java
src/main/java/io/github/parkwoong/sftp/sftp/util/SftpPath.java
src/main/java/io/github/parkwoong/sftp/sftp/util/StringUtils.java
src/main/java/io/github/parkwoong/sftp/webflux/config/WebfluxConfig.java
src/main/java/io/github/parkwoong/sftp/webflux/properties/WebfluxProperties.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/sftp/SftpPathTest.java
```

### 코드 설명

`SftpApiRouterConfig`가 POST `/sftp/upload`, `/sftp/download`를 `UploadHandler`, `DownloadHandler`에 연결합니다. 핸들러는 Base64 변환과 JSch 파일 전송을 처리합니다. `JschSessionHolder`는 요청마다 세션을 만들고 known_hosts로 서버를 확인한 뒤 채널과 세션을 닫습니다. `SftpPath`는 파일명을 검사하고 서버가 설정한 원격 디렉터리와 결합합니다. 블로킹 SFTP 작업은 boundedElastic 작업 스레드에서 실행합니다. `WebfluxConfig`는 요청 본문을 1 MiB로 제한합니다.

### 실행과 테스트

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

JDK 8과 Maven 3.6.3 이상을 사용합니다. 실제 전송 전 `SFTP_HOST`, `SFTP_USER`, `SFTP_PRIVATE_KEY_PATH`, `SFTP_KNOWN_HOSTS_PATH`, `SFTP_REMOTE_DIR` 환경변수를 설정하세요. 키 파일은 저장소에 넣지 않습니다. `mvn clean verify`는 SFTP 서버 없이 파일명 검증을 테스트합니다. `mvn spring-boot:run`은 로컬 API를 127.0.0.1:8080에서 실행합니다. 실제 업로드·다운로드는 별도 테스트 SFTP 서버와 유효한 인증 정보가 필요합니다.
