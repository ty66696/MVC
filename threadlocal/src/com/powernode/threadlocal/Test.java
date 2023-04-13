package com.powernode.threadlocal;
// 张三发送请求，对应一个线程t1
// 李四发送请求，对应一个线程t2
public class Test {
    public static void main(String[] args) {

        Thread thread = Thread.currentThread();
        System.out.println(thread);

        // 调用service
        UserService userService = new UserService();
        userService.save();
    }
}
