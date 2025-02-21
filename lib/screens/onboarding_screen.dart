import 'dart:async';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:messenger_chat_head/screens/conversations_screen.dart';

import 'package:lottie/lottie.dart';

class OnBoarding extends StatefulWidget {
  const OnBoarding({super.key});

  @override
  State<OnBoarding> createState() => _OnBoardingState();
}

class _OnBoardingState extends State<OnBoarding> {
  @override
  void initState() {
    Future.delayed(const Duration(seconds: 2), () {
      Get.off(
         ConversationsScreen(),
      );
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Lottie.asset('assets/lottie/loading_messages_lottie.json'),
      ),
    );
  }
}
