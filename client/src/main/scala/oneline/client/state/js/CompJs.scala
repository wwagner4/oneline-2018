package oneline.client.state.js

import oneline.client.state.logic._
import oneline.common.{OnelineRequest, OnelineResponse, OnelineResponseDownlaod}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import org.scalajs.dom.raw._
import upickle.default.{read, write}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}


trait CompJs {

  def doc: html.Document

  def getHtmlElem(id: String): HTMLElement = {
    doc.getElementById(id).asInstanceOf[HTMLImageElement]
  }

}

trait Bindable {

  def bind(implicit doc: html.Document): Unit

}

abstract class CompJsActionAbstract extends CompAction with CompJs with Bindable {

  var onActionOpt = Option.empty[Unit => Unit]

  override def action(onAction: Unit => Unit): Unit = {
    onActionOpt = Some(onAction)
  }


  def action(evt: Event): Unit = {
    val f = onActionOpt.getOrElse(throw new IllegalStateException("action not defined"))
    f(())
  }

}

case class CompJsActionButton(doc: html.Document, id: String, label: String) extends CompJsActionAbstract {

  // Call that method after the component is
  // registered in the dom tree
  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(id)
    elem.onclick = action
  }

}

case class CompJsActionSelectImage(doc: html.Document, id: String, idLabel: String)
  extends CompActionSelectImage with CompJs with Bindable {

  var onStartOpt = Option.empty[Unit => Unit]
  var onSuccessOpt = Option.empty[ImgBase64Url => Unit]
  var onFailureOpt = Option.empty[String => Unit]

  override def action(
                       onStart: Unit => Unit = () => _,
                       onSuccess: ImgBase64Url => Unit,
                       onFailure: String => Unit
                     ): Unit = {
    onStartOpt = Some(onStart)
    onSuccessOpt = Some(onSuccess)
    onFailureOpt = Some(onFailure)
  }

  def selectedFile(doc: html.Document)(evt: Event): Unit = {
    val input = evt.target.asInstanceOf[HTMLInputElement]
    if (input.files.length == 1) {
      val fr = new FileReader()
      fr.onload = fileReaderLoaded(doc)(_)
      fr.readAsDataURL(input.files(0))
    } else if (input.files.length < 1) {
      val f = onFailureOpt.get
      f("You selected no file")
    } else {
      val f = onFailureOpt.get
      f("You selected more than one file")
    }
  }

  def fileReaderLoaded(doc: html.Document)(evt: UIEvent): Unit = {
    val imgs = evt.target
      .asInstanceOf[FileReader]
      .result.asInstanceOf[String]
    onSuccessOpt.get(ImgBase64UrlJs(imgs)(doc))
  }

  // Call that method after the component is
  // registered in the dom tree
  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(id)
    elem.onchange = selectedFile(doc)(_)
  }

}

case class CompValueRangeInt(doc: html.Document, id: String,
                             min: Int, default: Int, max: Int
                            ) extends CompValue[Int] with CompResetable with CompJs with Bindable {

  require(min < max)
  require(default >= min)
  require(default <= max)

  private var _value = default

  override def value: Int = _value

  def valueChanged(a: Event): Unit = {
    _value = getHtmlElem(id).asInstanceOf[HTMLInputElement].value.toInt
  }

  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(id)
    elem.onchange = valueChanged
  }

  override def reset(): Unit = {
    getHtmlElem(id).asInstanceOf[HTMLInputElement].value = "" + default
    _value = default
  }
}

case class CompValueRangeDoubleLinear(doc: html.Document, id: String,
                                      min: Double, default: Double, max: Double,
                                     ) extends CompValue[Double] with CompResetable with CompJs with Bindable {

  private val prec = 1000

  private val conv = LinearConvert(min, max, prec)
  private var _value = default

  override def value: Double = _value

  def valueChanged(a: Event): Unit = {
    val handle: Int = getHtmlElem(id).asInstanceOf[HTMLInputElement].value.toInt
    _value = conv.value(handle)
  }

  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(id)
    elem.onchange = valueChanged
  }

  override def reset(): Unit = {
    getHtmlElem(id).asInstanceOf[HTMLInputElement].value = "" + conv.handle(default)
    _value = default
  }

}

