package oneline

import scala.collection.mutable
import scala.math._
import scala.util.{Failure, Success, Try}

class OnelineRaster(val pixels: List[OnelinePixel], val width: Int, val height: Int) {

  def reset(): Unit = {
    pixels foreach { p => p.reset() }
  }

  def maxDist: Double = sqrt(pow(width, 2) + pow(height, 2))

  override def toString: String = "Img[%d,%d|%s]" format(width, height, pixels)

}

class OnelinePixel(val x: Int, val y: Int, val brightness: Float) {

  var touches = 0

  def incTouches(): Unit = touches = touches + 1

  def reset(): Unit = touches = 0

  override
  def toString: String = "Pix[%d,%d|%.2f|%d]" format(x, y, brightness, touches)

}

class Position(val x: Int, val y: Int, val xOff: Double, val yOff: Double) {

  override def toString: String = "Pos[%d %d|%.3f %.3f]".format(x, y, xOff, yOff)

}

trait HillFunction {

  // Offset and variance must immutable
  private val offset = hillOffset
  private val variance = hillVariance

  def hill(x: Double): Double = {
    val d = x - offset
    pow(E, -(d * d) / variance)
  }

  protected def hillOffset: Double

  protected def hillVariance: Double
}

trait CachedHillFunction extends HillFunction with Cache {

  override def hill(x: Double): Double = {
    cachedValue(x)
  }

  protected def cachedFunction: Double => Double = { x => super.hill(x) }
}

trait Cache {

  private val cache = mutable.Map.empty[Int, Double]

  protected def cachedFunction: Double => Double

  protected def cachedValue(in: Double): Double = {
    val key = (in * 10).asInstanceOf[Int]
    val re = cache.get(key)
    re match {
      case None =>
        val y = cachedFunction(in)
        cache.put(key, y)
        y
      case Some(y) => y
    }
  }

}

trait Loaneable {

  def loan[A <: AutoCloseable, B](resource: A)(block: A => B): B = {
    Try(block(resource)) match {
      case Success(result) =>
        resource.close()
        result
      case Failure(e) =>
        resource.close()
        throw e
    }
  }

}


