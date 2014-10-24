package notecanvas

import java.lang.Math.random
import javafx.event.EventHandler
import javafx.scene.input.{KeyEvent, MouseEvent}

import org.jfugue.Player

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.future
import scalafx.Includes._
import scalafx.animation.Timeline
import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.Text

object Canvas extends JFXApp {

  val debugText: Text = new Text {
    text = "Debug:"
    alignmentInParent = Pos.CenterLeft
  }

  val rectangle = new Rectangle {
    x = 10
    y = 10
    width = 300
    height = 30
    fill = Color.LightGreen
  }

  val stackPane = new StackPane {
    content = List(rectangle, debugText)
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Notes canvas"
    width = 600
    height = 450
    scene = new Scene {
      fill = Color.Black
      content += stackPane

      onMouseClicked = new EventHandler[MouseEvent] {
        override def handle(me: MouseEvent) = {
          val color: Color = Color.color(random, random, random)
          createCircle(me.x, me.y, color)
          play(color)
        }
      }

      onKeyPressed = new EventHandler[KeyEvent] {
        override def handle(event: KeyEvent) = {
          event.getCode.name() match {
            case "Q" => play("[SYNTH_DRUM]", "C"); createCircle(random() * 600, random() * 450, Color.Red)
            case "W" => play("[SYNTH_DRUM]", "D"); createCircle(random() * 600, random() * 450, Color.Red)
            case "E" => play("[SYNTH_DRUM]", "E"); createCircle(random() * 600, random() * 450, Color.Red)
            case "R" => play("[SYNTH_DRUM]", "F"); createCircle(random() * 600, random() * 450, Color.Red)
            case "T" => play("[SYNTH_DRUM]", "G"); createCircle(random() * 600, random() * 450, Color.Red)
            case "Y" => play("[SYNTH_DRUM]", "A"); createCircle(random() * 600, random() * 450, Color.Red)
            case "U" => play("[SYNTH_DRUM]", "B"); createCircle(random() * 600, random() * 450, Color.Red)

            case "A" => play("[PIANO]", "C"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "S" => play("[PIANO]", "D"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "D" => play("[PIANO]", "E"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "F" => play("[PIANO]", "F"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "G" => play("[PIANO]", "G"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "H" => play("[PIANO]", "A"); createCircle(random() * 600, random() * 450, Color.Blue)
            case "J" => play("[PIANO]", "B"); createCircle(random() * 600, random() * 450, Color.Blue)

            case "Z" => play("[VIOLIN]", "C"); createCircle(random() * 600, random() * 450, Color.Green)
            case "X" => play("[VIOLIN]", "D"); createCircle(random() * 600, random() * 450, Color.Green)
            case "C" => play("[VIOLIN]", "E"); createCircle(random() * 600, random() * 450, Color.Green)
            case "V" => play("[VIOLIN]", "F"); createCircle(random() * 600, random() * 450, Color.Green)
            case "B" => play("[VIOLIN]", "G"); createCircle(random() * 600, random() * 450, Color.Green)
            case "N" => play("[VIOLIN]", "A"); createCircle(random() * 600, random() * 450, Color.Green)
            case "M" => play("[VIOLIN]", "B"); createCircle(random() * 600, random() * 450, Color.Green)
            case _ =>
          }
        }
      }

      private def createCircle(x: Double, y: Double, circleColor: Color) {
        val newCircle: Circle = new Circle {
          centerX = x
          centerY = y
          fill = circleColor
          effect = new DropShadow {
            radius = 25
            spread = 0.25
            color = circleColor
          }
        }
        content += newCircle

        new Timeline() {
          keyFrames = Seq(
            at(0 s) {
              newCircle.radius -> 0
            },
            at(10 s) {
              newCircle.radius -> 40
            },
            at(11 s) {
              newCircle.radius -> 0
            }
          )
        }.play()
      }
    }
  }

  def getInstrument(color: Color) = {
    Map(color.red -> "[SYNTH_DRUM]", color.green -> "[VIOLIN]", color.blue -> "[PIANO]").toList.sortBy(_._1).reverse.head._2
  }

  def getPartitura(instrument: String) = instrument match {
    case "[PIANO]" => "E5s A5s C6s B5s E5s B5s D6s C6i E6i G#5i E6i | A5s E5s A5s C6s B5s E5s B5s D6s C6i A5i Ri"
    case "[VIOLIN]" => "G5i A5i G5i F5i E5q C5q"
    case "[SYNTH_DRUM]" => (for (i <- 1 to 8) yield Seq("G5q", "G5s", "G5s")).flatten.mkString(" ")
    case _ => "C D E F G A B"
  }

  def play(color: Color): Unit = {
    val instrument = getInstrument(color)
    val partitura = getPartitura(instrument)
    play(instrument, partitura)
  }

  def play(instrument: String, partitura: String): Unit = {
    debugText.text = instrument
    future {
      val player = new Player()
      player.play(s"I$instrument $partitura")
    }
  }
}
