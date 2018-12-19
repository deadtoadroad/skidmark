package com.deadtoadroad.skidmark.cqrs.model.events

import com.deadtoadroad.skidmark.cqrs.model.{Aggregate, Id, Version}

trait Event[A <: Aggregate] extends Id with Version
