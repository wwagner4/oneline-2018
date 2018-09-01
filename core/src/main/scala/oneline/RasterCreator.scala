package oneline

import java.awt.Color
import java.awt.image.Raster
import java.io.InputStream

import javax.imageio.ImageIO


class RasterCreator {

  def create(inputStream: InputStream): OnelineRaster = {
    val bimg = ImageIO.read(inputStream)
    if (bimg == null) throw new IllegalArgumentException("Input stream contains no image")
    val w = bimg.getWidth
    val h = bimg.getHeight
    val pl = createList(bimg.getData, w, h)
    new OnelineRaster(pl, w, h)
  }

  private def createList(rimg: Raster, width: Int, height: Int): List[OnelinePixel] = {
    val re = for (j <- 0 until height; i <- 0 until width) yield {
      val rgb = rimg.getPixel(i, j, new Array[Int](3))
      val hsb = Color.RGBtoHSB(rgb(0), rgb(1), rgb(2), new Array[Float](3))
      new OnelinePixel(i, j, hsb(2))
    }
    re.toList
  }
}
