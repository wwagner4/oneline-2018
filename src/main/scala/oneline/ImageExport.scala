package oneline

import java.awt.image._
import java.awt.{BasicStroke, Color, Dimension, Graphics2D}
import java.nio.file.Path

import javax.imageio._

import scala.math._

trait ExportProperties {
  def exportWidth = 3000
  def exportHeight = 3000
  def exportFormat = "png"
  def exportLineWidth = 8.0

}

trait Exporter extends LinePainter {

  def export(img: OnelineImage, line: List[Position], props: ExportProperties, outFile: Path) {
    val dim = new Dimension(props.exportWidth, props.exportHeight)
    val bim = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_BYTE_GRAY)
    val g = bim.getGraphics.asInstanceOf[Graphics2D]
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
    g.setStroke(new BasicStroke(props.exportLineWidth.asInstanceOf[Float]))
    paintLine(g, dim, img, line, background = false, Color.WHITE)
    ImageIO.write(bim, props.exportFormat, outFile.toFile)
  }

}


trait LinePainter {


  def paintLine(g: Graphics2D, size: Dimension, img: OnelineImage, line: List[Position], background: Boolean, offColor: Color) {
    val pw = size.width
    val ph = size.height
    val params = new LinePainterParams(pw, ph, img)
    g.setColor(offColor)
    g.fillRect(0, 0, pw, ph)
    g.setColor(Color.WHITE)
    g.fillRect(params.xOffset, params.yOffset, (img.width * params.scaleFactor).toInt, (img.height * params.scaleFactor).toInt)
    if (background) paintImg(g, params, img.pixels)
    g.setColor(Color.BLACK)
    paintLine(g, params, line, img)
  }

  private def paintImg(g: Graphics2D, params: LinePainterParams, pixels: List[OnelinePixel]) {
    pixels match {
      case Nil =>
      case pix :: restImage =>
        paintPix(g, params, pix)
        paintImg(g, params, restImage)
    }
  }

  private def paintPix(g: Graphics2D, params: LinePainterParams, pix: OnelinePixel) {
    val b = pix.brightness + ((1.0f - pix.brightness) * 0.7f)
    g.setColor(Color.getHSBColor(1.0f, 0.0f, b))
    val xOff = params.xOffset + (pix.x * params.scaleFactor).toInt
    val yOff = params.yOffset + (pix.y * params.scaleFactor).toInt
    g.fillRect(xOff, yOff, ceil(params.scaleFactor).toInt, ceil(params.scaleFactor).toInt)
  }

  private def paintLine(g: Graphics2D, params: LinePainterParams, line: List[Position], img: OnelineImage) {
    val xVals = lineToArray(params.scaleFactor, { pos: Position => (pos.x, pos.xOff, params.xOffset) }, line)
    val yVals = lineToArray(params.scaleFactor, { pos: Position => (pos.y, pos.yOff, params.yOffset) }, line)
    g.drawPolyline(xVals, yVals, line.size)
  }


  private def lineToArray(sf: Double, f: Position => (Int, Double, Int), line: List[Position]): Array[Int] = {
    val f1 = { pos: Position => {
      val (i, pixOff, off) = f(pos)
      off + round(i.toDouble * sf + pixOff * sf).toInt
    }
    }
    line.map(f1).toArray[Int]
  }

  class LinePainterParams(panelWidth: Int, panelHeight: Int, img: OnelineImage) {

    val scaleFactor: Double = scaleFactorFunc(panelWidth, panelHeight, img)
    val xOffset: Int = xoffFunc
    val yOffset: Int = yoffFunc

    private def xoffFunc = (max(panelWidth - img.width * scaleFactor, 0.0) / 2.0).toInt

    private def yoffFunc = (max(panelHeight - img.height * scaleFactor, 0.0) / 2.0).toInt

    private def scaleFactorFunc(panelWidth: Int, panelHeight: Int, img: OnelineImage): Double = {
      val imgRatio = img.width.toDouble / img.height.toDouble
      val panelRatio = panelWidth.toDouble / panelHeight.toDouble
      if (panelRatio < imgRatio) panelWidth.toDouble / img.width
      else panelHeight.toDouble / img.height
    }
  }

}
