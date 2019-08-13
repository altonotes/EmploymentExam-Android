package jp.co.altonotes.EmploymentExam

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import java.io.IOException
import java.nio.charset.Charset


/**
 * ログイン画面
 */
class LoginView : ConstraintLayout {

    companion object {
        var accessToken: String? = null
    }

    lateinit var userNameEditText: AppCompatEditText
    lateinit var passwordEditText: AppCompatEditText
    lateinit var indicator: ProgressBar

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
        val layout = LayoutInflater.from(context).inflate(R.layout.login_view, this)
        userNameEditText = layout.findViewById(R.id.userNameEditText)
        passwordEditText = layout.findViewById(R.id.passwordEditText)
        indicator = layout.findViewById(R.id.indicator)
        layout.findViewById<AppCompatButton>(R.id.loginButton).setOnClickListener {
            onTapLoginButton(it)
        }
    }

    private fun onTapLoginButton(@Suppress("UNUSED_PARAMETER") view: View) {
        val client = OkHttpClient().newBuilder().build()
        val requestBuilder = okhttp3.Request.Builder().url("https://apidemo.altonotes.co.jp/login")
        val parameter = "userName=${userNameEditText.text ?: ""}&password=${passwordEditText.text ?: ""}"
        val body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), parameter)
        requestBuilder.post(body)
        val httpCall = client.newCall(requestBuilder.build())

        indicator.visibility = View.VISIBLE
        httpCall.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                handler.post {
                    indicator.visibility = View.INVISIBLE
                }
                val responseHeaders = response.headers()
                val allHeaderFields = mutableMapOf<String, String>()
                responseHeaders.names().forEach {
                    val headerValue = responseHeaders[it] ?: return@forEach
                    allHeaderFields[it] = headerValue
                }
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
                    val token = jsonObject.get("accessToken")
                    accessToken = if (!token.isJsonNull) token.asString else null

                    val result = jsonObject.get("result").asString
                    val message: String

                    message = when (result) {
                        "0" -> "ログインに成功しました。"
                        "1" -> "ユーザーネームまたはパスワードに誤りがあります。"
                        else -> jsonObject.get("message").asString
                    }
                    handler.post {
                        AlertDialog.Builder(context)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, null)
                                .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    indicator.visibility = View.INVISIBLE
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
