package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.message.LogLevel
import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.util.readEnum
import com.kosmos.engine.common.network.util.readUTF8String
import com.kosmos.engine.common.network.util.writeEnum
import com.kosmos.engine.common.network.util.writeUTF8String
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class LogMessage(): Message(MessageTarget.CLIENT) {

    lateinit var message: String
    var logLevel: LogLevel = LogLevel.INFO

    constructor(message: String, logLevel: LogLevel) : this() {
        this.message = message
        this.logLevel = logLevel
    }

    override fun read(buffer: ByteBuf) {
        message = buffer.readUTF8String()
        logLevel = buffer.readEnum()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeUTF8String(message)
        buffer.writeEnum(logLevel)
    }

    override fun handle(channel: Channel) {
        when(logLevel) {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warn(message)
            LogLevel.ERROR -> logger.error(message)
            LogLevel.DEBUG -> logger.debug(message)
            LogLevel.TRACE -> logger.trace(message)
        }
    }
}