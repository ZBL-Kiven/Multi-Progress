package com.zj.webkit.nimbus.client

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.zj.webkit.CCWebLogUtils
import com.zj.webkit.aidl.WebViewAidlIn

internal object ClientBridge {

    private var clientIn: WebViewAidlIn? = null
    private var onClientBind: (() -> Unit)? = null
    private val serviceConn = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            clientIn = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            CCWebLogUtils.log("On Client service connected")
            clientIn = WebViewAidlIn.Stub.asInterface(service)
            onClientBind?.invoke()
        }
    }

    fun postToClient(cmd: String, level: Int, callId: Int, content: String?) {
        clientIn?.dispatchCommend(cmd, level, callId, content)
    }

    fun isClientInit(): Boolean {
        return clientIn != null
    }

    fun bindClientService(service: Service,onClientBind: () -> Unit) {
        this.onClientBind = onClientBind
        val intent = Intent(ClientService.ACTION_NAME)
        intent.`package` = service.packageName
        service.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
    }

    fun destroy(service: Service, isStart: Boolean = false) {
        try {
            service.unbindService(serviceConn)
        } catch (e: Exception) {
            if (!isStart) Log.e("=====", "destroy: unbind  client service error case : ${e.message}", )
            e.printStackTrace()
        }
        clientIn = null
    }
}