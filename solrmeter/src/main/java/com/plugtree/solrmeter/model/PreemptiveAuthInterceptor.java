package com.plugtree.solrmeter.model;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import java.io.IOException;

class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
        // If no auth scheme available yet, try to initialize it
        // preemptively
        if (authState.getAuthScheme() == null) {
            CredentialsProvider credsProvider = (CredentialsProvider)
                    context.getAttribute(HttpClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
            Credentials creds = credsProvider.getCredentials(authScope);
            if(creds == null){

            }
            authState.update(new BasicScheme(), creds);
        }

    }
}