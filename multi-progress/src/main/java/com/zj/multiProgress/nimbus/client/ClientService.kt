package com.zj.multiProgress.nimbus.client

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.zj.multiProgress.*
import com.zj.multiProgress.aidl.WebViewAidlIn
import com.zj.multiProgress.exception.TargetNotFoundException
import com.zj.multiProgress.nimbus.web.ServerBridge

@Suppress("unused")
class ClientService : Service() {

    companion object {
        internal const val ACTION_NAME = "com.zj.multi.client"
        private var commendListener: ((cmd: String?, level: Int, callId: Int, content: String?) -> Int)? = null
        private var onServiceBind: ((onBind: Boolean) -> Unit)? = null
        private val handler = Handler(Looper.getMainLooper()) {
            return@Handler if (it.what == SERVICE_HEARTBEATS_CALL_ID) {
                if (ServerBridge.isServerInit()) {
                    ping()
                } else {
                    nextPing()
                }
                true
            } else false
        }

        fun setLogIn(logAble: Boolean, logIn: (s: String) -> Unit) {
            LogUtils.setLogIn(logAble, logIn)
        }

        /**
         * @param target , example : "com.zj.remoteTest.act.RemoteActivity" or your custom . see [2]
         * */
        fun startServer(c: Context, target: String, onServiceBind: (onBind: Boolean) -> Unit) {
            if (target.isEmpty()) throw TargetNotFoundException("Empty")
            ServerBridge.bindWebViewService(c.applicationContext, target, onServiceBind)
        }

        fun startServer(c: Context, target: Class<*>, onServiceBind: (onBind: Boolean) -> Unit) {
            startServer(c, target.canonicalName ?: throw TargetNotFoundException(target.name), onServiceBind)
        }

        fun stopServer() {
            ServerBridge.destroy(false)
        }

        /**
         * @param cmd: Command name, usually the event name agreed upon by both parties
         * @param level: The Level of this Command , egs: level-log , level-destroy ... has Default.
         * @param callId: Label the ID of this command, has default
         * @param content: The content , usually it is a JsonString...
         * */
        @Suppress("MemberVisibilityCanBePrivate")
        fun postToServer(cmd: String, level: Int = DEFAULT_I, callId: Int = DEFAULT_I, content: String? = "") {
            if (ServerBridge.isServerInit()) {
                var callStr = ""
                if (cmd != SERVICE_PING) {
                    callStr = "cmd=$cmd&level=$level&callId=$callId"
                    LogUtils.log("post to remote ---> $callStr  \ncontent = $content")
                }
                val result = ServerBridge.postToService(cmd, level, callId, content)
                if (cmd != SERVICE_PING) LogUtils.log("result form remote <--- $result  $callStr")
            }
        }

        fun registerCmdListener(l: (cmd: String?, level: Int, callId: Int, content: String?) -> Int) {
            commendListener = l
        }

        private fun nextPing() {
            handler.removeMessages(SERVICE_HEARTBEATS_CALL_ID)
            handler.sendEmptyMessageDelayed(SERVICE_HEARTBEATS_CALL_ID, 10000)
        }

        private fun ping() {
            postToServer(SERVICE_PING)
        }
    }

    private val appAidlIn = object : WebViewAidlIn.Stub() {
        override fun dispatchCommend(cmd: String?, level: Int, callId: Int, content: String?): Int {
            var result = HANDLE_OK
            when (cmd) {
                SERVICE_INIT -> nextPing()
                SERVICE_DESTROY -> ServerBridge.destroy()
                SERVICE_LOG -> LogUtils.log(content ?: "")
                SERVICE_PONG -> {
                    LogUtils.log("form remote: pong received  at ${System.currentTimeMillis()}")
                    nextPing()
                }
                else -> {
                    result = commendListener?.invoke(cmd, level, callId, content) ?: result
                    if (result != HANDLE_OK) {
                        LogUtils.log("form client : the listener [$commendListener] execute is not success")
                    }
                }
            }
            return result
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return appAidlIn
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        ServerBridge.onServiceDestroyed()
        commendListener = null
        super.onDestroy()
    }
}