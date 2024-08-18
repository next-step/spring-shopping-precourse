package shopping.apps.shopping.security.bean.encrypt;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;
import shopping.domains.user.core.domain.entity.EncryptStrategy;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AesEncryptStrategy implements EncryptStrategy {
    private final AesBytesEncryptor aesBytesEncryptor;

    @Override
    public @NonNull String encrypt(@NonNull final String origin) {
        final byte[] encrypt = aesBytesEncryptor.encrypt(origin.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    @Override
    public @NonNull String decrypt(@NonNull final String encrypted) {
        final byte[] decryptBytes = stringToByteArray(encrypted);
        final byte[] decrypt = aesBytesEncryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    @Override
    public boolean match(@NonNull final String origin, @NonNull final String encrypted) {
        return encrypted.equals(encrypt(origin));
    }

    private static String byteArrayToString(@NonNull final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte abyte : bytes) {
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString();
    }

    private static byte[] stringToByteArray(@NonNull final String byteString) {
        final String[] split = byteString.split("\\s");
        final ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (final String s : split) {
            buffer.put((byte) Integer.parseInt(s));
        }
        return buffer.array();
    }
}
