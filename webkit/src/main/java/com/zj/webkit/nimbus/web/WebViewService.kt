package com.zj.webkit.nimbus.web

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.activity.ComponentActivity
import com.zj.webkit.*
import com.zj.webkit.aidl.WebViewAidlIn
import com.zj.webkit.nimbus.client.ClientBridge
import com.zj.webkit.getProcessName
import java.lang.NullPointerException

class WebViewService : Service() {

    companion object {
        const val ACTION_NAME = "com.zj.web.service"
        private var commendListener: ((cmd: String?, level: Int, callId: Int, content: String?) -> Int)? = null

        fun isClientBind(): Boolean {
            return ClientBridge.isClientInit()
        }

        fun postToClient(cmd: String, level: Int, callId: Int, content: String?) {
            if (ClientBridge.isClientInit()) {
                CCWebLogUtils.log("post to Client ---> \ncmd = $cmd \nlevel = $level \ncallId = $callId \ncontent = $content")
                CCWebLogUtils.log("result form Client ---> ${ClientBridge.postToClient(cmd, level, callId, content)}")
            }
        }

        fun destroyService() {
            ClientBridge.postToClient(SERVICE_DESTROY, 0, 0, "")
        }

        fun registerCommendListener(token: ComponentActivity, l: (cmd: String?, level: Int, callId: Int, content: String?) -> Int) {
            token.lifecycle.addObserver(ComponentOwner())
            this.commendListener = l
        }

        private val appAidlIn = object : WebViewAidlIn.Stub() {
            override fun dispatchCommend(cmd: String?, level: Int, callId: Int, content: String?): Int {
                var result = HANDLE_OK
                if (cmd == SERVICE_PING) {
                    CCWebLogUtils.log("form server: ping received callId = $callId   ${System.currentTimeMillis()}")
                    postToClient(SERVICE_PONG, level, callId, content)
                    return result
                }
                result = commendListener?.invoke(cmd, level, callId, content) ?: { destroyService();HANDLE_ABANDON }.invoke()
                if (result != HANDLE_OK) {
                    CCWebLogUtils.log("form server : the commend executor is not exits")
                }
                CCWebLogUtils.log("form server : $cmd     $level    $callId    $content")
                return result
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        ClientBridge.destroy(this, true)
        ClientBridge.bindClientService(this) {
            val target = intent?.getStringExtra("target") ?: throw NullPointerException("the target activity is not found!")
            CCWebLogUtils.log("web running in  ${getProcessName(this)} , start target : $target")
            val i = Intent(target)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            if (isClientBind()) postToClient(SERVICE_INIT, 0, CALL_ID_SERVER_STARTED, "")
        }
        return appAidlIn
    }

    override fun onDestroy() {
        super.onDestroy()
        CCWebLogUtils.log("web service destroyed")
        commendListener = null
        ClientBridge.destroy(this)
    }
}