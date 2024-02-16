/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.configuration.schema;

import ch.twidev.swiftlogin.common.configuration.ConfigurationHeader;
import ch.twidev.swiftlogin.common.configuration.ConfigurationKey;
import ch.twidev.swiftlogin.common.configuration.ConfigurationMessage;
import ch.twidev.swiftlogin.common.configuration.ConfigurationSide;


@ConfigurationSide(isBackend = false)
@ConfigurationHeader(description = "Plugin message translation")
public class TranslationConfiguration {

    private static final String PLUGIN_PREFIX = "&a&lSwiftLogin &8&l» ";

    public static final ConfigurationMessage ERROR_USER_NOT_EXISTS = new ConfigurationMessage(
            String.class,
            "errorUserNotExists",
            "&aThis user does not exist"
    );

    public static final ConfigurationMessage ERROR_REGISTER_PREMIUM = new ConfigurationMessage(
            String.class,
            "errorRegisterPremium",
            PLUGIN_PREFIX + "&cYou cannot register as a premium player"
    );

    public static final ConfigurationMessage ERROR_ALREADY_REGISTERED = new ConfigurationMessage(
            String.class,
            "errorAlreadyRegistered",
            PLUGIN_PREFIX + "&cYou are already registered"
    );

    public static final ConfigurationMessage ERROR_ALREADY_LOGGED = new ConfigurationMessage(
            String.class,
            "errorAlreadyLogged",
            PLUGIN_PREFIX + "&cYou are already logged"
    );

    public static final ConfigurationMessage ERROR_ALREADY_PREMIUM = new ConfigurationMessage(
            String.class,
            "errorAlreadyPremium",
            PLUGIN_PREFIX + "&cYou are already premium"
    );

    public static final ConfigurationMessage ERROR_REGISTER_USAGE = new ConfigurationMessage(
            String.class,
            "errorRegisterUsage",
            PLUGIN_PREFIX + "&cPlease use /register <password> <captcha>"
    );

    public static final ConfigurationMessage ERROR_NOT_REGISTERED = new ConfigurationMessage(
            String.class,
            "errorNotRegistered",
            PLUGIN_PREFIX + "&cYou must register first"
    );

    public static final ConfigurationMessage ERROR_NOT_PREMIUM = new ConfigurationMessage(
            String.class,
            "errorNotPremium",
            PLUGIN_PREFIX + "&cYou aren't a premium account"
    );

    public static final ConfigurationMessage ERROR_PREMIUM_ALREADY_EXIST = new ConfigurationMessage(
            String.class,
            "errorPremiumAlreadyExist",
            PLUGIN_PREFIX + "&cA premium account already exists"
    );

    public static final ConfigurationMessage ERROR_DESTROY_PREMIUM_SESSION = new ConfigurationMessage(
            String.class,
            "errorDestroyPremiumSession",
            PLUGIN_PREFIX + "&cCannot destroy a premium user session"
    );

    public static final ConfigurationMessage ERROR_DESTROY_UNREGISTERED_SESSION = new ConfigurationMessage(
            String.class,
            "errorDestroyUnregisteredSession",
            PLUGIN_PREFIX + "&cCannot destroy a unregistered user session"
    );

    public static final ConfigurationMessage ERROR_DESTROY_NOT_LOGGED_SESSION = new ConfigurationMessage(
            String.class,
            "errorDestroyNotLoggedSession",
            PLUGIN_PREFIX + "&cCannot destroy not logged user session"
    );

    public static final ConfigurationMessage ERROR_DESTROY_INVALID_SESSION = new ConfigurationMessage(
            String.class,
            "errorDestroyInvalidSession",
            PLUGIN_PREFIX + "&cThis user hasn't a valid current session"
    );

    public static final ConfigurationMessage SUCCESS_SESSION_DESTROYED = new ConfigurationMessage(
            String.class,
            "successSessionDestroy",
            PLUGIN_PREFIX + "&cThis user session has been destroyed"
    );

    public static final ConfigurationMessage ERROR_CHANGE_PASSWORD_PREMIUM_USER = new ConfigurationMessage(
            String.class,
            "errorChangePasswordPremiumUser",
            PLUGIN_PREFIX + "&cYou cannot perform this action as a premium user"
    );

    public static final ConfigurationMessage ERROR_CHANGE_PASSWORD_UNREGISTERED_USER = new ConfigurationMessage(
            String.class,
            "errorChangePasswordUnregisteredUser",
            PLUGIN_PREFIX + "&cYou cannot perform this action as a unregistered user"
    );

