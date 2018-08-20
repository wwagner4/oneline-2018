import scala.swing._
import scala.swing.event._


trait Bindable extends Reactor {

  /**
    * Binds a TextComponent to a model property.
    * Further bind Methods for other components could be implemented.
    */
  protected def bind(comp: TextComponent, propertyName: String) {
    propValue(propertyName) match {
      case value: Int =>
        comp.text = value.toString
        listenTo(comp)
        reactions += {
          case EditDone(`comp`) => execute(errorDescription(comp.text, propertyName), _ => propValue(propertyName, parseInt(comp.text)))
        }
      case value: Long =>
        comp.text = value.toString
        listenTo(comp)
        reactions += {
          case EditDone(`comp`) => execute(errorDescription(comp.text, propertyName), _ => propValue(propertyName, parseLong(comp.text)))
        }
      case value: Double =>
        comp.text = formatDouble(value)
        listenTo(comp)
        reactions += {
          case EditDone(`comp`) => execute(errorDescription(comp.text, propertyName), _ => propValue(propertyName, parseDouble(comp.text)))
        }
      case value: String =>
        comp.text = value
        listenTo(comp)
        reactions += {
          case EditDone(`comp`) => execute(errorDescription(comp.text, propertyName), _ => propValue(propertyName, comp.text))
        }
      case value: Any =>
        throw new IllegalStateException("Unsupported property type '%s'" format value.asInstanceOf[AnyRef].getClass.getName)
    }
  }

  // You may define yourself how to handle validation faults
  protected def handleValidationError(msg: String)

  // Gets the model value using reflection
  private def propValue(name: String): Any = {
    getClass.getMethods.find(_.getName == name).get.invoke(this)
  }

  // Sets the model value using reflection
  private def propValue(name: String, value: Any): Unit = {
    getClass.getMethods.find(_.getName == name + "_$eq").get.invoke(this, value.asInstanceOf[AnyRef])
  }

  private class ValidationException(msg: String) extends Exception(msg)


  // Common error description for types of properties
  private def errorDescription(text: String, propertyDesc: String): String = {
    "Could not process input for property '%s'" format propertyDesc
  }

  // Common exception handling
  private def execute(errorDescription: String, func: Unit => Unit) {
    try {
      func()
    } catch {
      case e: Exception => handleValidationError("%s because: %s" format(errorDescription, e.getMessage))
    }
  }

  // Various conversion functions

  private def parseInt(text: String): Int = {
    try {
      text.toInt
    } catch {
      case _: NumberFormatException =>
        throw new ValidationException("'%s' is not a valid integer" format text)
    }
  }

  private def parseLong(text: String): Long = {
    try {
      text.toLong
    } catch {
      case _: NumberFormatException =>
        throw new ValidationException("'%s' is not a valid integer" format text)
    }
  }

  private def parseDouble(text: String): Double = {
    try {
      val df = new java.text.DecimalFormat()
      df.setGroupingUsed(false)
      val pp = new java.text.ParsePosition(0)
      val trimmedText = text.trim
      val re = df.parse(trimmedText, pp).doubleValue
      if (pp.getIndex < trimmedText.length) throw new ValidationException("'%s' is not a valid decimal number" format text)
      re
    } catch {
      case _: Exception =>
        throw new ValidationException("'%s' is not a valid decimal number" format text)
    }
  }

  private def formatDouble(value: Double): String = {
    "%.5f" format value
  }
}


