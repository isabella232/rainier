package com.stripe.rainier.core

import com.stripe.rainier.compute._
import com.stripe.rainier.sampler._

trait SBCModel {
  implicit val rng: RNG = ScalaRNG(1528673302081L)
  def sbc: SBC[_, _]
  val sampler: Sampler = HMC(1)
  val warmupIterations: Int = 10000
  val syntheticSamples: Int = 1000
  val nSamples: Int = 10
  def main(args: Array[String]): Unit = {
    sbc.animate(sampler, warmupIterations, syntheticSamples)
    println(s"\nnew goldset:")
    println(s"$samples")
    println(
      s"If this run looks good, please update the goldset in your SBCModel")
  }
  val samples: List[_] = sbc.posteriorSamples(goldset.size)
  def goldset: List[_]
  val description: String
}

/** Continuous **/
object SBCUniformNormal extends SBCModel {
  def sbc = SBC[Double, Continuous](Uniform(0, 1))((x: Real) => Normal(x, 1))
  def goldset =
    List(0.3727866136497995, 0.3512064843955568, 0.3898772177836063,
      0.3451490654882922, 0.3993386518441082, 0.3993386518441082,
      0.3454234181712832, 0.3711984345137768, 0.3711984345137768,
      0.35213959278258555, 0.3817449054434727, 0.356441472575042,
      0.3893678111672744, 0.3556550880664861, 0.3893259891087903,
      0.3893259891087903, 0.35229047846780714, 0.39400891333519783,
      0.35124232828958774, 0.3931563759569231, 0.35929141009622123,
      0.3575281515245998, 0.3882946352168886, 0.35765308519750394,
      0.3855620520771014, 0.3565738874293182, 0.3894360643895346,
      0.3550622271795219, 0.39096206040062204, 0.3714875923013654)
  val description = "Normal(x, 1) with Uniform(0, 1) prior"
}

object SBCLogNormal extends SBCModel {
  def sbc =
    SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => LogNormal(x, x))
  def goldset =
    List(0.13164619366907931, 0.12832026951774805, 0.1299114429648742,
      0.12861425267943213, 0.12959152781296665, 0.12959152781296665,
      0.12861477795126522, 0.12965153828871087, 0.1287034531054879,
      0.13044563905326742, 0.1288763337879329, 0.1297848917698844,
      0.12841777066049834, 0.13148749502319232, 0.12738507545241284,
      0.1311668674827308, 0.12856396550939797, 0.12856396550939797,
      0.12964078905006984, 0.1292281367496527, 0.12970729139485995,
      0.12884730753588944, 0.12884730753588944, 0.1300383642753817,
      0.12937988133859285, 0.1294593496275026, 0.1294593496275026,
      0.13018953613738607, 0.12968487409509566, 0.12963426586770474)
  val description = "LogNormal(x, x) with LogNormal(0, 1) prior"
}

/**
  * Note: SBCExponential and SBCLaplace are made-up goldsets. SBC on these is wildly slow.
  */
object SBCExponential extends SBCModel {
  def sbc =
    SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => Exponential(x))
  def goldset =
    List(0.4265683630081846, 0.5189050953677488, 0.49924580068677044,
      0.3879796746979638, 0.4341114186909587, 0.4341114186909587,
      0.46249827359385365, 0.5153090873282923, 0.44657645973736837,
      0.4818619620463942, 0.43936322908013287, 0.4437800418959559,
      0.367162365055694, 0.367162365055694, 0.367162365055694,
      0.367162365055694, 0.367162365055694, 0.4330711704882621,
      0.4330711704882621, 0.5628095742189261, 0.45466790056406947)
  val description = "Exponential(x) with LogNormal(0, 1) prior"
}

object SBCLaplace extends SBCModel {
  def sbc = SBC[Double, Continuous](LogNormal(0, 1))((x: Real) => Laplace(x, x))
  def goldset =
    List(0.4265683630081846, 0.5189050953677488, 0.49924580068677044,
      0.3879796746979638, 0.4341114186909587, 0.4341114186909587,
      0.46249827359385365, 0.5153090873282923, 0.44657645973736837,
      0.4818619620463942, 0.43936322908013287, 0.4437800418959559,
      0.367162365055694, 0.367162365055694, 0.367162365055694,
      0.367162365055694, 0.367162365055694, 0.4330711704882621,
      0.4330711704882621, 0.5628095742189261, 0.45466790056406947)
  val description = "Laplace(x, x) with LogNormal(0, 1) prior"
}

/** Discrete **/
object SBCBernoulli extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Bernoulli(x))
  def goldset =
    List(0.37044599123027455, 0.36936591152297416, 0.3731277052189443,
      0.36904774987518996, 0.3733674901923237, 0.36868129171452735,
      0.372745736771814, 0.36966955758161735, 0.3721978765621232,
      0.36830046866023, 0.373612527630787, 0.3635720476789248,
      0.3790285250924025, 0.3632054248079346, 0.3788887381860848,
      0.3632099123283084, 0.3785864969990134, 0.36035921711182756,
      0.382407492535607, 0.3565679195290551, 0.3844642240530769,
      0.35721961709469136, 0.3833461468175246, 0.35930580939229645,
      0.38349598240708666, 0.35923791382512876, 0.3835282495637178,
      0.3589650665140116, 0.38126926884750195, 0.3609840446470116)
  val description = "Bernoulli(x) with Uniform(0, 1) prior"
}

