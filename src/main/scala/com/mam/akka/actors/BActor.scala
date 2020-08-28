package com.mam.akka.actors

import akka.actor.{Actor, ActorRef}

class BActor extends Actor{
  override def receive: Receive = {

    case "我打" => {
      println("BActor(TOM) xlsbz")
      Thread.sleep(1000)
      sender() ! "我打"
    }
  }
}
