import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:messenger_chat_head/screens/onboarding_screen.dart';
import 'package:messenger_chat_head/services/chat_heads_service.dart';

late Size size;

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  ChatHeadService.initMethodChannel();
  runApp(const MainApp());
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    size = MediaQuery.of(context).size;
    return const GetMaterialApp(
        debugShowCheckedModeBanner: false, home: OnBoarding());
  }
}
