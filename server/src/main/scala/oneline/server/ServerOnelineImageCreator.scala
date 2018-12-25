package oneline.server

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.file.{Files, Path}
import java.util.Base64

import oneline._
import oneline.common.{OnelineRequest, OnelineResponse}

object ServerOnelineImageCreator extends Loaneable {

  def create(request: OnelineRequest): OnelineResponse = {
    val p2 = exportProperties(request)
    val ba = createByteArray(request)
    val img = new String(Base64.getEncoder.encode(ba))
    val imgType = p2.exportFormat.toLowerCase
    val prefix = s"data:image/$imgType;base64,"

    OnelineResponse(img = s"$prefix$img")
  }

  def createByteArray(request: OnelineRequest): Array[Byte] = {
    val p1 = lineDrawerProperties(request)
    val p2 = exportProperties(request)
    val base64Text = request.img.replaceFirst("^.*,", "")
    val bytes = Base64.getDecoder.decode(base64Text)
    val in = new ByteArrayInputStream(bytes)
    val out = new ByteArrayOutputStream(10000)
    loan(in){
      _in => loan(out){
        _out => new OnelineImageCreator().create(in = _in, out = _out, p1, p2)
      }
    }
    out.toByteArray
  }

  def createFile(request: OnelineRequest, outPath: Path): Unit = {
    val p1 = lineDrawerProperties(request)
    val p2 = exportProperties(request)
    val base64Text = request.img.replaceFirst("^.*,", "")
    val bytes = Base64.getDecoder.decode(base64Text)
    val in = new ByteArrayInputStream(bytes)
    val out = Files.newOutputStream(outPath)
    loan(in){
      _in => loan(out){
        _out => new OnelineImageCreator().create(in = _in, out = _out, p1, p2)
      }
    }
  }

  private def lineDrawerProperties(request: OnelineRequest) = {
    new LineDrawerProperties {
      override def lineLength: Int = request.lineLength

      override def seed: Long = request.seed

      override def distFactor: Double = request.distFactor

      override def distTrimmer: Double = request.distTrimmer

      override def brightnessFactor: Double = request.brightnessFactor

      override def touchesFactor: Double = request.touchesFactor

    }
  }

  private def exportProperties(request: OnelineRequest) = {
    new ExportProperties {
      override def exportWidth: Int = request.exportWidth

      override def exportHeight: Int = request.exportHeight

      override def exportFormat: String = request.exportFormat

      override def exportLineWidth: Double = request.exportLineWidth

    }
  }
}
