package com.zj.webkit

import android.app.ActivityManager
import android.content.Context

const val HANDLE_OK = 200
const val HANDLE_ABANDON = 505
internal const val CALL_ID_SERVER_STARTED = 100

internal const val SERVICE_PING = "ping"
internal const val SERVICE_PONG = "pong"
internal const val SERVICE_INIT = "web_service_init"
internal const val SERVICE_DESTROY = "web_service_destroy"

internal fun getProcessName(context: Context): String? {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    manager.runningAppProcesses.forEach {
        if (it.pid == android.os.Process.myPid()) {
            return it.processName
        }
    }
    return ""
}

internal object CCWebLogUtils {

    private var logIn: ((s: String) -> Unit)? = null

    @Suppress("unused")
    fun setLogIn(logAble: Boolean, logIn: (s: String) -> Unit) {
        if (logAble) this.logIn = logIn else this.logIn = null
    }

    fun log(s: String) {
        logIn?.invoke(s)
    }
}