case class CompValueRangeDoubleExp(doc: html.Document, id: String,
                                   default: Double,
                                  ) extends CompValue[Double] with CompResetable with CompJs with Bindable {
  private val prec = 1000

  private val conv = ExpConvert(prec)
  private var _value = default

  override def value: Double = _value

  def valueChanged(a: Event): Unit = {
    val handle: Int = getHtmlElem(id).asInstanceOf[HTMLInputElement].value.toInt
    _value = conv.value(handle)
  }

  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(id)
    elem.onchange = valueChanged
  }

  override def reset(): Unit = {
    getHtmlElem(id).asInstanceOf[HTMLInputElement].value = "" + conv.handle(default)
    _value = default
  }

}

case class CompJsTemplateImg(doc: html.Document, idImg: String, idAction: String)
  extends CompJsActionAbstract with CompTempl[ImgBase64Url] with CompJs {

  var _content: Option[ImgBase64Url] = None

  override def content(content: ImgBase64Url): Unit = {
    val img = getHtmlElem(idImg).asInstanceOf[HTMLImageElement]
    _content = Some(content)
    //noinspection ScalaDeprecation
    img.src = content.value
  }

  // Call that method after the component is
  // registered in the dom tree
  override def bind(implicit doc: html.Document): Unit = {
    val elem = getHtmlElem(idAction)
    elem.onclick = action
  }

  override def value: ImgBase64Url = _content.getOrElse(throw new IllegalStateException("No Image defined"))
}

case class CompJsSelectOrientation(doc: html.Document) extends CompJs with CompValue[DownloadOrientation] {

  override def value: DownloadOrientation = {
    val elem = getHtmlElem("downloadOrientation").asInstanceOf[HTMLSelectElement]
    val idx = elem.selectedIndex
    elem.options(idx).value match {
      case "l" => DO_Landscape
      case "p" => DO_Portrait
    }
  }

}

case class CompJsSelectSize(doc: html.Document) extends CompJs with CompValue[DownloadSize] {

  override def value: DownloadSize = {
    val elem = getHtmlElem("downloadSize").asInstanceOf[HTMLSelectElement]
    val idx = elem.selectedIndex
    elem.options(idx).value match {
      case "a5" => DS_A5
      case "a4" => DS_A4
      case "a3" => DS_A3
      case "a2" => DS_A2
    }
  }

}



case class CompJsContentImgBase64(doc: html.Document, id: String)
  extends CompContent[ImgBase64Url] with CompJs {

  override def content(content: ImgBase64Url): Unit = {
    val img = getHtmlElem(id).asInstanceOf[HTMLImageElement]
    //noinspection ScalaDeprecation
    img.src = content.value
  }

}

case class CompJsServiceCreate(urlPath: String) extends CompService[OnelineRequest, OnelineResponse] {

  override def call(param: OnelineRequest)
                   (
                     onStart: Unit => Unit,
                     onSuccess: OnelineResponse => Unit,
                     onFailure: String => Unit
                   ): Unit = {
    val body = write(param)
    Ajax.post(url = urlPath, data = body).onComplete {
      case Success(xhr) =>
        val resp = read[OnelineResponse](xhr.responseText)
        onSuccess(resp)
      case Failure(_) =>
        onFailure("Error calling the server. Check your connection")
    }
  }

}

case class CompJsServiceDown(urlPath: String, stateMachine: StateMachineJs) extends CompService[OnelineRequest, OnelineResponseDownlaod] {

  def download(url: String): Unit = {
    stateMachine.getHtmlElement("my_iframe").asInstanceOf[HTMLIFrameElement].src = url
  }


  override def call(param: OnelineRequest)
                   (
                     onStart: Unit => Unit,
                     onSuccess: OnelineResponseDownlaod => Unit,
                     onFailure: String => Unit
                   ): Unit = {
    val body = write(param)
    Ajax.post(url = urlPath, data = body).onComplete {
      case Success(xhr) =>
        val resp = read[OnelineResponseDownlaod](xhr.responseText)
        download(resp.url)
        onSuccess(resp)
      case Failure(_) =>
        onFailure("Error calling the server. Check your connection")
    }
  }

}

case class ImgBase64UrlJs(value: String)(implicit doc: html.Document) extends ImgBase64Url {

  import scala.concurrent._

  def onLoadFuture(img: HTMLImageElement): Future[ImgSize] = {
    val p = Promise[ImgSize]()
    img.onload = { _: Event =>
      //noinspection ScalaDeprecation
      p.success(ImgSize(width = img.naturalWidth, height = img.naturalHeight))
    }
    p.future
  }

  def size: Future[ImgSize] = {
    val img = doc.createElement("img").asInstanceOf[HTMLImageElement]
    val future = onLoadFuture(img)
    //noinspection ScalaDeprecation
    img.src = value
    future
  }


}
