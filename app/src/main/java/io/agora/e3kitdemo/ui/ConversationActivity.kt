package io.agora.e3kitdemo.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.chat.ChatClient
import io.agora.e3kitdemo.Constants
import io.agora.e3kitdemo.R
import io.agora.e3kitdemo.base.BaseActivity
import io.agora.e3kitdemo.databinding.ActivityConversationBinding


class ConversationActivity : BaseActivity() {
    private lateinit var binding: ActivityConversationBinding
    private var actionBar: ActionBar? = null
    private lateinit var conversationIdList: MutableList<String>
    private lateinit var adapter: ConversationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar = supportActionBar
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
        initListener()
    }

    private fun initView() {
        actionBar!!.title = "All Conversations"

        binding.conversationList.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.message_list_divider)?.let {
            divider.setDrawable(
                it
            )
        }
        binding.conversationList.addItemDecoration(divider)

        adapter = ConversationListAdapter()
        binding.conversationList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        initData();
    }

    private fun initListener() {
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@ConversationActivity, ChatActivity::class.java)
                intent.putExtra(Constants.SEND_TO, conversationIdList[position])
                startActivity(intent)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
            R.id.send_to -> {
                val sendToEt = EditText(this)
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Send to")
                builder.setView(sendToEt)
                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    if (sendToEt.text.toString().isNotEmpty()) {
                        val intent = Intent(this@ConversationActivity, ChatActivity::class.java)
                        intent.putExtra(Constants.SEND_TO, sendToEt.text.toString())
                        startActivity(intent)
                        dialog.dismiss()
                    }
                }
                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.conversation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initData() {
        val allConversations = ChatClient.getInstance().chatManager().allConversations
        conversationIdList = ArrayList(allConversations.size)
        allConversations.forEach {
            conversationIdList.add(it.key)
        }
        adapter.setConversationIdList(conversationIdList)
    }

    inner class ConversationListAdapter :
        RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder>() {
        private var conversationIdList: MutableList<String> = ArrayList(0)
        private var itemClickListener: OnItemClickListener? = null

        fun setConversationIdList(list: MutableList<String>) {
            conversationIdList = list
            notifyDataSetChanged()
        }

        fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_conversation_list, parent, false)
            return ConversationViewHolder(view)
        }

        override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
            holder.conversationId.text = conversationIdList[position]
            holder.itemView.setOnClickListener { itemClickListener!!.onItemClick(position) }
        }

        override fun getItemCount(): Int {
            return conversationIdList.size
        }

        inner class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val conversationId: TextView = view.findViewById(R.id.conversation_id)

        }


    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}

