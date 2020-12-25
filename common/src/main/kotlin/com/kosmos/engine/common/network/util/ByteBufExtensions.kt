package com.kosmos.engine.common.network.util

import bvanseg.kotlincommons.project.Version
import io.netty.buffer.ByteBuf
import org.joml.*
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

fun <T: Enum<T>> ByteBuf.writeEnum(enum: T) {
    this.writeInt(enum.ordinal)
}

inline fun <reified T: Enum<T>> ByteBuf.readEnum(): T = T::class.java.enumConstants[this.readInt()]

// JOML-related extensions

fun ByteBuf.writeVector2ic(vector: Vector2ic) {
    this.writeInt(vector.x())
    this.writeInt(vector.y())
}

fun ByteBuf.readVector2i(): Vector2i = Vector2i(this.readInt(), this.readInt())
fun ByteBuf.readVector2ic(): Vector2ic = Vector2i(this.readInt(), this.readInt())

fun ByteBuf.writeVector2fc(vector: Vector2fc) {
    this.writeFloat(vector.x())
    this.writeFloat(vector.y())
}

fun ByteBuf.readVector2f(): Vector2f = Vector2f(this.readFloat(), this.readFloat())
fun ByteBuf.readVector2fc(): Vector2fc = Vector2f(this.readFloat(), this.readFloat())

fun ByteBuf.writeVector2dc(vector: Vector2dc) {
    this.writeDouble(vector.x())
    this.writeDouble(vector.y())
}

fun ByteBuf.readVector2d(): Vector2d = Vector2d(this.readDouble(), this.readDouble())
fun ByteBuf.readVector2dc(): Vector2dc = Vector2d(this.readDouble(), this.readDouble())

fun ByteBuf.writeVector3ic(vector: Vector3ic) {
    this.writeInt(vector.x())
    this.writeInt(vector.y())
    this.writeInt(vector.z())
}

fun ByteBuf.readVector3i(): Vector3i = Vector3i(this.readInt(), this.readInt(), this.readInt())
fun ByteBuf.readVector3ic(): Vector3ic = Vector3i(this.readInt(), this.readInt(), this.readInt())

fun ByteBuf.writeVector3fc(vector: Vector3fc) {
    this.writeFloat(vector.x())
    this.writeFloat(vector.y())
    this.writeFloat(vector.z())
}

fun ByteBuf.readVector3f(): Vector3f = Vector3f(this.readFloat(), this.readFloat(), this.readFloat())
fun ByteBuf.readVector3fc(): Vector3fc = Vector3f(this.readFloat(), this.readFloat(), this.readFloat())

fun ByteBuf.writeVector3dc(vector: Vector3dc) {
    this.writeDouble(vector.x())
    this.writeDouble(vector.y())
    this.writeDouble(vector.z())
}

fun ByteBuf.readVector3d(): Vector3d = Vector3d(this.readDouble(), this.readDouble(), this.readDouble())
fun ByteBuf.readVector3dc(): Vector3dc = Vector3d(this.readDouble(), this.readDouble(), this.readDouble())

fun ByteBuf.writeVector4ic(vector: Vector4ic) {
    this.writeInt(vector.w())
    this.writeInt(vector.x())
    this.writeInt(vector.y())
    this.writeInt(vector.z())
}

fun ByteBuf.readVector4i(): Vector4i = Vector4i(this.readInt(), this.readInt(), this.readInt(), this.readInt())
fun ByteBuf.readVector4ic(): Vector4ic = Vector4i(this.readInt(), this.readInt(), this.readInt(), this.readInt())

fun ByteBuf.writeVector4fc(vector: Vector4fc) {
    this.writeFloat(vector.w())
    this.writeFloat(vector.x())
    this.writeFloat(vector.y())
    this.writeFloat(vector.z())
}

fun ByteBuf.readVector4f(): Vector4f = Vector4f(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat())
fun ByteBuf.readVector4fc(): Vector4fc = Vector4f(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat())

fun ByteBuf.writeVector4dc(vector: Vector4dc) {
    this.writeDouble(vector.w())
    this.writeDouble(vector.x())
    this.writeDouble(vector.y())
    this.writeDouble(vector.z())
}

fun ByteBuf.readVector4d(): Vector4d = Vector4d(this.readDouble(), this.readDouble(), this.readDouble(), this.readDouble())
fun ByteBuf.readVector4dc(): Vector4dc = Vector4d(this.readDouble(), this.readDouble(), this.readDouble(), this.readDouble())