    public static final ConfigurationMessage ERROR_CHANGE_PASSWORD_MISSING_CONFIRMATION_PASSWORD = new ConfigurationMessage(
            String.class,
            "errorChangePasswordMissingConfirmationPassword",
            PLUGIN_PREFIX + "&cYou must enter your current password to confirm the change"
    );

    public static final ConfigurationMessage SUCCESS_CHANGED_PASSWORD = new ConfigurationMessage(
            String.class,
            "successChangedPassword",
            PLUGIN_PREFIX + "&cYour password has been updated"
    );

    public static final ConfigurationMessage MOJANG_SERVERS_DOWN = new ConfigurationMessage(
            String.class,
            "mojangServersDown",
            PLUGIN_PREFIX + "&cThe mojang servers are down."
    );


    public static final ConfigurationMessage CAPTCHA_NOT_MATCH = new ConfigurationMessage(
            String.class,
            "captchaNotMatch",
            PLUGIN_PREFIX + "&cYour captcha code does not match!"
    );

    public static final ConfigurationMessage ERROR_PASSWORD_NOT_MATCH = new ConfigurationMessage(
            String.class,
            "errorPasswordNotMatch",
            PLUGIN_PREFIX + "&cYour password does not match!"
    );

    public static final ConfigurationMessage ERROR_UNSAFE_PASSWORD = new ConfigurationMessage(
            String.class,
            "errorUnsafePassword",
            PLUGIN_PREFIX + "&cYour password is not safe please chose a new one"
    );

    public static final ConfigurationMessage ERROR_UNSAFE_PASSWORD_CONTAINS_PLAYER_NAME = new ConfigurationMessage(
            String.class,
            "errorUnsafePasswordContainsPlayerName",
            PLUGIN_PREFIX + "&cYour password is not safe please chose a new one, please don't use your nickname in your password."
    );

    public static final ConfigurationMessage ERROR_ALREADY_CRACKED = new ConfigurationMessage(
            String.class,
            "errorAlreadyCracked",
            PLUGIN_PREFIX + "&cYou are already a cracked user"
    );

    public static final ConfigurationMessage SUCCESS_CRACKED_ENABLED = new ConfigurationMessage(
            String.class,
            "successCrackedEnabled",
            PLUGIN_PREFIX + "&cYou are now a cracked account"
    );

    public static final ConfigurationMessage SUCCESS_PREMIUM_ENABLED = new ConfigurationMessage(
            String.class,
            "successPremiumEnabled",
            PLUGIN_PREFIX + "&cYou are now a premium account"
    );

    public static final ConfigurationMessage SUCCESS_REDIRECTION = new ConfigurationMessage(
            String.class,
            "successRedirection",
            PLUGIN_PREFIX + "&aYou have been redirected to server %server% because %reason%"
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_REGISTER_MESSAGE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessRegisterMessage",
            "Register success!"
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_REGISTER_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessRegisterTitle",
            "&a&lRegistered"
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_REGISTER_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessRegisterSubTitle",
            "&7You have been successfully registered"
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_REGISTER_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessRegisterFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_REGISTER_STAY = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessRegisterStay",
            40
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_REGISTER_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessRegisterFadeOut",
            20
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_LOGIN_MESSAGE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessLoginMessage",
            "Login success!"
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_LOGIN_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessLoginTitle",
            "&a&lLogged In"
    );

