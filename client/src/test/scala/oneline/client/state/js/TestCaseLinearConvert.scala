package oneline.client.state.js

import utest._

object TestCaseLinearConvert extends TestSuite {

  def round(d: Double): Double = (d * 10000).toInt / 10000.0

  val tests = Tests{

    'valueRange1 - {
      val c = LinearConvert(0.0, 100.0, 1000)

      'test1 - {
        assert(c.value(500) == 50)
      }
      'test2 - {
        assert(c.value(0) == 0)
      }
      'test3 - {
        assert(c.value(1000) == 100)
      }
      'test4 - {
        assert(c.value(221) == 22.1)
      }
      'test5 - {
        val is = round(c.value(229))
        assert(is == 22.9)
      }
    }
    'valueRange2 - {
      val c = LinearConvert(-1.0, 1.0, 1000)

      'test1 - {
        assert(c.value(500) == 0.0)
      }
      'test2 - {
        assert(c.value(0) == -1)
      }
      'test3 - {
        assert(c.value(1000) == 1.0)
      }
    }
    'handle1 - {
      val c = LinearConvert(-1.0, 1.0, 1000)

      'test1 - {
        val a = c.handle(0.0)
        assert(a == 500)
      }
      'test2 - {
        assert(c.handle(-1.0) == 0)
      }
      'test3 - {
        assert(c.handle(-2.0) == 0)
      }
      'test4 - {
        assert(c.handle(1.0) == 1000)
      }
      'test4 - {
        assert(c.handle(2.0) == 1000)
      }
    }
    'handle2 - {
      val c = LinearConvert(-50, 50, 1000)

      'test1 - {
        val a = c.handle(-10.0)
        assert(a == 400)
      }
      'test2 - {
        val a = c.handle(10.0)
        assert(a == 600)
      }
    }
  }



}


