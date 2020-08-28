package com.mam.akka.yellowchick

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
import com.mam.yellowchick.common.{ClientMessage, ServerMessage}

class client(host: String, port: Int) extends Actor {

  var serverActorRefer: ActorSelection = _

  override def preStart(): Unit = {

    serverActorRefer = context.actorSelection(s"akka.tcp://Server@${host}:${port}/user/ycServer")
    println("this.serverActorRefer=" + this.serverActorRefer)
  }


  override def receive: Receive = {
    case "start" => println("客户端启动了!!...")
    case mes: String => {
      println("开始咨询了")
      serverActorRefer ! ClientMessage(mes)
    }
    case ServerMessage(mes) => {
      println("收到小黄鸡咨询老师(Server)：" + mes)
    }
  }
}


object client extends App {
  val (host, port, serverHost, serverPort) = ("127.0.0.1", 9990, "127.0.0.1", 9999)
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
        """.stripMargin)

  val actorSystem: ActorSystem = ActorSystem("client", config)
  val actorRef: ActorRef = actorSystem.actorOf(Props(new client(serverHost, serverPort.toInt)), "customerActor-01")
  actorRef ! "start"
  while (true) {
    val mes = StdIn.readLine()
    actorRef ! mes
  }


}
