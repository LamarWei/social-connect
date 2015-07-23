package com.lamarstudio.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lamarstudio.oauth.model.Constant;
import com.lamarstudio.oauth.model.OAuthSupplier;
import com.lamarstudio.oauth.utils.HttpOAuthClient;
import com.lamarstudio.oauth.utils.OAuthConfig;

public class QQOAuth20Connect {

    /**
     * init from config file
     */

    private final String GET_CODE_URI_QQ = Constant.OAUTH_URL_QQ + OAuthConfig.getCodeUrl(OAuthSupplier.QQ);
    private final String USERINFO_URL_PATTERN = Constant.OAUTH_URL_QQ + OAuthConfig.getUserInfoUrl(OAuthSupplier.QQ);
    private final String GET_TOKEN_BY_CODE_QQ = Constant.OAUTH_URL_QQ + OAuthConfig.getTokenUrl(OAuthSupplier.QQ);
    private final String GET_OPENID_BY_TOKEN_QQ = Constant.OAUTH_URL_QQ + OAuthConfig.getOpenIdUrl(OAuthSupplier.QQ);

    private final String CLIENT_ID = OAuthConfig.getClientID(OAuthSupplier.QQ);
    private final String CLIENT_SECRET = OAuthConfig.getClientSecret(OAuthSupplier.QQ);
    private final String CLIENT_REDIRECT_URI = OAuthConfig.getRedirectURI(OAuthSupplier.QQ);

    /**
     * generate OAuth authorization url
     * @param state
     * @return
     */
    public String generateOAuthURI(String state) {
        return String.format(GET_CODE_URI_QQ, CLIENT_ID, state, CLIENT_REDIRECT_URI);
    }

    /**
     * <p>
     * get Access_Token
     * </p>
     * 
     * @param code
     * @return
     */
    public String getAccesstoken(String code) {
        String tokenUrl = String.format(GET_TOKEN_BY_CODE_QQ, CLIENT_ID, CLIENT_SECRET, code, CLIENT_REDIRECT_URI);
        String tokenResult = HttpOAuthClient.getStringByGet(tokenUrl);
        Matcher m = Pattern.compile("access_token=(\\w+)").matcher(tokenResult);
        String accesstoken = StringUtils.EMPTY;
        if (m.find()) {
            accesstoken = m.group(1);
        }
        return accesstoken;
    }

    /**
     * <p>
     * get openid by access_token
     * </p>
     * 
     * @param accesstoken
     * @return
     */
    public String getUserID(String accesstoken) {
        String openIDUrl = String.format(GET_OPENID_BY_TOKEN_QQ, accesstoken);
        String openIDResult = HttpOAuthClient.getStringByGet(openIDUrl);
        if (StringUtils.isNotBlank(openIDResult)) {
            int startIndex = StringUtils.indexOf(openIDResult, '(');
            int endIndex = StringUtils.indexOf(openIDResult, ')');
            openIDResult = StringUtils.substring(openIDResult, startIndex + 1, endIndex);
            JSONObject openIDObj = JSON.parseObject(openIDResult);
            return openIDObj.getString("openid");
        }
        return null;
    }

    /**
     * <p>
     * get user by access_token and openid
     * </p>
     * 
     * @param token
     * @param userID openid
     * @return user JSONObject
     */
    public JSONObject getUserInfo(String token, String userID) throws IOException {
        String url = String.format(USERINFO_URL_PATTERN, token, CLIENT_ID, userID);
        String content = null;
        try {
            content = IOUtils.toString(new URI(url), "utf-8");
            JSONObject json = JSON.parseObject(content);
            return json;
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    /**
     * get user directly by code
     * 
     * @param code
     * @return user JSONObject
     */
    public JSONObject getUserJSONObject(String code) {
        String access_token = getAccesstoken(code);
        String openid = getUserID(access_token);
        JSONObject json = null;
        try {
            json = getUserInfo(access_token, openid);
        } catch (IOException e) {
            e.printStackTrace();
        } 
        if(null != json){
            json.put("openid", openid);
        }
        return json;
    }

}
