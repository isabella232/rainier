package com.stripe.rainier.core

import com.stripe.rainier.compute._

trait Placeholder[T, U] {
  def create(): U
  def variables(u: U, acc: List[Variable]): List[Variable]
  def extract(t: T, acc: List[Double]): List[Double]
}

trait LowPriPlaceholders {
  implicit val int: Placeholder[Int, Variable] =
    new Placeholder[Int, Variable] {
      def create() = new Variable
      def variables(u: Variable, acc: List[Variable]) =
        u :: acc
      def extract(t: Int, acc: List[Double]) =
        t.toDouble :: acc
    }
}

object Placeholder extends LowPriPlaceholders {
  implicit val double: Placeholder[Double, Variable] =
    new Placeholder[Double, Variable] {
      def create() = new Variable
      def variables(u: Variable, acc: List[Variable]) =
        u :: acc
      def extract(t: Double, acc: List[Double]) =
        t :: acc
    }

  implicit def zip[A, B, X, Y](
      implicit a: Placeholder[A, X],
      b: Placeholder[B, Y]): Placeholder[(A, B), (X, Y)] =
    new Placeholder[(A, B), (X, Y)] {
      def create() = (a.create(), b.create())
      def variables(u: (X, Y), acc: List[Variable]) =
        a.variables(u._1, b.variables(u._2, acc))
      def extract(t: (A, B), acc: List[Double]) =
        a.extract(t._1, b.extract(t._2, acc))
    }

  def vector[T, U](size: Int)(
      implicit ph: Placeholder[T, U]): Placeholder[Seq[T], Seq[U]] =
    new Placeholder[Seq[T], Seq[U]] {
      def create() = List.fill(size)(ph.create())
      def variables(u: Seq[U], acc: List[Variable]) =
        u.foldLeft(acc) { case (a, v) => ph.variables(v, a) }
      def extract(t: Seq[T], acc: List[Double]) =
        t.foldLeft(acc) { case (a, x) => ph.extract(x, a) }
    }

  def map[K, V, U](keys: List[K])(
      implicit ph: Placeholder[V, U]): Placeholder[Map[K, V], List[(K, U)]] =
    ???

  def item[T](keys: List[T]): Placeholder[T, List[(T, Variable)]] = ???
}
/*


  implicit def likelihood[T] =
    new Likelihood[Multinomial[T], Map[T, Int]] {
      def apply(multi: Multinomial[T]) = {
        val choices = multi.pmf.keys.toList
        val u = choices.map { k =>
          k -> new Variable
        }
        val r = logDensity(multi, u)
        val ex = new Likelihood.Extractor[Map[T, Int]] {
          val variables = u.map(_._2)
          def extract(t: Map[T, Int]) =
            choices.map { k =>
              t.getOrElse(k, 0).toDouble
            }
        }
        (r, ex)
      }
    }


  implicit def likelihood[T] =
    new Likelihood[Categorical[T], T] {
      def apply(c: Categorical[T]) = {
        val choices = c.pmf.keys.toList
        val u = choices.map { k =>
          k -> new Variable
        }
        val r = c.logDensity(u)
        val ex = new Likelihood.Extractor[T] {
          val variables = u.map(_._2)
          def extract(t: T) =
            choices.map { k =>
              if (t == k)
                1.0
              else
                0.0
            }
        }
        (r, ex)
      }
    }
}
 */
