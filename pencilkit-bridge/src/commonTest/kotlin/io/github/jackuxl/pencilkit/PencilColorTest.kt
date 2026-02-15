package io.github.jackuxl.pencilkit

import kotlin.test.Test
import kotlin.test.assertFailsWith

class PencilColorTest {
    @Test
    fun rejectsInvalidRanges() {
        assertFailsWith<IllegalArgumentException> {
            PencilColor(red = 1.2, green = 0.0, blue = 0.0)
        }
    }
}
