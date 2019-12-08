package com.stripe.rainier.core

import com.stripe.rainier.compute._
import com.stripe.rainier.sampler._
import com.stripe.rainier.optimizer._

case class Model(private[rainier] val targets: Set[Target]) {
  def merge(other: Model) = Model(targets ++ other.targets)

  def sample(sampler: Sampler,
             warmupIterations: Int,
             iterations: Int,
             keepEvery: Int = 1,
             nChains: Int = 1)(implicit rng: RNG): Sample = {
    val chains = 1.to(nChains).toList.map { _ =>
      sampler.sample(density(), warmupIterations, iterations, keepEvery)
    }
    Sample(chains, this)
  }

  def writeGraph(path: String, gradient: Boolean = false): Unit = {
    val gradVars = if (gradient) targetGroup.parameters else Nil
    val tuples = ("base", targetGroup.base, List.empty[Placeholder]) ::
      targetGroup.batched.zipWithIndex.map {
      case (b, i) =>
        (s"target$i", b.real, b.placeholders)
    }
    RealViz(tuples, gradVars).write(path)
  }

  def writeIRGraph(path: String,
                   gradient: Boolean = false,
                   methodSizeLimit: Option[Int] = None): Unit = {
    val tuples =
      (("base", targetGroup.base) ::
        targetGroup.batched.zipWithIndex.map {
        case (b, i) => (s"target$i" -> b.real)
      })

    RealViz
      .ir(tuples, targetGroup.parameters, gradient, methodSizeLimit)
      .write(path)
  }

  def optimize(): Estimate =
    Estimate(Optimizer.lbfgs(density()), this)

  lazy val targetGroup = TargetGroup(targets)
  lazy val dataFn =
    Compiler.default.compileTargets(targetGroup, true)

  private[rainier] def parameters: List[Parameter] = targetGroup.parameters
  private[rainier] def density(): DensityFunction =
    new DensityFunction {
      val nVars = targetGroup.parameters.size
      val inputs = new Array[Double](dataFn.numInputs)
      val globals = new Array[Double](dataFn.numGlobals)
      val outputs = new Array[Double](dataFn.numOutputs)
      def update(vars: Array[Double]): Unit = {
        System.arraycopy(vars, 0, inputs, 0, nVars)
        dataFn(inputs, globals, outputs)
      }
      def density = outputs(0)
      def gradient(index: Int) = outputs(index + 1)
    }
}

object Model {
  def observe[Y](ys: Seq[Y], dist: Distribution[Y]): Model = {
    val likelihood = dist.logDensity(ys)
    Model(Set(new Target(likelihood)))
  }

  def observe[X, Y](xs: Seq[X], ys: Seq[Y])(fn: X => Distribution[Y]): Model = {
    val likelihoods = (xs.zip(ys)).map {
      case (x, y) => fn(x).logDensity(y)
    }

    Model(likelihoods.map(new Target(_)).toSet)
  }

  def observe[X, Y](xs: Seq[X],
                    ys: Seq[Y],
                    fn: Fn[X, Distribution[Y]]): Model = {
    val likelihood = dist.logDensity(fn.encode(xs))
    Model(Set(new Target(likelihood)))
  }
}
