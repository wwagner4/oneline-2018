package oneline.client

import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.raw._

object Main {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = body
  }


  def body: String =
    """
      |<h1>oneline</h1>
      |<p>upload an image an create your own oneline image 06</p>
      |<input id="inp" type='file' onChange="selectedFile()"/>
      |<p id="pid">Image as text</p>
    """.stripMargin

  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    println("selected file")
    val input = dom.document.getElementById("inp").asInstanceOf[HTMLInputElement]
    println("inp len:" + input.files.length)
    for(i <- 0 until input.files.length) {
      println("f " + input.files(i).asInstanceOf[File].name)
      val fr = new FileReader()
      fr.onload = UIEvent =>  {
        // dom.document.getElementById("iid").asInstanceOf[HTMLImageElement].src = fr.result.asInstanceOf[String];
        dom.document.getElementById("pid").innerHTML = fr.result.asInstanceOf[String];
      }
      fr.readAsDataURL(input.files(i))
    }
  }
}
