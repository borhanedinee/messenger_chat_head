package com.example.messenger_chat_head

import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

import io.flutter.plugin.common.MethodChannel
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.animation.doOnEnd

import android.content.Intent



class ChatHeadTouchListener(
    private val context: Context,
    private val params: WindowManager.LayoutParams,
    private val windowManager: WindowManager,
    private val methodChannel: MethodChannel?,
    private val id: String,
    private val icon: String,
    private val name: String,
    private val deleteCircle: ImageView,
    private val deleteCircleParams: WindowManager.LayoutParams,
    private val onRemove: () -> Unit
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

                // Show delete circle
                deleteCircle.animate().alpha(1f).setDuration(200).start()

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - touchX).toInt()
                params.y = initialY + (event.rawY - touchY).toInt()

                println("params.x: ${params.x}")
                println("params.y: ${params.y}")

                // Update chat head position
                windowManager.updateViewLayout(view, params)

                return true
            }

            MotionEvent.ACTION_UP -> {
                val deltaX = abs(event.rawX - touchX)
                val deltaY = abs(event.rawY - touchY)

                if (deltaX < CLICK_THRESHOLD && deltaY < CLICK_THRESHOLD) {
                    // Treat as a click
                    Toast.makeText(context, "Opening conversation...", Toast.LENGTH_SHORT).show()
                    openApp()
                    val arguments = mapOf(
                        "id" to id, 
                        "icon" to icon,
                        "name" to name
                    )
                    methodChannel?.invokeMethod("open_chat", arguments)
                } else {
                    if (isNearDeleteCircle(view)) {
                        animateAndRemoveChatHead(view)  // Animate then remove
                    } else {
                        snapToNearestEdge(view)  // Return to screen edge
                    }
                }

                // Hide delete circle
                deleteCircle.animate().alpha(0f).setDuration(200).start()

                return true
            }
        }
        return false
    }

    private fun isNearDeleteCircle(view: View): Boolean {
        // values determined manually after debugging
        // these values determine the position of the delete button in function of 
        // position of chat head ( x and y )
        val deleteX = 470
        val deleteY = 1640

        val distance = sqrt(
            (params.x - deleteX).toDouble().pow(2.0) +
            (params.y - deleteY).toDouble().pow(2.0)
        )

        return distance < 300 // Threshold for snapping
    }

    private fun animateAndRemoveChatHead(view: View) {
        // values determined manually after debugging
        // these values determine the position of the delete button in function of 
        // position of chat head ( x and y )
        val deleteX = 472
        val deleteY = 1671

        val animatorX = ValueAnimator.ofInt(params.x, deleteX)
        val animatorY = ValueAnimator.ofInt(params.y, deleteY)

        animatorX.addUpdateListener { animation ->
            params.x = animation.animatedValue as Int
            windowManager.updateViewLayout(view, params)
        }

        animatorY.addUpdateListener { animation ->
            params.y = animation.animatedValue as Int
            windowManager.updateViewLayout(view, params)
        }

        animatorX.duration = 300
        animatorY.duration = 300
        animatorX.start()
        animatorY.start()

        animatorY.doOnEnd {
            onRemove() // Remove chat head after animation
        }
    }

    private fun openApp() {
        val packageName = context.packageName // Get your app's package name
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Ensure it opens in a new task
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "Unable to open app", Toast.LENGTH_SHORT).show()
        }
    }


    private fun snapToNearestEdge(view: View) {
        val screenWidth = windowManager.defaultDisplay.width
        val chatHeadCenterX = params.x + view.width / 2
        val targetX = if (chatHeadCenterX < screenWidth / 2) 0 else screenWidth - view.width

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
