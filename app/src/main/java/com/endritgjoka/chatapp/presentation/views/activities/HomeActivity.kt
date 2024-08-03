package com.endritgjoka.chatapp.presentation.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.pusher.MessageWrapper
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.pusher.PusherService
import com.endritgjoka.chatapp.data.utils.AppPreferences.activeUser
import com.endritgjoka.chatapp.data.utils.hideKeyboard
import com.endritgjoka.chatapp.data.utils.navigate
import com.endritgjoka.chatapp.databinding.ActivityHomeBinding
import com.endritgjoka.chatapp.presentation.adapter.ConversationsAdapter
import com.endritgjoka.chatapp.presentation.viewmodel.ChatViewModel
import com.endritgjoka.chatapp.presentation.views.fragments.LogOutDialogFragment
import com.google.gson.Gson
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    val chatViewModel: ChatViewModel by viewModels()
    private lateinit var conversationsAdapter: ConversationsAdapter
    var pusherService: PusherService ?= null
    var userChannel: PrivateChannel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openPopup.setOnClickListener {
            openPopupMenu(binding.openPopup)
        }
        subscribeChannel()
        getConversations()

        searchUsers()

    }

    private fun openPopupMenu(popupView: View) {
        popupView.setOnClickListener {
            val popupMenu = PopupMenu(this, popupView)
            popupMenu.menuInflater.inflate(R.menu.logout_popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_log_out -> {
                        val logoutDialogFragment = LogOutDialogFragment()
                        logoutDialogFragment.show(supportFragmentManager, "Log out")
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun searchUsers() {
        with(binding.search) {
            this.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = this.text.toString()
                    if (query.isNotEmpty()) {
                        this.hideKeyboard()
                        this.clearFocus()
                        binding.progressBar.isVisible = true
                        binding.chatsRecyclerView.isVisible = false
                        chatViewModel.searchUsers(query)
                    }

                }
                false
            }

            this.setOnFocusChangeListener { _, hasFocus ->
                binding.clearSearchQuery.isVisible = hasFocus
            }
        }

        binding.clearSearchQuery.setOnClickListener {
            binding.search.hideKeyboard()
            binding.clearSearchQuery.isVisible = false
            binding.search.clearFocus()
            binding.search.setText("")
            chatViewModel.allConversations.value?.let { list ->
                initializeConversationsRecyclerView(list)
            }
            binding.chatsRecyclerView.visibility = View.VISIBLE
//            binding.recyclerForSearchedPosts.visibility = View.GONE
        }

        chatViewModel.searchedConversations.observe(this){list ->
            list?.let {
                initializeConversationsRecyclerView(list)
            }
        }

    }

    private fun getConversations() {
        chatViewModel.getConversations()
        chatViewModel.allConversations.observe(this){list ->
            list?.let {
                initializeConversationsRecyclerView(list)
            }
        }

        chatViewModel.failedFetchingConversations.observe(this){message ->
            message?.let {
                binding.progressBar.isVisible = false
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                chatViewModel.failedFetchingConversations.postValue(null)
            }
        }
    }
    private fun initializeConversationsRecyclerView(list: ArrayList<ConversationResponse>){
        with(binding){
            progressBar.isVisible = false
            chatsRecyclerView.isVisible = true
            chatsRecyclerView.apply {
                conversationsAdapter = ConversationsAdapter()
                implementInterface()
                conversationsAdapter.list = list
                layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.VERTICAL, false)
                adapter = conversationsAdapter
            }
        }
    }

    private fun implementInterface(){
        conversationsAdapter.customListener = object : ConversationsAdapter.CustomListener{
            override fun onChatClicked(conversationResponse: ConversationResponse) {
                val gson = Gson()
                val conversation = gson.toJson(conversationResponse)
                val bundle = Bundle()
                bundle.putString("conversation", conversation)
                navigate(OneToOneChatActivity::class.java,this@HomeActivity, bundle)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        chatViewModel.getConversations()
    }

    private fun subscribeChannel() {
        pusherService = PusherService()
        pusherService?.connectPusher()
        userChannel = pusherService?.subscribeChannel("private-user.${activeUser?.id}")
//        userViewModel.blogChannel.postValue(blogChannel)
//        userViewModel.userChannel.postValue(userChannel)
//        ViewModelsHolder.userViewModel = userViewModel
        fetchNewMessages()
    }

    private fun fetchNewMessages() {
        val newPrivateEventListener = object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                try {
                    val jsonString = event?.data
                    val gson = Gson()
                    val messageData = gson.fromJson(jsonString, MessageWrapper::class.java)
                    var isNewConversation = true
                    Log.i("MYTAG", "onEvent: {${messageData.message.decryptedMessage}}")


//                    CoroutineScope(Dispatchers.Main).launch {
//                        for (i in 0 until conversationsList.size) {
//                            var conversation = conversationsList[i]
//                            if (conversation.id == messageData.cid) {
//                                isNewConversation = false
//                                messageData.message.fullname = messageData.user_fullname
//                                messageData.message.profile_picture = messageData.user_picture
//                                messageToBeInserted = messageData.message
//                                break
//                            }
//
//                        }
//                        if (isNewConversation) {
//                            var recipientID = if (messageData.message.user_id == userID) {
//                                messageData.conversationId.toInt()
//                            } else {
//                                if(messageData.conversationType =="group"){
//                                    messageData.conversationId.toInt()
//                                } else {
//                                    messageData.message.user_id
//                                }
//                            }
//
//                            messageData.message.messageState = MessageState.SENT.name
//                            var conversation = Conversation(
//                                messageData.cid,
//                                messageData.unread_messages,
//                                messageData.conversationType,
//                                messageData.message,
//                                recipientID,
//                                messageData.conversation_name,
//                                messageData.conversation_picture,
//                                userID
//                            )
//                            messageData.message.fullname = messageData.user_fullname
//                            messageData.message.profile_picture = messageData.user_picture
//                            messageToBeInserted = messageData.message
//                            insertNewConversation(
//                                messageData.message,
//                                getConversationEntity(conversation, messageData.message.id)
//                            )
//                        }
//                    }

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
    }
}

