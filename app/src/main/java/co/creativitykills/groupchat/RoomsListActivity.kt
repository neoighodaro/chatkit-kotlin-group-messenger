package co.creativitykills.groupchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.chatkit.*
import kotlinx.android.synthetic.main.activity_rooms_list.*

class RoomsListActivity : AppCompatActivity() {
    private val instanceLocator = "v1:us1:cb460a44-db4d-44f6-95d9-4a3faa31c47f"
    val adapter = RoomsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_list)
        initRecyclerView()
        initChatManager()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this@RoomsListActivity)
        recycler_view.adapter = adapter
    }

    private fun initChatManager() {
        val chatManager = ChatManager.Builder()
                .instanceLocator(instanceLocator)
                .context(this@RoomsListActivity)
                .tokenProvider(
                        ChatkitTokenProvider("http://10.0.2.2:3000/auth", intent.getStringExtra("extra"))
                )
                .build()


        chatManager.connect(object: UserSubscriptionListener {
            override fun onError(error: elements.Error?) {
                Log.d("TAG", error.toString())
            }

            override fun currentUserReceived(currentUser: CurrentUser?) {
                val userJoinedRooms = ArrayList<Room>(currentUser!!.rooms())
                for (i in 0 until userJoinedRooms.size) {
                    adapter.addRoom(userJoinedRooms[i])
                }

                AppController.currentUser = currentUser
                currentUser.getJoinableRooms(RoomsListener { rooms ->
                    runOnUiThread {
                        for (i in 0 until rooms.size) {
                            adapter.addRoom(rooms[i])
                        }
                    }
                })

                adapter.setInterface(object : RoomsAdapter.RoomClickedInterface {
                    override fun roomSelected(room: Room) {
                        if (room.memberUserIds.contains(currentUser.id)) {
                            // user already belongs to this room
                            roomJoined(room)
                        } else {
                            currentUser.joinRoom(room, RoomListener { room ->
                                roomJoined(room)
                            }, ErrorListener { error ->
                                Log.d("TAG", error.toString())
                            })
                        }
                    }
                })
            }
        })
    }

    private fun roomJoined(room: Room) {
        val intent = Intent(this@RoomsListActivity, ChatRoomActivity::class.java)
        intent.putExtra("room_id", room.id)
        intent.putExtra("room_name", room.name)
        startActivity(intent)
    }
}