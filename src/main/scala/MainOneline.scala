import java.awt.Color
import java.io.File
import java.net.URL

import org.apache.commons.io.FileUtils

import scala.swing._

private trait Properties extends DefaultLineDrawerProperties with ExportProperties {
  lineLength = 500
  brightnessFactor = 1.0
  distFactor = 1.0
  distTrimmer = 1.0
  touchesFactor = 1.0
  seed = -1
}

object MainOneline extends SimpleSwingApplication {

  def top: MainFrame with Exporter = new MainFrame with Exporter {

    // Needed to reference the main frame from inner classes
    selfMainFrame =>

    // Set look and feel
    try {
      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")
    } catch {
      case _: Throwable => println("== Faild loading NimbusLookAndFeel")
    }

    // Set a logo
    val logoUrlName = "logo.png"
    val logoUrl: URL = getClass.getClassLoader.getResource(logoUrlName)
    if (logoUrl != null) {
      val icon = new javax.swing.ImageIcon(logoUrl)
      iconImage = icon.getImage
    } else {
      println("== Found no logo url '%s'" format logoUrlName)
    }

    // Other configurations of the main frame
    title = "Oneline"
    preferredSize = new Dimension(900, 600)


    // Some initialisations
    var drawBackground = false
    val fileChooser = new FileChooser
    val defaultPathes = new ResourcesAsFiles(fileChooser.peer.getCurrentDirectory, "oneline", List("oneline01.jpg", "oneline02.jpg", "oneline03.jpg"))

    // Init the config dialog
    val props: ConfigDialog with Properties with Bindable = new ConfigDialog(this) with Properties with Bindable {

      // Bind the input fields of the config frame to the properties of Properties
      // Details see Bindable
      bind(lineLenghtTF, "lineLength")
      bind(brightnessFactorTF, "brightnessFactor")
      bind(distFactorTF, "distFactor")
      bind(distTrimmerTF, "distTrimmer")
      bind(touchesFactorTF, "touchesFactor")
      bind(seedTF, "seed")
      bind(exportWidthTF, "exportWidth")
      bind(exportHeightTF, "exportHeight")
      bind(exportLineWidthTF, "exportLineWidth")
      bind(exportFormatTF, "exportFormat")
      bind(exportOutDirTF, "exportOutDir")
      bind(exportFileNameTF, "exportFileName")

      // Do some handling for validation faults that might happen
      def handleValidationError(msg: String) {
        Dialog.showMessage(contents(0), msg)
      }
    }

    // Create a dialog variable to make the code more readable
    val dialog: ConfigDialog with Properties with Bindable = props

    // Load an initial image and draw a line to ensure there is something on the screen if the frame opens the first time
    var img: OnelineImage = OnelineImageCreator.createFileOnelineImageCreator(defaultPathes.filePathes(0)).createOnelineImg
    var line: List[Position] = LineDrawer.createDefaultLineDrawer(props).drawLine(img)

    // Init the draw panel where you can preview your oneline-image
    val drawPanel: Panel with LinePainter = new Panel() with LinePainter {
      override def paint(g: Graphics2D) {
        paintLine(g, size, img, line, drawBackground, new Color(0.95f, 0.95f, 0.95f))
      }
    }

    // Init all buttons
    val drawButton = new Button
    val loadButton = new Button
    val backgroundButton = new ToggleButton
    val exportButton = new Button
    val configButton = new Button

    /**
      * Superclass for all Actions in the MainFrame
      * Prepared for mixin Executors in subclasses and
      * provides exception handling (using ExceptionHandlingExecutor)
      * for all subclasses
      */
    private abstract class BaseAction(name: String) extends Action(name)
      with ExceptionHandlingExecuter {
      // Implementations for the ExceptionHandlingExecutor
      def handleActionExecutionException(msg: String) {
        Dialog.showMessage(contents(0), msg)
      }

      def actionDescription: String = {
        name
      }

      // Preparation for mixin executors
      def apply {
        // Calling executeAction ensures that all the Executors that
        // will be mixed in in subclasses are automatically applied
        executeAction()
      }

      // The action method that must be defined for each 'real' action
    }

    // All actions
    // Pay attention to the mixed in Executors that control the
    // behaviour of the actions

    drawButton.action = new BaseAction("Draw") with DisableComponentsExecuter with BackgroundExecuter {
      def executable: Unit => Unit = {
        _ => {
          println("=== Drawing ===")
          line = LineDrawer.createDefaultLineDrawer(props).drawLine(img)
          drawPanel.repaint
        }
      }

      def toBeDisabledComponents: Iterable[Component] = {
        List(loadButton, drawButton)
      }
    }

    loadButton.action = new BaseAction("Load") with DisableComponentsExecuter with BackgroundExecuter {
      def executable: Unit => Unit = {
        _ => {
          val re = fileChooser.showOpenDialog(drawPanel)
          if (re == FileChooser.Result.Approve) {
            println("=== You selected: " + fileChooser.selectedFile)
            img = OnelineImageCreator.createFileOnelineImageCreator(fileChooser.selectedFile.getAbsolutePath).createOnelineImg
            line = LineDrawer.createDefaultLineDrawer(props).drawLine(img)
            drawPanel.repaint
          } else {
            println("=== Canceled")
          }
        }
      }

      def toBeDisabledComponents: Iterable[Component] = {
        List(loadButton, drawButton)
      }
    }

    backgroundButton.action = new BaseAction("Background") {
      def executable: Unit => Unit = {
        _ => {
          drawBackground = backgroundButton.selected
          drawPanel.repaint
        }
      }
    }

    configButton.action = new BaseAction("Config") {
      def executable: Unit => Unit = {
        _ => {
          dialog.pack
          dialog.resizable = false
          val mainLoc = selfMainFrame.location
          val x = scala.math.max(0, mainLoc.x - dialog.size.width)
          dialog.location = new java.awt.Point(x, mainLoc.y)
          dialog.open
        }
      }
    }

    exportButton.action = new BaseAction("Export") with DisableComponentExecuter with ExceptionHandlingExecuter with BackgroundExecuter {
      def toBeDisabledComponent: Component = {
        exportButton
      }

      def executable: Unit => Unit = {
        _ => {
          import Dialog._
          val outDir = new File(props.exportOutDir)
          if (!outDir.exists) outDir.mkdirs
          val outFile = new File(outDir, "%s.%s" format(props.exportFileName, props.exportFormat))
          if (outFile.exists) {
            val options = List("Yes", "No")
            Dialog.showOptions(contents(0),
              "File '%s' exists. Do you want to overwrite it ?" format outFile.toString,
              "File exists",
              Options.YesNo, Message.Question,
              Swing.EmptyIcon, options, 1) match {
              case Result.Yes =>
                export(img, line, props, outFile)
                Dialog.showMessage(contents(0), "Exported to '%s'" format outFile)
              case _ => println("== Not exported")
            }
          } else {
            export(img, line, props, outFile)
            Dialog.showMessage(contents(0), "Exported to '%s'" format outFile)
          }
        }
      }
    }

    // Finally layout all the components

    val buttonsPanel = new FlowPanel(FlowPanel.Alignment.Left)(loadButton, drawButton, configButton, backgroundButton, exportButton)
    contents = new BorderPanel() {
      add(buttonsPanel, BorderPanel.Position.North)
      add(drawPanel, BorderPanel.Position.Center)
    }
    centerOnScreen
  }
}

