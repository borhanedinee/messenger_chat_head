package com.example.messenger_chat_head

import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

import io.flutter.plugin.common.MethodChannel


class ChatHeadTouchListener(
    private val context: Context,
    private val params: WindowManager.LayoutParams,
    private val closeParams: WindowManager.LayoutParams?,
    private val windowManager: WindowManager,
    private val closeButton: ImageView?,
    private val methodChannel: MethodChannel?,
    private val id: String
) : View.OnTouchListener {

    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f
    private val CLICK_THRESHOLD = 10  // Threshold to distinguish click vs. drag

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                touchX = event.rawX
                touchY = event.rawY

                // Show close button smoothly
                closeButton?.animate()?.alpha(1f)?.setDuration(200)?.start()

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - touchX).toInt()
                params.y = initialY + (event.rawY - touchY).toInt()

                // Update chat head position
                windowManager.updateViewLayout(view, params)

                // Move close button dynamically
                updateCloseButtonPosition()

                return true
            }

            MotionEvent.ACTION_UP -> {
                val deltaX = Math.abs(event.rawX - touchX)
                val deltaY = Math.abs(event.rawY - touchY)

                // If movement is small, treat it as a click
                if (deltaX < CLICK_THRESHOLD && deltaY < CLICK_THRESHOLD) {
                    Toast.makeText(context, "Opening conversation...", Toast.LENGTH_SHORT).show()
                    methodChannel?.invokeMethod("open_chat", id)
                } else {
                    snapToNearestEdge(view)
                }

                // Hide close button smoothly
                closeButton?.animate()?.alpha(1f)?.setDuration(200)?.start()

                return true
            }
        }
        return false
    }

    private fun snapToNearestEdge(view: View) {
        val screenWidth = windowManager.defaultDisplay.width
        val chatHeadCenterX = params.x + view.width / 2
        val targetX = if (chatHeadCenterX < screenWidth / 2) 0 else screenWidth - view.width

        // Animate chat head to nearest edge
        animateChatHeadToEdge(view, targetX)

        // Animate close button to move along
        animateCloseButton(targetX)
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

    private fun animateCloseButton(targetX: Int) {
        closeParams?.let { params ->
            val animator = ValueAnimator.ofInt(params.x, targetX)
            animator.duration = 300
            animator.addUpdateListener { valueAnimator ->
                params.x = valueAnimator.animatedValue as Int
                windowManager.updateViewLayout(closeButton, params)
            }
            animator.start()
        }
    }

    private fun updateCloseButtonPosition() {
        closeParams?.apply {
            x = params.x
            y = params.y - 150
            windowManager.updateViewLayout(closeButton, this)
        }
    }
}
