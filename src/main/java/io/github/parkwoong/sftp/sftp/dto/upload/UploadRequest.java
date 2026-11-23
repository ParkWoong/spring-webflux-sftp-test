package io.github.parkwoong.sftp.sftp.dto.upload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadRequest {

    private String payload; // base64 encoded
    private String name;

    private byte[] data;
    private String destination;
}
