/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.servers;

import ch.twidev.swiftlogin.api.servers.SwiftServer;

import java.util.Comparator;

public class BackendServerComparator implements Comparator<SwiftServer> {

    private final ServerComparatorStrategy serverComparatorType;

    public BackendServerComparator(ServerComparatorStrategy serverComparatorType) {
        this.serverComparatorType = serverComparatorType;
    }

    @Override
    public int compare(SwiftServer o1, SwiftServer o2) {
        if(serverComparatorType == ServerComparatorStrategy.MITIGATION) {
            if(!o1.isOnline() || !o2.isOnline()) {
                return Boolean.compare(o1.isOnline(), o2.isOnline());
            }else{
                if(o1.isFull() || o2.isFull()) {
                    return Boolean.compare(o1.isOnline(), o2.isOnline());
                }

                return o1.getPlayerCount() - o2.getPlayerCount();
            }
        }else if(serverComparatorType == ServerComparatorStrategy.SLOTS) {
            int o1AvailableSlots = o1.getSlots() - o1.getPlayerCount();
            int o2AvailableSlots = o2.getSlots() - o2.getPlayerCount();

            if(!o1.isOnline() || !o2.isOnline()) {
                return Boolean.compare(o1.isOnline(), o2.isOnline());
            }else{
                if(o1.isFull() || o2.isFull()) {
                    return Boolean.compare(o1.isOnline(), o2.isOnline());
                }

                return o2AvailableSlots - o1AvailableSlots;
            }
        }

        return 0;
    }
}
