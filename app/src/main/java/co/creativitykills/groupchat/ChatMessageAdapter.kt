package co.creativitykills.groupchat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pusher.chatkit.Message

class ChatMessageAdapter: RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    private var list = ArrayList<Message>()

    fun setList(list:ArrayList<Message>){
        this.list = list
        notifyDataSetChanged()
    }

    fun addMessage(e: Message){
        list.add(e)
        notifyDataSetChanged()
    }

    init {
        setList(ArrayList())
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context)
                                 .inflate(R.layout.custom_chat_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.userName.text = list[position].userId
        holder.message.text = list[position].text
    }

    inner class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView!!.findViewById(R.id.text_user_name)
        var message: TextView = itemView!!.findViewById(R.id.chat_message)
    }
}