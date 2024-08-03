package com.endritgjoka.chatapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.databinding.ChatCardViewBinding
import com.endritgjoka.chatapp.databinding.LeftMessageCardBinding
import com.endritgjoka.chatapp.databinding.RightMessageCardBinding

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {
    var list = ArrayList<Message>()
    var activeUserId: Int = -1

    companion object {
        private const val VIEW_TYPE_RIGHT_MESSAGE = 0
        private const val VIEW_TYPE_LEFT_MESSAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            VIEW_TYPE_LEFT_MESSAGE -> {
                LeftMessageCardBinding.inflate(inflater, parent, false)
            }

            VIEW_TYPE_RIGHT_MESSAGE -> {
                RightMessageCardBinding.inflate(inflater, parent, false)
            }

            else -> {
                RightMessageCardBinding.inflate(inflater, parent, false)
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
        val message = list[position]
        return when (message.type) {
            "text" -> {
                if (message.userId == activeUserId) {
                    VIEW_TYPE_RIGHT_MESSAGE
                } else {
                    VIEW_TYPE_LEFT_MESSAGE
                }
            }
            else -> {
                VIEW_TYPE_RIGHT_MESSAGE
            }
        }
    }


    inner class MyViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(message: Message) {
            when (binding) {
                is LeftMessageCardBinding -> {
                    with(binding){
                        content.text = message.decryptedMessage
                        fullName.text = message.user.fullName
                        time.text = message.formattedTime
                    }
                }

                is RightMessageCardBinding -> {
                    with(binding){
                        content.text = message.decryptedMessage
                        time.text = message.formattedTime
                    }
                }
            }
        }
    }


}