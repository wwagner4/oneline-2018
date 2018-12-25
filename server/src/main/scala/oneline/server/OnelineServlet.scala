package oneline.server

import java.nio.file.{Files, Path, Paths}
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import oneline.Loaneable
import oneline.common.{OnelineRequest, OnelineResponse, OnelineResponseDownlaod}
import org.scalatra._
import org.scalatra.util.io._


class OnelineServlet extends ScalatraServlet with Loaneable {

  get("/index") {
    readFile("page.html")
  }

  get("/res/:name") {
    readFile(params("name"))
  }

  get("/js/:name") {
    readFile(params("name"))
  }

  get("/tmp/:name") {
    val url = params("name")
    val ts: String = ZonedDateTime.now().format(DateTimeFormatter.ofPattern( "uuuu.MM.dd.HH.mm.ss" ))
    val headers = Map(
      "Content-Type" -> "image/png",
      "Content-Disposition" -> s"""attachment; filename=\"online_$ts.png\""""
    )
    val file = tmpDir.resolve(url)
    readFilePath(file, headers)
  }

  post("/trans") {
    import upickle.default._

    val req = read[OnelineRequest](request.body)
    val resp: OnelineResponse = ServerOnelineImageCreator.create(req)
    val body: String = write(resp)
    Ok(body)
  }

  post("/down") {
    import upickle.default._

    val req = read[OnelineRequest](request.body)
    val fname = downloadFileName
    val fpath = tmpDir.resolve(fname)
    ServerOnelineImageCreator.createFile(req, fpath)
    val resp = write(OnelineResponseDownlaod(s"/tmp/$fname"))
    Ok(resp)
  }

  private def tmpDir: Path = {
    val tmpDirStr = System.getProperty("java.io.tmpdir")
    Paths.get(tmpDirStr)
  }

  private def downloadFileName: String = {
    val id = UUID.randomUUID().toString
    s"oneline_$id.png"
  }

  private val paths = Seq(
    "res",
    "client/target/scala-2.12",
    "client/src/main/webapp",
    "client/src/main/webapp/res",
  )

  def readFile(fileNameStr: String): ActionResult = {
    val contentType = findContentType(fileNameStr)
    paths
      .map(ps => Paths.get(ps, fileNameStr))
      .find(p => Files.exists(p))
      .map { resFile =>
        loan(Files.newInputStream(resFile)) { in =>
          val headers = Map("Content-Type" -> contentType)
          Ok(readBytes(in), headers)
        }
      }.getOrElse {
      val absPaths = paths.map(ps => Paths.get(ps).toAbsolutePath).mkString(", ")
      println(s"Could not find resource $fileNameStr in any of the following paths $absPaths")
      NotFound()
    }
  }

  def readFilePath(resFile: Path, headers: Map[String, String]): ActionResult ={
    loan(Files.newInputStream(resFile)) { in =>
      Ok(readBytes(in), headers)
    }
  }

  def findContentType(name: String): String = {
    val lower = name.toLowerCase
    if (lower.endsWith("png")) "image/png"
    else if (lower.endsWith("jpg")) "image/jpeg"
    else if (lower.endsWith("jpeg")) "image/jpeg"
    else if (lower.endsWith("js")) "application/javascript"
    else if (lower.endsWith("map")) "application/javascript"
    else if (lower.endsWith("html")) "text/html"
    else if (lower.endsWith("htm")) "text/html"
    else if (lower.endsWith("css")) "text/css"
    else throw new IllegalStateException(s"Could not determine content type for $name")
  }

}
