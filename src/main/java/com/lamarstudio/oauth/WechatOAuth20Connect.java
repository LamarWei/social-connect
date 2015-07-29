package com.lamarstudio.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lamarstudio.oauth.model.Constant;
import com.lamarstudio.oauth.model.OAuthSupplier;
import com.lamarstudio.oauth.model.PostParameter;
import com.lamarstudio.oauth.utils.HttpOAuthClient;
import com.lamarstudio.oauth.utils.OAuthConfig;

public class WechatOAuth20Connect {
	private final String GET_CODE_URL = OAuthConfig.getCodeUrl(OAuthSupplier.WECHAT);
	private final String GET_ACCESS_TOKEN_URL = Constant.OAUTH_URL_WECHAT + OAuthConfig.getTokenUrl(OAuthSupplier.WECHAT);
	private final String GET_USER_URL = Constant.OAUTH_URL_WECHAT + OAuthConfig.getUserInfoUrl(OAuthSupplier.WECHAT);

	private final String CLIENT_ID = OAuthConfig.getClientID(OAuthSupplier.WECHAT);
	private final String CLIENT_SECRET = OAuthConfig.getClientSecret(OAuthSupplier.WECHAT);
	private final String CLIENT_REDIRECT_URI = OAuthConfig.getRedirectURI();

	/**
	 * Generate wechat oauth2.0 authorization page url
	 * @param state
	 * @return
	 */
	public String generateOAuthURI(String state) {
		return String.format(GET_CODE_URL, CLIENT_ID, CLIENT_REDIRECT_URI, state);
	}

	/**
	 * Get access_token and openid
	 * @param code
	 * @return {"access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN",
	 * 			"openid":"OPENID","scope":"SCOPE","unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"}
	 */
	private JSONObject getAccesstoken(String code) {
		String tokenResult = HttpOAuthClient.getStringByGet(GET_ACCESS_TOKEN_URL,new PostParameter[]{
				new PostParameter("appid", this.CLIENT_ID),
				new PostParameter("secret", this.CLIENT_SECRET),
				new PostParameter("code", code),
				new PostParameter("grant_type", "authorization_code"),
		});
		JSONObject json = JSON.parseObject(tokenResult);
		if (null == json) {
			return null;
		} else {
			return json;
		}
	}

	/**
	 * Get user info
	 * @param code
	 * @return
	 */
	public JSONObject getUserJSONObject(String code) {
		JSONObject obj = null;
		obj = this.getAccesstoken(code);
		if (null != obj) {
			String userResult = HttpOAuthClient.getStringByGet(GET_USER_URL,new PostParameter[]{
					new PostParameter("access_token", obj.getString("access_token")),
					new PostParameter("openid", obj.getString("openid"))
			});
			return JSON.parseObject(userResult);
		} else {
			return null;
		}
	}
}
