package oneline.server

import org.scalatra._

class OnelineServlet extends ScalatraServlet {

  get("/") {
    println("in the server")
    """<html>
      |<body>
      |<p>hallo</p>
      |</body>
      |</html>
    """.stripMargin
  }

}
