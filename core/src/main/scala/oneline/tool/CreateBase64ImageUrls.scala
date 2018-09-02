package oneline.tool

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, Path, Paths}
import java.util.Base64

import oneline.Loaneable

import scala.collection.JavaConverters._

object CreateBase64ImageUrls extends App with Loaneable {

  val inputDir = Paths.get("/Users/wwagner4/prj/oneline/oneline-2018/core/src/main/resources")
  val urls = Files.list(inputDir)
    .iterator()
    .asScala
    .toSeq
    .map(createUrl)

  for(url <- urls) {
    println(url)
  }


  def createUrl(file: Path): String = {
    val fname = file.getFileName.toString
    val name = fname.substring(0, fname.length - 4)
    val format = fname.substring(fname.length - 3).toLowerCase
    val out = new ByteArrayOutputStream()
    loan(out)(_out => Files.copy(file, _out))
    val raw = new String(Base64.getEncoder.encode(out.toByteArray))
    val complete = s"data:image/$format;base64,$raw"
    val chunks = complete.grouped(120).map(str => s"      |$str")
    val chunkStr = chunks.mkString("\n")
    val triple = "\"\"\""
    s"""
   def $name: String = $triple
$chunkStr
      $triple.stripMargin.replaceAll("\\\\s", "")
    """
  }


}
