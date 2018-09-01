package oneline.common

import upickle.default.{macroRW, ReadWriter => RW}

object OnelineResponse {
  implicit def rw: RW[OnelineResponse] = macroRW
}


case class OnelineResponse(
                         img: String, // The image base64 encoded. data:image/jpeg;base64,/9j/4ZOSRX...
                       )