object SBCBinomial extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Binomial(x, 10))
  def goldset =
    List(0.3820726445594915, 0.37873507902420117, 0.3820182686511396,
      0.3788359428176704, 0.38198265428011524, 0.37867026589452546,
      0.38220583323982193, 0.3786695828243192, 0.3817657322125167,
      0.37904762234478745, 0.38178651738060415, 0.378946778280673,
      0.38186967076247047, 0.37835897871728114, 0.38250763283931954,
      0.3783283111444465, 0.38254110247127276, 0.3780188543969237,
      0.38284353473104543, 0.37803782032310335, 0.3827972706584269,
      0.37765068954923714, 0.3829147015895818, 0.3775043890234473,
      0.3833115473747673, 0.377493615372221, 0.3833921572973716,
      0.3774566018684416, 0.3833967014440806, 0.3769321438730932)
  val description = "Binomial(x, 10) with Uniform(0, 1) prior"
}

object SBCGeometric extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => Geometric(x))
  def goldset =
    List(0.38450024173245057, 0.3947678731400176, 0.37981452817349803,
      0.3956473337365212, 0.3769393992295735, 0.3988570344890013,
      0.3988570344890013, 0.3764619865082788, 0.39354343454493573,
      0.39354343454493573, 0.3830545374385094, 0.39858839162329024,
      0.37469961465785967, 0.40089438674685196, 0.37601236878695576,
      0.3942977164722503, 0.3942977164722503, 0.3862396961854713,
      0.3869414874288193, 0.37875784243507066, 0.3996002043577692,
      0.37837854743411176, 0.4004927091422286, 0.37516327942434596,
      0.3975239984423935, 0.37755174196225405, 0.39477741390984367,
      0.3769359923032865, 0.3769359923032865, 0.3988284326102455)
  val description = "Geometric(x) with Uniform(0, 1) prior"
}

object SBCGeometricZeroInflated extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) =>
      Geometric(.3).zeroInflated(x))
  def goldset =
    List(0.3682741265983013, 0.3852529612682177, 0.3604238333388064,
      0.386596856795002, 0.3555033555350904, 0.39217253775032623,
      0.39217253775032623, 0.3550136947273093, 0.38182164324962603,
      0.38182164324962603, 0.36722421685006434, 0.3902898312064711,
      0.3531099114009195, 0.3942443064061138, 0.35565699293349323,
      0.38180128466000113, 0.38180128466000113, 0.37379355799257075,
      0.3692587358496806, 0.3588953235783851, 0.3931958168156864,
      0.3585549007172857, 0.39441682029588876, 0.353399981549561,
      0.38860439210841013, 0.35795264331182514, 0.38336146622540296,
      0.35691143054863744, 0.35691143054863744, 0.39069497264393727)
  val description = "Geometric(.3).zeroInflated(x) with Uniform(0, 1) prior"
}

object SBCNegativeBinomial extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 1))((x: Real) => NegativeBinomial(x, 10))
  def goldset = List(0.3786635431319488, 0.3883936536812426, 0.37830965265317323, 0.388718061996669, 0.37834050642635986, 0.3887492515188195, 0.37820159686813914, 0.3887410773022988, 0.37832755241059474, 0.3884654939105368, 0.3785777804223668, 0.3885016316656111, 0.3782970770432337, 0.3887333073366291, 0.37828266645099945, 0.38876647586139423, 0.37824504661285613, 0.38884231212230375, 0.37803906965726847, 0.3889191411005212, 0.3781367010742226, 0.38893862156446435, 0.37814977860366283, 0.38892039926646776, 0.37814866244875134, 0.3889235259061543, 0.3780613666645658, 0.3889884793301932, 0.3780591395955844, 0.3881920315462985)
  val description = "NegativeBinomial(x, 10) with Uniform(0, 1) prior"
}

object SBCBinomialPoissonApproximation extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0, 0.04))((x: Real) => Binomial(x, 200))
  def goldset =
    List(0.015340335981998274, 0.014851004851939782, 0.015344890002348927,
      0.014844878453869628, 0.015353294915503057, 0.014896078392751238,
      0.014896078392751238, 0.015307070425898817, 0.014888682324709612,
      0.015301870083783062, 0.014908396537794558, 0.015304965857890627,
      0.01488508260998481, 0.015305515748556233, 0.014900064787657128,
      0.01529013099035783, 0.014943520847029955, 0.015252679776129556,
      0.014940784586446517, 0.015275395561568789, 0.014917804841811584,
      0.01530404771977475, 0.014925441861312276, 0.015269116554445506,
      0.014922775324925162, 0.015268028594921407, 0.014923115139935668,
      0.015267923697036294, 0.014923844841593679, 0.015269119553668956)
  val description =
    "Poisson approximation to Binomial: Binomial(x, 200) with Uniform(0, 0.04) prior"
}

object SBCBinomialNormalApproximation extends SBCModel {
  def sbc =
    SBC[Int, Discrete](Uniform(0.4, 0.6))((x: Real) => Binomial(x, 300))
  def goldset =
    List(1, 6, 10, 234, 10, 3, 4, 9, 8, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3)
  val description =
    "Normal approximation to Binomial: Binomial(x, 200) with Uniform(0.4, 0.6) prior"
}
