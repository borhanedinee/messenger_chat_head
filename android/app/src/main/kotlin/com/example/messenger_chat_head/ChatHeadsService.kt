package com.example.messenger_chat_head

import android.animation.ObjectAnimator
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast


class ChatHeadService : Service() {
    private lateinit var windowManager: WindowManager
    private val chatHeadViews = mutableMapOf<String, View>()
    private val closeButtonsViews = mutableMapOf<String, View>()
    private val activeIcons = mutableMapOf<String, View>()
  

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getStringExtra("CHAT_ID") ?: return START_NOT_STICKY
        val action = intent.action

        if (action == "REMOVE_CHAT_HEAD") {
            val icon = intent.getStringExtra("ICON_NAME") ?: return START_NOT_STICKY
            removeChatHead(id , icon)
        } else {
            val icon = intent.getStringExtra("ICON_NAME") ?: return START_NOT_STICKY
            createChatHead(id, icon)
        }
        return START_STICKY
    }

    private fun createChatHead(id: String, icon: String) {
        if (activeIcons.containsKey(icon)) {
            Toast.makeText(applicationContext, "Chat head already exists!", Toast.LENGTH_SHORT).show()
            return
        }

        val chatHeadView = LayoutInflater.from(this).inflate(R.layout.chat_head, null)
        val imageView = chatHeadView.findViewById<ImageView>(R.id.chatHeadIcon)

        // the icon must be in drawable
        val resourceId = resources.getIdentifier(icon, "drawable", packageName)
        imageView.setImageResource(resourceId)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 200
        }

        windowManager.addView(chatHeadView, params)

        // Close button logic
        val closeButton = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_delete)
                alpha = 1f  // Initially hidden
        }
        val closeParams = WindowManager.LayoutParams(
            120, 120,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = params.x
            y = params.y - 150
        }
        windowManager.addView(closeButton, closeParams)
        

        // Attach touch listener to chat head
        chatHeadView.setOnTouchListener(ChatHeadTouchListener(params, closeParams, windowManager, closeButton))

        chatHeadViews[id] = chatHeadView
        closeButtonsViews[id] = closeButton
        activeIcons[icon] = chatHeadView

        closeButton?.setOnClickListener {
            removeChatHead(id , icon)
        }
    }

    private fun removeChatHead(id: String , icon: String) {
        val view = chatHeadViews.remove(id) ?: return
        val closeButtonView = closeButtonsViews.remove(id) ?: return
        windowManager.removeView(view) 
        windowManager.removeView(closeButtonView) 
        activeIcons.remove(icon)

    }

    override fun onDestroy() {
        super.onDestroy()
        chatHeadViews.values.forEach { windowManager.removeView(it) }
        chatHeadViews.clear()
        closeButtonsViews.values.forEach { windowManager.removeView(it) }
        closeButtonsViews.clear()
        activeIcons.clear()
    }
}
