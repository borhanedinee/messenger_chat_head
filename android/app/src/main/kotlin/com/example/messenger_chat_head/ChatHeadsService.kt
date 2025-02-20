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

class ChatHeadService : Service() {
    private lateinit var windowManager: WindowManager
    private val chatHeadViews = mutableMapOf<String, View>()
    private var closeButton: ImageView? = null
    private var closeParams: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
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
            x = 50
            y = 200
        }

        windowManager.addView(chatHeadView, params)

        // Close button logic
        if (closeButton == null) {
            closeButton = ImageView(this).apply {
                setImageResource(android.R.drawable.ic_delete)
                alpha = 0f  // Initially hidden
            }
            closeParams = WindowManager.LayoutParams(
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
        }

        // Attach touch listener to chat head
        chatHeadView.setOnTouchListener(ChatHeadTouchListener(params, closeParams, windowManager, closeButton) { newX, newY ->
            updateCloseButtonPosition(newX, newY)
        })

        chatHeadViews[id] = chatHeadView

        closeButton?.setOnClickListener {
            removeChatHead(id)
        }
    }

    private fun updateCloseButtonPosition(x: Int, y: Int) {
        closeParams?.apply {
            this.x = x
            this.y = y - 150
            windowManager.updateViewLayout(closeButton, this)
        }
    }

    private fun removeChatHead(id: String) {
        val view = chatHeadViews.remove(id) ?: return
        windowManager.removeView(view)

        if (chatHeadViews.isEmpty()) {
            closeButton?.let {
                windowManager.removeView(it)
                closeButton = null
                closeParams = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatHeadViews.values.forEach { windowManager.removeView(it) }
        chatHeadViews.clear()
        closeButton?.let {
            windowManager.removeView(it)
            closeButton = null
            closeParams = null
        }
    }
}
