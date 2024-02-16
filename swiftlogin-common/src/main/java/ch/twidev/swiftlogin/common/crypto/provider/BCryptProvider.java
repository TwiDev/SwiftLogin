/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.crypto.provider;

import at.favre.lib.crypto.bcrypt.BCrypt;
import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.api.crypto.HashedPassword;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;
import ch.twidev.swiftlogin.common.crypto.CryptoPassword;

public class BCryptProvider extends AbstractCryptoProvider {

    public static final BCrypt.Hasher HASHER = BCrypt
            .with(BCrypt.Version.VERSION_2A);
    public static final BCrypt.Verifyer VERIFIER = BCrypt
            .verifyer(BCrypt.Version.VERSION_2A);

    public BCryptProvider(SwiftLoginImplementation<?, ?> swiftLoginImplementation) {
        super(swiftLoginImplementation);
    }

    @Override
    public HashedPassword createHashedPassword(String password) {
        try {
            final String raw = HASHER.hashToString(10, password.toCharArray());

            return this.convertRawBcrypt(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean matchPassword(HashedPassword hashedPassword, String password) {
        try {
            return VERIFIER.verify(password.toCharArray(),
                    hashedPassword.getRaw().toCharArray()
            ).verified;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public CryptoType getCryptoType() {
        return CryptoType.BCRYPT;
    }

    private HashedPassword convertRawBcrypt(String raw) {
        return new CryptoPassword(
                raw,
                this
        );
    }

}
