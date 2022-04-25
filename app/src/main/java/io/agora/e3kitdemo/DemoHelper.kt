package io.agora.e3kitdemo

import android.content.Context
import io.agora.chat.ChatClient
import io.agora.chat.ChatOptions

class DemoHelper private constructor() {
    companion object {
        val demoHelper: DemoHelper by lazy { DemoHelper() }
    }

    private lateinit var context: Context
    private lateinit var device: Device

    public fun getContext(): Context {
        return context
    }

    public fun initAgoraSdk(context: Context) {
        this.context = context
        //Initialize Agora Chat SDK
        if (initSDK(context.applicationContext)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            ChatClient.getInstance().setDebugMode(true)
        }
    }

    private fun initSDK(context: Context): Boolean {
        // Set Chat Options
        val options: ChatOptions = initChatOptions(context) ?: return false

        // Configure custom rest server and im server
        //options.setRestServer(BuildConfig.APP_SERVER_DOMAIN);
        //options.setIMServer("106.75.100.247");
        //options.setImPort(6717);
        options.usingHttpsOnly = true
        ChatClient.getInstance().init(context, options)
        return ChatClient.getInstance().isSdkInited
    }

    private fun initChatOptions(context: Context): ChatOptions {
        val options = ChatOptions()

        // Sets whether to automatically accept friend invitations. Default is true
        options.acceptInvitationAlways = false
        // Set whether read confirmation is required by the recipient
        options.requireAck = true
        // Set whether confirmation of delivery is required by the recipient. Default: false
        options.requireDeliveryAck = true
        // Set whether to delete chat messages when exiting (actively and passively) a group
        return options
    }

    fun initEThree(identity: String, context: Context) {
        device = Device(identity, context.applicationContext)
        device.initialize {
            device.register { }
        }
    }

    fun logout() {
        device.logout()
    }

}