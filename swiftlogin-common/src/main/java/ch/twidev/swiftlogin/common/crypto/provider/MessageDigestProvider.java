/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.crypto.provider;

import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.api.crypto.HashedPassword;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.crypto.CryptoPassword;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class MessageDigestProvider extends AbstractCryptoProvider{

    private final CryptoType algorithm;

    public MessageDigestProvider(CryptoType cryptoType, SwiftLoginImplementation<?, ?> swiftLoginImplementation) {
        super(cryptoType, swiftLoginImplementation);

        this.algorithm = cryptoType;
    }

    @Override
    public HashedPassword createHashedPassword(String password) {
        try {
            byte[] salt = getRandomSalt();

            return this.convertMessageDigest(
                    this.hashRaw(password, salt)
            );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean matchPassword(HashedPassword hashedPassword, String password) {
        String[] splitRaw = hashedPassword.getRaw().split("\\$");
        String salt = splitRaw[0];

        try {
            String hashedClonedPassword = hashRaw(password, Base64.getDecoder().decode(salt));

            return hashedClonedPassword.equals(hashedPassword.getRaw());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CryptoType getCryptoType() {
        return algorithm;
    }

    private String hashRaw(String raw, byte[] salt) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(algorithm.getAlgorithmName());
        md.update(salt);
        byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return Base64.getEncoder().encodeToString(salt) + "$" + sb;
    }

    private HashedPassword convertMessageDigest(String raw) {
        return new CryptoPassword(
                raw,
                this
        );
    }

    private static byte[] getRandomSalt() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
