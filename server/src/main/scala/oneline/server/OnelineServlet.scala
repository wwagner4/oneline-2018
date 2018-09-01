package oneline.server

import java.nio.file.{Files, Paths}
import java.util.Base64

import oneline.Loaneable
import oneline.common.OnelineRequest
import org.scalatra._
import org.scalatra.util.io._


class OnelineServlet extends ScalatraServlet with Loaneable {

  get("/") {
    println("in the server /")
    NotFound
  }

  get("/oneline-2018-client-fastopt.js") {
    val root = Paths.get(".")
        .resolve("oneline-2018-client-fastopt.js")
    println(s"reading from $root")
    loan(Files.newInputStream(root))(i => Ok(readBytes(i)))
  }

  get("/index") {
    val _h = Map(
      "Content-Type" -> "text/html",
      "charset" -> "utf-8",
    )
    println("in the server /index (1)")
    Ok("""<!DOCTYPE html>
      |<html>
      |<head>
      |    <meta charset="UTF-8">
      |    <title>oneline</title>
      |</head>
      |<body>
      |<script type="text/javascript" src="oneline-2018-client-fastopt.js"></script>
      |</body>
      |</html>
    """.stripMargin, headers = _h)
  }

  post("/trans") {
    println("_in the server /trans")
    import upickle.default._

    val data = read[OnelineRequest](request.body)
    val img = data.img
    println(s"data:'$data'")
    println(s"img:'$img'")
    //val base64Text = photoBase64.replaceFirst("^.*,", "")
    val bytes = Base64.getDecoder.decode(img)
    Ok()
  }

}
