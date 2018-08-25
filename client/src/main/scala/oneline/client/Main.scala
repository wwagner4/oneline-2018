package oneline.client

import org.scalajs.dom
import org.scalajs.dom.html.Input

import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.raw._
import scalatags.JsDom
import org.scalajs.dom.ext.Ajax


object Main {

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = body
  }

  def body: String = {
    import scalatags.JsDom.all._


    div(
      h1("oneline"),
      p("upload an image an create your own oneline image 06"),
      input(id := "inp", `type` := "file", onchange := "selectedFile()"),
      img(id:="iid", src:="#"),
      p(id := "pid", "Image as text")
    ).toString()
  }


  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    println("selected file")
    val input = dom.document.getElementById("inp").asInstanceOf[HTMLInputElement]
    println("inp len:" + input.files.length)
    for (i <- 0 until input.files.length) {
      println("f " + input.files(i).name)
      val fr = new FileReader()
      fr.onload = _ => {
        val imgs = fr.result.asInstanceOf[String]
        val img = dom.document.getElementById("iid").asInstanceOf[HTMLImageElement]
        img.src = imgs
        dom.document.getElementById("pid").innerHTML = imgs
      }
      fr.readAsDataURL(input.files(i))
    }
  }
}
