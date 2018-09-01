package oneline.client

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._

import scala.scalajs.js.annotation.JSExportTopLevel


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
      img(id := "iid", src := "#"),
      p(id := "pid", "Image as text")
    ).toString()
  }


  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    import oneline.common.DataTransfer
    import org.scalajs.dom
    import upickle.default._

    def ajexResult(data: Blob, status: String): Unit = {
      println("data received from ajax: " + data)
      println("status received from ajax: " + status)
    }

    val input = dom.document.getElementById("inp").asInstanceOf[HTMLInputElement]
    println("inp len:" + input.files.length)
    for (i <- 0 until input.files.length) {
      println("f " + input.files(i).name)
      val fr = new FileReader()
      fr.onload = _ => {
        val imgs = fr.result.asInstanceOf[String]

        val trans = DataTransfer(img = imgs)
        val body = write(trans)
        val url = s"/trans"
        println(s"post $url")
        Ajax.post(url = url, data = body)

        val img = dom.document.getElementById("iid").asInstanceOf[HTMLImageElement]
        //noinspection ScalaDeprecation
        img.src = imgs
        dom.document.getElementById("pid").innerHTML = imgs
      }
      fr.readAsDataURL(input.files(i))
    }
  }


}
