package io.github.parkwoong.sftp.sftp.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("sftp")
public class SftpProperties {
    private String serverAddress;
    private String serverUsername;
    private String defaultPath;
    private String privateKeyPath;
    private String knownHostsPath;
    private int serverPort = 22;
    private int uploadChmod = 0644;
}
