package com.zj.webkit.nimbus.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.zj.webkit.DEFAULT_I
import com.zj.webkit.HANDLE_ABANDON
import com.zj.webkit.aidl.WebViewAidlIn
import com.zj.webkit.nimbus.web.WebViewService.Companion.logToClient
import kotlin.system.exitProcess


internal object ClientBridge {

    private var clientIn: WebViewAidlIn? = null
    private var onClientBind: ((String) -> Unit)? = null
    private var isClientRunning = false
    private var nextBind: BindIn? = null
    private var target: String = ""
    private var context: Context? = null
    private val serviceConn = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logToClient("On client service connected")
            clientIn = WebViewAidlIn.Stub.asInterface(service)
            onClientBind?.invoke(target)
        }
    }

    fun postToClient(cmd: String, level: Int = DEFAULT_I, callId: Int = DEFAULT_I, content: String? = ""): Int {
        return clientIn?.dispatchCommend(cmd, level, callId, content) ?: HANDLE_ABANDON
    }

    fun isClientInit(): Boolean {
        return clientIn != null
    }

    /**
     * Called when the service is determined to be terminated or lost
     * If accompanied by a start command, you need to try to connect to the new service after destroying the previous service
     * */
    fun onServiceDestroyed() {
        isClientRunning = false
        clientIn = null
        onClientBind = null
        nextBind?.let { bi ->
            bindClientService(bi.context, bi.target, bi.onClientBind)
        } ?: logToClient("On client service disconnected")
        nextBind = null
        exitProcess(0)
    }

    /**
     *
     * */
    fun bindClientService(context: Context, target: String, onClientBind: (String) -> Unit) {
        if (isClientRunning && context == this.context) {
            logToClient("the client is already running !! ");return
        } else if (isClientRunning) {
            nextBind = BindIn(context, target, onClientBind);destroy();return
        }
        isClientRunning = true
        this.context = context
        this.target = target
        this.onClientBind = onClientBind
        val intent = Intent(ClientService.ACTION_NAME)
        intent.`package` = context.packageName
        context.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
    }

    fun destroy() {
        try {
            logToClient("unbind client service and disconnecting")
            this.context?.unbindService(serviceConn)
            context = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private data class BindIn(val context: Context, val target: String, val onClientBind: (String) -> Unit)
}