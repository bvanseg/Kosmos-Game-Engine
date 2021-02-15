import com.kosmos.engine.common.network.util.toBooleanArray
import com.kosmos.engine.common.network.util.toByte
import com.kosmos.engine.common.network.util.toInt
import com.kosmos.engine.common.network.util.toLong
import com.kosmos.engine.common.network.util.toShort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BooleanTransformTest {

    @Test
    fun testBooleanArrayToByte() {
        // GIVEN
        val booleanArray = booleanArrayOf(true, false, false, false, false, false, false)

        // WHEN
        val byte = booleanArray.toByte(false)
        val byteReverse = booleanArray.toByte(true)

        // THEN
        assertEquals(64.toByte(), byte)
        assertEquals(1.toByte(), byteReverse)
    }

    @Test
    fun testBooleanArrayToShort() {
        // GIVEN
        val booleanArray = booleanArrayOf(true, false, false, false, false, false, false)

        // WHEN
        val short = booleanArray.toShort(false)
        val shortReverse = booleanArray.toShort(true)

        // THEN
        assertEquals(64.toShort(), short)
        assertEquals(1.toShort(), shortReverse)
    }

    @Test
    fun testBooleanArrayToInt() {
        // GIVEN
        val booleanArray = booleanArrayOf(true, false, false, false, false, false, false)

        // WHEN
        val int = booleanArray.toInt(false)
        val intReverse = booleanArray.toInt(true)

        // THEN
        assertEquals(64, int)
        assertEquals(1, intReverse)
    }

    @Test
    fun testBooleanArrayToLong() {
        // GIVEN
        val booleanArray = booleanArrayOf(true, false, false, false, false, false, false)

        // WHEN
        val long = booleanArray.toLong(false)
        val longReverse = booleanArray.toLong(true)

        // THEN
        assertEquals(64L, long)
        assertEquals(1L, longReverse)
    }

    @Test
    fun testByteToBooleanArray() {
        // GIVEN
        val booleanArray = booleanArrayOf(false, true, false, false, false, false, false, false)

        // WHEN
        val byte = 64

        // THEN
        assertTrue(booleanArray.contentEquals(byte.toByte().toBooleanArray(false)))
    }
}