    public static final ConfigurationMessage AUTHORIZATION_SUCCESS_LOGIN_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSuccessLoginSubTitle",
            "&7You have been successfully logged in"
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_LOGIN_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessLoginFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_LOGIN_STAY = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessLoginStay",
            40
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SUCCESS_LOGIN_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "authorizationSuccessLoginFadeOut",
            20
    );

    public static final ConfigurationMessage AUTHORIZATION_PREMIUM_MESSAGE = new ConfigurationMessage(
            String.class,
            "authorizationPremiumMessage",
            "&aPremium Session"
    );

    public static final ConfigurationMessage AUTHORIZATION_PREMIUM_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationPremiumTitle",
            "&a&lPremium"
    );

    public static final ConfigurationMessage AUTHORIZATION_PREMIUM_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationPremiumSubTitle",
            "&7You have been automatically connected"
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_PREMIUM_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "authorizationPremiumFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_PREMIUM_STAY = new ConfigurationKey<>(
            Integer.class,
            "authorizationPremiumStay",
            40
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_PREMIUM_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "authorizationPremiumFadeOut",
            20
    );

    public static final ConfigurationMessage AUTHORIZATION_SESSION_MESSAGE = new ConfigurationMessage(
            String.class,
            "authorizationSessionMessage",
            "&aSession valid success!"
    );

    public static final ConfigurationMessage AUTHORIZATION_SESSION_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSessionTitle",
            "&a&lLogged In"
    );

    public static final ConfigurationMessage AUTHORIZATION_SESSION_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "authorizationSessionSubTitle",
            "&7You have been automatically connected"
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SESSION_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "authorizationSessionFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SESSION_STAY = new ConfigurationKey<>(
            Integer.class,
            "authorizationSessionStay",
            40
    );

    public static final ConfigurationKey<Integer> AUTHORIZATION_SESSION_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "authorizationSessionFadeOut",
            20
    );

    public static final ConfigurationMessage ERROR_REGISTER_MISSING_CONFIRMATION_PASSWORD = new ConfigurationMessage(
            String.class,
            "errorRegisterMissingConfirmationPassword",
            PLUGIN_PREFIX + "&cYou must enter your current password to confirm your register"
    );

    public static final ConfigurationMessage ERROR_REGISTER_INVALID_CONFIRMATION_PASSWORD = new ConfigurationMessage(
            String.class,
            "errorRegisterInvalidConfirmationPassword",
            PLUGIN_PREFIX + "&cYour confirmation password does not match."
    );


    public static final ConfigurationMessage REQUIRE_REGISTER_MESSAGE = new ConfigurationMessage(
            String.class,
            "requireRegisterMessage",
            "Require Register"
    );

    public static final ConfigurationMessage REQUIRE_REGISTER_TITLE = new ConfigurationMessage(
            String.class,
            "requireRegisterTitle",
            "&6&lPlease Register"
    );

    public static final ConfigurationMessage REQUIRE_REGISTER_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "requireRegisterSubTitle",
            "&7/register <password> <captcha>"
    );

    public static final ConfigurationKey<Integer> REQUIRE_REGISTER_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "requireRegisterFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> REQUIRE_REGISTER_STAY = new ConfigurationKey<>(
            Integer.class,
            "requireRegisterStay",
            9999
    );

    public static final ConfigurationKey<Integer> REQUIRE_REGISTER_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "requireRegisterFadeOut",
            20
    );

    public static final ConfigurationMessage REQUIRE_LOGIN_MESSAGE = new ConfigurationMessage(
            String.class,
            "requireLoginMessage",
            "Require Login"
    );

    public static final ConfigurationMessage REQUIRE_LOGIN_TITLE = new ConfigurationMessage(
            String.class,
            "requireLoginTitle",
            "&6&lPlease Login"
    );

    public static final ConfigurationMessage REQUIRE_LOGIN_SUB_TITLE = new ConfigurationMessage(
            String.class,
            "requireLoginSubTitle",
            "&7/login <password>"
    );

    public static final ConfigurationKey<Integer> REQUIRE_LOGIN_FADE_IN = new ConfigurationKey<>(
            Integer.class,
            "requireLoginFadeIn",
            20
    );

    public static final ConfigurationKey<Integer> REQUIRE_LOGIN_STAY = new ConfigurationKey<>(
            Integer.class,
            "requireLoginStay",
            9999
    );

    public static final ConfigurationKey<Integer> REQUIRE_LOGIN_FADE_OUT = new ConfigurationKey<>(
            Integer.class,
            "requireLoginFadeOut",
            20
    );

    public ConfigurationMessage getErrorUserNotExists() {
        return ERROR_USER_NOT_EXISTS;
    }

    public ConfigurationMessage getErrorRegisterPremium() {
        return ERROR_REGISTER_PREMIUM;
    }

    public ConfigurationMessage getErrorAlreadyRegistered() {
        return ERROR_ALREADY_REGISTERED;
    }

    public ConfigurationMessage getErrorRegisterUsage() {
        return ERROR_REGISTER_USAGE;
    }

    public ConfigurationMessage getAuthorizationSuccessRegisterMessage() {
        return AUTHORIZATION_SUCCESS_REGISTER_MESSAGE;
    }

    public ConfigurationMessage getAuthorizationSuccessRegisterTitle() {
        return AUTHORIZATION_SUCCESS_REGISTER_TITLE;
    }

    public ConfigurationMessage getAuthorizationSuccessRegisterSubTitle() {
        return AUTHORIZATION_SUCCESS_REGISTER_SUB_TITLE;
    }

    public Integer getAuthorizationSuccessRegisterFadeIn() {
        return AUTHORIZATION_SUCCESS_REGISTER_FADE_IN.getValue();
    }

    public Integer getAuthorizationSuccessRegisterStay() {
        return AUTHORIZATION_SUCCESS_REGISTER_STAY.getValue();
    }

    public Integer getAuthorizationSuccessRegisterFadeOut() {
        return AUTHORIZATION_SUCCESS_REGISTER_FADE_OUT.getValue();
    }

    public ConfigurationMessage getAuthorizationSuccessLoginMessage() {
        return AUTHORIZATION_SUCCESS_LOGIN_MESSAGE;
    }

    public ConfigurationMessage getAuthorizationSuccessLoginTitle() {
        return AUTHORIZATION_SUCCESS_LOGIN_TITLE;
    }

    public ConfigurationMessage getAuthorizationSuccessLoginSubTitle() {
        return AUTHORIZATION_SUCCESS_LOGIN_SUB_TITLE;
    }

    public Integer getAuthorizationSuccessLoginFadeIn() {
        return AUTHORIZATION_SUCCESS_LOGIN_FADE_IN.getValue();
    }

    public Integer getAuthorizationSuccessLoginStay() {
        return AUTHORIZATION_SUCCESS_LOGIN_STAY.getValue();
    }

    public Integer getAuthorizationSuccessLoginFadeOut() {
        return AUTHORIZATION_SUCCESS_LOGIN_FADE_OUT.getValue();
    }

    public ConfigurationMessage getAuthorizationPremiumMessage() {
        return AUTHORIZATION_PREMIUM_MESSAGE;
    }

    public ConfigurationMessage getAuthorizationPremiumTitle() {
        return AUTHORIZATION_PREMIUM_TITLE;
    }

    public ConfigurationMessage getAuthorizationPremiumSubTitle() {
        return AUTHORIZATION_PREMIUM_SUB_TITLE;
    }

    public Integer getAuthorizationPremiumFadeIn() {
        return AUTHORIZATION_PREMIUM_FADE_IN.getValue();
    }

    public Integer getAuthorizationPremiumStay() {
        return AUTHORIZATION_PREMIUM_STAY.getValue();
    }

    public Integer getAuthorizationPremiumFadeOut() {
        return AUTHORIZATION_PREMIUM_FADE_OUT.getValue();
    }

    public ConfigurationMessage getAuthorizationSessionMessage() {
        return AUTHORIZATION_SESSION_MESSAGE;
    }

    public ConfigurationMessage getAuthorizationSessionTitle() {
        return AUTHORIZATION_SESSION_TITLE;
    }

    public ConfigurationMessage getAuthorizationSessionSubTitle() {
        return AUTHORIZATION_SESSION_SUB_TITLE;
    }

    public Integer getAuthorizationSessionFadeIn() {
        return AUTHORIZATION_SESSION_FADE_IN.getValue();
    }

    public Integer getAuthorizationSessionStay() {
        return AUTHORIZATION_SESSION_STAY.getValue();
    }

    public Integer getAuthorizationSessionFadeOut() {
        return AUTHORIZATION_SESSION_FADE_OUT.getValue();
    }

    public ConfigurationMessage getRequireRegisterMessage() {
        return REQUIRE_REGISTER_MESSAGE;
    }

    public ConfigurationMessage getRequireRegisterTitle() {
        return REQUIRE_REGISTER_TITLE;
    }

    public ConfigurationMessage getRequireRegisterSubTitle() {
        return REQUIRE_REGISTER_SUB_TITLE;
    }

    public Integer getRequireRegisterFadeIn() {
        return REQUIRE_REGISTER_FADE_IN.getValue();
    }

    public Integer getRequireRegisterStay() {
        return REQUIRE_REGISTER_STAY.getValue();
    }

    public Integer getRequireRegisterFadeOut() {
        return REQUIRE_REGISTER_FADE_OUT.getValue();
    }

    public ConfigurationMessage getRequireLoginMessage() {
        return REQUIRE_LOGIN_MESSAGE;
    }

    public ConfigurationMessage getRequireLoginTitle() {
        return REQUIRE_LOGIN_TITLE;
    }

    public ConfigurationMessage getRequireLoginSubTitle() {
        return REQUIRE_LOGIN_SUB_TITLE;
    }

    public Integer getRequireLoginFadeIn() {
        return REQUIRE_LOGIN_FADE_IN.getValue();
    }

    public Integer getRequireLoginStay() {
        return REQUIRE_LOGIN_STAY.getValue();
    }

    public Integer getRequireLoginFadeOut() {
        return REQUIRE_LOGIN_FADE_OUT.getValue();
    }
}