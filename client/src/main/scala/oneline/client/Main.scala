package oneline.client

import oneline.client.state.js.StateMachineJs
import org.scalajs.dom


object Main {

  def main(args: Array[String]): Unit = {
    val machine = StateMachineJs()(dom.document)
    machine.toStateStart(None)
  }

}