private class ConfigDialog(owner: Window) extends Dialog {

  val lineLenghtTF = new TextField()
  val brightnessFactorTF = new TextField()
  val distFactorTF = new TextField()
  val distTrimmerTF = new TextField()
  val touchesFactorTF = new TextField()
  val seedTF = new TextField()
  val exportWidthTF = new TextField()
  val exportHeightTF = new TextField()
  val exportLineWidthTF = new TextField()
  val exportFormatTF = new TextField()
  val exportOutDirTF = new TextField()
  val exportFileNameTF = new TextField()

  title = "Oneline Config"
  contents = new MigPanel("", "[100][200]", "") {

    add(new Label("line length"))
    add(lineLenghtTF, "w max, grow, wrap")

    add(new Label("brightness factor"))
    add(brightnessFactorTF, "w max, grow, wrap")

    add(new Label("distance factor"))
    add(distFactorTF, "w max, grow, wrap")

    add(new Label("distance trimmer"))
    add(distTrimmerTF, "w max, grow, wrap")

    add(new Label("touches factor"))
    add(touchesFactorTF, "w max, grow, wrap")

    add(new Label("seed"))
    add(seedTF, "w max, grow, wrap")

    add(new Separator(Orientation.Horizontal), "span 2, grow, wrap")

    add(new Label("export width"))
    add(exportWidthTF, "w max, grow, wrap")

    add(new Label("export height"))
    add(exportHeightTF, "w max, grow, wrap")

    add(new Label("export line width"))
    add(exportLineWidthTF, "w max, grow, wrap")

    add(new Label("export format"))
    add(exportFormatTF, "w max, grow, wrap")

    add(new Label("export dir"))
    add(exportOutDirTF, "w max, grow, wrap")

    add(new Label("export file name"))
    add(exportFileNameTF, "w max, grow, wrap")

  }

}

private class ResourcesAsFiles(val baseDir: File, val workDir: String, val resourceNames: List[String]) {

  def filePathes: List[String] = {
    if (!existTestfiles) createTestFiles()
    val files = testDir.listFiles.filter(file => file.isFile)
    files.map(file => file.getAbsolutePath).toList
  }

  private def existTestfiles: Boolean = {
    testDir.exists && resourceNames.forall(resName => existsFile(testDir, resName))
  }

  private def testDir: File = {
    new File(baseDir, workDir)
  }

  private def existsFile(testDir: File, fileName: String): Boolean = {
    new File(testDir, fileName).exists
  }

  private def createTestFiles(): Unit = {
    createTestDir()
    resourceNames.foreach(name => createFile(name, testDir))
  }

  private def createTestDir(): Unit = {
    if (!testDir.exists) testDir.mkdirs
  }

  private def createFile(name: String, outDir: File) {
    FileUtils.copyURLToFile(getClass.getClassLoader.getResource(name), new File(outDir, name))
  }
}

