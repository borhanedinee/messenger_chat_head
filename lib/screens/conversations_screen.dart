import 'package:dash_bubble/dash_bubble.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';
import 'package:messenger_chat_head/main.dart';
import 'package:messenger_chat_head/services/chat_heads_service.dart';

class ConversationsScreen extends StatelessWidget {
  final List<Map<String, dynamic>> conversations = [
    {
      'id': '1',
      'name': 'John Doe',
      'lastMessage': 'Hey! How are you?',
      'time': DateTime.now().subtract(const Duration(minutes: 5)),
      'profilePic': 'userone.png'
    },
    {
      'id': '2',
      'name': 'Jane Smith',
      'lastMessage': 'Let’s catch up tomorrow!',
      'time': DateTime.now().subtract(const Duration(hours: 1)),
      'profilePic': 'usertwo.png'
    },
    {
      'id': '3',
      'name': 'Alex Johnson',
      'lastMessage': 'Can you send the files?',
      'time': DateTime.now().subtract(const Duration(hours: 3)),
      'profilePic': 'userthree.png'
    },
    {
      'id': '4',
      'name': 'Emma Brown',
      'lastMessage': 'Thanks! See you later.',
      'time': DateTime.now().subtract(const Duration(days: 1)),
      'profilePic': 'userfour.png'
    },
  ];

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.grey[200],
        body: Column(
          children: [
            Container(
              margin: const EdgeInsets.only(bottom: 16),
              padding: const EdgeInsets.symmetric(horizontal: 16),
              height: 80,
              width: size.width,
              decoration: const BoxDecoration(
                color: Colors.indigo,
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(10),
                  bottomRight: Radius.circular(10),
                ),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  IconButton(
                    onPressed: () {},
                    icon: const Icon(
                      Icons.menu,
                      color: Colors.white,
                    ),
                  ),
                  const Text(
                    'Chats',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20,
                    ),
                  ),
                  IconButton(
                    onPressed: () {},
                    icon: const Icon(
                      Icons.person,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: TextField(
                decoration: InputDecoration(
                  contentPadding: const EdgeInsets.symmetric(vertical: 12),
                  hintText: 'Search...',
                  prefixIcon: const Icon(Icons.search),
                  filled: true,
                  fillColor: Colors.white,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(10),
                    borderSide: BorderSide.none,
                  ),
                ),
              ),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: conversations.length,
                itemBuilder: (context, index) {
                  final conversation = conversations[index];
                  return ListTile(
                    leading: CircleAvatar(
                      backgroundImage: AssetImage(
                          'assets/profilepic/${conversation['profilePic']}'),
                      radius: 28,
                    ),
                    title: Text(
                      conversation['name'],
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text(
                      conversation['lastMessage'],
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    trailing: Text(
                      DateFormat('hh:mm a').format(conversation['time']),
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    onTap: () {
                      // Handle conversation tap
                    },
                    onLongPress: () {
                      showModalWhenLongPressConvo(
                        context,
                        conversation['profilePic']
                            .toString()
                            .replaceFirst(RegExp(r'\.png$'), ''),
                        conversation['id'],
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<dynamic> showModalWhenLongPressConvo(
      BuildContext context, String icon , id) {
    return showModalBottomSheet(
      context: context,
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _openChatHeadTile(icon , id),
              _blockUserTile(context),
            ],
          ),
        );
      },
    );
  }

  ListTile _blockUserTile(BuildContext context) {
    return ListTile(
      leading: const Icon(Icons.block, color: Colors.red),
      title: const Text('Block', style: const TextStyle(color: Colors.red)),
      onTap: () {
        Navigator.pop(context);
        // Handle block action
      },
    );
  }

  ListTile _openChatHeadTile(String icon ,String id) {
    return ListTile(
      leading: const Icon(Icons.chat_bubble_outline),
      title: const Text('Open Chat Head'),
      onTap: () {
        ChatHeadService.startChatHead(icon , id);
      },
    );
  }

  void requestChatHeadPermissions(String icon) async {
    final isGranted = await DashBubble.instance.requestOverlayPermission();
    if (isGranted) {
      Get.showSnackbar(
        const GetSnackBar(
          duration: Durations.long4,
          message: 'Chat head is now enabled',
        ),
      );

      // start buble
      startBubble(
          bubbleOptions: BubbleOptions(
            bubbleIcon: icon,
            bubbleSize: 70,
            enableAnimateToEdge: true,
            enableBottomShadow: true,
            distanceToClose: 20,
            keepAliveWhenAppExit: true,
          ),
          onTap: () {
            print('chat head tapped');
          });
    } else {
      // Open chat head
      Get.showSnackbar(
        const GetSnackBar(
          duration: Durations.long4,
          message: 'Chat head is not enabled',
        ),
      );
    }
  }

  void startBubble(
      {required BubbleOptions bubbleOptions, Function()? onTap}) async {
    final hasStarted = await DashBubble.instance.startBubble(
      bubbleOptions: bubbleOptions,
      onTap: onTap,
    );
    if (hasStarted) {
      Get.showSnackbar(
        const GetSnackBar(
          duration: Durations.long4,
          message: 'Chat head is now enabled',
        ),
      );
    } else {
      Get.showSnackbar(
        const GetSnackBar(
          duration: Durations.long4,
          message: 'Chat head is not enabled',
        ),
      );
    }
  }
}
