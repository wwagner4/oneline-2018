import java.awt.Color
import java.awt.image.{BufferedImage, Raster}
import java.io.File

import javax.imageio.{IIOException, ImageIO}


trait OnelineImageCreator {
  def createOnelineImg: OnelineImage
}

object OnelineImageCreator {
  def createFileOnelineImageCreator(fileName: String): OnelineImageCreator = {
    new FileOnelineImageCreator(fileName)
  }

}

private class FileOnelineImageCreator(val fileName: String) extends OnelineImageCreator {

  def createOnelineImg: OnelineImage = {
    val bimg = readImageFile(fileName)
    if (bimg == null) throw new IllegalArgumentException("'" + fileName + "' contains no image")
    val w = bimg.getWidth
    val h = bimg.getHeight
    val pl = createPixelList(bimg.getData, w, h)
    new OnelineImage(pl, w, h)
  }

  private def createPixelList(rimg: Raster, width: Int, height: Int): List[OnelinePixel] = {
    val re = for (j <- 0 until height; i <- 0 until width) yield {
      val rgb = rimg.getPixel(i, j, new Array[Int](3))
      val hsb = Color.RGBtoHSB(rgb(0), rgb(1), rgb(2), new Array[Float](3))
      new OnelinePixel(i, j, hsb(2))
    }
    re.toList
  }

  private def readImageFile(fileName: String): BufferedImage = {
    try {
      ImageIO.read(new File(fileName))
    } catch {
      case ioe: IIOException => throw new IllegalStateException(
        "Error reading '" + fileName + "'. " + ioe.getMessage, ioe)
    }

  }


}
