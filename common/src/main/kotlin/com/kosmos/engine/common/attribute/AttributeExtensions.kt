package com.kosmos.engine.common.attribute

import bvanseg.kotlincommons.math.to

inline fun <reified T: Number> Attribute<T>.add(value: Attribute<T>) {
    this.set((this.get().toDouble() + value.get().toDouble()).to())
}

inline fun <reified T: Number> Attribute<T>.sub(value: Attribute<T>) {
    this.set((this.get().toDouble() - value.get().toDouble()).to())
}

inline fun <reified T: Number> Attribute<T>.mult(value: Attribute<T>) {
    this.set((this.get().toDouble() * value.get().toDouble()).to())
}

inline fun <reified T: Number> Attribute<T>.div(value: Attribute<T>) {
    this.set((this.get().toDouble() / value.get().toDouble()).to())
}

inline fun <reified T: Number> Attribute<T>.rem(value: Attribute<T>) {
    this.set((this.get().toDouble() % value.get().toDouble()).to())
}