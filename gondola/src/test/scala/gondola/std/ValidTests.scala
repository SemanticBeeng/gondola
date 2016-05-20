package gondola.std

import gondola.MonadError
import org.scalatest.{Matchers, FunSpec}

object ValidImpl extends ValidMonads[String]

class ValidTests extends FunSpec with Matchers {

  import ValidImpl._
  import MonadError._

  type X[T] = Valid[String, T]

  describe("Testing Valid Monad") {
    describe("Checking basic types") {
      val m = validMonad
      it("should return false when checking that int 3 with mapIntIsEven") {
        ImplicitMonadTest.mapIntIsEven[X](m.pure(3)) should equal(m.pure(false))
      }
      it("should return true when checking that int 6 with mapIntIsEven") {
        ImplicitMonadTest.mapIntIsEven[X](m.pure(6)) should equal(m.pure(true))
      }
      it("should return true when comparing an int with its own toString value") {
        ImplicitMonadTest.flatMapValue[X](m.pure(5))(i => m.pure(i.toString)) should equal(m.pure("5"))
      }
      it("should return false when comparing an int with a wrong toString result") {
        ImplicitMonadTest.flatMapValue[X](m.pure(5))(i => m.pure(i.toString)) should not equal(m.pure("5i"))
      }
      it("should return true when comparing an int with its own toString value") {
        ImplicitMonadTest.errorValue[X, String](m.pure(5), "Not Odd") should equal(m.pure(true))
      }
      //ImplicitMonadTest.errorValue[X, String](m.pure(4), "Not Odd") should equal (m.pure("Not Odd"))
    }
  }
}
