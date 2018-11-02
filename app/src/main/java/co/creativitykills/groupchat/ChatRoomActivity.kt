package co.creativitykills.groupchat

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.chatkit.messages.Message
import com.pusher.util.Result
import com.pusher.chatkit.rooms.RoomListeners
import kotlinx.android.synthetic.main.activity_chat_room.*
import android.view.inputmethod.InputMethodManager
import android.view.View

class ChatRoomActivity : AppCompatActivity() {
    lateinit var adapter:ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar!!.title = intent.getStringExtra("room_name")
        adapter = ChatMessageAdapter()
        setUpRecyclerView()

        val currentUser = AppController.currentUser
        val roomId = intent.getStringExtra("room_id")

        currentUser.subscribeToRoom(
                roomId = roomId,
                listeners = RoomListeners(
                    onMessage = { message ->
                        Log.d("TAG",message.text)
                        adapter.addMessage(message)
                    },
                    onErrorOccurred = { error ->
                        Log.d("TAG", error.toString())
                    }
                ),
                messageLimit = 100, // Optional
                callback = { subscription ->
                    // Called when the subscription has started.
                    // You should terminate the subscription with subscription.unsubscribe()
                    // when it is no longer needed
                }
        )

        button_send.setOnClickListener {
            if (edit_text.text.isNotEmpty()){
                currentUser.sendMessage(
                        roomId = roomId,
                        messageText = edit_text.text.toString(),
                        callback = { result -> //Result<Int, Error>
                            when (result) {
                                is Result.Success -> {
                                    runOnUiThread {
                                        edit_text.text.clear()
                                        hideKeyboard()
                                    }
                                }
                                is Result.Failure -> {
                                    Log.d("TAG", "error: " + result.error.toString())
                                }
                            }
                        }
                )
            }
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus

        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setUpRecyclerView() {
        recycler_view.layoutManager= LinearLayoutManager(this@ChatRoomActivity)
        recycler_view.adapter = adapter
    }
}
