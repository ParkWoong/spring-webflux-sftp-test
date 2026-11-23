package io.github.parkwoong.sftp.webflux.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("webflux")
public class WebfluxProperties {

    private int maxInMemorySize;
}
