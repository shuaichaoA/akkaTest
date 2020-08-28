package com.mam.akka.mw.common


// worker注册信息
case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int)

// 这个是WorkerInfo, 保存在master的hashmap中的
// 从形式上看和RegisterWorkerInfo，但是后面功能增加后，就会有改进
// 因此，我们不使用RegisterWorkerInfo，而是单独的定义个类，来保存
class WorkerInfo(val id: String, val cpu: Int, val ram: Int) {
  var lastHeartBeat = System.currentTimeMillis()
}

// master向worker回复注册成功消息
case object RegisteredWorkerInfo

case object SendHeartBeat

case class HeartBeat(id: String)

case object StartTimeOutWorker

case object RemoveTimeOutWorker

