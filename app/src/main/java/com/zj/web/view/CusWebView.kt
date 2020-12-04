package com.zj.web.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.zj.web.`in`.CusWebJI
import com.zj.webkit.CCWebView
import com.zj.webkit.proctol.WebErrorType

class CusWebView @JvmOverloads constructor(c: Context, attrs: AttributeSet? = null, def: Int = 0) : CCWebView<CusWebJI>(c, attrs, def) {


    private val webJI = CusWebJI()

    override val javaScriptClient: CusWebJI; get() = webJI

    override fun onError(type: WebErrorType, view: WebView?, request: WebResourceRequest?) {
        super.onError(type, view, request)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)

    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }
}