package com.zj.webkit.nimbus.web

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.zj.webkit.CCWebLogUtils
import com.zj.webkit.DEFAULT_I
import com.zj.webkit.HANDLE_ABANDON
import com.zj.webkit.aidl.WebViewAidlIn
import com.zj.webkit.nimbus.client.ClientBridge
import java.lang.IllegalArgumentException

internal object ServerBridge {

    private var context: Context? = null
    private var serverIn: WebViewAidlIn? = null
    private var onServiceBind: ((Boolean) -> Unit)? = null
    private var isDestroyed: Boolean = false
    private var nextBind: BindIn? = null
    private var isServerRunning = false
    private val serviceConn = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            onServiceDestroyed()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            CCWebLogUtils.log("On server service connected")
            serverIn = WebViewAidlIn.Stub.asInterface(service)
            onServiceBind?.invoke(true)
        }
    }

    /**
     *Send a command to the remote service
     * */
    fun postToService(cmd: String, level: Int = DEFAULT_I, callId: Int = DEFAULT_I, content: String? = ""): Int {
        return serverIn?.dispatchCommend(cmd, level, callId, content) ?: if (!isDestroyed) throw IllegalArgumentException("the server in may not initialized ,has you call bindWebViewService before?") else HANDLE_ABANDON
    }

    /**
     * Return whether the remote service is connected at current
     * */
    fun isServerInit(): Boolean {
        return serverIn != null
    }

    /**
     * Connect and start the remote service, Bridge only supports binding a single service
     * */
    fun bindWebViewService(context: Context, target: String, onServiceBind: (Boolean) -> Unit) {
        if (isServerRunning && this.context == context) {
            onServiceBind(false);CCWebLogUtils.log("the server is already running !! ");return
        } else if (isServerRunning) {
            nextBind = BindIn(context, target, onServiceBind);destroy(true);return
        }
        isServerRunning = true
        this.context = context
        this.onServiceBind = onServiceBind
        isDestroyed = false
        val intent = Intent(WebViewService.ACTION_NAME)
        intent.`package` = context.packageName
        intent.putExtra("target", target)
        context.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
    }

    /**
     * Called when the service is determined to be terminated or lost
     * If accompanied by a start command, you need to try to connect to the new service after destroying the previous service
     * */
    fun onServiceDestroyed() {
        isServerRunning = false
        ClientBridge.destroy()
        serverIn = null
        context = null
        nextBind?.let { bi ->
            onServiceBind = null
            bindWebViewService(bi.context, bi.target, bi.onServiceBind)
        } ?: {
            CCWebLogUtils.log("On server service disconnected")
            onServiceBind?.invoke(false)
            onServiceBind = null
        }.invoke()
        nextBind = null
    }

    /**
     * Destroy current running remote service
     * */
    internal fun destroy(isStart: Boolean = false) {
        isDestroyed = true
        try {
            CCWebLogUtils.log("unbind server service and disconnecting")
            context?.unbindService(serviceConn)
        } catch (e: Exception) {
            if (!isStart) Log.e("=====", "destroy: unbind server service error case : ${e.message}", )
            serviceConn.onServiceDisconnected(null)
        }
    }

    private data class BindIn(val context: Context, val target: String, val onServiceBind: (Boolean) -> Unit)
}