import scala.annotation._
import scala.math._
import scala.util.Random


trait LineDrawer {

  def drawLine(img: OnelineImage): List[Position]

}

trait DefaultLineDrawerProperties {
  var lineLength = 100
  var seed: Long = -1L
  var distFactor = 1.0
  var distTrimmer = 1.0
  var brightnessFactor = 1.0
  var touchesFactor = 1.0

  def appendNameValues(sb: StringBuilder, format: String) {
    sb.append(format.format("line length", lineLength.toString))
    sb.append("\n")
    sb.append(format.format("seed", seed.toString))
    sb.append("\n")
    sb.append(format.format("dist factor", distFactor.toString))
    sb.append("\n")
    sb.append(format.format("dist trimmer", distTrimmer.toString))
    sb.append("\n")
    sb.append(format.format("brightness factor", brightnessFactor.toString))
    sb.append("\n")
    sb.append(format.format("touches factor", touchesFactor.toString))
    sb.append("\n")
  }
}

object LineDrawer {

  def createDefaultLineDrawer(props: DefaultLineDrawerProperties): LineDrawer = {
    new DefaultLineDrawer(props)
  }

}

private class DefaultLineDrawer(props: DefaultLineDrawerProperties) extends LineDrawer with CachedHillFunction {

  private val ran = initRandom

  protected def hillOffset = 1.0

  protected def hillVariance = 1.0

  def initRandom: Random = {
    if (props.seed <= 0) new Random
    else new Random(props.seed)
  }

  def drawLine(img: OnelineImage): List[Position] = {
    img.reset()
    val re = drawLine(List(), img)
    re
  }

  private def drawLine(line: List[Position], img: OnelineImage): List[Position] = {
    if (line.size > props.lineLength) line.reverse
    else line match {
      case Nil => drawLine(createStartPosition(img) :: line, img)
      case prev :: _ => drawLine(createPosition(prev, img) :: line, img)
    }
  }

  protected def createStartPosition(img: OnelineImage): Position = {
    val ranX = ran.nextInt(img.width)
    val ranY = ran.nextInt(img.height)
    new Position(ranX, ranY, ran.nextDouble, ran.nextDouble)
  }

  protected def createPosition(prev: Position, img: OnelineImage): Position = {
    img.pixels match {
      case Nil => throw new IllegalStateException("Image must contain at least one pixel")
      case pix :: rest =>
        val opt = findOptimalPixel(pix, rest, prev, img.maxDist)
        opt.incTouches()
        new Position(opt.x, opt.y, ran.nextDouble, ran.nextDouble)
    }
  }

  @tailrec
  private def findOptimalPixel(optimal: OnelinePixel, img: List[OnelinePixel], prev: Position, maxDist: Double): OnelinePixel = {
    img match {
      case Nil => optimal
      case pix :: rest => findOptimalPixel(better(optimal, pix, prev, maxDist), rest, prev, maxDist)
    }
  }

  protected def better(p1: OnelinePixel, p2: OnelinePixel, prev: Position, maxDist: Double): OnelinePixel = {
    val v1 = measure(p1, prev, maxDist)
    val v2 = measure(p2, prev, maxDist)
    if (v1 > v2) p1
    else p2
  }

  protected def measure(pix: OnelinePixel, prev: Position, maxDist: Double): Double = {
    val d = dist(pix, prev, maxDist)
    val b = 1.0 - pix.brightness
    val t = 1.0 / (pix.touches + 1)
    val re = (d * props.distFactor) + (b * props.brightnessFactor) + (t * props.touchesFactor)
    re
  }

  protected def dist(pix: OnelinePixel, pos: Position, maxDist: Double): Double = {
    val dx = pix.x - pos.x
    val dy = pix.y - pos.y
    val d = sqrt(dx * dx + dy * dy)
    val d2 = d * 2.5 * (10.0 / props.distTrimmer) / maxDist
    val re = hill(d2)
    re
  }
}

  

