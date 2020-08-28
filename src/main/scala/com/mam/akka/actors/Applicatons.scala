package com.mam.akka.actors

import akka.actor.{ActorSystem, Props}

object Applicatons {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("1")
    val b = system.actorOf(Props[BActor], "b")
    val a = system.actorOf(Props(new AActor(b)), "a")

    a ! "start"

  }
}
