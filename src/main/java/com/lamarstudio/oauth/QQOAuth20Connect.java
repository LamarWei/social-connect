package com.lamarstudio.oauth;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lamarstudio.oauth.model.Constant;
import com.lamarstudio.oauth.model.OAuthSupplier;
import com.lamarstudio.oauth.model.PostParameter;
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
    private final String CLIENT_REDIRECT_URI = OAuthConfig.getRedirectURI();

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
    private String getAccesstoken(String code) {
        String tokenResult = HttpOAuthClient.getStringByGet(this.GET_TOKEN_BY_CODE_QQ,new PostParameter[]{
        		new PostParameter("grant_type", "authorization_code"),
        		new PostParameter("client_id", this.CLIENT_ID),
        		new PostParameter("client_secret", this.CLIENT_SECRET),
        		new PostParameter("code", code),
        		new PostParameter("redirect_uri", this.CLIENT_REDIRECT_URI)
        });
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
    private String getUserID(String accesstoken) {
        String openIDResult = HttpOAuthClient.getStringByGet(this.GET_OPENID_BY_TOKEN_QQ,new PostParameter[]{
        		new PostParameter("access_token", accesstoken)});
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
    private JSONObject getUserInfo(String token, String userID) throws IOException {
        String content = null;
        content = HttpOAuthClient.getStringByGet(this.USERINFO_URL_PATTERN, new PostParameter[]{
			new PostParameter("access_token", token),
			new PostParameter("oauth_consumer_key", this.CLIENT_ID),
			new PostParameter("openid", userID),
			new PostParameter("format", "json")
		});
		JSONObject json = JSON.parseObject(content);
		return json;
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
