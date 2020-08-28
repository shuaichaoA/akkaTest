package com.mam.yellowchick.client

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.mam.yellowchick.common.{ClientMessage, ServerMessage}

import scala.io.StdIn

class CustomerActor(host: String, port: Int) extends Actor {
  //说明
  //1. 定义服务端的代理对象,需要通过它发送消息给小黄鸡客户
  var serverActorRef: ActorSelection = _

  // 在receive方法之前调用
  override def preStart(): Unit = {
    // 说明

    // 1. akka.tcp://Server@127.0.0.1:9999 是服务器url
    // 2. 让客户端获取到YellowChickServer这个Actor的代理对象 (ActorRef类型)
    // 3. 在receive方法使用这个代理对象就可以给YellowChickServer这个Actor发送消息了
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${host}:${port}/user/yellowChick-01")
  }

  override def receive: Receive = { //
    case "start" => println("客户端start...")
    case msg: String => {
      // 把客户端输入的内容发送给 服务端（actorRef）-> 服务端的receive获取
      serverActorRef ! ClientMessage(msg)
    }
    //匹配ServerMessage(msg) 样例对象
    case ServerMessage(msg) => println(s"收到小黄鸡客服(server)消息：$msg")
  }
}

object CustomerActor extends App {

  val host = "127.0.0.1"
  val port = 9990

  val serverHost = "127.0.0.1"
  val serverPort = 9999

  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
        """.stripMargin)

  //创建client的工厂
  val clientSystem = ActorSystem("client", config)

  // 创建客户的actorRef
  val actorRef = clientSystem.actorOf(Props(new CustomerActor(serverHost, serverPort.toInt)), "customer")

  // 给自己发送了一条消息 到自己的mailbox => receive
  actorRef ! "start"

  while (true) {
    val mes = StdIn.readLine()
    actorRef ! mes
  }

}
