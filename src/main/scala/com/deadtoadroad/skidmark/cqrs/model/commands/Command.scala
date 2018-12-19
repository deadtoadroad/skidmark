package com.deadtoadroad.skidmark.cqrs.model.commands

import com.deadtoadroad.skidmark.cqrs.model.Aggregate

trait Command[A <: Aggregate]
