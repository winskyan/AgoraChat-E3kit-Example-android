package io.agora.e3kitdemo.utils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.agora.Error
import io.agora.e3kitdemo.net.ErrorCode
import io.agora.e3kitdemo.net.Resource
import io.agora.e3kitdemo.net.Result
import io.agora.e3kitdemo.utils.EaseThreadManager.Companion.instance
import io.agora.util.EMLog

/**
 * This class is used to pull asynchronous data from Agora Chat SDK or other time-consuming operations
 *
 * @param <ResultType>
</ResultType> */
abstract class NetworkOnlyResource<ResultType> {
    private val mThreadManager: EaseThreadManager? = instance
    private val result = MediatorLiveData<Resource<ResultType>>()

    /**
     * work on main thread
     */
    private fun init() {
        result.setValue(Resource.loading(null))
        fetchFromNetwork()
    }

    /**
     * work on main thread
     */
    private fun fetchFromNetwork() {
        createCall(object : ResultCallBack<LiveData<ResultType>?>() {
            override fun onSuccess(apiResponse: LiveData<ResultType>) {
                mThreadManager!!.runOnMainThread(Runnable {
                    result.addSource(apiResponse) { response: ResultType? ->
                        result.removeSource(apiResponse)
                        if (response != null) {
                            if (response is Result<*>) {
                                val code = (response as Result<*>).code
                                if (code != Error.EM_NO_ERROR) {
                                    fetchFailed(code, null)
                                }
                            }
                            mThreadManager.runOnIOThread(Runnable {
                                try {
                                    saveCallResult(processResponse(response))
                                } catch (e: Exception) {
                                    EMLog.e(TAG, "save call result failed: $e")
                                }
                                result.postValue(Resource.success(response))
                            })
                        } else {
                            fetchFailed(ErrorCode.ERR_UNKNOWN, null)
                        }
                    }
                })
            }

            override fun onError(error: Int, errorMsg: String) {
                mThreadManager!!.runOnMainThread(Runnable { fetchFailed(error, errorMsg) })
            }
        })
    }

    @MainThread
    private fun fetchFailed(code: Int, message: String?) {
        onFetchFailed()
        result.setValue(Resource.error(code, message, null))
    }

    /**
     * Called to save the result of the API response into the database
     *
     * @param item
     */
    @WorkerThread
    protected fun saveCallResult(item: ResultType) {
    }

    /**
     * Process request response
     *
     * @param response
     * @return
     */
    @WorkerThread
    protected fun processResponse(response: ResultType): ResultType {
        return response
    }

    /**
     * This is designed as a callback mode to facilitate asynchronous operations in this method
     *
     * @return
     */
    @MainThread
    protected abstract fun createCall(callBack: ResultCallBack<LiveData<ResultType>?>)

    /**
     * Called when the fetch fails. The child class may want to reset components like rate limiter.
     */
    protected fun onFetchFailed() {}

    /**
     * Returns a LiveData object that represents the resource that's implemented
     * in the base class.
     *
     * @return
     */
    fun asLiveData(): LiveData<Resource<ResultType>> {
        return result
    }

    companion object {
        private const val TAG = "NetworkBoundResource"
    }

    init {
        if (mThreadManager!!.isMainThread) {
            init()
        } else {
            mThreadManager.runOnMainThread(Runnable { init() })
        }
    }
}