package io.agora.e3kitdemo.net

import android.text.TextUtils
import io.agora.Error
import io.agora.e3kitdemo.DemoHelper.Companion.demoHelper
import io.agora.e3kitdemo.enums.Status
import io.agora.e3kitdemo.net.ErrorCode.Error.Companion.parseMessage

class Resource<T> {
    @JvmField
    var status: Status?

    @JvmField
    var data: T?

    @JvmField
    var errorCode: Int
    private var message: String? = null
    private var messageId = 0

    constructor(status: Status?, data: T, errorCode: Int) {
        this.status = status
        this.data = data
        this.errorCode = errorCode
        messageId = parseMessage(errorCode).messageId
    }

    constructor(status: Status?, data: T, errorCode: Int, message: String?) {
        this.status = status
        this.data = data
        this.errorCode = errorCode
        this.message = message
    }

    /**
     * Get error message
     *
     * @return
     */
    fun getMessage(): String? {
        if (!TextUtils.isEmpty(message)) {
            return message
        }
        return if (messageId > 0) {
            demoHelper.getContext().getString(messageId)
        } else ""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val resource = other as Resource<*>
        if (errorCode != resource.errorCode) return false
        if (status != resource.status) return false
        if (if (data != null) data != resource.data else resource.data != null) return false
        return if (message != null) message == resource.message else resource.message == null
    }

    override fun hashCode(): Int {
        var result = if (status != null) status.hashCode() else 0
        result = 31 * result + if (data != null) data.hashCode() else 0
        result = 31 * result + errorCode
        result = 31 * result + if (message != null) message.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Resource{" +
                "mStatus=" + status +
                ", data=" + data +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}'
    }

    companion object {
        fun <T> success(data: T?): Resource<T?> {
            return Resource(Status.SUCCESS, data, Error.EM_NO_ERROR)
        }

        fun <T> error(code: Int, data: T?): Resource<T?> {
            return Resource(Status.ERROR, data, code)
        }

        fun <T> error(code: Int, message: String?, data: T?): Resource<T?> {
            return if (TextUtils.isEmpty(message)) Resource(Status.ERROR, data, code) else Resource(
                Status.ERROR, data, code, message
            )
        }

        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data, Error.EM_NO_ERROR)
        }
    }
}