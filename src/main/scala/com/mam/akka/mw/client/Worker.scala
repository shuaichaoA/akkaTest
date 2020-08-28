package com.mam.akka.mw.client

import java.util.UUID


import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.mam.akka.mw.common.{HeartBeat, RegisterWorkerInfo, RegisteredWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

class Worker(host: String, port: Int) extends Actor {
  var masterProxy: ActorSelection = _

  override def preStart(): Unit = {
    masterProxy = context.actorSelection(s"akka.tcp://Server@${host}:${port}/user/master")
    println(masterProxy)
  }

  val id = UUID.randomUUID().toString

  override def receive: Receive = {
    case "start" => {
      println("worker1 启动")
      masterProxy !  RegisterWorkerInfo(id, 16, 16 * 1024)
    }
    case RegisteredWorkerInfo => {
      println("worker -" + id + "注册成功")
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
    }
    case SendHeartBeat => {
      println("Worker=" + id + "给master发送心跳")

      masterProxy ! HeartBeat(id)
    }
  }
}

object WorkerDemo extends App {

  val host = "127.0.0.1"
  val port = 10003

  val serverHost = "127.0.0.1"
  val serverPort = 10005

  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
        """.stripMargin)

  //创建client的工厂
  val clientSystem = ActorSystem("client", config)
  val worker: ActorRef = clientSystem.actorOf(Props(new Worker(serverHost, serverPort)), "worker1")
  worker ! "start"
}
