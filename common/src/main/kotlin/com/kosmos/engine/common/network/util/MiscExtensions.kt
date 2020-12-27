package com.kosmos.engine.common.network.util

fun Byte.toBoolean(): Boolean = this.toInt() != 0
fun Short.toBoolean(): Boolean = this.toInt() != 0
fun Int.toBoolean(): Boolean = this != 0
fun Long.toBoolean(): Boolean = this.toInt() != 0

fun Boolean.toByte(): Byte = if(this) 1 else 0
fun Boolean.toShort(): Short = if(this) 1 else 0
fun Boolean.toInt(): Int = if(this) 1 else 0
fun Boolean.toLong(): Long = if(this) 1 else 0

fun Byte.toBooleanArray(reverseRead: Boolean = true): BooleanArray {
    val booleanArray = BooleanArray(Byte.SIZE_BITS)

    val intVal = this.toInt()

    var position = if(reverseRead) 0 else booleanArray.size - 1
    for(i in booleanArray.indices) {

        booleanArray[i] = ((intVal shr position) and 1).toBoolean()

        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return booleanArray
}

fun Short.toBooleanArray(reverseRead: Boolean = true): BooleanArray {
    val booleanArray = BooleanArray(Short.SIZE_BITS)

    val intVal = this.toInt()

    var position = if(reverseRead) 0 else booleanArray.size - 1
    for(i in booleanArray.indices) {

        booleanArray[i] = ((intVal shr position) and 1).toBoolean()

        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return booleanArray
}

fun Int.toBooleanArray(reverseRead: Boolean = true): BooleanArray {
    val booleanArray = BooleanArray(Int.SIZE_BITS)

    var position = if(reverseRead) 0 else booleanArray.size - 1
    for(i in booleanArray.indices) {

        booleanArray[i] = ((this shr position) and 1).toBoolean()

        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return booleanArray
}

fun Long.toBooleanArray(reverseRead: Boolean = true): BooleanArray {
    val booleanArray = BooleanArray(Long.SIZE_BITS)

    var position = if(reverseRead) 0 else booleanArray.size - 1
    for(i in booleanArray.indices) {

        booleanArray[i] = ((this shr position) and 1).toBoolean()

        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return booleanArray
}

fun BooleanArray.toByte(reverseRead: Boolean = true): Byte {
    var value = 0

    var position = if(reverseRead) 0 else this.size - 1
    for(element in this) {
        if (element) {
            value = value or (1 shl position)
        }
        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return value.toByte()
}

fun BooleanArray.toShort(reverseRead: Boolean = true): Short {
    var value = 0

    var position = if(reverseRead) 0 else this.size - 1
    for(element in this) {
        if (element) {
            value = value or (1 shl position)
        }
        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return value.toShort()
}

fun BooleanArray.toInt(reverseRead: Boolean = true): Int {
    var value = 0

    var position = if(reverseRead) 0 else this.size - 1
    for(element in this) {
        if (element) {
            value = value or (1 shl position)
        }
        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return value
}

fun BooleanArray.toLong(reverseRead: Boolean = true): Long {
    var value = 0

    var position = if(reverseRead) 0 else this.size - 1
    for(element in this) {
        if (element) {
            value = value or (1 shl position)
        }
        if(reverseRead) {
            position++
        }
        else {
            position--
        }
    }

    return value.toLong()
}