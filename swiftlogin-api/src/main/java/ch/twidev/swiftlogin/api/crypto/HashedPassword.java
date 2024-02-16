/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.crypto;

public abstract class HashedPassword {

    private final CryptoProvider cryptoProvider;

    public HashedPassword(CryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    public boolean match(String password) {
        return cryptoProvider.matchPassword(this, password);
    }

    public abstract String getRaw();

    public abstract String toPasswordString();

}
