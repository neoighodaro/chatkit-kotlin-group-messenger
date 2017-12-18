package co.creativitykills.groupchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.chatkit.*
import elements.Error
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {
    lateinit var adapter:ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar!!.title = intent.getStringExtra("room_name")
        adapter = ChatMessageAdapter()
        setUpRecyclerView()

        val currentUser = AppController.currentUser
        val room = currentUser.getRoom(intent.getIntExtra("room_id",-1))

        currentUser.subscribeToRoom(room!!,100,object: RoomSubscriptionListeners {
            override fun onNewMessage(message: Message?) {
                Log.d("TAG",message!!.text)
                adapter.addMessage(message)
            }

            override fun onError(error: Error?) {
                Log.d("TAG", error.toString())
            }
        })

        button_send.setOnClickListener {
            if (edit_text.text.isNotEmpty()){
                currentUser.addMessage(edit_text.text.toString(),room, MessageSentListener {
                    Log.d("TAG", "message sent")
                }, ErrorListener {
                    Log.d("TAG", "error")
                })
            }
        }
    }

    private fun setUpRecyclerView() {
        recycler_view.layoutManager= LinearLayoutManager(this@ChatRoomActivity)
        recycler_view.adapter = adapter
    }
}
