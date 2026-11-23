package io.github.parkwoong.sftp.sftp.handler;

import static io.github.parkwoong.sftp.sftp.util.StringUtils.isBlank;

import io.github.parkwoong.sftp.sftp.config.JschSessionHolder;
import io.github.parkwoong.sftp.sftp.dto.download.DownloadRequest;
import io.github.parkwoong.sftp.sftp.dto.download.DownloadResponse;
import io.github.parkwoong.sftp.sftp.exception.InvalidRequestData;
import io.github.parkwoong.sftp.sftp.properties.SftpProperties;
import io.github.parkwoong.sftp.sftp.util.SftpPath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class DownloadHandler {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private final SftpProperties sftpProperties;
    private final JschSessionHolder sessionHolder;

    public Mono<ServerResponse> handle(ServerRequest serverRequest) {

        return serverRequest
                .bodyToMono(DownloadRequest.class)
                .filter(request -> !isBlank(request.getName()))
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new InvalidRequestData(DownloadRequest.class))))
                .map(
                        request ->
                                SftpPath.resolve(
                                        sftpProperties.getDefaultPath(), request.getName()))
                .flatMap(
                        destination ->
                                Mono.using(
                                                sessionHolder::getSftpChannel,
                                                channel ->
                                                        Mono.fromSupplier(
                                                                () -> {
                                                                    log.info(
                                                                            "Download File from"
                                                                                + " SFTP Server");

                                                                    String payload = null;
                                                                    boolean result = false;

                                                                    if (channel != null)
                                                                        try (FastByteArrayOutputStream
                                                                                outputStream =
                                                                                        new FastByteArrayOutputStream()) {

                                                                            channel.get(
                                                                                    destination,
                                                                                    outputStream);

                                                                            payload =
                                                                                    ENCODER
                                                                                            .encodeToString(
                                                                                                    outputStream
                                                                                                            .toByteArray());
                                                                            result = true;

                                                                        } catch (Exception e) {
                                                                            log.error(
                                                                                    e.getMessage());
                                                                        }

                                                                    DownloadResponse
                                                                            downloadResponse =
                                                                                    DownloadResponse
                                                                                            .builder()
                                                                                            .result(
                                                                                                    result)
                                                                                            .payload(
                                                                                                    payload)
                                                                                            .build();

                                                                    log.info(
                                                                            "========= sftp Request"
                                                                                + " END status : {}"
                                                                                + " =================="
                                                                                + " ",
                                                                            downloadResponse
                                                                                    .isResult());

                                                                    return downloadResponse;
                                                                }),
                                                sessionHolder::close)
                                        .subscribeOn(Schedulers.boundedElastic()))
                .flatMap(ServerResponse.ok()::bodyValue);
    }
}
