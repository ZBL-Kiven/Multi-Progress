package com.zj.multiProgress.nimbus.web

import androidx.lifecycle.*

@Suppress("unused")
internal class ComponentOwner : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            RemoteService.destroyService()
        }
    }
}