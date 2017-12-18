package co.creativitykills.groupchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.chatkit.*
import kotlinx.android.synthetic.main.activity_rooms_list.*

class RoomsListActivity : AppCompatActivity() {
    private val instanceLocator = "v1:us1:a198a551-439e-4b78-af06-477e7bbd110d"
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
                            currentUser.joinRoom(room, RoomListener { theRoom ->
                                roomJoined(theRoom)
                            }, ErrorListener { error ->
                                Log.d("TAG", error.toString())
                            })
                        }
                    }
                })
            }

            // Other events can be handled...

            override fun removedFromRoom(roomId: Int) {
                // Fired when a user is removed from the room
            }

            override fun userLeft(user: User?, room: Room?) {
                // Fired when a user leaves
            }

            override fun usersUpdated() {
                // Fired when the users list is updated
            }

            override fun userCameOnline(user: User?) {
                // Fired when user comes online
            }

            override fun roomUpdated(room: Room?) {
                // Fired when room is updated
            }

            override fun addedToRoom(room: Room?) {
                // Fired when user is added to room
            }

            override fun roomDeleted(roomId: Int) {
                // Fired when room is deleted
            }

            override fun userWentOffline(user: User?) {
                // Fired when a user goes offline
            }

            override fun userStoppedTyping(user: User?) {
                // Fired when a user stops typing
            }

            override fun userJoined(user: User?, room: Room?) {
                // Fired when a user joins the room
            }

            override fun userStartedTyping(user: User?) {
                // Fired when the user starts typing
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