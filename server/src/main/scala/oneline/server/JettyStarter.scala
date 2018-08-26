package oneline.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object JettyStarter extends App {
  val port = 8099

  val server = new Server(port)
  val context = new WebAppContext()
  context setContextPath "/"
  context.setResourceBase("src/main/webapp")
  context.addEventListener(new ScalatraListener)
  context.addServlet(classOf[oneline.server.OnelineServlet], "/")

  server.setHandler(context)

  server.start()
  server.join()


}
