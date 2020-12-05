package com.zj.webkit.nimbus.web

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.zj.webkit.CCWebLogUtils
import com.zj.webkit.aidl.WebViewAidlIn
import java.lang.IllegalArgumentException

internal object ServerBridge {

    private var serverIn: WebViewAidlIn? = null
    private var onServiceBind: (() -> Unit)? = null
    private var isDestroyed: Boolean = false
    private val serviceConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            CCWebLogUtils.log("onServiceDisconnected")
            serverIn = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            CCWebLogUtils.log("On web view service connected")
            serverIn = WebViewAidlIn.Stub.asInterface(service)
            onServiceBind?.invoke()
        }
    }

    fun postToService(cmd: String, level: Int, callId: Int, content: String?) {
        serverIn?.dispatchCommend(cmd, level, callId, content) ?: if (!isDestroyed) throw IllegalArgumentException("the server in may not initialized ,has you call bindWebViewService before?")
    }

    fun isServerInit(): Boolean {
        return serverIn != null
    }

    fun bindWebViewService(context: Context, target: String, onServiceBind: () -> Unit) {
        this.onServiceBind = onServiceBind
        isDestroyed = false
        val intent = Intent(WebViewService.ACTION_NAME)
        intent.`package` = context.packageName
        intent.putExtra("target", target)
        context.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
    }

    internal fun destroy(context: Context, isStart: Boolean = false) {
        try {
            isDestroyed = true
            context.unbindService(serviceConn)
            CCWebLogUtils.log("unbind service and disconnected")
        } catch (e: Exception) {
            if (!isStart) Log.e("=====", "destroy: unbind server service error case : ${e.message}", )
            e.printStackTrace()
        }
        onServiceBind = null
        serverIn = null
    }
}