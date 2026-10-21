package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResourceLookupTest {
    @Test fun existingResourceKeepsItsIdentifier() {
        assertEquals(0x7f010001, resolveResourceId("hello_fragment", "string") { _, _ -> 0x7f010001 })
    }

    @Test fun missingResourceDoesNotReachTheLoader() {
        assertNull(resolveResourceId("missing", "drawable") { _, _ -> 0 })
    }

    @Test fun absentPluginResourcesReturnNull() {
        assertNull(resolveResourceId("fragment_blank", "layout") { _, _ -> null })
    }

    @Test fun allResourceKindsForwardTheirExactNameAndType() {
        for (type in listOf("drawable", "string", "id", "layout")) {
            var calls = 0
            val result = resolveResourceId("resource_name", type) { name, actualType ->
                calls++
                assertEquals("resource_name", name)
                assertEquals(type, actualType)
                42
            }
            assertEquals(42, result)
            assertEquals(1, calls)
        }
    }

    @Test fun zeroIsRejectedForEveryResourceKind() {
        for (type in listOf("drawable", "string", "id", "layout")) {
            assertNull(resolveResourceId("missing", type) { _, _ -> 0 })
        }
    }
}
