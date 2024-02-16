/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.helpers;

import java.util.List;

public class StringList extends ListHelper<String> {

    public StringList(List<String> list) {
        super(list, String.class);
    }

    public StringList() {
        super(String.class);
    }

    public StringList append(String s) {
        super.add(s);

        return this;
    }
}
