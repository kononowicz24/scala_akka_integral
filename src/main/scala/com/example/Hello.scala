package com.example

import akka.util.Timeout
import akka.pattern.ask
import com.example.actors.{PrinterActor, Result, GovernorActor}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import akka.actor.{ActorSystem, Props}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control._
import scalafx.scene.layout.{FlowPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object Hello extends JFXApp {
    val txtFieldX0= new TextField{style = "-fx-background-color: white"}
    val txtFieldX1= new TextField{style = "-fx-background-color: white"}
    val txtFieldStep= new TextField{style = "-fx-background-color: white"}
    val txtFieldPolynomial = new TextField{style = "-fx-background-color: white"}
    val spinnerSteps = new Spinner[Double](10,1000,70,5) {

    }
    val resultLabel = new Label{style = "-fx-background-color: white"}
    val canvas = new Canvas(500,300)
    stage = new JFXApp.PrimaryStage {
      title.value = "Hello Stage"
      width = 600
      height = 450
      scene = new Scene {
        fill = Color.rgb(100,200,150)
        //content = new Button {
        title = "Calka"
        root = new VBox {
          children = Seq(
            new Label("Policz calke"),
            new FlowPane(){
              children = Seq(
                new Label("x0:"),
                txtFieldX0
              )
            },
            new FlowPane(){
              children = Seq(
                new Label("x1:"),
                txtFieldX1
              )
            },
            new FlowPane{
              children = Seq(
                new Label("step:"),
                txtFieldStep
              )
            },
            new FlowPane{
              children = Seq(
                new Label("polynomial(most significant left, separate with commas, no spaces):"),
                txtFieldPolynomial
              )
            },
            new ButtonBar {
              buttons = Seq(new Button {
                text = "Licz teraz!"
                style = "-fx-font-size: 24pt;" +
                  "-fx-background-color: white"
                minHeight = 30
                minWidth = 100
                onAction = handle {
                  Integral_Go()
                 }
                }
               )
            },
          resultLabel,
          canvas
          )
        }
        //}
      }
    }
    def Integral_Go():Unit = {
      val system = ActorSystem("MySystem")
      val initialActor = system.actorOf(Props[GovernorActor], name = "governorActor")
      val printerActor = system.actorOf(Props[PrinterActor], name = "ladnaDrukarka")

      println ("rozmiar, n*array[i]")
      //val rozmiar = scala.io.StdIn.readInt()
      //val lista = ArrayBuffer.empty[Double]
      val x0 = txtFieldX0.text.value.toDouble
      val x1 = txtFieldX1.text.value.toDouble
      val step = txtFieldStep.text.value.toDouble
      val polynomial = txtFieldPolynomial.text.value.mkString.split(',').toList.map(_.toDouble)
      val steps = spinnerSteps.value.value

      initialActor ! (x0, x1, polynomial, step)
      printerActor ! (polynomial, x0, x1, (x1-x0)/steps)

      implicit val timeout = Timeout(20 seconds)
      //val future: Future[Array[Int]] = ask(initialActor, Result).mapTo[Array[Int]]
      val future:Future[Double] = (initialActor ? Result).mapTo[Double]
      val result = Await.result(future, 10 second)

      val farFuture:Future[(List[Double],Double,Double)] = (printerActor ? Result).mapTo[(List[Double],Double,Double)]
      val farResult = Await.result(farFuture, 10 second)

      resultLabel.setText(result.toString)

      Await.result (system.whenTerminated, Duration.Inf)
    }
}
