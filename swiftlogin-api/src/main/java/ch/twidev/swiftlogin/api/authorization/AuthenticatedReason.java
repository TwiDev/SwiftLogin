/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.authorization;

import ch.twidev.swiftlogin.api.utils.Include;

@Include
public enum AuthenticatedReason {

    LOGIN,
    REGISTER,
    SESSION,
    PREMIUM

}
