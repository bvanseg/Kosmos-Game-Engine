package com.kosmos.engine.common.network.message.ctx

import com.kosmos.engine.common.game.GameContainer

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class GameContext(val gameContainer: GameContainer): MessageContext(gameContainer.networker)