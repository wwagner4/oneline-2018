import org.scalatest.{FunSpec, MustMatchers}

class HillFunctionSpec extends FunSpec with MustMatchers {

  def dl(from: Double, to: Double, step: Double): Seq[Double] = {
    (BigDecimal.valueOf(from) to to by step).map(_.toDouble)
  }

  describe("A central HillFunction") {
    val f1 = new TestHillFunction(0.0, 1.0)
    val f2 = new TestHillFunction(0.0, 2.0)
    val f3 = new TestHillFunction(0.0, 3.0)
    it("should have 1 at 0") {
      f1.hill(0.0) mustBe 1
      f2.hill(0.0) mustBe 1
      f3.hill(0.0) mustBe 1
    }
    it("values at 1") {
      assert(f1.hill(1.0) < 0.5)
      assert(f2.hill(1.0) > 0.5)
      assert(f3.hill(1.0) > 0.5)
    }
    it("values at 2") {
      assert(f1.hill(2.0) < 0.1)
      assert(f2.hill(2.0) > 0.1)
      assert(f3.hill(2.0) > 0.1)
    }
  }

  describe("A displaced HillFunction") {
    val f1 = new TestHillFunction(5.0, 1.0)
    val f2 = new TestHillFunction(5.0, 2.0)
    val f3 = new TestHillFunction(5.0, 3.0)
    it("should have 5 at 0") {
      f1.hill(5.0) mustBe 1
      f2.hill(5.0) mustBe 1
      f3.hill(5.0) mustBe 1
    }
    it("values at 6") {
      assert(f1.hill(6.0) < 0.5)
      assert(f2.hill(6.0) > 0.5)
      assert(f3.hill(6.0) > 0.5)
    }
    it("values at 7") {
      assert(f1.hill(7.0) < 0.1)
      assert(f2.hill(7.0) > 0.1)
      assert(f3.hill(7.0) > 0.1)
    }
    it("values at 3") {
      assert(f1.hill(3.0) < 0.1)
      assert(f2.hill(3.0) > 0.1)
      assert(f3.hill(3.0) > 0.1)
    }
  }

  class TestHillFunction(offset: Double, variance: Double) extends CachedHillFunction {
    def hillOffset: Double = offset

    def hillVariance: Double = variance
  }

}