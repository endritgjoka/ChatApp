package com.endritgjoka.chatapp.presentation.views.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.Message
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.pusher.MessageWrapper
import com.endritgjoka.chatapp.data.model.requests.MessageRequest
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.pusher.PusherService
import com.endritgjoka.chatapp.data.utils.AppPreferences
import com.endritgjoka.chatapp.data.utils.hideKeyboard
import com.endritgjoka.chatapp.data.utils.navigate
import com.endritgjoka.chatapp.data.utils.showKeyboard
import com.endritgjoka.chatapp.databinding.ActivityOneToOneChatBinding
import com.endritgjoka.chatapp.databinding.ChatCardViewBinding
import com.endritgjoka.chatapp.presentation.ChatApp
import com.endritgjoka.chatapp.presentation.ChatApp.Companion.userChannel
import com.endritgjoka.chatapp.presentation.adapter.ConversationsAdapter
import com.endritgjoka.chatapp.presentation.adapter.MessagesAdapter
import com.endritgjoka.chatapp.presentation.viewmodel.ChatViewModel
import com.google.gson.Gson
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@AndroidEntryPoint
class OneToOneChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOneToOneChatBinding
    val chatViewModel: ChatViewModel by viewModels()
    private var conversationResponse: ConversationResponse? = null
    private var messagesAdapter: MessagesAdapter? = null
    private var activeUser: User? = null
    private var messageToBeSent: String = ""
    private var previousPrivateEventListener: PrivateChannelEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneToOneChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activeUser = AppPreferences.activeUser
        val gson = Gson()
        val conversationStr = intent.getStringExtra("conversation")
        conversationResponse = gson.fromJson(conversationStr, ConversationResponse::class.java)
        markConversationAsRead()
        setRecipient()
        fetchNewMessages()
        getMessages()
        onWritingMessage()
        binding.sendMessage.setOnClickListener {
            var messageContent = binding.addMessageEditText.text.toString()
            messageContent = messageContent.trim()
            if (messageContent.isNotEmpty()) {
                sendTextMessage(messageContent)
            }
        }

        binding.goBack.setOnClickListener {
            finish()
        }

    }

    private fun setRecipient() {
        with(binding) {
            fullName.text = conversationResponse?.recipient?.fullName
        }
    }

    private fun getMessages() {
        conversationResponse?.recipient?.id?.let { chatViewModel.getConversationMessages(it) }
        chatViewModel.messages.observe(this) { list ->
            list?.let {
                if (list.isNotEmpty()) {
                    initializeMessagesRecyclerView(list)
                } else {
                    binding.progressBar.isVisible = false
                    binding.recyclerViewForMessages.isVisible = false
                    binding.noMessagesLayout.isVisible = true
                    binding.receiverName.text = conversationResponse?.recipient?.fullName
                }
            }

        }

        chatViewModel.failedFetchingMessages.observe(this) { message ->
            message?.let {
                binding.progressBar.isVisible = false
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                chatViewModel.failedFetchingMessages.postValue(null)
            }
        }
    }

    private fun initializeMessagesRecyclerView(list: ArrayList<Message>) {
        with(binding) {
            progressBar.isVisible = false
            recyclerViewForMessages.isVisible = true
            recyclerViewForMessages.apply {
                messagesAdapter = MessagesAdapter()
                activeUser?.let {
                    messagesAdapter?.activeUserId = it.id
                }
                messagesAdapter?.list = list
                layoutManager = LinearLayoutManager(this@OneToOneChatActivity).apply {
                    stackFromEnd = true
                    reverseLayout = false
                }
                adapter = messagesAdapter
                recyclerViewForMessages.post {
                    binding.recyclerViewForMessages.smoothScrollToPosition(messagesAdapter?.itemCount!! - 1)
                }
            }
        }
    }

    private fun updateSendMessageButtonState() {
        val isEmpty = binding.addMessageEditText.text.toString().isEmpty()
        var sendButtonResId = 1
        if (isEmpty) {
            sendButtonResId = R.drawable.ic_send
        } else {
            sendButtonResId = R.drawable.ic_send_blue
        }
        binding.sendMessage.setImageResource(sendButtonResId)
    }

    private fun onWritingMessage() {
        binding.addMessageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSendMessageButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.addMessageEditText.setOnFocusChangeListener { _, focus ->
            if (focus) {
                binding.sendMessage.visibility = View.VISIBLE
                binding.sendMessage.showKeyboard()
            } else {
                binding.sendMessage.visibility = View.GONE
                binding.sendMessage.hideKeyboard()
            }
        }
    }

    fun displayMessage(
        message: Message
    ) {
        messagesAdapter?.list?.add(message)
        binding.noMessagesLayout.visibility = View.GONE
        binding.recyclerViewForMessages.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        binding.recyclerViewForMessages.layoutManager = layoutManager

        binding.recyclerViewForMessages.adapter = messagesAdapter
        binding.recyclerViewForMessages.post {
            binding.recyclerViewForMessages.smoothScrollToPosition(
                messagesAdapter?.list?.size!! - 1
            )
        }
        messagesAdapter?.notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun displayYourNewMessage(content: String) {
        val currentDate = Date()
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val formattedDate = dateFormat.format(currentDate)
        messageToBeSent = content

        val message = Message(
            0,
            activeUser?.id!!,
            "private",
            "",
            messageToBeSent,
            "",
            formattedDate,
            "",
            activeUser!!
        )
        displayMessage(message)

        binding.addMessageEditText.setText("")
        binding.sendMessage.setImageResource(R.drawable.ic_send)

    }

    private fun sendTextMessage(content: String) {
        if (messagesAdapter == null) {
            initializeMessagesRecyclerView(ArrayList())
        }
        displayYourNewMessage(content)
        val messageRequest = MessageRequest(content)
        conversationResponse?.recipient?.id?.let { chatViewModel.sendMessage(it, messageRequest) }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    private fun markConversationAsRead() {
        conversationResponse?.recipient?.id?.let { chatViewModel.markConversationAsRead(it) }
    }

    private fun fetchNewMessages() {
        if (previousPrivateEventListener != null) {
            userChannel?.unbind("NewMessage", previousPrivateEventListener)
        }
        val newPrivateEventListener = object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                val jsonString = event?.data
                try {
                    val gson = Gson()
                    val messageData = gson.fromJson(jsonString, MessageWrapper::class.java)
                    val messageUserId = messageData.message.userId

                    if (messageUserId != activeUser?.id) {
                        CoroutineScope(Dispatchers.Main).launch {
                            displayMessage(messageData.message)
                        }
                        conversationResponse?.recipient?.id?.let {
                            chatViewModel.markConversationAsRead(
                                it
                            )
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.i("MYTAG", e.message.toString())
                }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.i("MYTAG", "123")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.i("MYTAG", message.toString())
            }
        }
        userChannel?.bind("NewMessage", newPrivateEventListener)
        previousPrivateEventListener = newPrivateEventListener
    }

    override fun onDestroy() {
        preventFetchingEventMultipleTimes()
        super.onDestroy()
    }

    private fun preventFetchingEventMultipleTimes() {
        if (userChannel?.isSubscribed == true) {
            if (previousPrivateEventListener != null) {
                userChannel?.unbind("NewMessage", previousPrivateEventListener)
                previousPrivateEventListener = null
            }
        }
    }
}