package com.lamarstudio.oauth.model;

public class Constant {
    
    // Sina Weibo auth2.0 domain
    public static final String OAUTH_URL_WEIBO = System.getProperty("OAUTH_WEIBO", "https://api.weibo.com");
    // QQ auth2.0 domain
    public static final String OAUTH_URL_QQ = System.getProperty("OAUTH_QQ", "https://graph.qq.com");

    // url parameters prefix
    public static final String OAUTH_CODE_URL = "get_code_url_";
    public static final String OAUTH_OPENID_URL = "get_openid_url_";
    public static final String OAUTH_ACCESS_TOKEN_URL = "get_token_url_";
    public static final String OAUTH_USER_URL = "get_userinfo_url_";
    public static final String OAUTH_REDIRECT_URI="redirect_URI_";
    // client info prefix
    public static final String OAUTH_CLIENT_ID="client_ID_";
    public static final String OAUTH_CLIENT_SECRET="client_SECRET_";
}
