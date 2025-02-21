package com.example.messenger_chat_head

import android.content.Context
import android.widget.Toast
import android.view.View
import io.flutter.plugin.common.MethodChannel

class ChatHeadClickListener(
    private val context: Context, // Pass application context
    private val methodChannel: MethodChannel?, // Pass method channel
    private val chatId: String // Chat ID to open conversation
) : View.OnClickListener {

    override fun onClick(v: View?) {
        // Show a Toast message
        Toast.makeText(context, "Invoking Channel To Open Conversation!", Toast.LENGTH_SHORT).show()

        // Invoke Flutter method channel to open the chat
        methodChannel?.invokeMethod("open_chat", chatId)
    }
}
