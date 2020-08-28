package com.mam.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class SayHelloActor extends Actor {
  override def receive: Receive = {
    case "hello" => println("收到Hello 回应hello too:")
    case "ok" => println("收到ok 回应ok too:")
    case "exit" => context.stop(self); context.system.terminate()
    case _ => println("匹配不到")
  }
}


object SayHelloActorDemo {
  private val actoryFactory: ActorSystem = ActorSystem("actoryFactory")

  private val sayHelloActor: ActorRef = actoryFactory.actorOf(Props[SayHelloActor], "sayHelloActor")

  def main(args: Array[String]): Unit = {
    sayHelloActor ! "hello"
    sayHelloActor ! "ok"
    sayHelloActor ! "ok~"
    sayHelloActor ! "exit"
  }

}


