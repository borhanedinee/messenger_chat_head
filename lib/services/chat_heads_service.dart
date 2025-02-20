import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_overlay_window/flutter_overlay_window.dart';
import 'package:get/get.dart';

class ChatHeadService {
  static const MethodChannel _channel = MethodChannel('chat_head_channel');

  // Request Android to start a chat head with a unique ID
  static Future<void> startChatHead(String icon) async {
    try {
      // Check if overlay permission is granted
      if (!await FlutterOverlayWindow.isPermissionGranted()) {
        bool? granted = await FlutterOverlayWindow.requestPermission();
        if (!granted!) {
          Get.showSnackbar(
            const GetSnackBar(
              duration: Durations.long4,
              message: 'Chat head is not enabled',
            ),
          );
          return;
        }
      }

      // Now invoke the method safely
      await _channel.invokeMethod('startChatHead', {
        'id': DateTime.timestamp().microsecondsSinceEpoch.toString(),
        'icon': icon
      });
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
}
