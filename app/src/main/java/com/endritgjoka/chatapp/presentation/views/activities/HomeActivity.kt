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
import com.endritgjoka.chatapp.data.model.Conversation
import com.endritgjoka.chatapp.data.model.User
import com.endritgjoka.chatapp.data.model.pusher.MessageWrapper
import com.endritgjoka.chatapp.data.model.responses.ConversationResponse
import com.endritgjoka.chatapp.data.pusher.PusherService
import com.endritgjoka.chatapp.data.utils.AppPreferences.activeUser
import com.endritgjoka.chatapp.data.utils.hideKeyboard
import com.endritgjoka.chatapp.data.utils.navigate
import com.endritgjoka.chatapp.databinding.ActivityHomeBinding
import com.endritgjoka.chatapp.presentation.ChatApp
import com.endritgjoka.chatapp.presentation.ChatApp.Companion.clickedConversationRecipientId
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    val chatViewModel: ChatViewModel by viewModels()
    private var conversationsAdapter: ConversationsAdapter ? = null
    var pusherService: PusherService ?= null
    var userChannel: PrivateChannel?= null
    private var conversationsList = ArrayList<ConversationResponse>()

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
        onRefreshListener()
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
                if (list.isEmpty()){
                    binding.emptyView.isVisible = true
                    binding.emptySearchView.isVisible = false
                    binding.chatsRecyclerView.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.swipeRefreshLayout.isRefreshing = false
                }else{
                    initializeConversationsRecyclerView(list)
                }
            }
        }

        chatViewModel.searchedConversations.observe(this){list ->
            list?.let {
                if(list.isEmpty()){
                    binding.emptySearchView.isVisible = true
                    binding.emptyView.isVisible = false
                    binding.chatsRecyclerView.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.swipeRefreshLayout.isRefreshing = false
                }else{
                    initializeConversationsRecyclerView(list)
                }
            }
        }

    }

    private fun onRefreshListener(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
    }

    private fun onRefresh() {
        chatViewModel.getConversations()
    }

    private fun getConversations() {
        chatViewModel.getConversations()
        chatViewModel.allConversations.observe(this){list ->
            list?.let {
                if(list.isEmpty()){
                    binding.emptyView.isVisible = true
                    binding.emptySearchView.isVisible = false
                    binding.chatsRecyclerView.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.swipeRefreshLayout.isRefreshing = false
                }else{
                    initializeConversationsRecyclerView(list)
                }
            }
        }

        chatViewModel.failedFetchingConversations.observe(this){message ->
            message?.let {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.isVisible = false
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                chatViewModel.failedFetchingConversations.postValue(null)
            }
        }
    }
    private fun initializeConversationsRecyclerView(list: ArrayList<ConversationResponse>){
        with(binding){
            progressBar.isVisible = false
            binding.emptyView.isVisible = false
            binding.emptySearchView.isVisible = false
            binding.swipeRefreshLayout.isRefreshing = false
            chatsRecyclerView.isVisible = true
            chatsRecyclerView.apply {
                conversationsAdapter = ConversationsAdapter()
                implementInterface()
                conversationsAdapter?.list = list
                layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.VERTICAL, false)
                adapter = conversationsAdapter
            }
        }
    }

    private fun implementInterface(){
        conversationsAdapter?.customListener = object : ConversationsAdapter.CustomListener{
            override fun onChatClicked(conversationResponse: ConversationResponse) {
                binding.search.setText("")
                val gson = Gson()
                val conversation = gson.toJson(conversationResponse)
                val bundle = Bundle()
                bundle.putString("conversation", conversation)
                clickedConversationRecipientId = conversationResponse.recipient.id
                navigate(OneToOneChatActivity::class.java,this@HomeActivity, bundle)
                val conv = conversationsAdapter?.list?.find { conversationResponse.recipient.id == it.recipient.id }
                val index = conversationsAdapter?.list?.indexOf(conversationResponse) ?: 0
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000L)
                    chatViewModel.allConversations.value?.let { list ->
                        conversationsAdapter?.list = list
                    }
                    if (conv != null) {
                        if(conv.conversation != null){
                            conv.conversation.unreadMessages = 0
                        }
                        if(conversationsAdapter?.itemCount!! > 0){
                            conversationsAdapter?.list?.set(index, conv)
                            conversationsAdapter?.notifyDataSetChanged()
                        }
                    }
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
//        chatViewModel.getConversations()
    }

    private fun subscribeChannel() {
        pusherService = PusherService()
        pusherService?.connectPusher()
        userChannel = pusherService?.subscribeChannel("private-user.${activeUser?.id}")
        ChatApp.userChannel = userChannel
        fetchNewMessages()
    }

    private fun fetchNewMessages() {
        val newPrivateEventListener = object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                try {
                    val jsonString = event?.data
                    val gson = Gson()
                    val messageData = gson.fromJson(jsonString, MessageWrapper::class.java)
                    var isNewConversation =  conversationsAdapter?.list?.find { messageData.otherUserId == it.recipient.id } == null
                    Log.i("MYTAG", "onEvent: {${messageData.message.decryptedMessage}}")
                    val recipient = messageData.message.user
                    val conversationObj = if(isNewConversation){
                        Conversation(-1, 1,"private",messageData.message, activeUser?.id!!,messageData.otherUserId,"" ,"","")
                    }else{
                        conversationsAdapter?.list?.find { messageData.otherUserId == it.recipient.id }?.conversation
                    }
                    if(clickedConversationRecipientId == messageData.otherUserId){
                        conversationObj?.unreadMessages= 0
                    }
                    val conversationResponse = ConversationResponse(conversationObj,messageData.message,recipient)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(isNewConversation){
                            isNewConversation = false
                            if(conversationsAdapter == null){
                                initializeConversationsRecyclerView(ArrayList())
                            }
                            displayNewConversation(conversationResponse)
                        }else{
                            val conv = conversationsAdapter?.list?.find { messageData.otherUserId == it.recipient.id }
                            if (conv != null) {
                                conversationsAdapter?.list?.remove(conv)
                                conv.lastMessage = messageData.message
                                if(conv.conversation != null && messageData.otherUserId == conversationResponse.recipient.id){
                                    conv.conversation.unreadMessages += 1
                                }
                                if(clickedConversationRecipientId == messageData.otherUserId){
                                    conv.conversation?.unreadMessages= 0
                                }

                                conversationsAdapter?.list?.add(0, conv)
                                conversationsAdapter?.notifyDataSetChanged()
                            }

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
    }

    private fun displayNewConversation(conversationResponse: ConversationResponse){
        conversationsAdapter?.list?.add(0,conversationResponse)
        conversationsAdapter?.notifyDataSetChanged()
    }
}

