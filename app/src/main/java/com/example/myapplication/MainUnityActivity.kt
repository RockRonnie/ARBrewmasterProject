package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.startActivity
import com.example.testing.OverrideUnityActivity
import com.unity3d.player.UnityPlayer
import java.util.jar.Attributes

class MainUnityActivity : OverrideUnityActivity() {
    // Setup activity layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addControlsToUnityFrame()
        val intent: Intent = intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
        setIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        if (intent == null || intent.extras == null) return
        if (intent.extras!!.containsKey("doQuit")) if (mUnityPlayer != null) {
            finish()
        }
        if (intent.extras!!.containsKey("playerName")) if (mUnityPlayer != null) {
            run{
                UnityPlayer.UnitySendMessage("PlayerNameChange","changeName",intent.extras!!.getString("playerName"))
            }
        }
    }

    override fun showMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
    override fun setUserStatus(userStatus: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("setUserStatus", userStatus)
        startActivity(intent)
    }
    override fun sendCommand(command: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("newCommand", command)
        startActivity(intent)
    }
    override fun onUnityPlayerUnloaded() {
        showMainActivity()
    }
    fun addControlsToUnityFrame() {
        val layout: FrameLayout = mUnityPlayer
        run {
            val myButton = Button(this)
            myButton.text = "back"
            myButton.x = 0f
            myButton.y = 0f
            myButton.setOnClickListener { sendCommand("map") }
            layout.addView(myButton, 100, 100)
        }
    }

}