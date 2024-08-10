package com.endritgjoka.chatapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
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
        @SuppressLint("NotifyDataSetChanged", "ResourceAsColor", "ClickableViewAccessibility",
            "UseCompatLoadingForDrawables"
        )
        fun bind(conversationResponse: ConversationResponse) {
            when (binding) {
                is ChatCardViewBinding -> {
                    with(binding) {
                        recipientName.text = conversationResponse.recipient.fullName

                        val unreadMessages = conversationResponse.conversation?.unreadMessages ?: 0
                        val hasUnreadMessages = unreadMessages > 0

                        if (conversationResponse.lastMessage != null) {
                            if (hasUnreadMessages) {
                                binding.badge.visibility = View.VISIBLE
                                lastMessage.setTextColor(R.color.ChatBlue)
                                lastMessage.text = ChatApp.application.getString(
                                    if (unreadMessages == 1) R.string.new_message else R.string.new_messages
                                )
                                binding.unreadNotificationsCount.text = if (unreadMessages > 10) "10+" else unreadMessages.toString()
                            } else {
                                lastMessage.setTextColor(R.color.text_color)
                                binding.badge.visibility = View.INVISIBLE
                                lastMessage.text = conversationResponse.lastMessage?.decryptedMessage
                            }
                            time.text = conversationResponse.lastMessage?.formattedTime
                        } else {
                            lastMessage.text = ChatApp.application.getString(R.string.no_messages_yet)
                            time.text = ""
                        }

                        val updateState: (Boolean) -> Unit = { isPressed ->
                            root.background = ChatApp.application.resources.getDrawable(
                                if (isPressed) R.drawable.bg_chat_card_pressed else R.drawable.bg_chat_card
                            )

                        }

                        root.setOnClickListener {
                            updateState(true)
                            customListener.onChatClicked(conversationResponse)
                            Handler(Looper.getMainLooper()).post {
                                updateState(false)
                            }
                        }

                        root.setOnTouchListener { _, event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> updateState(true)
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> updateState(false)
                            }
                            false
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