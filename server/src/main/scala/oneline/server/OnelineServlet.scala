package oneline.server

import org.scalatra._

class OnelineServlet extends ScalatraServlet {

  get("/") {
    println("at the sever /")
    "<p>was at the servlet</p>"
  }


}
