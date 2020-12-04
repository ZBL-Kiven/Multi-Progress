package com.zj.webkit.nimbus.web

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

@Suppress("unused")
class ComponentOwner : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        WebViewService.destroyService()
    }
}