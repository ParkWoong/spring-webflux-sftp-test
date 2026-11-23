package io.github.parkwoong.sftp.webflux.config;

import io.github.parkwoong.sftp.webflux.properties.WebfluxProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebfluxConfig implements WebFluxConfigurer {

    private final WebfluxProperties properties;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {

        configurer.defaultCodecs().maxInMemorySize(properties.getMaxInMemorySize());
    }
}
