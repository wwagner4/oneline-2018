package oneline.client.state.js

case class LinearConvert(min: Double, max: Double, precision: Int) {

  require(min < max, s"min ($min) must be smaller max ($max)")

  def value(handle: Int): Double = {
    if (handle < 0) min
    else if (handle > precision) max
    else {
      val rel = handle.toDouble / precision
      val range = max - min
      min + (range * rel)
    }
  }

  def handle(value: Double): Int = {
    if (value <= min) 0
    else if (value >= max) precision
    else {
      val diff = max - min
      (((value - min) / diff) * precision).toInt
    }
  }

}
