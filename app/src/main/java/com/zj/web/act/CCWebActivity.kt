package com.zj.web.act

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zj.web.R
import com.zj.web.view.CusWebView
import com.zj.webkit.nimbus.web.WebViewService
import java.lang.IllegalArgumentException

open class CCWebActivity : AppCompatActivity() {

    private lateinit var webView: CusWebView
    private lateinit var adView: TextView
    private lateinit var crashView: TextView
    private lateinit var anrView: TextView
    private lateinit var oomView: TextView
    private lateinit var strangeView: TextView

    private val commendListener = { cmd: String?, level: Int, callId: Int, content: String? ->
        when (cmd) {
            "adReady" -> {
                runOnUiThread {
                    adView.visibility = View.VISIBLE
                    adView.setOnClickListener {
                        WebViewService.postToClient("showAd", 2, 100273, "")
                    }
                }
            }
        }
        200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cc_web_activity_content)
        WebViewService.registerCommendListener(this, commendListener)
        webView = findViewById(R.id.cus_web_view)
        adView = findViewById(R.id.cc_web_show_ad)
        crashView = findViewById(R.id.cc_web_show_crash)
        anrView = findViewById(R.id.cc_web_show_anr)
        oomView = findViewById(R.id.cc_web_show_oom)
        strangeView = findViewById(R.id.cc_web_show_strange)
        webView.loadUrl("https://www.baidu.com")
        WebViewService.postToClient("loadAd", 2, 19263, "")
        crashView.setOnClickListener {
            throw IllegalArgumentException("test illegal crash")
        }
        anrView.setOnClickListener {
            while (true) {
                Thread.sleep(100)
            }
        }
        oomView.setOnClickListener {
            val lst = mutableListOf<TextView>()

            while (true) {
                lst.add(TextView(this))
            }
        }
        strangeView.setOnClickListener {
            val lst = mutableListOf<Bitmap>()
            while (true) {
                try {
                    Thread.sleep(16)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val bmp = BitmapFactory.decodeResource(resources, R.drawable.asd)
                lst.add(bmp)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}