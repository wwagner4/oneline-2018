package oneline.client.state.js

import oneline.client.ImageUrls
import oneline.client.state.logic._
import oneline.common.{OnelineRequest, OnelineResponse, OnelineResponseDownlaod}
import org.scalajs.dom.html
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

case class StateMachineJs()(implicit doc: html.Document) extends StateMachine {

  private val _selImage = CompJsActionSelectImage(doc, "selImage", "selImageLabel")
  private val _tempA = CompJsTemplateImg(doc, "templAImg", "templAAction")
  private val _tempB = CompJsTemplateImg(doc, "templBImg", "templBAction")
  private val _tempC = CompJsTemplateImg(doc, "templCImg", "templCAction")
  private val _img = CompJsContentImgBase64(doc, "img")
  private val _imgOneline = CompJsContentImgBase64(doc, "imgOneline")
  private val _paraLineLength = CompValueRangeInt(doc, "lineLength", 100, 1000, 2000)
  private val _paraDistFactor = CompValueRangeDoubleExp(doc, "distFact", 1.0)
  private val _paraDistTrimmer = CompValueRangeDoubleExp(doc, "distTrimmer", 1.0)
  private val _paraTouchesFactor = CompValueRangeDoubleExp(doc, "touchesFact", 1.0)
  private val _paraBrightnessFactor = CompValueRangeDoubleExp(doc, "brightFact", 1.0)
  private val _createButton = CompJsActionButton(doc, "createButton", "create oneline image")
  private val _resetButton = CompJsActionButton(doc, "resetButton", "reset all parameters")
  private val _downloadButton = CompJsActionButton(doc, "downloadButton", "download a great image")
  private val _createService = CompJsServiceCreate("/trans")
  private val _downService = CompJsServiceDown("/down", this)
  private val _downSize = CompJsSelectSize(doc)
  private val _downOrientation = CompJsSelectOrientation(doc)

  getHtmlElement("closeModal").onclick = (_: MouseEvent) => hideElement("myModal")


  _selImage.bind
  _tempA.bind
  _tempB.bind
  _tempC.bind
  _paraLineLength.bind
  _paraDistFactor.bind
  _paraDistTrimmer.bind
  _paraTouchesFactor.bind
  _paraBrightnessFactor.bind
  _createButton.bind
  _resetButton.bind
  _downloadButton.bind

  _tempA.content(createImg(ImageUrls.peacedove))
  _tempB.content(createImg(ImageUrls.donquichotte))
  _tempC.content(createImg(ImageUrls.corrida))

  private val comps = new Components {

    override def templA: CompTempl[ImgBase64Url] = _tempA

    override def templB: CompTempl[ImgBase64Url] = _tempB

    override def templC: CompTempl[ImgBase64Url] = _tempC

    override def selectImg: CompActionSelectImage = _selImage

    override def img: CompContent[ImgBase64Url] = _img

    override def paraLineLength: CompValue[Int] with CompResetable = _paraLineLength

    override def paraDistFactor: CompValue[Double] with CompResetable = _paraDistFactor

    override def paraDistTrimmer: CompValue[Double] with CompResetable = _paraDistTrimmer

    override def paraBrightnessFactor: CompValue[Double] with CompResetable = _paraBrightnessFactor

    override def paraTouchesFactor: CompValue[Double] with CompResetable = _paraTouchesFactor

    override def createButton: CompAction = _createButton

    override def resetButton: CompAction = _resetButton

    override def createService: CompService[OnelineRequest, OnelineResponse] = _createService

    override def imgOneline: CompContent[ImgBase64Url] = _imgOneline

    override def downloadButton: CompAction = _downloadButton

    override def downService: CompService[OnelineRequest, OnelineResponseDownlaod] = _downService

    override def downloadSize: CompValue[DownloadSize] = _downSize

    override def downloadOrientation: CompValue[DownloadOrientation] = _downOrientation

  }

  override def toStateStart(error: Option[String]): Unit = {

    if (error.isDefined) {
      getHtmlElement("modalText").innerHTML = error.get
      showElement("myModal")
    } else {
      showElement("region01")
      hideElement("region02")
      hideElement("region03")
    }
    StateStart(comps, this).init()

  }

  override def toStateCreate(currentImage: ImgBase64Url, error: Option[String]): Unit = {

    if (error.isDefined) {
      getHtmlElement("modalText").innerHTML = error.get
      showElement("myModal")
      StateStart(comps, this).init()
    } else {
      showElement("region01")
      showElement("region02")
      hideElement("region03")
      StateCreate(currentImage, comps, this).init()
    }
  }

  override def toStateCreated(currentImg: ImgBase64Url, onelineImg: ImgBase64Url, error: Option[String]): Unit = {


    if (error.isDefined) {
      getHtmlElement("modalText").innerHTML = error.get
      showElement("myModal")
      StateCreate(currentImg, comps, this).init()
    } else {
      showElement("region01")
      showElement("region02")
      showElement("region03")
      StateCreated(currentImg, onelineImg, comps, this).init()
    }
  }

  def createImg(value: String): ImgBase64Url = {
    ImgBase64UrlJs(value)
  }

  def hideElement(id: String): Unit = {
    getHtmlElement(id).style.display = "none"
  }

  def showElement(id: String): Unit = {
    getHtmlElement(id).style.display = "block"
  }

  def getHtmlElement(id: String): HTMLElement = {
    doc.getElementById(id).asInstanceOf[HTMLElement]
  }

}
