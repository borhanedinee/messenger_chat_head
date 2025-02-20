package com.example.messenger_chat_head

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView

class ChatHeadService : Service() {
    private lateinit var windowManager: WindowManager
    private val chatHeadViews = mutableMapOf<String, View>()
    private lateinit var closeButton: ImageView
    private lateinit var closeParams: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupCloseButton()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getStringExtra("CHAT_ID") ?: return START_NOT_STICKY
        val action = intent.action

        if (action == "REMOVE_CHAT_HEAD") {
            removeChatHead(id)
        } else {
            val icon = intent.getStringExtra("ICON_NAME") ?: return START_NOT_STICKY
            createChatHead(id, icon)
        }

        return START_STICKY
    }

    private fun setupCloseButton() {
        closeButton = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_delete)
            visibility = View.GONE
        }

        closeParams = WindowManager.LayoutParams(
            100, 100,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }

        windowManager.addView(closeButton, closeParams)

        closeButton.setOnClickListener {
            removeCurrentChatHead()
        }
    }

    private fun createChatHead(id: String, icon: String) {
        if (chatHeadViews.containsKey(id)) return

        val chatHeadView = LayoutInflater.from(this).inflate(R.layout.chat_head, null)
        val imageView = chatHeadView.findViewById<ImageView>(R.id.chatHeadIcon)
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
            x = 100
            y = 100
        }

        windowManager.addView(chatHeadView, params)

        chatHeadView.setOnTouchListener(ChatHeadTouchListener(params, windowManager) { newX, newY ->
            closeParams.x = if (newX > resources.displayMetrics.widthPixels / 2) newX - 100 else newX + 100
            closeParams.y = newY - 100
            windowManager.updateViewLayout(closeButton, closeParams)
            closeButton.visibility = View.VISIBLE
        })

        chatHeadViews[id] = chatHeadView
    }

    private fun removeCurrentChatHead() {
        if (chatHeadViews.isNotEmpty()) {
            val firstKey = chatHeadViews.keys.firstOrNull() ?: return
            val view = chatHeadViews.remove(firstKey)
            if (view != null) {
                windowManager.removeView(view)
            }
        }
        closeButton.visibility = View.GONE
    }

    private fun removeChatHead(id: String) {
        val view = chatHeadViews.remove(id) ?: return
        windowManager.removeView(view)
        if (chatHeadViews.isEmpty()) {
            closeButton.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatHeadViews.values.forEach { windowManager.removeView(it) }
        chatHeadViews.clear()
        windowManager.removeView(closeButton)
    }
}
