package io.github.parkwoong.sftp;

import static org.junit.jupiter.api.Assertions.*;

import io.github.parkwoong.sftp.sftp.exception.InvalidRequestData;
import io.github.parkwoong.sftp.sftp.util.SftpPath;

import org.junit.jupiter.api.Test;

class SftpPathTest {
    @Test
    void confinesNameToConfiguredDirectory() {
        assertEquals("/upload/report.txt", SftpPath.resolve("/upload", "report.txt"));
        assertThrows(InvalidRequestData.class, () -> SftpPath.resolve("/upload", "../secret"));
        assertThrows(InvalidRequestData.class, () -> SftpPath.resolve("/upload", "a/b"));
        assertThrows(InvalidRequestData.class, () -> SftpPath.resolve("/upload", ".."));
    }
}
