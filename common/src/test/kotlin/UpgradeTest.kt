import com.kosmos.engine.common.attribute.AttributeMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpgradeTest {

    @Test
    fun testUpgrades() {
        val attributeMap = AttributeMap()

        val healthAttribute = attributeMap.createAttribute("health", 100)
        healthAttribute.attributeMutationSchema = HealthMutationSchema(healthAttribute)

        assertEquals(100, healthAttribute.get())
        attributeMap.upgrade()
        assertEquals(125, healthAttribute.get())

        attributeMap.upgrade()
        assertEquals(150, healthAttribute.get())

        attributeMap.upgrade()
        assertEquals(175, healthAttribute.get())

        attributeMap.downgrade()
        assertEquals(150, healthAttribute.get())

        attributeMap.downgrade()
        assertEquals(125, healthAttribute.get())
    }
}