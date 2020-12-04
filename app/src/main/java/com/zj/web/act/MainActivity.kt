package com.zj.web.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.zj.admod.ADListener
import com.zj.admod.AdtMod
import com.zj.admod.base.AdType
import com.zj.web.R
import com.zj.webkit.nimbus.client.ClientService

class MainActivity : AppCompatActivity() {

    private var adInstance: AdtMod? = null
    private var currTime = 0L
    private var isClickAd = false
    private var isRewarded = false
    private val token = this::class.java.name
    private var key = "axgCENkcTZun8RlVf03YNawXiUCAROwH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAd()
        findViewById<View>(R.id.hello).setOnClickListener {
            initWeb()
        }
    }

    private fun initWeb() {
        ClientService.startWebAct(this, "com.zj.web.act.CCWebActivity")
        ClientService.addCommendListener(token) { cmd: String?, level: Int, callId: Int, content: String? ->
            when (cmd) {
                "loadAd" -> {
                    adInstance?.loadAd(AdType.INTERSTITIAL)
                }

                "showAd" -> {
                    adInstance?.showAd(AdType.INTERSTITIAL)
                }
            }
            200
        }
    }

    private fun initAd() {
        adInstance = AdtMod.with(this, key).setRetryCount(5).debugAble(true).create(object : ADListener {

            override fun initAdSuccess(p0: Array<out AdType>?) {
                this@MainActivity.onLog("init success")
            }

            override fun initAdError(p0: Array<out AdType>?, p1: String?) {
                this@MainActivity.onLog("init error case: $p1")
                //resolveError(INIT_ERROR, p1 ?: "UNKOWN ERROR")
            }

            override fun onADAvailableChanged(p0: AdType?, p1: Boolean) {
                this@MainActivity.onLog("onVideoAvailableChanged $p1")
                if (p0 == AdType.INTERSTITIAL && p1) {
                    ClientService.postToWebService("adReady", 2, 100928, "")
                }
                //advert(if (p1) ClipClapsConstant.AdvertisementEvent.load else ClipClapsConstant.AdvertisementEvent.fail, JSONObject("{type:${p0?.toTypeString()}}"))
            }

            override fun onAdStarted(p0: AdType?, p1: Int) {
                this@MainActivity.onLog("onAdStarted $p1")
            }

            override fun onAdShowFailed(p0: AdType?, p1: Int, ec: String?) {
                this@MainActivity.onLog("onVideoAdShowFailed $p1 case $ec")
                //resolveError(SHOW_ERROR, ec ?: "UNKOWN ERROR", p0)
            }

            override fun getToken(): String {
                return this@MainActivity.token
            }

            override fun onAdClicked(p0: AdType?, p1: Int) {
                this@MainActivity.onLog("onVideoAdClicked $p1")
                isClickAd = true
                //advert(ClipClapsConstant.AdvertisementEvent.click, JSONObject("{type:${p0?.toTypeString()}}"))
            }

            override fun onAdRewarded(p0: AdType?, p1: Int) {
                isRewarded = true
            }

            override fun onAdShowed(p0: AdType?, p1: Int) {
                this@MainActivity.onLog("onVideoAdShowed $p1")
                currTime = System.currentTimeMillis()
                //advert(ClipClapsConstant.AdvertisementEvent.show, JSONObject("{type:${p0?.toTypeString()}}"))
            }

            override fun onError(type: AdType?, p1: String?) {
                //resolveError(SHOW_ERROR, p1 ?: "UNKOWN ERROR", type)
            }

            override fun onAdClosed(p0: AdType?, p1: Int) {
                //消失
                val endTime = System.currentTimeMillis()
                //展示时长
                val showTime = (endTime - currTime) / 1000f
                this@MainActivity.onLog("onVideoAdClosed $p1")
                // advert(ClipClapsConstant.AdvertisementEvent.hide, JSONObject("{type:${p0?.toTypeString()},duration:${showTime},clicked:${isClickAd},rewarded:${isRewarded}}"))
            }

            override fun onAdEnded(p0: AdType?, p1: Int) {
            }
        })
    }

    private fun onLog(data: String) {
        Log.e("=====", data)
    }
}