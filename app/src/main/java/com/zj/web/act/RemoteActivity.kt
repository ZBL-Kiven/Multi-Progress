package com.zj.web.act

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zj.multiProgress.nimbus.web.RemoteService
import com.zj.web.R

open class RemoteActivity : AppCompatActivity() {

    private lateinit var adView: TextView

    private val cmdListener = { cmd: String?, _: Int, _: Int, _: String? ->
        when (cmd) {
            "actionResult" -> {
                runOnUiThread {
                    Log.e("=====", "$cmd")
                }
            }
        }
        200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cc_web_activity_content)
        adView = findViewById(R.id.cc_web_show_ad)
        RemoteService.registerCmdListener(this, cmdListener)
        adView.setOnClickListener {
            RemoteService.postToClient("onAction")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RemoteService.destroyService()
    }
}