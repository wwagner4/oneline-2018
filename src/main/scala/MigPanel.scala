import javax.swing.JPanel
import net.miginfocom.swing.MigLayout

import scala.swing._

class MigPanel(
                val layoutConstraints: String = "",
                val columnConstraints: String = "",
                val rowConstraints: String = "") extends Panel with LayoutContainer {

  def add(comp: Component, constr: String): Unit = peer.add(comp.peer, constr)

  def add(comp: Component): Unit = add(comp, "")

  override lazy val peer: JPanel with SuperMixin = {
    val mig = new MigLayout(
      layoutConstraints,
      columnConstraints,
      rowConstraints
    )
    new javax.swing.JPanel(mig) with SuperMixin
  }

  type Constraints = String

  protected def constraintsFor(comp: Component): Constraints =
    peer.getLayout.asInstanceOf[MigLayout].getComponentConstraints(comp.peer).asInstanceOf[String]

  protected def areValid(constr: Constraints): (Boolean, String) = (true, "")

}
