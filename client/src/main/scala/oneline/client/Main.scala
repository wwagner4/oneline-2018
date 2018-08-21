package oneline.client

import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel
import org.querki.jquery._
import org.scalajs.dom.raw.{File, HTMLInputElement, UIEvent}

object Main {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = body
  }


  def body: String =
    """
      |<h1>oneline</h1>
      |<p>upload an image an create your own oneline image 05</p>
      |<input id="inp" type='file' onChange="selectedFile()"/>
    """.stripMargin

  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    println("selected file")
    val input = dom.document.getElementById("inp").asInstanceOf[HTMLInputElement]
    println("inp len:" + input.files.length)
    for(i <- 0 until input.files.length) {
      println("f " + input.files(i).asInstanceOf[File].name)
    }
  }
}
