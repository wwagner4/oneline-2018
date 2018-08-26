import javax.servlet.ServletContext
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
    context.mount(new oneline.server.OnelineServlet, "/*")
  }

}
