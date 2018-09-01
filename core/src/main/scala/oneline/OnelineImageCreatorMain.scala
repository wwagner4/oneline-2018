package oneline

import java.nio.file.{Files, Paths}

object OnelineImageCreatorMain extends App with Loaneable {

  val props = new LineDrawerProperties with ExportProperties {
    override def lineLength: Int = 500
    override def exportLineWidth = 1

    override def exportWidth: Int = 500
    override def exportHeight: Int = 400
  }

  val inFile = Paths.get("core","src", "main", "resources", "oneline03.jpg")
  var outFile = Paths.get("core","target", "out_oneline.jpg")
  val c = new OnelineImageCreator
  val in = Files.newInputStream(inFile)
  val out = Files.newOutputStream(outFile)
  loan(in){
    _in => loan(out)(_out => c.create(_in, _out, props, props))
  }
  println(s"--- Created onleine-image at $outFile from $inFile")


}
