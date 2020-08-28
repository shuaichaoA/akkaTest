package com.mam.akka.mw.server

import akka.actor.{Actor, ActorSystem, Props}
import com.mam.akka.mw.common.{HeartBeat, RegisterWorkerInfo, RegisteredWorkerInfo, RemoveTimeOutWorker, SendHeartBeat, StartTimeOutWorker, WorkerInfo}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration.DurationInt

class Master extends Actor {
  val hm = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => {
      println("master 开始启动")
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker => {
      val values = hm.values
      val now = System.currentTimeMillis()
      values.filter(n => now - n.lastHeartBeat > 6000)
        .foreach(n => hm.remove(n.id))
      println("当前有"+hm.size+"个存活")
    }
    case RegisterWorkerInfo(id, cpu, ram) => {
      if (!hm.contains(id)) {
        hm += ((id, new WorkerInfo(id, cpu, ram)))
        println(id + "注册成功！")
        sender() ! RegisteredWorkerInfo
      }
    }
    case HeartBeat(id) => {
      val info = hm(id)
      info.lastHeartBeat = System.currentTimeMillis()
      println(id + "更新时间为" + info.lastHeartBeat)
    }
  }
}

object MasterDemo {
  def main(args: Array[String]): Unit = {

    val host = "127.0.0.1" //服务端ip地址
    val port = 10005
    //创建config对象,指定协议类型，监听的ip和端口
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=$host
         |akka.remote.netty.tcp.port=$port
        """.stripMargin)
    val serverActorSystem = ActorSystem("Server", config)
    val master = serverActorSystem.actorOf(Props[Master], "master")
    master ! "start"


  }
}
