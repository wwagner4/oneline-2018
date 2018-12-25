package oneline.client.state.logic

import oneline.common
import oneline.common._

import scala.concurrent._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

sealed trait DownloadSize {
  def width: Int

  def height: Int
}

case object DS_A2 extends DownloadSize {
  def width = 594

  def height = 420
}

case object DS_A3 extends DownloadSize {
  def width = 420

  def height = 297
}

case object DS_A4 extends DownloadSize {
  def width = 297

  def height = 210
}

case object DS_A5 extends DownloadSize {
  def width = 210

  def height = 148
}


sealed trait DownloadOrientation

case object DO_Landscape extends DownloadOrientation

case object DO_Portrait extends DownloadOrientation


trait State {

  def components: Components

  def stateMachine: StateMachine

  def init(): Unit

}

trait OnelineState {

  def components: Components

  def initVorl(stateMachine: StateMachine): Unit = {
    components.templA.action(onAction = _ => {
      stateMachine.toStateCreate(components.templA.value, None)
    })
    components.templB.action(onAction = _ => {
      stateMachine.toStateCreate(components.templB.value, None)
    })
    components.templC.action(onAction = _ => {
      stateMachine.toStateCreate(components.templC.value, None)
    })
  }

  def selectImage(stateMachine: StateMachine): Unit = {
    val maxKb = 10

    components.selectImg.action(
      onSuccess = img => {
        if (!img.value.contains("data:image/jpeg")) {
          val msg = s"only .jpg files are permitted"
          stateMachine.toStateStart(Some(msg))
        } else {
          img.size.foreach { size =>
            if (size.width * size.height > maxKb * 1000) {
              val msg = s"images with more than ${maxKb}k pixel are not permitted"
              stateMachine.toStateStart(Some(msg))
            }
            else {
              stateMachine.toStateCreate(img, None)
            }
          }
        }
      },
      onFailure = msg => {
        stateMachine.toStateStart(Some(msg))
      })

  }

  def createSmallImage(img: ImgBase64Url, stateMachine: StateMachine): Unit = {
    components.createButton.action(_ => {
      val req = common.OnelineRequest(
        img = img.value,
        lineLength = components.paraLineLength.value,
        distFactor = components.paraDistFactor.value,
        distTrimmer = components.paraDistTrimmer.value,
        brightnessFactor = components.paraBrightnessFactor.value,
        touchesFactor = components.paraTouchesFactor.value
      )
      components.createService.call(req)(
        onSuccess = response => stateMachine.toStateCreated(img, stateMachine.createImg(response.img), None),
        onFailure = msg => stateMachine.toStateCreate(img, Some(msg))
      )
    })
  }

  def initReset(): Unit = {
    components.resetButton.action { _ =>
      components.paraBrightnessFactor.reset()
      components.paraDistFactor.reset()
      components.paraDistTrimmer.reset()
      components.paraLineLength.reset()
      components.paraTouchesFactor.reset()
    }

  }

  def initDownload(imgTempl: ImgBase64Url, imgSmall: ImgBase64Url, stateMachine: StateMachine): Unit = {
    components.downloadButton.action(_ => {

      val ds = components.downloadSize.value
      val dor = components.downloadOrientation.value

      val dpi = 300
      val border = 0.95

      def pix(mm: Double): Int = (dpi * mm / 25.4).toInt

      def calcPortrait(size: DownloadSize): (Int, Int) = {
        val w = size.width * border
        val h = size.height * border
        (pix(h), pix(w))
      }

      def calcLandscape(size: DownloadSize): (Int, Int) = {
        val w = size.width * border
        val h = size.height * border
        (pix(w), pix(h))
      }

      val (wi, he) = dor match {
        case DO_Portrait => calcPortrait(ds)
        case DO_Landscape => calcLandscape(ds)
      }

      val fact = pix(ds.height) / 800

      val req = common.OnelineRequest(
        img = imgTempl.value,
        lineLength = components.paraLineLength.value,
        distFactor = components.paraDistFactor.value,
        distTrimmer = components.paraDistTrimmer.value,
        brightnessFactor = components.paraBrightnessFactor.value,
        touchesFactor = components.paraTouchesFactor.value,
        exportWidth = wi,
        exportHeight = he,
        exportLineWidth = fact
      )
      components.downService.call(req)(
        onSuccess = _ => stateMachine.toStateCreated(imgTempl, imgSmall, None),
        onFailure = msg => stateMachine.toStateCreated(imgTempl, imgSmall, Some(msg))
      )
    })
  }
}

trait Components {
  def templA: CompTempl[ImgBase64Url]

  def templB: CompTempl[ImgBase64Url]

  def templC: CompTempl[ImgBase64Url]

  def selectImg: CompActionSelectImage

  def img: CompContent[ImgBase64Url]

  def paraLineLength: CompValue[Int] with CompResetable

  def paraDistFactor: CompValue[Double] with CompResetable

  def paraDistTrimmer: CompValue[Double] with CompResetable

  def paraBrightnessFactor: CompValue[Double] with CompResetable

  def paraTouchesFactor: CompValue[Double] with CompResetable

  def createButton: CompAction

  def resetButton: CompAction

  def downloadButton: CompAction

  def createService: CompService[OnelineRequest, OnelineResponse]

  def downService: CompService[OnelineRequest, OnelineResponseDownlaod]

  def imgOneline: CompContent[ImgBase64Url]

  def downloadSize: CompValue[DownloadSize]

  def downloadOrientation: CompValue[DownloadOrientation]

}

case class StateStart(
                       components: Components,
                       stateMachine: StateMachine,
                     ) extends State with OnelineState {

  def init(): Unit = {
    initVorl(stateMachine)
    selectImage(stateMachine)
  }

}


case class StateCreate(
                        img: ImgBase64Url,
                        components: Components,
                        stateMachine: StateMachine,
                      ) extends State with OnelineState {

  def init(): Unit = {
    components.img.content(img)
    initVorl(stateMachine)
    initReset()
    selectImage(stateMachine)
    createSmallImage(img, stateMachine)
  }
}

case class StateCreated(
                         img: ImgBase64Url,
                         imgOneline: ImgBase64Url,
                         components: Components,
                         stateMachine: StateMachine,
                       ) extends State with OnelineState {

  def init(): Unit = {
    components.img.content(img)
    components.imgOneline.content(imgOneline)
    initReset()

    initVorl(stateMachine)
    initDownload(img, imgOneline, stateMachine)
    selectImage(stateMachine)
    createSmallImage(img, stateMachine)
  }
}

case class ImgSize(
                    width: Int,
                    height: Int)

trait ImgBase64Url {

  def value: String

  def size: Future[ImgSize]

}

