package io.agora.e3kitdemo.net

import io.agora.Error

/**
 * Result base class
 * @param <T> The entity class of the request result
</T> */
class Result<T> {
    var code = 0

    @JvmField
    var result: T? = null

    constructor() {}
    constructor(code: Int, result: T) {
        this.code = code
        this.result = result
    }

    constructor(code: Int) {
        this.code = code
    }

    fun setResult(result: T) {
        this.result = result
    }

    val isSuccess: Boolean
        get() = code == Error.EM_NO_ERROR
}