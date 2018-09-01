package oneline.client

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._

import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  val ID_FILESELECT = "id-fileselect"
  val ID_IMG = "id-img"
  val ID_BASE64 = "id-base64"
  val ID_RESPONSE = "id-response"

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = body
  }

  def body: String = {
    import scalatags.JsDom.all._
    div(
      h1("oneline"),
      p("upload an image an create your own oneline image 06"),
      input(id := ID_FILESELECT, `type` := "file", onchange := "selectedFile()"),
      img(id := ID_IMG, src := "#"),
      p(id := ID_RESPONSE, "--- Response ---"),
      p(id := ID_BASE64, "--- Image as text ---")
    ).toString()
  }


  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    import oneline.common.OnelineRequest
    import org.scalajs.dom
    import upickle.default._

    def ajexResult(data: Blob, status: String): Unit = {
      println("data received from ajax: " + data)
      println("status received from ajax: " + status)
    }

    val input = dom.document.getElementById(ID_FILESELECT).asInstanceOf[HTMLInputElement]
    println("input.files.length:" + input.files.length)
    for (i <- 0 until input.files.length) {
      println(s"input.files($i).name" + input.files(i).name)
      val fr = new FileReader()
      fr.onload = _ => {
        val imgs = fr.result.asInstanceOf[String]

        val trans = OnelineRequest(img = imgs)
        val body = write(trans)
        println(s"body:$body")
        val url = s"/trans"
        println(s"url:$url")
        Ajax.post(url = url, data = body)

        val img = dom.document.getElementById(ID_IMG).asInstanceOf[HTMLImageElement]
        //noinspection ScalaDeprecation
        img.src = imgs
        dom.document.getElementById(ID_BASE64).innerHTML = imgs
      }
      fr.readAsDataURL(input.files(i))
    }
  }


}
