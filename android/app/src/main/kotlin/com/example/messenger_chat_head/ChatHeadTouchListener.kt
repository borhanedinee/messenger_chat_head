package com.example.messenger_chat_head

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.animation.ValueAnimator

class ChatHeadTouchListener(
    private val params: WindowManager.LayoutParams,
    private val windowManager: WindowManager,
    private val onPositionChanged: (Int, Int) -> Unit  // Added callback parameter
) : View.OnTouchListener {
    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                touchX = event.rawX
                touchY = event.rawY
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - touchX).toInt()
                params.y = initialY + (event.rawY - touchY).toInt()
                windowManager.updateViewLayout(view, params)
                onPositionChanged(params.x, params.y)  // Invoke callback
                return true
            }

            MotionEvent.ACTION_UP -> {
                snapToEdge(view)
                return true
            }
        }
        return false
    }

    private fun snapToEdge(view: View) {
        val screenWidth = windowManager.defaultDisplay.width
        val centerX = screenWidth / 2
        val targetX = if (params.x < centerX) 0 else screenWidth - view.width
        animateChatHeadToEdge(view, targetX)
    }

    private fun animateChatHeadToEdge(view: View, targetX: Int) {
        val animator = ValueAnimator.ofInt(params.x, targetX)
        animator.duration = 300
        animator.addUpdateListener { valueAnimator ->
            params.x = valueAnimator.animatedValue as Int
            windowManager.updateViewLayout(view, params)
        }
        animator.start()
    }
}
