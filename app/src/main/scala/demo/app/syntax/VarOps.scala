package demo.app.syntax

import com.raquo.airstream.core.Observer
import com.raquo.airstream.state.Var

extension [A](v: Var[A])
  def updatePF[B](pf: PartialFunction[A, A]): Unit =
    v.update(a => pf.applyOrElse(a, identity))

  def updaterPF[B](pf: PartialFunction[(A, B), A]): Observer[B] =
    v.updater((a, b) => pf.applyOrElse((a, b), _._1))
