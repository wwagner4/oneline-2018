package oneline

import java.io.File

object OnelineImageCreatorMain extends App {
  
  val myProps = new DefaultLineDrawerProperties {}

  private val creator: OnelineImageCreator = OnelineImageCreator.createFileOnelineImageCreator("src\\main\\resources\\oneline01.jpg")
  val img = creator.createOnelineImg
  
  val ld = LineDrawer.createDefaultLineDrawer(myProps)

  private val line: List[Position] = ld.drawLine(img)

  val myExporter = new Exporter {}


  var outFile = new File("target/out_oneline.jpg")

  val myExportProps = new ExportProperties {}

  myExporter.export(img, line, myExportProps, outFile)

  println("created image at " + outFile)
}
