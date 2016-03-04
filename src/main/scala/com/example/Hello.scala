package com.example

import com.example.actors.GovernorActor
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{TextField, ButtonBar, Button, Label}
import scalafx.scene.layout.{FlowPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

object Hello extends JFXApp {
    val txtFieldX0= new TextField()
    val txtFieldX1= new TextField()
    val txtFieldStep= new TextField()
    val txtFieldPolynomial = new TextField()
    val resultLabel = new Label()
    stage = new JFXApp.PrimaryStage {
      title.value = "Hello Stage"
      width = 600
      height = 450
      scene = new Scene {
        fill = Color.LightGreen
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
                style = "-fx-font-size: 24pt"
                minHeight = 30
                minWidth = 100
                onAction = handle {
                  Integral_Go()
                 }
                }
               )
             },
            resultLabel
          )
        }
        //}
      }
    }
    def Integral_Go():Unit = {
        val system = ActorSystem("MySystem")
        val initialActor = system.actorOf(Props[GovernorActor], name = "governorActor")

        println ("rozmiar, n*array[i]")
        //val rozmiar = scala.io.StdIn.readInt()
        //val lista = ArrayBuffer.empty[Double]
        val x0 = txtFieldX0.text.value.toDouble
        val x1 = txtFieldX1.text.value.toDouble
        val step = txtFieldStep.text.value.toDouble

        //for (i <- 0 to rozmiar) {
           // lista += scala.io.StdIn.readDouble()
       // }
        val polynomial = txtFieldPolynomial.text.value.mkString.split(',').toList.map(_.toDouble)

        initialActor ! (x0, x1, polynomial, step)

        Await.result (system.whenTerminated, Duration.Inf)
    }
}
