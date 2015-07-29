package com.lamarstudio.oauth.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lamarstudio.oauth.model.PostParameter;

/**
 * 
 * <p>
 * http request class
 * </p>
 *
 * @author WeiWei
 * @Date 2015-7-21
 *
 */
public class HttpOAuthClient {

    private static HttpClient client = new HttpClient();

    /**
     * get response string by GET
     * @param uri
     * @return
     */
    public static String getStringByGet(String uri) {
        return getStringByGet(uri, null, null);
    }
    
    /**
     * get response string by GET
     * @param uri
     * @return
     */
    public static String getStringByGet(String uri,PostParameter[] params) {
        return getStringByGet(uri, null, params);
    }
    
    /**
     * get response string by GET
     * @param uri
     * @param token access_token
     * @param params query parameters
     * @return
     */
    public static String getStringByGet(String uri, String token, PostParameter[] params){
        if (null != params && params.length > 0) {
            String encodedParams = encodeParameters(params);
            if (uri.indexOf("?") != -1) {
                uri += "&" + encodedParams;
            } else {
                uri += "?" + encodedParams;
            }
        }
        GetMethod get = new GetMethod(uri);
        if(null==token||token.isEmpty()){
            return httpRequest(get, false, null);
        }
        else{
            return httpRequest(get, true, token);
        }
    }

    /**
     * get a JSONObject from GET response
     * @param uri
     * @param token
     * @param params
     * @return
     */
    public static JSONObject getJSONObjectByGet(String uri, String token, PostParameter[] params) {
        return JSON.parseObject(getStringByGet(uri, token, params));
    }

    /**
     * get response string by POST
     * @param uri
     * @param params
     * @param WithTokenHeader add access_token to request header or not
     * @param token access_token
     * @return
     */
    public static String getStringByPost(String uri, PostParameter[] params, boolean WithTokenHeader, String token) {
        PostMethod post = new PostMethod(uri);
        for (PostParameter postParams : params) {
            post.addParameter(postParams.getName(), postParams.getValue());
        }
        return httpRequest(post);
    }

    /**
     * get a JSONObject from POST response
     * @param uri
     * @param params
     * @return
     */
    public static JSONObject getJSONObjectByPost(String uri, PostParameter[] params) {
        return JSON.parseObject(getStringByPost(uri, params, false, null));
    }

    /**
     * execute http request
     * @param method
     * @return
     */
    private static String httpRequest(HttpMethod method) {
        return httpRequest(method, false, null);
    }

    /**
     * execute http request
     * @param method
     * @param WithTokenHeader add access_token to request header or not
     * @param token access_token
     * @return
     */
    private static String httpRequest(HttpMethod method, boolean WithTokenHeader, String token) {
        int status = -1;
        String resString = StringUtils.EMPTY;
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        InetAddress ipaddr;
        try {
            ipaddr = InetAddress.getLocalHost();
            List<Header> headers = new ArrayList<Header>();
            if (WithTokenHeader) {
                if (token == null) {
                    throw new IllegalStateException("Oauth2 token is not set!");
                }
                headers.add(new Header("Authorization", "OAuth2 " + token));
                headers.add(new Header("API-RemoteIP", ipaddr.getHostAddress()));
                client.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
            }
            status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK) {
                resString = method.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resString;
    }

    /**
     * encode parameters
     * @param postParams
     * @return
     */
    public static String encodeParameters(PostParameter[] postParams) {
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < postParams.length; j++) {
            if (j != 0) {
                buf.append("&");
            }
            try {
                buf.append(URLEncoder.encode(postParams[j].getName(), "UTF-8")).append("=").append(URLEncoder.encode(postParams[j].getValue(), "UTF-8"));
            } catch (java.io.UnsupportedEncodingException neverHappen) {
            }
        }
        return buf.toString();
    }
}
