import com.kosmos.engine.common.attribute.AttributeMap
import com.kosmos.engine.common.attribute.AttributeMutationSchema

/**
 * @author Boston Vanseghi
 * @since 1.0.0
 */
class HealthMutationSchema(attribute: AttributeMap.Attribute<Int>): AttributeMutationSchema<Int>(attribute) {

    init {
        setUpgradeSchema {
            it.set(it.initialValue + (currentLevel * 25 + 25).toInt())
        }

        setDowngradeSchema {
            it.set(it.initialValue + (currentLevel * 25 - 25).toInt())
        }
    }
}