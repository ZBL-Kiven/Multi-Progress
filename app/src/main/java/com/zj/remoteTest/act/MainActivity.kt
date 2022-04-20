package com.zj.remoteTest.act

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.zj.remoteTest.R
import com.zj.multiProgress.nimbus.client.ClientService

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
        ClientService.startServer(this, "com.zj.remoteTest.act.RemoteActivity") {
            runOnUiThread { progress.visibility = if (it) View.VISIBLE else View.GONE }
        }
        ClientService.setLogIn(true) {
            Log.e("===== ", it)
        }
        ClientService.registerCmdListener { cmd: String?, level: Int, callId: Int, content: String? ->
            when (cmd) {
                "onAction" -> {
                    Log.e("===== ACTION -->", "$cmd")
                    ClientService.postToServer("actionResult")
                }
            }
            200
        }
    }
}