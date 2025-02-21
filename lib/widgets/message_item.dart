import 'package:flutter/material.dart';

class MessageItem extends StatelessWidget {
  final bool isCurrentUser;
  final String message;

  const MessageItem({
    super.key,
    required this.isCurrentUser,
    required this.message,
  });

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: isCurrentUser ? Alignment.centerRight : Alignment.centerLeft,
      child: SizedBox(
        width: MediaQuery.of(context).size.width - 100,
        child: Column(
          crossAxisAlignment:
              isCurrentUser ? CrossAxisAlignment.end : CrossAxisAlignment.start,
          children: [
            Container(
              margin:
                  const EdgeInsets.symmetric(vertical: 4.0, horizontal: 8.0),
              padding: const EdgeInsets.all(12.0),
              decoration: BoxDecoration(
                color: isCurrentUser
                    ? Theme.of(context).primaryColor
                    : Colors.grey[300],
                borderRadius: BorderRadius.circular(12.0),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    message,
                    style: TextStyle(
                      color: isCurrentUser ? Colors.white : Colors.black,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getStatusText(status) {
    switch (status) {
      case 'sending':
        return "Sending...";
      case 'sent':
        return "Sent";
      case 'failed':
        return "Failed. Tap to retry.";
      default:
        return "sent";
    }
  }

  String formatDateTime(DateTime dateTime) {
    final now = DateTime.now();
    final difference = now.difference(dateTime);

    final time =
        "${dateTime.hour % 12 == 0 ? 12 : dateTime.hour % 12}:${dateTime.minute.toString().padLeft(2, '0')} ${dateTime.hour >= 12 ? 'pm' : 'am'}";

    if (difference.inDays == 0 && now.day == dateTime.day) {
      return "Today at $time";
    } else if (difference.inDays == 1 && now.day - dateTime.day == 1) {
      return "Yesterday at $time";
    } else {
      final weekday = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
      return "${weekday[dateTime.weekday % 7]} at $time";
    }
  }
}
