package com.example.actors

import akka.actor.{OneForOneStrategy, ActorRef, Actor, Props}
import akka.actor.SupervisorStrategy._

import scala.concurrent.duration.Duration

case class Result()

class GovernorActor extends Actor {
  var wynik1 = 0.0
  val actorlist:Array[ActorRef] = new Array[ActorRef](4)
  var count:Int =0
  var master:ActorRef=null
  import context._
  for (i <- 0 to 3) {
    actorlist(i) = actorOf(Props[GreeterActor], name = "X" + i.toString)
  }

  def receive = {
    case calka:Double => {
      println("GovernorReceiveFromChild "+ calka)
      context.stop(sender())
      wynik1+=calka
      count += 1
      if (count == 4) {
        println(wynik1)
        master ! wynik1
        println("""GovernorSystemTerminate""")
        context.system.terminate()
        context.stop(self)
      }
    }
    case (x0:Double,x1:Double, lista:List[Double], step:Double) => {
      println("""GovernorReceiveFromUi""")
      actorlist(0) ! (lista, x0, (3.0*x0+x1)/4.0, step)
      actorlist(1) ! (lista, (3.0*x0+x1)/4.0, (2.0*x0+2.0*x1)/4.0, step)
      actorlist(2) ! (lista, (2.0*x0+2.0*x1)/4.0, (1.0*x0+3.0*x1)/4.0, step)
      actorlist(3) ! (lista, (x0+3.0*x1)/4.0, x1, step)
      println("GovernorSendToChildren")
      }
    case Result => {
      master=sender()
      println("""GovernorReceiveResultQuestion""")
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = Duration(5000,"millis")) {
      case _:Throwable => {
        Restart
      }
    }
}

