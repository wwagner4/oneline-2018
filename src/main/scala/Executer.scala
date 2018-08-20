import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.swing._

/**
  * Base class for all executors
  */
trait ActionExecuter {

  def executable: Unit => Unit

  def executeAction(): Unit = {
    executable
  }

}

/**
  * Handles any exception that might be thrown during action execution
  */
trait ExceptionHandlingExecuter extends ActionExecuter {

  override def executeAction(): Unit = {
    try {
      super.executeAction()
    } catch {
      case e: Exception =>
        val cause = exractMessage(e)
        handleActionExecutionException("Could not execute '%s' because %s" format(actionDescription, cause))
      case e: Throwable =>
        e.printStackTrace()
        val cause = exractMessage(e)
        handleActionExecutionException("FATAL: The application will exit. Reason: Could not execute '%s' because %s" format(actionDescription, cause))
        System.exit(1)
    }
  }

  private def exractMessage(e: Throwable): String = {
    e.getMessage match {
      case null => e.toString
      case "" => e.toString
      case _ => e.getMessage
    }
  }

  /**
    * Handle the exception according to the environment your action was executed in
    */
  def handleActionExecutionException(msg: String)

  /**
    * Define a description for your action that will occur in the error message
    */
  def actionDescription: String

}

/**
  * Runs the execution in a separate thread
  *
  */
trait BackgroundExecuter extends ActionExecuter {

  def exec(): Unit = {
    super.executeAction()
  }

  override def executeAction(): Unit = {
    Future {
      exec()
    }
  }
}

/**
  * Does something before and after the execution
  *
  */
trait BeforAfterExecuter extends ActionExecuter {

  def beforeExecuteAction()

  def afterExecuteAction()

  override def executeAction(): Unit = {
    beforeExecuteAction()
    try {
      super.executeAction()
    } finally {
      afterExecuteAction()
    }
  }
}

/**
  * Disables a component at the start of an execution and enables it if the execution
  * is finished
  */
trait DisableComponentExecuter extends BeforAfterExecuter {

  def toBeDisabledComponent: Component

  def beforeExecuteAction(): Unit = {
    toBeDisabledComponent.enabled = false
  }

  def afterExecuteAction(): Unit = {
    toBeDisabledComponent.enabled = true
  }

}

/**
  * Disables multiple components at the start of an execution and enables them if the execution
  * is finished
  */
trait DisableComponentsExecuter extends BeforAfterExecuter {

  def toBeDisabledComponents: Iterable[Component]

  def beforeExecuteAction(): Unit = {
    toBeDisabledComponents.foreach(c => c.enabled = false)
  }

  def afterExecuteAction(): Unit = {
    toBeDisabledComponents.foreach(c => c.enabled = true)
  }

}

trait ProgressIndicatingExecuter extends BeforAfterExecuter {

  def progressParentComponent: Window

  private def progressDialog = new Dialog(progressParentComponent)

  def beforeExecuteAction(): Unit = {
    progressDialog.open
  }

  def afterExecuteAction(): Unit = {
    progressDialog.close
  }

}


