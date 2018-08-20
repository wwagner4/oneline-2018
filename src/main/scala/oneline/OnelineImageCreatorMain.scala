package oneline

import java.io.File

object OnelineImageCreatorMain extends App {

  val inputFilePath = "src/main/resources/oneline03.jpg"
  var outFile = new File("target/out_oneline.jpg")

  val myProps = new DefaultLineDrawerProperties with ExportProperties {
    override def lineLength: Int = 3500
    override def exportLineWidth = 2
  }

  val img = OnelineImageCreator.createFileOnelineImageCreator(inputFilePath).createOnelineImg
  val line: List[Position] = LineDrawer.createDefaultLineDrawer(myProps).drawLine(img)
  new Exporter {}.export(img, line, myProps, outFile)

  println("created image at " + outFile.getAbsolutePath)
}