fun ByteBuf.writeMatrix2fc(matrix: Matrix2fc) {
    this.writeFloat(matrix.m00())
    this.writeFloat(matrix.m01())
    this.writeFloat(matrix.m10())
    this.writeFloat(matrix.m11())
}

fun ByteBuf.readMatrix2f(): Matrix2f = Matrix2f(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat())
fun ByteBuf.readMatrix2fc(): Matrix2fc = Matrix2f(this.readFloat(), this.readFloat(), this.readFloat(), this.readFloat())

fun ByteBuf.writeMatrix2dc(matrix: Matrix2dc) {
    this.writeDouble(matrix.m00())
    this.writeDouble(matrix.m01())
    this.writeDouble(matrix.m10())
    this.writeDouble(matrix.m11())
}

fun ByteBuf.readMatrix2d(): Matrix2d = Matrix2d(this.readDouble(), this.readDouble(), this.readDouble(), this.readDouble())
fun ByteBuf.readMatrix2dc(): Matrix2dc = Matrix2d(this.readDouble(), this.readDouble(), this.readDouble(), this.readDouble())

fun ByteBuf.writeMatrix3fc(matrix: Matrix3fc) {
    this.writeFloat(matrix.m00())
    this.writeFloat(matrix.m01())
    this.writeFloat(matrix.m02())
    this.writeFloat(matrix.m10())
    this.writeFloat(matrix.m11())
    this.writeFloat(matrix.m12())
    this.writeFloat(matrix.m20())
    this.writeFloat(matrix.m21())
    this.writeFloat(matrix.m22())
}

fun ByteBuf.readMatrix3f(): Matrix3f = Matrix3f(
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat()
)
fun ByteBuf.readMatrix3fc(): Matrix3fc = Matrix3f(
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat()
)

fun ByteBuf.writeMatrix3dc(matrix: Matrix3dc) {
    this.writeDouble(matrix.m00())
    this.writeDouble(matrix.m01())
    this.writeDouble(matrix.m02())
    this.writeDouble(matrix.m10())
    this.writeDouble(matrix.m11())
    this.writeDouble(matrix.m12())
    this.writeDouble(matrix.m20())
    this.writeDouble(matrix.m21())
    this.writeDouble(matrix.m22())
}

fun ByteBuf.readMatrix3d(): Matrix3d = Matrix3d(
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble()
)
fun ByteBuf.readMatrix3dc(): Matrix3dc = Matrix3d(
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble()
)

fun ByteBuf.writeMatrix4fc(matrix: Matrix4fc) {
    this.writeFloat(matrix.m00())
    this.writeFloat(matrix.m01())
    this.writeFloat(matrix.m02())
    this.writeFloat(matrix.m03())
    this.writeFloat(matrix.m10())
    this.writeFloat(matrix.m11())
    this.writeFloat(matrix.m12())
    this.writeFloat(matrix.m13())
    this.writeFloat(matrix.m20())
    this.writeFloat(matrix.m21())
    this.writeFloat(matrix.m22())
    this.writeFloat(matrix.m23())
    this.writeFloat(matrix.m30())
    this.writeFloat(matrix.m31())
    this.writeFloat(matrix.m32())
    this.writeFloat(matrix.m33())
}

fun ByteBuf.readMatrix4f(): Matrix4f = Matrix4f(
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
)
fun ByteBuf.readMatrix4fc(): Matrix4fc = Matrix4f(
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
    this.readFloat(),
)

fun ByteBuf.writeMatrix4dc(matrix: Matrix4dc) {
    this.writeDouble(matrix.m00())
    this.writeDouble(matrix.m01())
    this.writeDouble(matrix.m02())
    this.writeDouble(matrix.m03())
    this.writeDouble(matrix.m10())
    this.writeDouble(matrix.m11())
    this.writeDouble(matrix.m12())
    this.writeDouble(matrix.m13())
    this.writeDouble(matrix.m20())
    this.writeDouble(matrix.m21())
    this.writeDouble(matrix.m22())
    this.writeDouble(matrix.m23())
    this.writeDouble(matrix.m30())
    this.writeDouble(matrix.m31())
    this.writeDouble(matrix.m32())
    this.writeDouble(matrix.m33())
}

fun ByteBuf.readMatrix4d(): Matrix4d = Matrix4d(
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
)
fun ByteBuf.readMatrix4dc(): Matrix4dc = Matrix4d(
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
    this.readDouble(),
)