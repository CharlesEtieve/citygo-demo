package com.eurosportdemo.app.domain.repository

import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleObserver
import com.eurosportdemo.app.R
import com.eurosportdemo.app.domain.model.ErrorEvent
import io.reactivex.subjects.PublishSubject

open class BaseRepository : LifecycleObserver {

    var error: PublishSubject<RepositoryErrorEvent> = PublishSubject.create()

    enum class RepositoryErrorEvent(@StringRes private val resourceId: Int) : ErrorEvent {
        NETWORK(R.string.network_error),
        UNKNOWN(R.string.unknown_error);

        override fun getErrorResource() = resourceId
    }

}