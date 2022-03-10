package pt.isel.leic.pc.echo

import java.io.BufferedWriter

/**
 * Extension function that prints a new line with the given string to this [BufferedWriter].
 */
fun BufferedWriter.println(str: String) {
    write(str)
    newLine()
    flush()
}
