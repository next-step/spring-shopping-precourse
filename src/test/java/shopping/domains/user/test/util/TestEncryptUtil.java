package shopping.domains.user.test.util;

import lombok.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TestEncryptUtil {
    private static final AesBytesEncryptor encryptor = new AesBytesEncryptor("test", "123123");

    private static final BCryptPasswordEncoder hash = new BCryptPasswordEncoder();

    @NonNull
    public static final String hash(@NonNull final String origin) {
        return hash.encode(origin);
    }

    @NonNull
    public static final String encrypt(@NonNull final String origin) {
        final byte[] encrypt = encryptor.encrypt(origin.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypt);
    }

    @NonNull
    public static final String decrypt(@NonNull final String encrypted) {
        final byte[] decryptBytes = Base64.getDecoder().decode(encrypted);
        final byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }
}
