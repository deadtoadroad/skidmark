package com.deadtoadroad.skidmark.reflect

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

object Utilities {
  def getRuntimeClassFromClassTag[T](implicit ct: ClassTag[T]): RuntimeClass = ct.runtimeClass

  def getRuntimeClassFromTypeTag[T: TypeTag]: RuntimeClass = typeTag[T].mirror.runtimeClass(typeOf[T])
}
