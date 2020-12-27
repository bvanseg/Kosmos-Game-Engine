package com.kosmos.engine.common.network.message.impl

import com.kosmos.engine.common.network.message.Message
import com.kosmos.engine.common.network.message.MessageTarget
import com.kosmos.engine.common.network.message.ctx.GameContext

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
abstract class GameMessage(targetSide: MessageTarget): Message<GameContext>(targetSide)