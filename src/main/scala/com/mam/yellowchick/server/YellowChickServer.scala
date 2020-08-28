package com.mam.yellowchick.server

import akka.actor.{Actor, ActorSystem, Props}
import com.mam.yellowchick.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

class YellowChickenServer extends Actor {
  override def receive: Receive = {
    case "start" => println("服务器在9999端口上监听了....")
    case ClientMessage(mes) => {
      println("客户咨询问题是:" + mes)
      mes match {
        case "大数据学费是多少" => sender() ! ServerMessage("15000RMB")
        case "学校地址" => sender() ! ServerMessage("昌平区宏福大楼xxx路")
        case "可以学哪些技术" => sender() ! ServerMessage("JavaEE 大数据 Python")
        case _ => sender() ! ServerMessage("你说啥子~~")

      }
    }
  }
}

object YellowChickServerDemo extends App {
  val host = "127.0.0.1" //服务端ip地址
  val port = 9999

  //创建config对象,指定协议类型，监听的ip和端口
  //待
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
        """.stripMargin)

  // 指定IP 和 端口
  val actorSystem = ActorSystem("Server", config)

  val serverActorRef = actorSystem.actorOf(Props[YellowChickenServer], "ycServer")
  //发送到自己的mailbox -》 receive方法处理
  serverActorRef ! "start"



}
