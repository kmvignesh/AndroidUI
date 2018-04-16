package com.example.vicky.androidui

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.OkHttpClient
import org.json.JSONObject
import okhttp3.RequestBody


var jsonResult = ""
var postResult = ""

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login.setOnClickListener {
            if (username.text.toString().isNotEmpty() && password.text.toString().isNotEmpty())
                makePostRequst(this, username.text.toString(), password.text.toString()).execute()
        }
    }


    class makePostRequst(var activity: MainActivity, var username: String, var password: String) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            val client = OkHttpClient()
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()
            val request = Request.Builder()
                    .url("http://192.168.43.212/postlogin.php")
                    .post(requestBody)
                    .build()
            val response = client.newCall(request).execute()
            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                val obj = JSONObject(result)
                postResult = obj.getString("message")
                makeJSONRequst(activity, username, password).execute()
            }
            super.onPostExecute(result)
        }

    }

    class makeJSONRequst(var activity: MainActivity, var username: String, var password: String) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            val JSON = MediaType.parse("application/json; charset=utf-8")
            val client = OkHttpClient()
            val requestObject = com.example.vicky.androidui.Model.Request()
            requestObject.username = username
            requestObject.password = password
            val body = RequestBody.create(JSON, Gson().toJson(requestObject))
            val request = Request.Builder()
                    .url("http://192.168.43.212/login.php")
                    .post(body)
                    .build()
            val response = client.newCall(request).execute()
            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                val obj = JSONObject(result)
                jsonResult = obj.getString("message")
            }
            val dialog = AlertDialog.Builder(activity)
            val view = activity.layoutInflater.inflate(R.layout.dialog_result, null)
            dialog.setView(view)
            view.findViewById<TextView>(R.id.json_result).text = jsonResult
            view.findViewById<TextView>(R.id.post_result).text = postResult
            dialog.show()
            super.onPostExecute(result)
        }
    }

}
