package de.fynder.jtransfer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;


public class Hasher {
    private final MessageDigest digest;

    public Hasher(MessageDigest digest) {
        this.digest = digest;
    }

    String hashFile(Path p) throws IOException {
        final FileChannel channel = new FileInputStream(p.toFile()).getChannel();
        return hashBytesToString(computeHashBytes(channel, 0, channel.size()));
    }

    private String hashBytesToString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & aHash));
        }
        return hexString.toString();
    }

    private byte[] computeHashBytes(FileChannel channel, long offset, long length) throws IOException {
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, offset, length);
        digest.reset();
        digest.update(buffer);
        channel.close();
        return digest.digest();
    }

    public String hashFilePart(Path p, long offset, long length) throws IOException {
        final FileChannel channel = new FileInputStream(p.toFile()).getChannel();
        return hashBytesToString(computeHashBytes(channel, offset, length));
    }
}
