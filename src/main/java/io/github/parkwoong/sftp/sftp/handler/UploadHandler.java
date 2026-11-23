package io.github.parkwoong.sftp.sftp.handler;

import static io.github.parkwoong.sftp.sftp.util.StringUtils.isBlank;

import io.github.parkwoong.sftp.sftp.config.JschSessionHolder;
import io.github.parkwoong.sftp.sftp.dto.upload.UploadRequest;
import io.github.parkwoong.sftp.sftp.dto.upload.UploadResponse;
import io.github.parkwoong.sftp.sftp.exception.InvalidRequestData;
import io.github.parkwoong.sftp.sftp.properties.SftpProperties;
import io.github.parkwoong.sftp.sftp.util.SftpPath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadHandler {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private final SftpProperties sftpProperties;
    private final JschSessionHolder sessionHolder;

    public Mono<ServerResponse> handle(ServerRequest serverRequest) {

        return serverRequest
                .bodyToMono(UploadRequest.class)
                .filter(request -> !isBlank(request.getName()) && !isBlank(request.getPayload()))
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new InvalidRequestData(UploadRequest.class))))
                .doOnNext(
                        request -> {
                            final String destination =
                                    SftpPath.resolve(
                                            sftpProperties.getDefaultPath(), request.getName());
                            request.setDestination(destination);
                            request.setData(DECODER.decode(request.getPayload()));
                        })
                .flatMap(
                        request ->
                                Mono.using(
                                                sessionHolder::getSftpChannel,
                                                channel ->
                                                        Mono.fromSupplier(
                                                                () -> {
                                                                    log.info(
                                                                            "UploadFile to SFTP"
                                                                                    + " Server");

                                                                    boolean result = false;
                                                                    try (ByteArrayInputStream
                                                                            inputStream =
                                                                                    new ByteArrayInputStream(
                                                                                            request
                                                                                                    .getData())) {

                                                                        channel.put(
                                                                                inputStream,
                                                                                request
                                                                                        .getDestination());
                                                                        channel.chmod(
                                                                                sftpProperties
                                                                                        .getUploadChmod(),
                                                                                request
                                                                                        .getDestination());

                                                                        result = true;

                                                                    } catch (Exception e) {
                                                                        log.error(e.getMessage());
                                                                    }

                                                                    UploadResponse uploadResponse =
                                                                            UploadResponse.builder()
                                                                                    .result(result)
                                                                                    .location(
                                                                                            request
                                                                                                    .getDestination())
                                                                                    .build();

                                                                    log.info(
                                                                            "========= sftp Request"
                                                                                + " END status : {}"
                                                                                + " ==================",
                                                                            uploadResponse
                                                                                    .isResult());

                                                                    return uploadResponse;
                                                                }),
                                                sessionHolder::close)
                                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMap(ServerResponse.ok()::bodyValue);
    }
}
