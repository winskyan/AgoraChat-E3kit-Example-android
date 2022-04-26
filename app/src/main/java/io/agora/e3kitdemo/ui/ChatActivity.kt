package io.agora.e3kitdemo.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.*
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.CallBack
import io.agora.MessageListener
import io.agora.chat.ChatClient
import io.agora.chat.ChatMessage
import io.agora.chat.TextMessageBody
import io.agora.e3kitdemo.Constants
import io.agora.e3kitdemo.R
import io.agora.e3kitdemo.base.BaseActivity
import io.agora.e3kitdemo.databinding.ActivityChatBinding


class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private var sendTo: String? = null
    private var actionBar: ActionBar? = null
    private lateinit var messageList: MutableList<ChatMessage>
    private lateinit var adapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
        initData()
        initListener()
    }

    private fun initView() {
        sendTo = intent.getStringExtra(Constants.SEND_TO)
        actionBar!!.title = sendTo

        binding.messageList.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.message_list_divider)?.let {
            divider.setDrawable(
                it
            )
        }
        binding.messageList.addItemDecoration(divider)

        adapter = MessageListAdapter()
        binding.messageList.adapter = adapter
    }

    private fun initListener() {
        binding.messageInputEt.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || event != null && KeyEvent.KEYCODE_ENTER == event.keyCode
                    && KeyEvent.ACTION_DOWN == event.action
                ) {
                    if (!TextUtils.isEmpty(binding.messageInputEt.text.toString())) {
                        sendTxtMessage(binding.messageInputEt.text.toString())
                    }
                    binding.messageInputEt.text = null
                    return true
                }
                return false
            }

        })

        ChatClient.getInstance().chatManager().addMessageListener(object : MessageListener {
            override fun onMessageReceived(messages: List<ChatMessage>?) {
                runOnUiThread {
                    if (messages != null) {
                        messageList.addAll(messages)
                        setListData();
                        refreshToLastView()
                    }
                }
            }

            override fun onCmdMessageReceived(messages: MutableList<ChatMessage>?) {
                TODO("Not yet implemented")
            }

            override fun onMessageRead(messages: MutableList<ChatMessage>?) {
                TODO("Not yet implemented")
            }

            override fun onMessageDelivered(messages: MutableList<ChatMessage>?) {
                TODO("Not yet implemented")
            }

            override fun onMessageRecalled(messages: MutableList<ChatMessage>?) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        messageList = ArrayList(0)
        val conversation = ChatClient.getInstance().chatManager().getConversation(sendTo)
        if (null != conversation) {
            val lastMessage = conversation.allMessages
            messageList.addAll(conversation.loadMoreMsgFromDB(lastMessage[0].msgId, 50))
            messageList.addAll(lastMessage)
            setListData();
            refreshToLastView()
        }
    }

    private fun sendTxtMessage(content: String) {
        val message = ChatMessage.createTxtSendMessage(content, sendTo)
        message.chatType = ChatMessage.ChatType.Chat
        message.setMessageStatusCallback(object : CallBack {
            override fun onSuccess() {
                Log.i(Constants.TAG, "send success")
                runOnUiThread {
                    messageList.add(message)
                    setListData();
                    refreshToLastView()
                }
            }

            override fun onError(i: Int, s: String) {
                Log.i(Constants.TAG, "send error:$s")

            }

            override fun onProgress(i: Int, s: String) {

            }
        })
        ChatClient.getInstance().chatManager().sendMessage(message)
    }

    fun refreshToLastView() {
        binding.messageList.smoothScrollToPosition(adapter.itemCount - 1)
    }

    fun setListData() {
        val iterator: MutableIterator<ChatMessage> = messageList.iterator()
        while (iterator.hasNext()) {
            val message: ChatMessage = iterator.next()
            if (message.body !is TextMessageBody) {
                iterator.remove()
            }
        }
        adapter.setMessageList(messageList)
    }

    inner class MessageListAdapter :
        RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>() {
        private var messageList: MutableList<ChatMessage> = ArrayList(0)

        fun setMessageList(list: MutableList<ChatMessage>) {
            messageList = list
            notifyDataSetChanged()
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val senderGroup: Group = view.findViewById(R.id.sender_group)
            val sender: TextView = view.findViewById(R.id.sender)
            val sendContent: TextView = view.findViewById(R.id.send_content)

            val receiverGroup: Group = view.findViewById(R.id.receiver_group)
            val receiver: TextView = view.findViewById(R.id.receiver)
            val receiverContent: TextView = view.findViewById(R.id.receiver_content)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_list, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val chatMessage = messageList[position]
            if (chatMessage.from == ChatClient.getInstance().currentUser) {
                holder.receiverGroup.visibility = GONE
                holder.senderGroup.visibility = VISIBLE
                holder.sender.text = chatMessage.from
                holder.sendContent.text = (chatMessage.body as TextMessageBody).message
            } else {
                holder.senderGroup.visibility = GONE
                holder.receiverGroup.visibility = VISIBLE
                holder.receiver.text = chatMessage.from
                holder.receiverContent.text = (chatMessage.body as TextMessageBody).message
            }
        }

        override fun getItemCount(): Int {
            return messageList.size
        }
    }
}

