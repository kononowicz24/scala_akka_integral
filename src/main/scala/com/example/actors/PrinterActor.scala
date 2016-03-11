package com.example.actors

import akka.actor.{ActorRef, ActorLogging, Actor}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by krzysiekk on 11.03.16.
  */
class PrinterActor extends Actor with ActorLogging{
  val list:ArrayBuffer[Double] = new ArrayBuffer[Double]()
  var maxval:Double = Double.MinValue
  var minval:Double = Double.MaxValue
  var master:ActorRef = null
  def receive = {
    case (lista:List[Double], x0:Double, x1:Double, step:Double) =>
      master = sender()
      for (i <- x0 to x1 by step) {
        var pr0= Horner.horner(lista, i)
        list += pr0
        if (pr0>maxval) maxval=pr0
        if (pr0<minval) minval=pr0
      }
      sender() ! (list.toList, maxval, minval)
    case Result => master ! (list.toList, maxval, minval)
  }

}
