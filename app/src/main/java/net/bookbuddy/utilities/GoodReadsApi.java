package net.bookbuddy.utilities;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

/**
 * Created by Jenni on 2.5.2017.
 */

/**
 * Handles OAuth1 to GoodReads Api as per ScribeJava Custom Api guidelines.
 */
public class GoodReadsApi extends DefaultApi10a {

    /**
     * Authorization url.
     */
    private static final String AUTHORIZE_URL = "http://www.goodreads.com/oauth/authorize?oauth_token=%s";

    /**
     * Default constructor.
     */
    protected GoodReadsApi() {
    }

    /**
     * Instance holder.
     */
    private static class InstanceHolder {
        private static final GoodReadsApi INSTANCE = new GoodReadsApi();
    }

    /**
     * Returns instance.
     *
     * @return GoodReadsApi
     */
    public static GoodReadsApi instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Returns access token endpoint.
     *
     * @return String access token endpoint url
     */
    @Override
    public String getAccessTokenEndpoint() {
        return "http://www.goodreads.com/oauth/access_token";
    }

    /**
     * Returns request token endpoint.
     *
     * @return String request token endpoint url
     */
    @Override
    public String getRequestTokenEndpoint() {
        return "http://www.goodreads.com/oauth/request_token";
    }

    /**
     * Returns authorization url.
     *
     * @param requestToken request token
     * @return String authorization url
     */
    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }
}