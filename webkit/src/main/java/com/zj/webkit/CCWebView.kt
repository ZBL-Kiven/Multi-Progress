package com.zj.webkit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.*
import com.zj.webkit.proctol.WebErrorType
import com.zj.webkit.proctol.WebJavaScriptIn

@Suppress("unused")
abstract class CCWebView<T : WebJavaScriptIn> @JvmOverloads constructor(c: Context, attrs: AttributeSet? = null, def: Int = 0) : WebView(c, attrs, if (def != 0) def else android.R.attr.webViewStyle) {

    companion object {
        private lateinit var cacheFileDir: String
        private val isMultiProcessSuffix = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        /**
         * must call [onAppAttached] first in [android.app.Application.onCreate] to specify a separate CacheDir path in different processes.
         * */
        fun onAppAttached(c: Context) {
            val appDir = c.externalCacheDir?.absolutePath ?: c.cacheDir.absolutePath
            val pn = "web-cache"
            val suffix = getProcessName(c) ?: ""
            if (isMultiProcessSuffix) pn.plus(suffix)
            cacheFileDir = "$appDir/$pn"
            if (isMultiProcessSuffix && suffix.endsWith(":web")) {
                setDataDirectorySuffix(suffix)
            }
        }
    }

    private var isRedirect = false
    open val javaScriptEnabled = true
    open val removeSessionAuto = false
    abstract val webDebugEnable: Boolean
    abstract val javaScriptClient: T

    private val mWebViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            isRedirect = true
            val interrupt = this@CCWebView.shouldOverrideUrlLoading(view, request)
            return if (!interrupt) false else {
                if (Build.VERSION.SDK_INT >= 24) {
                    if (request?.isRedirect == true) {
                        request.url?.let { url -> view?.loadUrl(url.path) };true
                    } else false
                } else {
                    request?.url?.let { url -> view?.loadUrl(url.path) }
                    true
                }
            }
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            this@CCWebView.onLoadResource(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            this@CCWebView.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (isRedirect) return
            val w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            val h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            measure(w, h)
            this@CCWebView.onPageFinished(view, url)
        }

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            onError(WebErrorType.HTTP_ERROR.onHttpError(errorResponse), view, request)
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            super.onReceivedSslError(view, handler, error)
            onError(WebErrorType.SSL_ERROR.onSSLError(error), view, null)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            onError(WebErrorType.RESOURCE_ERROR.onResourceError(error), view, request)
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            this@CCWebView.onReceivedTitle(view, title)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            this@CCWebView.onProgressChanged(view, newProgress)
        }
    }

    init {
        isEnabled = true
        isFocusable = true
        requestFocus()
        initWebSettings()
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebSettings() {
        settings?.let {
            setWebContentsDebuggingEnabled(webDebugEnable)
            it.javaScriptEnabled = javaScriptEnabled
            it.allowFileAccess = true
            it.builtInZoomControls = false
            it.displayZoomControls = false
            it.setSupportZoom(false)
            //setting the content automatic the app screen size
            it.useWideViewPort = true
            it.loadWithOverviewMode = true
            it.cacheMode = WebSettings.LOAD_DEFAULT
            it.domStorageEnabled = true
            it.databaseEnabled = true
            it.setAppCacheEnabled(true)
            //set the app cache dir path ,the webView are only support set a once
            it.setAppCachePath(cacheFileDir)
            webViewClient = mWebViewClient
            webChromeClient = mWebChromeClient
            //sync cookies
            CookieManager.getInstance().flush()
            //always allow http & https content mix
            it.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            if (removeSessionAuto) CookieManager.getInstance().removeSessionCookies(null)
            post {
                this.addJavascriptInterface(javaScriptClient, javaScriptClient.name)
            }
        }
    }

    open fun onPageFinished(view: WebView, url: String) {}

    open fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {}

    open fun onLoadResource(view: WebView?, url: String?) {}

    open fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.let {
            if (it.isForMainFrame || it.url.scheme?.startsWith("http") == true) return false
        }
        return true
    }

    open fun onReceivedTitle(view: WebView?, title: String?) {}

    open fun onProgressChanged(view: WebView?, newProgress: Int) {}

    open fun onError(type: WebErrorType, view: WebView?, request: WebResourceRequest?) {
        val s = when (type) {
            WebErrorType.HTTP_ERROR -> type.httpError?.reasonPhrase
            WebErrorType.SSL_ERROR -> type.sslError?.url
            WebErrorType.RESOURCE_ERROR -> type.resourceError?.toString()
        }
        CCWebLogUtils.log("${type.name} : desc = $s")
    }

    fun destroyWebView() {
        stopLoading()
        clearAnimation()
        clearFormData()
        clearHistory()
        clearDisappearingChildren()
        removeAllViews()
        (parent as? ViewGroup)?.removeView(this)
        super.destroy()
    }
}