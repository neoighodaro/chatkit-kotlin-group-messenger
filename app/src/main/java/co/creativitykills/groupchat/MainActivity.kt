package co.creativitykills.groupchat

import retrofit2.Call
import android.os.Bundle
import android.util.Log
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import android.widget.Toast
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener {
            if (text_user_name.text.isNotEmpty()) {
                createNewUser(text_user_name.text.toString())
            } else {
                Toast.makeText(this@MainActivity,"Please enter a username", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createNewUser(userName: String) {
        val jsonObject = JSONObject()
        jsonObject.put("username", userName)

        RetrofitClient().getClient().createUser(userName).enqueue(object: Callback<String>{
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.d("TAG", t.toString())
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response!!.code() == 200){
                    startActivity(Intent(this@MainActivity, RoomsListActivity::class.java)
                            .putExtra("extra",userName))
                }
            }
        })
    }
}