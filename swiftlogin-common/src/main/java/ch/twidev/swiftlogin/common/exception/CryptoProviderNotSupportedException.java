/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.exception;

import ch.twidev.swiftlogin.api.crypto.CryptoType;

public class CryptoProviderNotSupportedException extends RuntimeException{

    private final CryptoType cryptoType;

    public CryptoProviderNotSupportedException(CryptoType cryptoType) {
        this.cryptoType = cryptoType;
    }

    public CryptoType getCryptoType() {
        return cryptoType;
    }
}
