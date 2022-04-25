package io.agora.e3kitdemo.utils

/**
 * Uses to parse Resource<T>
 * hideErrorMsg is false by default
 * @param <T>
</T></T> */
abstract class OnResourceParseCallback<T> {
    private var hideErrorMsg = false

    constructor() {}

    /**
     * Whether to display error messages
     * @param hideErrorMsg
     */
    constructor(hideErrorMsg: Boolean) {
        this.hideErrorMsg = hideErrorMsg
    }

    /**
     * success
     * @param data
     */
    abstract fun onSuccess(data: T?)

    /**
     * fail
     * @param code
     * @param message
     */
    open fun onError(code: Int, message: String?) {}

    /**
     * in progress
     */
    fun onLoading(data: T?) {}

    /**
     * hide loading
     */
    fun onHideLoading() {}
}