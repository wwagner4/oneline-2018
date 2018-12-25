package oneline.client.state.logic

trait StateMachine {

  def toStateStart(error: Option[String]): Unit

  def toStateCreate(currentImage: ImgBase64Url, error: Option[String]): Unit

  def toStateCreated(currentImg: ImgBase64Url, onelineImg: ImgBase64Url, error: Option[String]): Unit
  
  def createImg(value: String): ImgBase64Url

}
