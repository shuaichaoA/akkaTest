package com.mam.akka.actors

import akka.actor.{Actor, ActorRef}

class AActor(bActor: ActorRef) extends Actor{
  override def receive: Receive = {
    case "start" => {
      println("AActor 出招了， start ok")
      self ! "我打"
    }
    case "我打" => {
      println("AActor(JACK) fswyj")
      Thread.sleep(1000)
      bActor ! "我打"
    }
  }
}
