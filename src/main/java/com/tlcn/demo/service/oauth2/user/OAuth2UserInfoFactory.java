package com.tlcn.demo.service.oauth2.user;


import com.tlcn.demo.exception.OAuth2AuthenticationProcessingException;
import com.tlcn.demo.util.contant.Constant;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(Constant.GOOGLE)) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
