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
src/main/resources/application.yml
src/test/java/io/github/parkwoong/sftp/SftpPathTest.java
```

### Code explanation

`SftpApiRouterConfig` maps POST `/sftp/upload` and `/sftp/download` to the original
`UploadHandler` and `DownloadHandler` flow. The handlers decode or encode Base64 and transfer
data with JSch. `JschSessionHolder` creates a session for a request, verifies the server through
known_hosts, and closes the channel and session. `SftpPath` accepts a file name and joins it
with the configured remote directory. The handlers move blocking SFTP work to a boundedElastic
worker. `WebfluxConfig` limits the request body to 1 MiB.

### Run and test

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

Use JDK 8 and Maven 3.6.3+. Set `SFTP_HOST`, `SFTP_USER`, `SFTP_PRIVATE_KEY_PATH`,
`SFTP_KNOWN_HOSTS_PATH`, and `SFTP_REMOTE_DIR` in your environment before making a transfer. Do
not put key files in this repository. `mvn clean verify` checks filename confinement without an
SFTP server. `mvn spring-boot:run` starts the local API on 127.0.0.1:8080. A live
upload/download requires your own test SFTP server and valid credentials.

#### Postman API

**Setup**

Set SFTP_HOST, SFTP_USER, SFTP_PRIVATE_KEY_PATH, SFTP_KNOWN_HOSTS_PATH, and SFTP_REMOTE_DIR. Use
a test SFTP server with /upload available.

##### 1. Upload hello.txt

**Request**

- Method: POST
- URL: http://localhost:8080/sftp/upload
- Content-Type: application/json

~~~json
{
  "name": "hello.txt",
  "payload": "aGVsbG8="
}
~~~

**Response**

- Status: 200 OK

~~~json
{
  "result": true,
  "location": "/upload/hello.txt"
}
~~~

##### 2. Download hello.txt

**Request**

- Method: POST
- URL: http://localhost:8080/sftp/download
- Content-Type: application/json

~~~json
{
  "name": "hello.txt"
}
~~~

**Response**

- Status: 200 OK

~~~json
{
  "result": true,
  "payload": "aGVsbG8="
}
~~~

**Note:** The location changes if SFTP_REMOTE_DIR is different.

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
src/main/resources/application.yml
src/test/java/io/github/parkwoong/sftp/SftpPathTest.java
```

### 코드 설명

`SftpApiRouterConfig`가 POST `/sftp/upload`, `/sftp/download`를
`UploadHandler`, `DownloadHandler`에 연결한다. 핸들러는 Base64 변환과 JSch 파일
전송을 처리한다. `JschSessionHolder`는 요청마다 세션을 만들고 known_hosts로 서버를 확인한 뒤
채널과 세션을 닫는다. `SftpPath`는 파일명을 검사하고 서버가 설정한 원격 디렉터리와 결합한다. 블로킹 SFTP
작업은 boundedElastic 작업 스레드에서 실행한다. `WebfluxConfig`는 요청 본문을 1 MiB로
제한한다.

### 실행과 테스트

JDK 8, Maven 3.6.3 이상 사용. 저장소 루트에서 실행.

```sh
java -version
mvn clean verify
mvn spring-boot:run
```

실제 전송 전 `SFTP_HOST`, `SFTP_USER`, `SFTP_PRIVATE_KEY_PATH`,
`SFTP_KNOWN_HOSTS_PATH`, `SFTP_REMOTE_DIR` 환경변수를 설정. 키 파일은 저장소에 넣지
않는다. `mvn clean verify`는 SFTP 서버 없이 파일명 검증을 테스트한다.
`mvn spring-boot:run`은 로컬 API를 127.0.0.1:8080에서 실행한다. 실제 업로드·다운로드는
별도 테스트 SFTP 서버와 유효한 인증 정보가 필요하다.

#### Postman API

**준비**

테스트 SFTP 서버 준비. SFTP_HOST, SFTP_USER, SFTP_PRIVATE_KEY_PATH,
SFTP_KNOWN_HOSTS_PATH, SFTP_REMOTE_DIR 설정. /upload 디렉터리 필요.

##### 1. hello.txt 업로드

**요청**

- 메서드: POST
- URL: http://localhost:8080/sftp/upload
- Content-Type: application/json

~~~json
{
  "name": "hello.txt",
  "payload": "aGVsbG8="
}
~~~

**응답**

- 상태: 200 OK

~~~json
{
  "result": true,
  "location": "/upload/hello.txt"
}
~~~

##### 2. hello.txt 다운로드

**요청**

- 메서드: POST
- URL: http://localhost:8080/sftp/download
- Content-Type: application/json

~~~json
{
  "name": "hello.txt"
}
~~~

**응답**

- 상태: 200 OK

~~~json
{
  "result": true,
  "payload": "aGVsbG8="
}
~~~

**참고:** SFTP_REMOTE_DIR이 다르면 location도 변경.
