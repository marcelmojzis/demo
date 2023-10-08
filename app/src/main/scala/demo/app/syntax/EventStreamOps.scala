package demo.app.syntax

import com.raquo.airstream.core.EventStream

extension (e: EventStream.type)
  def from[T](iterable: Iterable[T]): EventStream[T] = EventStream.fromSeq(iterable.toSeq)
