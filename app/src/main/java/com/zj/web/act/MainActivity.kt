package com.zj.web.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.zj.web.R
import com.zj.webkit.nimbus.client.ClientService

class MainActivity : AppCompatActivity() {

    private val token = this::class.java.name
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = findViewById(R.id.progress)
        findViewById<View>(R.id.hello).setOnClickListener {
            progress.visibility = View.VISIBLE
            initWeb()
        }
    }

    private fun initWeb() {
        ClientService.startServer(this, "com.zj.web.act.CCWebActivity") {
            runOnUiThread { progress.visibility = if (it) View.VISIBLE else View.GONE }
        }
        ClientService.setLogIn(true) {
            Log.e("===== ", it)
        }
        ClientService.addCommendListener(token) { cmd: String?, level: Int, callId: Int, content: String? ->
            when (cmd) {
                "loadAd" -> {
                    ClientService.postToWebService("adReady")
                }

                "showAd" -> {
                    this@MainActivity.onLog("onVideoAdShowed")
                }
            }
            200
        }
    }

    private fun onLog(data: String) {
        Log.e("=====", data)
    }
}