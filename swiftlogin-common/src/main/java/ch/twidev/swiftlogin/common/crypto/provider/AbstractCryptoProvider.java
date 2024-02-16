/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.crypto.provider;

import ch.twidev.swiftlogin.api.crypto.CryptoProvider;
import ch.twidev.swiftlogin.api.crypto.CryptoType;
import ch.twidev.swiftlogin.common.SwiftLoginImplementation;

public abstract class AbstractCryptoProvider implements CryptoProvider {

    public AbstractCryptoProvider(SwiftLoginImplementation<?,?> swiftLoginImplementation) {

        // Load crypto provider
        swiftLoginImplementation.getLoadedCryptoProvider().put(
                this.getCryptoType(), this
        );
    }

    public AbstractCryptoProvider(CryptoType cryptoType, SwiftLoginImplementation<?, ?> swiftLoginImplementation) {

        // Load crypto provider
        swiftLoginImplementation.getLoadedCryptoProvider().put(
                cryptoType, this
        );
    }
}
