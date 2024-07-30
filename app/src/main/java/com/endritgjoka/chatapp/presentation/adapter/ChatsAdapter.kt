package com.endritgjoka.chatapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.databinding.ChatCardViewBinding

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.MyViewHolder>(){
    var list = ArrayList<Message>()
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
        return when(list[position].id == 0){
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
        fun bind(message: Message) {
            when (binding) {
                is ChatCardViewBinding -> {

                }
            }
        }
    }



}