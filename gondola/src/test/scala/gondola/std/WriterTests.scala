package gondola.std

import cats.Monoid
import org.scalatest.{Matchers, FunSuite}

trait StringVectorMonoid {
  implicit val writerMonoid: Monoid[Vector[String]] = new Monoid[Vector[String]] {
    def empty: Vector[String] = Vector.empty

    def combine(x: Vector[String], y: Vector[String]): Vector[String] = x ++ y
  }
}

object WriterImpl
  extends StringVectorMonoid
  with WriterMonads[Vector[String]]
  with WriterValidMonads[Vector[String], String]
  with WriterStateMonads[Vector[String], Int]

class WriterTests extends FunSuite with Matchers {

  import WriterImpl._

  test("Writer Monad") {
    type X[T] = Writer[Vector[String], T]
    val m = writerMonad
    ImplicitMonadTest.mapIntIsEven[X](m.pure(3)) should equal (m.pure(false))
    ImplicitMonadTest.flatMapValue[X](m.pure(5))(i => m.pure(i.toString)) should equal (Writer("5"))
    val (r, (v, w)) = ImplicitMonadTest.write[X, Vector[String]]()
    r should equal (Writer(v, w))
  }

  test("Writer Valid Monad") {
    type X[T] = WriterValid[Vector[String], String, T]
    val m = writerValidMonad
    ImplicitMonadTest.mapIntIsEven[X](m.pure(3)) should equal (m.pure(false))
    ImplicitMonadTest.flatMapValue[X](m.pure(5))(i => m.pure(i.toString)) should equal (m.pure("5"))
    ImplicitMonadTest.errorValue[X, String](m.pure(5), "Not Odd") should equal (m.pure(true))
    val (r, p) = ImplicitMonadTest.write[X, Vector[String]]()
    r should equal (m.writer(p))
  }

  test("Writer State Monad") {
    type X[T] = WriterState[Vector[String], Int, T]
    val m = writerStateMonad
    ImplicitMonadTest.mapIntIsEven[X](m.pure(3))
      .run.run(0).value should equal (0 -> (Vector.empty -> false))
    ImplicitMonadTest.flatMapValue[X](m.pure(5))(i => m.pure(i.toString))
      .run.run(0).value should equal (0 -> (Vector.empty -> "5"))

    val (r, p) = ImplicitMonadTest.write[X, Vector[String]]()
    r.run.run(0).value should equal (0 -> p)

    val r2 = ImplicitMonadTest.state[X](3)
    r2.run.run(0).value should equal (3 -> (Vector.empty -> false))
  }

}

