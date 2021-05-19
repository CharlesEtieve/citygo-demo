package com.eurosportdemo.app.model

import com.eurosportdemo.app.domain.model.Book
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.util.*

class BookUnitTest {

    @Test
    @Throws(Exception::class)
    fun test_parsing() {
        val body = loadJsonFixture("mockBook.json")
        val bookList = Book.fromJson(body)
        assertEquals(bookList.size, 7)
        val book = bookList[0]
        assertEquals(book.isbn, "c8fabf68-8374-48fe-a7ea-a00ccd07afff")
        assertEquals(book.title, "Henri Potier à l'école des sorciers")
        assertEquals(book.price, 35.0)
        assertEquals(book.cover, "http://henri-potier.xebia.fr/hp0.jpg")
        assertEquals(book.synopsis.size, 3)
        assertEquals(book.synopsis.first(), "Après la mort de ses parents (Lily et James Potier), Henri est recueilli par sa tante Pétunia (la sœur de Lily) et son oncle Vernon à l'âge d'un an. Ces derniers, animés depuis toujours d'une haine féroce envers les parents du garçon qu'ils qualifient de gens « bizarres », voire de « monstres », traitent froidement leur neveu et demeurent indifférents aux humiliations que leur fils Dudley lui fait subir. Henri ignore tout de l'histoire de ses parents, si ce n'est qu'ils ont été tués dans un accident de voiture")
        assertEquals(book.inBasket, false)
    }

    @Throws(IOException::class)
    fun loadJsonFixture(filename: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(filename)
        return inputStream?.let {
            val scanner = Scanner(it, "UTF-8").useDelimiter("\\A")
            if (scanner.hasNext()) scanner.next() else ""
        } ?: ""
    }
}