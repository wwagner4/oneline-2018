package oneline

import java.io.{InputStream, OutputStream}

class OnelineImageCreator extends Loaneable {

  def create(in: InputStream, out: OutputStream, lineDrawerProperties: LineDrawerProperties, exportProperties: ExportProperties): Unit = {
    val imgCreator = new RasterCreator()
    val lineDrawer = new LineDrawer(lineDrawerProperties)
    val exporter = new Exportor(exportProperties)

    val img = imgCreator.create(in)
    val line: List[Position] = lineDrawer.drawLine(img)
    exporter.export(img, line, out)

  }
}
