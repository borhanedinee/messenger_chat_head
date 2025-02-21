import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_overlay_window/flutter_overlay_window.dart';
import 'package:get/get.dart';
import 'package:messenger_chat_head/screens/chats_screen.dart';

class ChatHeadService {
  static const MethodChannel _channel = MethodChannel('chat_head_channel');

  // Request Android to start a chat head with a unique ID
  static Future<void> startChatHead(String icon, String id, String name) async {
    try {
      // Check if overlay permission is granted

      if (!await FlutterOverlayWindow.isPermissionGranted()) {
        bool? granted = await FlutterOverlayWindow.requestPermission();
        if (!granted!) {
          Get.showSnackbar(
            const GetSnackBar(
              duration: Durations.extralong4,
              message: 'Chat head is not enabled',
            ),
          );
          return;
        }
      }

      // Now invoke the method safely
      await _channel.invokeMethod(
          'startChatHead', {'id': id, 'icon': icon, 'name': name});
    } on PlatformException catch (e) {
      print("Failed to start chat head: ${e.message}");
    }
  }

  // Request Android to remove a chat head by ID
  static Future<void> removeChatHead(String id) async {
    try {
      await _channel.invokeMethod('removeChatHead', {'id': id});
    } on PlatformException catch (e) {
      print("Failed to remove chat head: ${e.message}");
    }
  }

  // open conversation after invoking method channel from Kotlin
  static void initMethodChannel() {
    _channel.setMethodCallHandler((MethodCall call) async {
      if (call.method == "open_chat") {
        final Map<dynamic, dynamic> args = call.arguments;
        String chatId = args['id'] as String;
        String icon = args['icon'] as String;
        String name = args['name'] as String;
        _openChatScreen(
          chatId,
          icon,
          name,
        );
      }
    });
  }

  static void _openChatScreen(String chatId, String icon, String name) {
    Get.to(
      ChatsScreen(
        convoId: int.parse(chatId),
        icon: icon,
        name: name,
      ),
    );
  }
}
