import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:messenger_chat_head/screens/onboarding_screen.dart';
late Size size ;

void main() {
  runApp(const MainApp());
}


class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    size = MediaQuery.of(context).size;
    return const GetMaterialApp(
      debugShowCheckedModeBanner: false,
      home: OnBoarding()
    );
  }
}
