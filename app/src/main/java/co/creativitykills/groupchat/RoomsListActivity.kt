package co.creativitykills.groupchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.chatkit.*
import com.pusher.chatkit.rooms.Room
import com.pusher.util.Result
import kotlinx.android.synthetic.main.activity_rooms_list.*

class RoomsListActivity : AppCompatActivity() {
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
        val chatManager = ChatManager(
                instanceLocator = "YOUR_INSTANCE_LOCATOR",
                userId = "YOUR_USER_ID",
                dependencies = AndroidChatkitDependencies(
                        tokenProvider = ChatkitTokenProvider(
                                endpoint = "http://10.0.2.2:3000/auth",
                                userId = "YOUR_USER_ID"
                        )
                )
        )

        // TODO: What do we need to do with these that were here before?
//                .context(this@RoomsListActivity)
//        intent.getStringExtra("extra")

        chatManager.connect(listeners = ChatListeners(
                onErrorOccurred = { },
                onAddedToRoom = { },
                onRemovedFromRoom = { },
                onCurrentUserReceived = { },
                onNewReadCursor = { },
                onRoomDeleted = { },
                onRoomUpdated = { },
                onPresenceChanged = { u, n, p -> },
                onUserJoinedRoom = { u, r -> },
                onUserLeftRoom = { u, r -> },
                onUserStartedTyping = { u, r -> },
                onUserStoppedTyping = { u, r -> }
        )) { result ->
            when (result) {
                is Result.Success -> {
                    // We have connected!
                    val currentUser = result.value
                    AppController.currentUser = currentUser
                    val userJoinedRooms = ArrayList<Room>(currentUser.rooms)
                    for (i in 0 until userJoinedRooms.size) {
                        adapter.addRoom(userJoinedRooms[i])
                    }

                    currentUser.getJoinableRooms { result ->
                        when (result) {
                            is Result.Success -> {
                                // Do something with List<Room>
                                val rooms = result.value
                                runOnUiThread {
                                    for (i in 0 until rooms.size) {
                                        adapter.addRoom(rooms[i])
                                    }
                                }
                            }
                        }
                    }

                    adapter.setInterface(object : RoomsAdapter.RoomClickedInterface {
                        override fun roomSelected(room: Room) {
                            if (room.memberUserIds.contains(currentUser.id)) {
                                // user already belongs to this room
                                roomJoined(room)
                            } else {
                                currentUser.joinRoom(
                                        roomId = room.id,
                                        callback = { result ->
                                            when (result) {
                                                is Result.Success -> {
                                                    // Joined the room!
                                                    roomJoined(result.value)
                                                }
                                                is Result.Failure -> {
                                                    Log.d("TAG", result.error.toString())
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    })
                }

                is Result.Failure -> {
                    // Failure
                    Log.d("TAG", result.error.toString())
                }
            }
        }
    }

    private fun roomJoined(room: Room) {
        val intent = Intent(this@RoomsListActivity, ChatRoomActivity::class.java)
        intent.putExtra("room_id", room.id)
        intent.putExtra("room_name", room.name)
        startActivity(intent)
    }
}