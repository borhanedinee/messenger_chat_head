package com.example.messenger_chat_head

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView

class ChatHeadTouchListener(
    private val params: WindowManager.LayoutParams,
    private val closeParams: WindowManager.LayoutParams?,
    private val windowManager: WindowManager,
    private val closeButton: ImageView?,
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
                snapToNearestEdge(view)
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
