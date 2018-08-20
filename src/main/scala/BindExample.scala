import scala.swing._
import scala.swing.event._

/**
  * Model object that will be modified by the GUI
  * Must be a trait that it can be mixed into the panel
  */
trait Person {
  val firstName = "A"
  val lastName = "B"
  val age = 20
  val bodyMassIndex = 1.4

  override def toString: String = "Person['%s' '%s' %d %.4f]" format(firstName, lastName, age, bodyMassIndex)
}

/**
  * Contains all components but no logic. Responsable for layout
  */
trait PersonPanel extends MigPanel {

  // Instantiate the components
  val firstNameTextField = new TextField()
  val lastNameTextField = new TextField()
  val ageTextField = new TextField()
  val bodyMassIndexTextField = new TextField()
  val infoButton = new Button("Info")
  val resetButton = new Button("Reset")

  // Add the components to the panel
  add(new Label("First Name"))
  add(firstNameTextField, "w 150, wrap")

  add(new Label("Last Name"))
  add(lastNameTextField, "grow, wrap")

  add(new Label("Age"))
  add(ageTextField, "grow, wrap")

  add(new Label("Body Mass Index"))
  add(bodyMassIndexTextField, "grow, wrap")

  add(new FlowPanel(infoButton, resetButton), "span 2, grow")


}


object PersonController {

  // Export the model and the panel
  def panel: PersonPanel = boundPersonAndPanel

  def person: Person = boundPersonAndPanel

  // Create a bound person and panel object
  private val boundPersonAndPanel = new PersonPanel with Person with Bindable {

    // Bind the components to the properties of the model
    bind(firstNameTextField, "firstName")
    bind(lastNameTextField, "lastName")
    bind(ageTextField, "age")
    bind(bodyMassIndexTextField, "bodyMassIndex")

    // Do some handling for validation faults that might happen
    def handleValidationError(msg: String) {
      Dialog.showMessage(this, msg)
    }
  }

  boundPersonAndPanel.infoButton.action = Action("Info") {
    println("--- Info --- %s" format person.toString)
  }
  boundPersonAndPanel.resetButton.action = Action("Reset") {
    println("--- Reset ---")
    panel.firstNameTextField.text = "A"
    panel.lastNameTextField.text = "B"
    panel.ageTextField.text = "20"
    panel.bodyMassIndexTextField.text = "1,4"

    // Publish the new Values that they occur also in the model
    panel.firstNameTextField.publish(EditDone(panel.firstNameTextField))
    panel.lastNameTextField.publish(EditDone(panel.lastNameTextField))
    panel.ageTextField.publish(EditDone(panel.ageTextField))
    panel.bodyMassIndexTextField.publish(EditDone(panel.bodyMassIndexTextField))
  }

}

object Main extends SimpleSwingApplication {

  val controller: PersonController.type = PersonController

  def top: MainFrame = new MainFrame {
    title = "Bind Example"
    contents = controller.panel
    centerOnScreen
  }

}
      

    
