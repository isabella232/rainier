package com.stripe.rainier.core

import com.stripe.rainier.compute._
import scala.collection.mutable.ArrayBuffer

trait Likelihood[T] {
  def real: Real
  def variables: List[Variable]
  def extract(t: T): List[Double]

  def fit(seq: Seq[T]): RandomVariable[Unit] = {
    val arrayBufs =
      variables.map { _ =>
        new ArrayBuffer[Double]
      }
    seq.foreach { t =>
      val doubles = extract(t)
      arrayBufs.zip(doubles).foreach {
        case (a, d) => a += d
      }
    }
    val placeholdersMap =
      variables.zip(arrayBufs.map(_.toArray)).toMap
    val target = new Target(real, placeholdersMap)
    new RandomVariable((), Set(target))
  }
}

trait LikelihoodMaker {
  type M[T, P]

  def maker[T, P](implicit ph: Placeholder[T, P]): M[T, P]
  def fromInt = maker[Int, Variable]
  def fromDouble = maker[Double, Variable]
  def fromIntPair = maker[(Int, Int), (Variable, Variable)]
  def fromDoublePair = maker[(Double, Double), (Variable, Variable)]
  def fromIntVector(size: Int) =
    maker[Seq[Int], Seq[Variable]](Placeholder.vector(size))
  def fromDoubleVector(size: Int) =
    maker[Seq[Double], Seq[Variable]](Placeholder.vector(size))
}

object Likelihood extends LikelihoodMaker {
  class Maker[T, P](ph: Placeholder[T, P]) {
    def apply(fn: P => Real): Likelihood[T] = {
      val p = ph.create()
      new Likelihood[T] {
        val real = fn(p)
        val variables = ph.variables(p, Nil)
        def extract(t: T) = ph.extract(t, Nil)
      }
    }
  }

  type M[T, P] = Maker[T, P]
  def maker[T, P](implicit ph: Placeholder[T, P]) =
    new Maker[T, P](ph)

  class Fitter[T, P](seq: Seq[T], ph: Placeholder[T, P]) {
    val m = maker(ph)
    def to(fn: P => Real): RandomVariable[Unit] =
      m(fn).fit(seq)
  }

  def fit[T, P](seq: Seq[T])(implicit ph: Placeholder[T, P]) =
    new Fitter(seq, ph)
}

trait ToLikelihood[L, T] {
  def apply(l: L): Likelihood[T]
}
