package io.github.parkwoong.sftp.sftp.util;

import io.github.parkwoong.sftp.sftp.exception.InvalidRequestData;

public final class SftpPath {
    private SftpPath() {}

    public static String resolve(String base, String name) {
        if (base == null
                || !base.startsWith("/")
                || base.contains("..")
                || name == null
                || !name.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,127}")
                || name.equals("..")
                || name.equals(".")) throw new InvalidRequestData(SftpPath.class);
        return base.endsWith("/") ? base + name : base + "/" + name;
    }
}
