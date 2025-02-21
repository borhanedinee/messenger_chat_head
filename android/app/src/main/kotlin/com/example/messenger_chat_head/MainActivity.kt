package com.example.messenger_chat_head

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodCall

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "chat_head_channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Cache the Flutter Engine
        FlutterEngineCache.getInstance().put("chat_head_engine", flutterEngine)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startChatHead" -> {
                    val id = call.argument<String>("id") ?: return@setMethodCallHandler
                    val icon = call.argument<String>("icon") ?: return@setMethodCallHandler
                    val name = call.argument<String>("name") ?: return@setMethodCallHandler
                    startChatHead(id, icon , name)
                    result.success(null)
                }
                "removeChatHead" -> {
                    val id = call.argument<String>("id") ?: return@setMethodCallHandler
                    removeChatHead(id)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startChatHead(id: String, icon: String , name: String) {
        val serviceIntent = Intent(this, ChatHeadService::class.java).apply {
            putExtra("CHAT_ID", id)
            putExtra("ICON_NAME", icon)
            putExtra("NAME", name)
        }
        startService(serviceIntent)
    }

    private fun removeChatHead(id: String) {
        val serviceIntent = Intent(this, ChatHeadService::class.java).apply {
            putExtra("CHAT_ID", id)
            action = "REMOVE_CHAT_HEAD"
        }
        startService(serviceIntent)
    }
}
