package co.creativitykills.groupchat

import android.app.Application
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.Room

class AppController(): Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var currentUser:CurrentUser
        lateinit var room:Room
    }
}