package oneline.client.state.logic

/**
  * Action component. E.g. a button
  */
trait CompAction {

  def action(onAction: Unit => Unit): Unit

}

/**
  * External call
  *
  * @tparam P Parameter type
  * @tparam R Return Value
  */
trait CompService[P, R] {

  def call(param: P)(
    onStart: Unit => Unit = () => _,
    onSuccess: R => Unit,
    onFailure: String => Unit
  ): Unit

}

trait CompActionSelectImage {

  def action(
              onStart: Unit => Unit = () => _,
              onSuccess: ImgBase64Url => Unit,
              onFailure: String => Unit
            ): Unit
}

trait CompContent[C] {

  def content(content: C): Unit

}

trait CompValue[V] {

  def value: V

}

trait CompResetable {

  def reset(): Unit

}

trait CompTempl[T] extends CompContent[T] with CompValue[T] with CompAction

