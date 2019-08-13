package jp.co.altonotes.EmploymentExam

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import java.io.IOException
import java.nio.charset.Charset


/**
 * データ表示画面
 */
class DataListView : ConstraintLayout {

    lateinit var recyclerView: RecyclerView
    val adapter: DataListAdapter = DataListAdapter(context)

    var items: List<Map<String, String>>? = null

    constructor(context: Context) : super(context) {
        commonInit(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        commonInit(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        commonInit(context, attrs)
    }

    private fun commonInit(context: Context, attrs: AttributeSet? = null) {
        val layout = LayoutInflater.from(context).inflate(R.layout.data_list_view, this)
        recyclerView = layout.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    fun requestData() {
        val client = OkHttpClient().newBuilder().build()
        val requestBuilder = okhttp3.Request.Builder()
                .url("https://apidemo.altonotes.co.jp/data")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
        val accessToken = LoginView.accessToken
        if (accessToken != null) {
            requestBuilder.addHeader("Authorization", accessToken)
        }
        requestBuilder.get()

        val httpCall = client.newCall(requestBuilder.build())

        httpCall.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val statusCode = response.code()
                if (statusCode != 200) {
                    handler.post {
                        AlertDialog.Builder(context)
                                .setMessage("ステータスコードエラーが発生しました。[$statusCode]")
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, null)
                                .show()
                    }
                    return
                }

                val body = response.body()?.bytes() ?: return
                try {
                    val jsonString = String(bytes = body, charset = Charset.forName("UTF-8"))
                    val jsonObject = Gson().fromJson(jsonString, JsonObject::class.java)
                    handler.post {
                        adapter.data = jsonObject.get("items").asJsonArray
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    AlertDialog.Builder(context)
                            .setMessage("ステータスコードエラーが発生しました。[$0]")
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                }
            }
        })
    }
}
