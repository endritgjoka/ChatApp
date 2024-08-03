package com.endritgjoka.chatapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.databinding.ChatCardViewBinding
import com.endritgjoka.chatapp.presentation.ChatApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ConversationsAdapter : RecyclerView.Adapter<ConversationsAdapter.MyViewHolder>(){
    lateinit var customListener: CustomListener
    var list = ArrayList<ConversationResponse>()
    companion object {
        private const val READ_MESSAGE = 0
        private const val UNREAD_MESSAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            READ_MESSAGE -> {
                ChatCardViewBinding.inflate(inflater, parent, false)
            }
            UNREAD_MESSAGE -> {
                ChatCardViewBinding.inflate(inflater, parent, false)
            }
            else -> {
                ChatCardViewBinding.inflate(inflater, parent, false)
            }
        }
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].conversation?.unreadMessages == 0){
            true ->{
                UNREAD_MESSAGE
            }
            false->{
                READ_MESSAGE
            }
        }
    }


    inner class MyViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(conversationResponse: ConversationResponse) {
            when (binding) {
                is ChatCardViewBinding -> {
                    with(binding){
                        recipientName.text = conversationResponse.recipient.fullName
                        if(conversationResponse.lastMessage != null){
                            lastMessage.text = conversationResponse.lastMessage.decryptedMessage
                            time.text = conversationResponse.lastMessage.formattedTime
                        }else{
                            lastMessage.text = ChatApp.application.getString(R.string.no_messages_yet)
                            time.text = ""
                        }

                        root.setOnClickListener{
                            customListener.onChatClicked(conversationResponse)
                        }
                    }
                }
            }
        }
    }

    interface CustomListener{
        fun onChatClicked(conversationResponse: ConversationResponse)
    }

}