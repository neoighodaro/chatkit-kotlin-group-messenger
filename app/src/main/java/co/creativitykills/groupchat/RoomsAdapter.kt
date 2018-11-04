package co.creativitykills.groupchat

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pusher.chatkit.rooms.Room
import android.view.LayoutInflater
import android.support.v7.widget.RecyclerView

class RoomsAdapter: RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    private var list = ArrayList<Room>()
    lateinit var roomClickedInterface:RoomClickedInterface

    fun addRoom(room:Room){
        list.add(room)
        notifyDataSetChanged()
    }

    fun setInterface(roomClickedInterface:RoomClickedInterface){
        this.roomClickedInterface = roomClickedInterface
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                                 .inflate(
                                         android.R.layout.simple_list_item_1,
                                         parent,
                                         false
                                 )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.roomName.text = list[position].name
    }

    inner class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(p0: View?) {
            roomClickedInterface.roomSelected(list[adapterPosition])
        }

        var roomName: TextView = itemView!!.findViewById(android.R.id.text1)

        init {
            itemView!!.setOnClickListener(this)
        }
    }

    interface RoomClickedInterface{
        fun roomSelected(room:Room)
    }
}