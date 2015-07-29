package com.lamarstudio.oauth.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.lamarstudio.oauth.model.Constant;

/**
 * 
 * <p>
 * Read config info from oauth_config.properties
 * </p>
 *
 * @author WeiWei
 * @Date 2015-7-21
 *
 */
public class OAuthConfig {

    private static Properties prop = new Properties();

    static {
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("oauth_config.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return prop.getProperty(key);
    }

    public static String getCodeUrl(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_CODE_URL + oauthSupplier);
    }
    
    public static String getOpenIdUrl(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_OPENID_URL + oauthSupplier);
    }
    
    public static String getTokenUrl(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_ACCESS_TOKEN_URL + oauthSupplier);
    }
    
    public static String getUserInfoUrl(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_USER_URL + oauthSupplier);
    }

    public static String getClientID(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_CLIENT_ID + oauthSupplier);
    }

    public static String getClientSecret(String oauthSupplier) {
        return prop.getProperty(Constant.OAUTH_CLIENT_SECRET + oauthSupplier);
    }

    public static String getRedirectURI() {
        return prop.getProperty(Constant.OAUTH_REDIRECT_URI);
    }

}
