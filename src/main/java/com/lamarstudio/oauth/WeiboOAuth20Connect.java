package com.lamarstudio.oauth;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.lamarstudio.oauth.model.Constant;
import com.lamarstudio.oauth.model.OAuthSupplier;
import com.lamarstudio.oauth.model.PostParameter;
import com.lamarstudio.oauth.utils.HttpOAuthClient;
import com.lamarstudio.oauth.utils.OAuthConfig;

public class WeiboOAuth20Connect{

    private final String GET_CODE_URL = Constant.OAUTH_URL_WEIBO + OAuthConfig.getCodeUrl(OAuthSupplier.WEIBO);
    private final String GET_ACCESS_TOKEN_URL = Constant.OAUTH_URL_WEIBO + OAuthConfig.getTokenUrl(OAuthSupplier.WEIBO);
    private final String GET_USER_URL = Constant.OAUTH_URL_WEIBO + OAuthConfig.getUserInfoUrl(OAuthSupplier.WEIBO);
    
    private final String CLIENT_ID=OAuthConfig.getClientID(OAuthSupplier.WEIBO);
    private final String CLIENT_SECRET=OAuthConfig.getClientSecret(OAuthSupplier.WEIBO);
    private final String CLIENT_REDIRECT_URI=OAuthConfig.getRedirectURI(OAuthSupplier.WEIBO);

    public String generateOAuthURI(String state) {
        return String.format(GET_CODE_URL, CLIENT_ID, state, CLIENT_REDIRECT_URI);
    }
    
    /**
     * get authorized user's uid and access_token
     * @param code
     * @return jsonObject{"access_token": "ACCESS_TOKEN","expires_in": 1234,"remind_in":"798114","uid":"uid"}
     * @throws IOException
     */
    public JSONObject getAccessToken(String code) throws IOException {
        return HttpOAuthClient.getJSONObjectByPost(GET_ACCESS_TOKEN_URL, new PostParameter[]{
                new PostParameter("client_id", CLIENT_ID),
                new PostParameter("client_secret", CLIENT_SECRET),
                new PostParameter("grant_type", "authorization_code"),
                new PostParameter("code", code),
                new PostParameter("redirect_uri", CLIENT_REDIRECT_URI)
        });
    }

    /**
     * get user detail info
     * @param token access_token
     * @param uid uid
     * @return
     * @throws IOException
     */
    public JSONObject getUserObject(String token, String uid) throws IOException {
        return HttpOAuthClient.getJSONObjectByGet(GET_USER_URL, token, 
                new PostParameter[]{new PostParameter("uid", uid)});
    }
    
    /**
     * get user in one step
     * @param code
     * @return
     */
    public JSONObject getUserInOne(String code){
        try {
            JSONObject json = getAccessToken(code);
            return getUserObject(json.getString("access_token"), json.getString("uid"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
