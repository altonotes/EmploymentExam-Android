package jp.co.altonotes.EmploymentExam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    lateinit var contentFrameLayout: FrameLayout
    lateinit var footerLeftButton: AppCompatButton
    lateinit var footerRightButton: AppCompatButton
    lateinit var loginView: LoginView
    lateinit var dataListView: DataListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        setupActions()
        updateContentsVisibility()
    }

    private fun bindViews() {
        contentFrameLayout = findViewById(R.id.contentFrame)
        footerLeftButton = findViewById(R.id.footerLeftButton)
        footerRightButton = findViewById(R.id.footerRightButton)
        loginView = findViewById(R.id.loginView)
        dataListView = findViewById(R.id.dataListView)
    }

    private fun setupActions() {
        footerLeftButton.setOnClickListener {
            it.isEnabled = false
            footerRightButton.isEnabled = true
            updateContentsVisibility()
        }
        footerRightButton.setOnClickListener {
            it.isEnabled = false
            footerLeftButton.isEnabled = true
            updateContentsVisibility()
            dataListView.requestData()
        }
    }

    private fun updateContentsVisibility() {
        loginView.visibility = if (footerLeftButton.isEnabled) View.INVISIBLE else View.VISIBLE
        dataListView.visibility = if (footerRightButton.isEnabled) View.INVISIBLE else View.VISIBLE
    }
}
