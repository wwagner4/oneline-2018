package oneline.client

import oneline.common.{OnelineRequest, OnelineResponse}
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw._
import upickle.default.{read, write}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success}


object Main {

  val ID_FILESELECT = "id-fileselect"
  val ID_FILESELECT_LABEL = "id-fileselect-label"
  val ID_IMGORIG = "id-imgorig"
  val ID_IMGRESP = "id-imgresp"
  val ID_CONTENT = "id-content"

  case class SampleImage(
                          id: String,
                          img: String,
                        )

  val sampleImages = Seq(
    SampleImage("id-sample01", ImageUrls.donquichotte),
    SampleImage("id-sample02", ImageUrls.corrida),
    SampleImage("id-sample03", ImageUrls.peacedove),
  )

  def main(args: Array[String]): Unit = {
    dom.document.body.innerHTML = renderBody

    dom.document.body.style =
      """    background-color: #38457d;
      """.stripMargin
    getHtmlElem(ID_CONTENT).style =
      """    background-color: #38457d;
        |    color: #cac9ff;
        |    font-family: sans-serif;
        |    margin: 40px;
        |    font-size: large;
      """.stripMargin
    getHtmlElem(ID_FILESELECT_LABEL).style =
      s"""    padding: 10px;
         |    cursor: pointer;
         |    background-color: #7474b3;
         |    border-style: solid;
         |    border-width: 1px;
         |      """.stripMargin
    getHtmlElem(ID_IMGORIG).style =
      """    image-rendering: pixelated;
        |    width: 0px;
        |    height: auto;
      """.stripMargin
    getHtmlElem(ID_IMGRESP).style =
      """    image-rendering: pixelated;
        |    width: 0px;
        |    height: auto;
      """.stripMargin
    sampleImages.foreach { si =>
      getHtmlElem(si.id).style =
        """    image-rendering: pixelated;
          |    width: auto;
          |    height: 200px;
          |    padding-right: 5px;
        """.stripMargin
    }

  }

  def renderBody: String = {
    import scalatags.JsDom.all._
    div(id := ID_CONTENT,
      h1("oneline"),
      p("select one of the sample images"),
      p(sampleImages.map(si => img(id := si.id, src := si.img))),
      p(label(id := ID_FILESELECT_LABEL, `for` := ID_FILESELECT, "or click here to upload your own image"),
        input(id := ID_FILESELECT, style := "display: none;", `type` := "file", onchange := "selectedFile()")
      ),
      p(img(id := ID_IMGORIG, src := ImageUrls.background)),
      p(img(id := ID_IMGRESP, src := ImageUrls.background)),
    ).toString()
  }

  var selectedImage = Option.empty[String]

  @JSExportTopLevel("selectedFile")
  def selectedFile(e: UIEvent): Unit = {

    val input = dom.document.getElementById(ID_FILESELECT).asInstanceOf[HTMLInputElement]
    println("input.files.length:" + input.files.length)
    for (i <- 0 until input.files.length) {
      println(s"input.files($i).name" + input.files(i).name)
      val fr = new FileReader()
      fr.onload = (evt: UIEvent) => {
        println(s"evt:$evt")
        val imgs = fr.result.asInstanceOf[String]
        getHtmlElem(ID_IMGORIG).style =
          """image-rendering: pixelated;
            |width:600px;
            |height:auto;
            |margin-top: 7px;
          """.stripMargin
        setImage(ID_IMGORIG, imgs)
        selectedImage = Some(imgs)
      }
      fr.readAsDataURL(input.files(i))
    }
  }

  def callAjax(): Unit = {
    if (selectedImage.isDefined) {
      val trans = OnelineRequest(img = selectedImage.get)
      val body = write(trans)
      println(s"body:$body")
      val url = s"/trans"
      println(s"url:$url")
      Ajax.post(url = url, data = body).onComplete {
        case Success(xhr) =>
          println(s"xhr:$xhr")
          val resp = read[OnelineResponse](xhr.responseText)
          println(s"resp:$resp")
          getHtmlElem(ID_IMGRESP).style =
            """width:auto;
              |height:auto;
            """.stripMargin
          setImage(ID_IMGRESP, resp.img)
        case Failure(_e) =>
          println(_e.toString)
      }
    } else {
      println("NO IMAGE SELECTED")
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
