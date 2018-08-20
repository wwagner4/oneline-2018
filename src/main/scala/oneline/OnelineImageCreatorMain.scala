package oneline

import java.nio.file.Paths


object OnelineImageCreatorMain extends App {

  val inFile = Paths.get("src", "main", "resources", "oneline03.jpg")
  var outFile = Paths.get("target", "out_oneline.jpg")

  val myProps = new DefaultLineDrawerProperties with ExportProperties {
    override def lineLength: Int = 3500
    override def exportLineWidth = 2
  }

  val img = OnelineImageCreator.createFileOnelineImageCreator(inFile).createOnelineImg
  val line: List[Position] = LineDrawer.createDefaultLineDrawer(myProps).drawLine(img)
  new Exporter {}.export(img, line, myProps, outFile)

  println(s"Created onleine-image at $outFile from $inFile")
}
