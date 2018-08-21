package oneline

import java.nio.file.{Files, Paths}

object OnelineImageCreatorMain extends App with Loaneable {

  val props = new LineDrawerProperties with ExportProperties {
    override def lineLength: Int = 3500

    override def exportLineWidth = 2
  }

  val inFile = Paths.get("core","src", "main", "resources", "oneline03.jpg")
  var outFile = Paths.get("core","target", "out_oneline.jpg")

  val imgCreator = new ImageCreator()
  val lineDrawer = new LineDrawer(props)
  val exporter = new Exportor(props)

  val img = loan(Files.newInputStream(inFile))(in => imgCreator.createOnelineImg(in))
  val line: List[Position] = lineDrawer.drawLine(img)
  loan(Files.newOutputStream(outFile))(out => exporter.export(img, line, out))

  println(s"--- Created onleine-image at $outFile from $inFile")


}
