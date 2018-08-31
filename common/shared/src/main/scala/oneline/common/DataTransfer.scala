package oneline.common

import upickle.default.{macroRW, ReadWriter => RW}

object DataTransfer {
  implicit def rw: RW[DataTransfer] = macroRW
}

case class DataTransfer(
                         img: String, // The image base64 encoded. data:image/jpeg;base64,/9j/4ZOSRX...
                         exportWidth: Int = 800,
                         exportHeight: Int = 640,
                         exportFormat: String = "png", // jpg, png
                         exportLineWidth: Double = 1.0,
                         lineLength: Int = 100,
                         seed: Long = -1L,
                         distFactor: Double = 1.0,
                         distTrimmer: Double = 1.0,
                         brightnessFactor: Double = 1.0,
                         touchesFactor: Double = 1.0,
                       )
