package oneline

import java.nio.file.{Files, Paths}

import scala.util.{Failure, Success, Try}


object OnelineImageCreatorMain extends App {

  val inFile = Paths.get("src", "main", "resources", "oneline03.jpg")
  val props = new DefaultLineDrawerProperties with ExportProperties {
    override def lineLength: Int = 3500

    override def exportLineWidth = 2
  }
  val img = loan(Files.newInputStream(inFile))(in => OnelineImageCreator.createFileOnelineImageCreator(in).createOnelineImg)
  val line: List[Position] = LineDrawer.createDefaultLineDrawer(props).drawLine(img)
  var outFile = Paths.get("target", "out_oneline.jpg")
  loan(Files.newOutputStream(outFile))(out => new Exporter {}.export(img, line, props, out))

  println(s"Created onleine-image at $outFile from $inFile")

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
