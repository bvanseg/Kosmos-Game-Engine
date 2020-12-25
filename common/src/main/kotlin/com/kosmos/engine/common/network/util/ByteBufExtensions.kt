package com.kosmos.engine.common.network.util

import bvanseg.kotlincommons.project.Version
import io.netty.buffer.ByteBuf
import java.nio.charset.StandardCharsets
import java.util.*

fun ByteBuf.writeUTF8String(string: String) {
    this.writeShort(string.length)
    this.writeBytes(string.toByteArray())
}

fun ByteBuf.readUTF8String(): String {
    val length = this.readShort().toInt()
    return this.readBytes(length).toString(StandardCharsets.UTF_8)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUUID(): UUID = UUID(this.readLong(), this.readLong())

fun ByteBuf.writeVersion(version: Version) {
    this.writeInt(version.major)
    this.writeInt(version.minor)
    this.writeInt(version.patch)
    this.writeUTF8String(version.label)
}

fun ByteBuf.readVersion(): Version = Version(this.readInt(), this.readInt(), this.readInt(), this.readUTF8String())