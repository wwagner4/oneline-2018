package oneline.client.state.js

case class ExpConvert(precision: Double) {
  
  val min = 0.1
  val n = 2.485
  val k = 0.0000002

  def value(handle: Int): Double = {
    if (handle < 0) min
    else {
      min + k * math.pow(handle, n)
    }
  }

  def handle(value: Double): Int = {
    if (value <= min) 0
    else {
      math.pow((value - min) / k, 1 / n).toInt
    }
  }

}
