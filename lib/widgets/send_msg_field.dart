import 'package:flutter/material.dart';
import 'package:messenger_chat_head/main.dart';

class SendMessageField extends StatefulWidget {
  const SendMessageField({
    super.key,
  });

  @override
  State<SendMessageField> createState() => _SendMessageFieldState();
}

class _SendMessageFieldState extends State<SendMessageField> {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: 70,
      width: size.width,
      padding: const EdgeInsets.symmetric(
        horizontal: 8.0,
        vertical: 10,
      ),
      color: Colors.grey[100],
      child: Row(
        children: [
          Expanded(
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Type a message...',
                contentPadding:
                    const EdgeInsets.symmetric(vertical: 12, horizontal: 10),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12.0),
                ),
              ),
            ),
          ),
          IconButton.filled(
            icon: const Icon(
              Icons.send,
              color: Colors.yellow,
              size: 14,
            ),
            onPressed: () {},
          ),
        ],
      ),
    );
  }
}
