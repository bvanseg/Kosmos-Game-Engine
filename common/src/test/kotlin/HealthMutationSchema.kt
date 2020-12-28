import com.kosmos.engine.common.attribute.Attribute
import com.kosmos.engine.common.attribute.AttributeMutationSchema

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class HealthMutationSchema(attribute: Attribute<Int>): AttributeMutationSchema<Int>(attribute) {

    init {
        setUpgradeSchema {
            it.set(it.initialValue + (currentLevel * 25 + 25).toInt())
        }

        setDowngradeSchema {
            it.set(it.initialValue + (currentLevel * 25 - 25).toInt())
        }
    }
}