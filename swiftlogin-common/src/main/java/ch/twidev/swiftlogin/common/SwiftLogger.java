/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common;

import ch.twidev.swiftlogin.common.exception.PluginIssues;
import jodd.util.StringUtil;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SwiftLogger extends Logger {

    private static final String REPORT_MESSAGE = "To resolve your issue please read this wiki page: ";
    private static final String REPORT_MESSAGE_URL = "https://github.com/TwiDev/swiftlogin-api/wiki#issues";
    private static final String PLUGIN_ERROR_MESSAGE = "IT IS NOT A SWIFTLOGIN ERROR! DO **NOT** REPORT IT TO THE AUTHOR!";

    private final String name;

    public SwiftLogger(String name) {
        this(SwiftLoginImplementation.getInstance().getLogger(), name);
    }

    public SwiftLogger(Logger logger) {
        this(logger, null);
    }

    public SwiftLogger(Logger logger, String name) {
        super(name, null);

        if(name == null) {
            this.name = "[SwiftLogin] ";
        }else {
            this.name = "[SwiftLogin] [" + name + "] ";
        }
        this.setParent(
                logger
        );
    }

    public void sendEnableMessage() {
        this.info("SwiftLogin v"+SwiftLoginImplementation.VERSION+" is enabling...");
        this.info(" ");

        this.info("#================[ SWIFT LOGIN IS LOADING ]=================#");
        this.info("#                                                           #");
        this.info("# Thank you for using SwiftLogin !                          #");
        this.info("# Please read carefully all outputs coming from it.         #");
        this.info("# if you encounter an error go to the wiki to resolve it    #");
        this.info("# at https://github.com/TwiDev/swiftlogin-api/wiki#issues   #");
        this.info("#                                                           #");
        this.info("#===========================================================#");
        this.info(" ");
    }

    public void sendRawPluginInfo(String messages, Object... args) {
        String[] v = String.format(messages, args).split("\n");
        this.sendPluginInfo(v);
    }

    public void sendPluginInfo(String... messages) {
        int longestLine = 0;
        for (String s : messages) {
            if(s.length() > longestLine) {
                longestLine = s.length();
            }
        }

        int boxLength = longestLine + 6;

        this.info("#"+ StringUtil.repeat("=", boxLength) +"#");
        this.info("#"+ StringUtil.repeat(" ", boxLength) +"#");
        for (String s : messages) {
            this.info("# " + s + StringUtil.repeat(" ", boxLength - s.length() - 1) + "#");
        }
        this.info("#"+ StringUtil.repeat(" ", boxLength) +"#");
        this.info("#"+ StringUtil.repeat("=", boxLength) +"#");
    }

    public void sendRawPluginError(PluginIssues issueCode, String messages, Object... args) {
        String[] v = String.format(messages, args).split("\n");
        this.sendPluginError(Level.SEVERE, issueCode, v);
    }


    public void sendRawPluginError(Level level, PluginIssues issueCode, String messages, Object... args) {
        String[] v = String.format(messages, args).split("\n");
        this.sendPluginError(level, issueCode, v);
    }

    public void sendPluginError(Level level, PluginIssues issueCode) {
        this.sendPluginError(level, issueCode, issueCode.getMessage());
    }

    public void sendPluginError(PluginIssues issueCode) {
        this.sendPluginError(Level.SEVERE, issueCode, issueCode.getMessage());
    }

    public void sendRawPluginError(PluginIssues issueCode) {
        this.sendRawPluginError(issueCode, issueCode.getMessage());
    }

    public void sendPluginError(PluginIssues issueCode, String... messages) {
        this.sendPluginError(Level.SEVERE,issueCode, messages);
    }

    public void sendPluginError(Level level, PluginIssues issueCode, String... messages) {
        int longestLine = 0;
        for (String s : messages) {
            if(s.length() > longestLine) {
                longestLine = s.length();
            }
        }

        int boxLength = Math.max(REPORT_MESSAGE.length(), longestLine) + 6;

        this.log(level,"#"+ StringUtil.repeat("=", boxLength) +"#");
        this.log(level,"#"+ StringUtil.repeat(" ", boxLength) +"#");
        for (String s : messages) {
            this.log(level,"# " + s + StringUtil.repeat("",boxLength - s.length() - 1) + "#");
        }
        this.log(level,"#"+ StringUtil.repeat(" ", boxLength) +"#");
        if(issueCode != PluginIssues.NONE) {
            this.log(level, "# " + PLUGIN_ERROR_MESSAGE + StringUtil.repeat(" ", boxLength - PLUGIN_ERROR_MESSAGE.length() - 1) + "#");
            this.log(level, "# " + REPORT_MESSAGE + StringUtil.repeat(" ", boxLength - REPORT_MESSAGE.length() - 1) + "#");
            this.log(level, "# " + REPORT_MESSAGE_URL + StringUtil.repeat(" ", boxLength - REPORT_MESSAGE_URL.length() - 1) + "#");
            if (issueCode != null) {
                String issueCodeMessage = "Your issue code: " + issueCode.getCode() + " | " + issueCode;
                this.log(level, "#" + StringUtil.repeat(" ", boxLength) + "#");
                this.log(level, "# " + issueCodeMessage + StringUtil.repeat(" ", boxLength - issueCodeMessage.length() - 1) + "#");
            }
            this.log(level, "#" + StringUtil.repeat(" ", boxLength) + "#");
        }
        this.log(level,"#"+ StringUtil.repeat("=", boxLength) +"#");
    }

    public void sendWarningError(PluginIssues issueCode, String... messages) {
        int longestLine = 0;
        for (String s : messages) {
            if(s.length() > longestLine) {
                longestLine = s.length();
            }
        }

        int boxLength = Math.max(REPORT_MESSAGE.length(), longestLine) + 6;

        this.warning("#"+ StringUtil.repeat("=", boxLength) +"#");
        this.warning("#"+ StringUtil.repeat(" ", boxLength) +"#");
        for (String s : messages) {
            this.warning("# " + s + StringUtil.repeat(" ", boxLength - s.length() - 1) + "#");
        }
        this.warning("#"+ StringUtil.repeat(" ", boxLength) +"#");
        this.warning("# " + PLUGIN_ERROR_MESSAGE + StringUtil.repeat(" ", boxLength - PLUGIN_ERROR_MESSAGE.length() - 1) + "#");
        this.warning("# " + REPORT_MESSAGE + StringUtil.repeat(" ", boxLength - REPORT_MESSAGE.length() - 1) + "#");
        this.warning("# " + REPORT_MESSAGE_URL + StringUtil.repeat(" ", boxLength - REPORT_MESSAGE_URL.length() - 1) + "#");
        if(issueCode != null) {
            String issueCodeMessage = "Your issue code: " + issueCode.getCode() + " | " + issueCode;
            this.warning("#"+ StringUtil.repeat(" ", boxLength) +"#");
            this.warning("# " + issueCodeMessage + StringUtil.repeat(" ", boxLength - issueCodeMessage.length() - 1) + "#");
        }
        this.warning("#"+ StringUtil.repeat(" ", boxLength) +"#");
        this.warning("#"+ StringUtil.repeat("=", boxLength) +"#");
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(this.name + record.getMessage());
        super.log(record);
    }
}
