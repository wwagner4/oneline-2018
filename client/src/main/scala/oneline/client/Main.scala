package oneline.client

import oneline.common.OnelineResponse
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._

import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success}

object Main {

  val ID_FILESELECT = "id-fileselect"
  val ID_FILESELECT_LABEL = "id-fileselect-label"
  val ID_IMGORIG = "id-imgorig"
  val ID_IMGRESP = "id-imgresp"

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = body
    getHtmlElem(ID_FILESELECT_LABEL).style =
      s"""    padding: 10px;
         |    cursor: pointer;
         |    background-color: #7474b3;
         |    border-style: solid;
         |    border-radius: 12px;
         |    border-width: 1px;
      """.stripMargin
    getHtmlElem(ID_IMGORIG).style =
      """    image-rendering: pixelated;
        |    width: 600px;
        |    height: auto;
      """.stripMargin
    dom.document.body.style =
      """    background-color: #38457d;
        |    color: #cac9ff;
        |    font-family: sans-serif;
        |    margin: 20px;
      """.stripMargin
  }

  def body: String = {
    import scalatags.JsDom.all._
    div(
      h1("oneline"),
      p("upload an image an create your own oneline image 06"),
      p(label(id := ID_FILESELECT_LABEL, `for` := ID_FILESELECT, "click here to upload your image"),
        input(id := ID_FILESELECT, style := "display: none;", `type` := "file", onchange := "selectedFile()")
      ),
      p(img(id := ID_IMGORIG, src := "#")),
      p(img(id := ID_IMGRESP, src := "#")),
    ).toString()
  }


  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {
    import oneline.common.OnelineRequest
    import org.scalajs.dom
    import upickle.default._

    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

    val input = dom.document.getElementById(ID_FILESELECT).asInstanceOf[HTMLInputElement]
    println("input.files.length:" + input.files.length)
    for (i <- 0 until input.files.length) {
      println(s"input.files($i).name" + input.files(i).name)
      val fr = new FileReader()
      fr.onload = (evt: UIEvent) => {
        println(s"evt:$evt")
        val imgs = fr.result.asInstanceOf[String]
        val trans = OnelineRequest(img = imgs)
        val body = write(trans)
        println(s"body:$body")
        val url = s"/trans"
        println(s"url:$url")
        Ajax.post(url = url, data = body).onComplete {
          case Success(xhr) =>
            println(s"xhr:$xhr")
            val resp = read[OnelineResponse](xhr.responseText)
            println(s"resp:$resp")
            setImage(ID_IMGRESP, resp.img)
          case Failure(_e) =>
            println(_e.toString)
        }
        val img = dom.document.getElementById(ID_IMGORIG).asInstanceOf[HTMLImageElement]
        //noinspection ScalaDeprecation
        setImage(ID_IMGORIG, imgs)
      }
      fr.readAsDataURL(input.files(i))
    }
  }

  def setImage(id: String, value: String): Unit = {
    val img = dom.document.getElementById(id).asInstanceOf[HTMLImageElement]
    //noinspection ScalaDeprecation
    img.src = value
  }

  def setInnerHtml(id: String, value: String): Unit = {
    dom.document.getElementById(id).innerHTML = value
  }

  def getHtmlElem(id: String): HTMLElement = {
    dom.document.getElementById(id).asInstanceOf[HTMLImageElement]
  }
}
