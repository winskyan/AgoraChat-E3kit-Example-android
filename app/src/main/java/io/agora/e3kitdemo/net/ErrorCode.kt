package io.agora.e3kitdemo.net

import io.agora.Error
import io.agora.e3kitdemo.R

/**
 * Define some local error codes
 */
object ErrorCode : Error() {
    /**
     * Network is unavailable
     */
    const val NETWORK_ERROR = -2

    /**
     * Haven't logged in to Agora Chat SDK
     */
    const val NOT_LOGIN = -8

    /**
     * result parsing error
     */
    const val PARSE_ERROR = -10

    /**
     * Network problem, please try again later
     */
    const val ERR_UNKNOWN = -20

    /**
     * Android version issue, only supports above 4.4
     */
    const val ERR_IMAGE_ANDROID_MIN_VERSION = -50

    /**
     * file does not exist
     */
    const val ERR_FILE_NOT_EXIST = -55

    /**
     * Add self as a friend
     */
    const val ADD_SELF_ERROR = -100

    /**
     * Already friends
     */
    const val FRIEND_ERROR = -101

    /**
     * Has been added to the blacklist
     */
    const val FRIEND_BLACK_ERROR = -102

    /**
     * No group members
     */
    const val ERR_GROUP_NO_MEMBERS = -105

    /**
     * Failed to delete conversation
     */
    const val DELETE_CONVERSATION_ERROR = -110
    const val DELETE_SYS_MSG_ERROR = -115

    enum class Error(private val code: Int, val messageId: Int) {
        NETWORK_ERROR(
            ErrorCode.NETWORK_ERROR,
            R.string.error_network_error
        ),
        NOT_LOGIN(ErrorCode.NOT_LOGIN, R.string.error_not_login), PARSE_ERROR(
            ErrorCode.PARSE_ERROR, R.string.error_parse_error
        ),
        ERR_UNKNOWN(
            ErrorCode.ERR_UNKNOWN,
            R.string.error_err_unknown
        ),
        ERR_IMAGE_ANDROID_MIN_VERSION(
            ErrorCode.ERR_IMAGE_ANDROID_MIN_VERSION, R.string.err_image_android_min_version
        ),
        ERR_FILE_NOT_EXIST(
            ErrorCode.ERR_FILE_NOT_EXIST, R.string.err_file_not_exist
        ),
        ADD_SELF_ERROR(ErrorCode.ADD_SELF_ERROR, R.string.error_add_self), FRIEND_ERROR(
            ErrorCode.FRIEND_ERROR, R.string.error_already_friend
        ),
        FRIEND_BLACK_ERROR(
            ErrorCode.FRIEND_BLACK_ERROR,
            R.string.error_already_friend_but_in_black
        ),
        ERR_GROUP_NO_MEMBERS(
            ErrorCode.ERR_GROUP_NO_MEMBERS, R.string.error_group_no_members
        ),
        DELETE_CONVERSATION_ERROR(
            ErrorCode.DELETE_CONVERSATION_ERROR, R.string.error_delete_conversation
        ),
        DELETE_SYS_MSG_ERROR(
            ErrorCode.DELETE_SYS_MSG_ERROR, R.string.error_delete_not_exist_msg
        ),
        USER_ALREADY_EXIST(
            io.agora.Error.USER_ALREADY_EXIST,
            R.string.error_user_already_exist
        ),
        UNKNOWN_ERROR(-9999, 0);

        companion object {
            @JvmStatic
            fun parseMessage(errorCode: Int): Error {
                for (error in values()) {
                    if (error.code == errorCode) {
                        return error
                    }
                }
                return UNKNOWN_ERROR
            }
        }
    }
}