package com.zj.web.act

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.zj.web.R
import com.zj.webkit.nimbus.client.ClientService

class MainActivity : AppCompatActivity() {

    private val token = this::class.java.name
    private lateinit var progress: ProgressBar

//    private val appKey = "OtnCjcU7ERE0D21GRoquiQBY6YXR3YLl"
//    private val mPlacementId = "4902"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = findViewById(R.id.progress)
        findViewById<View>(R.id.hello).setOnClickListener {
            progress.visibility = View.VISIBLE
            initWeb()
        }
//        init(this, appKey, true).setPlacementId(mPlacementId).create()
//        addAdListener(object : ADListener() {
//            override fun onADAvailableChanged(type: AdType?, isAvailable: Boolean) {
//                Log.e("-----", "onAvailabilityChanged : type = $type available =  $isAvailable")
//                if (isAvailable) ClientService.postToWebService("adReady")
//            }
//
//            override fun onAdShowed(type: AdType?) {
//                Log.e("-----", "onAdShowed : type = $type")
//            }
//
//            override fun onAdShowFailed(type: AdType?, ec: String?) {
//                Log.e("-----", "onAdShowFailed  case: $ec")
//            }
//
//            override fun onAdClicked(type: AdType?) {
//                Log.e("-----", "onAdClicked  : type = $type")
//            }
//
//            override fun onAdClosed(type: AdType?) {
//                Log.e("-----", "onAdClosed : type = $type")
//            }
//
//            override fun onAdStarted(type: AdType?) {
//                Log.e("-----", "onAdStarted : type = $type")
//            }
//
//            override fun onAdEnded(type: AdType?) {
//                Log.e("-----", "onAdEnded : type = $type")
//            }
//
//            override fun onAdRewarded(type: AdType?) {
//                Log.e("-----", "onAdRewarded : type = $type")
//            }
//        })
    }

    private fun initWeb() {
        ClientService.startServer(this, "com.zj.web.act.CCWebActivity") {
            runOnUiThread { progress.visibility = if (it) View.VISIBLE else View.GONE }
        }
        ClientService.setLogIn(true) {
            Log.e("===== ", it)
        }
        ClientService.addCommendListener(token) { cmd: String?, _: Int, _: Int, _: String? ->
            when (cmd) {
                "loadAd" -> {
//                    initAds(AdType.INTERSTITIAL)
                }

                "showAd" -> {
//                    showAd(AdType.INTERSTITIAL)
                }
            }
            200
        }
    }
}