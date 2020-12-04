package com.zj.webkit.proctol

import android.net.http.SslError
import android.webkit.WebResourceError
import android.webkit.WebResourceResponse

enum class WebErrorType {

    HTTP_ERROR, SSL_ERROR, RESOURCE_ERROR;

    var httpError: WebResourceResponse? = null
    var sslError: SslError? = null
    var resourceError: WebResourceError? = null

    internal fun onHttpError(httpError: WebResourceResponse?): WebErrorType {
        this.httpError = httpError
        return this
    }

    internal fun onSSLError(sslError: SslError?): WebErrorType {
        this.sslError = sslError
        return this
    }

    internal fun onResourceError(resourceError: WebResourceError?): WebErrorType {
        this.resourceError = resourceError
        return this
    }

}