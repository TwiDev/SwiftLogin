/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.api.players;

import ch.twidev.swiftlogin.api.crypto.HashedPassword;
import ch.twidev.swiftlogin.api.utils.Unique;

import java.util.UUID;

public interface SwiftPlayer extends Unique {

    UUID getPremiumId();

    String getRecentName();

    HashedPassword getHashedPassword();

    String getRecentServer();

    String getLastAddress();

    long getRecentSeen();

    String getFirstAddress();

    long getFirstSeen();

    boolean isPremium();

    boolean isRegistered();

    boolean isLogged();

    boolean isOnline();

    boolean exists();

    long getCurrentSession();

    boolean hasCurrentSession();

    void setCurrentName(String name);

    void setCurrentAddress(String name);

    void setCurrentServer(String name);

    void setHashedPassword(HashedPassword name);

    boolean hasRecentName();

    boolean hasRecentServer();

    void save();

    void setLogged(boolean v);

    String getCachedCaptcha();

    void setCachedCaptcha(String captcha);

    String toRawString();

}
