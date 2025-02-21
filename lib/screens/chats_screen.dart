import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:messenger_chat_head/main.dart';
import 'package:messenger_chat_head/widgets/message_item.dart';
import 'package:messenger_chat_head/widgets/send_msg_field.dart';

class ChatsScreen extends StatelessWidget {
  ChatsScreen({super.key, required this.convoId, required this.icon, required this.name});
  final int convoId;
  final String icon;
  final String name;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        appBar: _appBar(),
        body: SizedBox(
            height: size.height,
            width: size.width,
            child: SizedBox(
              height: size.height,
              width: size.width,
              child: Stack(
                children: [
                  Positioned.fill(
                    top: 0,
                    bottom: 70,
                    child: _loadOldConversation(convoId),
                  ),
                  const Positioned(
                    bottom: 0,
                    child: SendMessageField(),
                  ),
                ],
              ),
            ),
          ),
      ),
    );
  }

  AppBar _appBar() {
    return AppBar(
      actions: [
        IconButton(
          icon: const Icon(Icons.info_outline),
          onPressed: () {},
        )
      ],
      leading: IconButton(
        icon: const Icon(
          Icons.navigate_before,
        ),
        onPressed: () {
          Get.back();
        },
      ),
      elevation: 5,
      title: Row(
        children: [
          CircleAvatar(
            radius: 15,
            backgroundImage: AssetImage('assets/profilepic/$icon.png'),
          ),
          const SizedBox(width: 15),
          Text(
            name,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  ListView _loadOldConversation(int convoId) {
    final messages = conversations[convoId]!;
    return ListView.builder(
      itemCount: messages.length,
      itemBuilder: (context, index) {
        final message = messages[index];
        final messageContent = message['messageContent'];
        final isCurrentUser = message['isFromMe'];

        return Padding(
          padding: EdgeInsets.only(
            top: index == 0 ? 30 : 0,
          ),
          child: Column(
            children: [
              Padding(
                padding: EdgeInsets.only(
                  bottom: index == messages.length - 1 ? 20.0 : 0.0,
                ),
                child: MessageItem(
                  isCurrentUser: isCurrentUser,
                  message: messageContent,
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  Map<int, List<Map<String, dynamic>>> conversations = {
    1: [
      {"isFromMe": true, "messageContent": "Hey! How are you?"},
      {"isFromMe": false, "messageContent": "I'm good, thanks! How about you?"},
      {
        "isFromMe": true,
        "messageContent": "I'm doing great! Just working on a project."
      },
    ],
    2: [
      {
        "isFromMe": false,
        "messageContent": "Hey, did you see the latest update?"
      },
      {"isFromMe": true, "messageContent": "Yes! It's really cool."},
      {
        "isFromMe": false,
        "messageContent": "I think they added some nice features."
      },
      {
        "isFromMe": true,
        "messageContent": "Yeah, I especially like the new UI changes."
      },
    ],
    3: [
      {
        "isFromMe": true,
        "messageContent": "What time are we meeting tomorrow?"
      },
      {"isFromMe": false, "messageContent": "Around 3 PM should work."},
      {"isFromMe": true, "messageContent": "Alright, see you then!"},
    ],
    4: [
      {"isFromMe": false, "messageContent": "Did you finish the assignment?"},
      {"isFromMe": true, "messageContent": "Not yet, but I'm working on it."},
      {
        "isFromMe": false,
        "messageContent": "Let me know if you need any help!"
      },
      {
        "isFromMe": true,
        "messageContent": "Thanks! I'll reach out if I get stuck."
      },
      {"isFromMe": false, "messageContent": "Sounds good!"},
    ],
  };
}
