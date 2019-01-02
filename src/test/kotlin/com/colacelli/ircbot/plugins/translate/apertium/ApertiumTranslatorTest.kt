package com.colacelli.ircbot.plugins.translate.apertium

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ApertiumTranslatorTest {
    private val translator = ApertiumTranslator()

    @Test
    fun translate() {
        runBlocking {
            val translation = translator.translate("en", "es", "hello").await()
            assertNotNull(translation)
            assertEquals("hola", translation?.translation)
        }
    }
}