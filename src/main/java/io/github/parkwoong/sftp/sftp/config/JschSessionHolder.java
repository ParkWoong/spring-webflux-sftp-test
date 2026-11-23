package io.github.parkwoong.sftp.sftp.config;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import io.github.parkwoong.sftp.sftp.properties.SftpProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JschSessionHolder {
    private final SftpProperties properties;

    public ChannelSftp getSftpChannel() throws Exception {
        JSch jsch = new JSch();
        jsch.setKnownHosts(
                Objects.requireNonNull(properties.getKnownHostsPath(), "known hosts path"));
        jsch.addIdentity(
                Objects.requireNonNull(properties.getPrivateKeyPath(), "private key path"));
        Session session =
                jsch.getSession(
                        properties.getServerUsername(),
                        properties.getServerAddress(),
                        properties.getServerPort());
        session.setConfig("StrictHostKeyChecking", "yes");
        session.connect(5000);
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        try {
            channel.connect(5000);
            return channel;
        } catch (Exception error) {
            session.disconnect();
            throw error;
        }
    }

    public void close(ChannelSftp channel) {
        if (channel != null) {
            Session session;
            try {
                session = channel.getSession();
            } catch (com.jcraft.jsch.JSchException error) {
                channel.disconnect();
                return;
            }
            channel.disconnect();
            session.disconnect();
        }
    }
}
