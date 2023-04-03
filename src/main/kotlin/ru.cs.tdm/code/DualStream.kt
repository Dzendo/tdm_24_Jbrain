package ru.cs.tdm.code
// https://www.geeksforgeeks.org/printstream-flush-method-in-java-with-examples/
import java.io.FileOutputStream
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Направление вывода в файл и в консоль
 */
class DualStream(out1: PrintStream, private val out2: PrintStream) : PrintStream(out1) {
    override fun write(buf: ByteArray, off: Int, len: Int) =
        try {
            super.write(buf, off, len)
            out2.write(buf, off, len)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    override fun flush() = super.flush().also { out2.flush() }
}
fun printDual(): Boolean {

    val localDateNow = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm").format(LocalDateTime.now())
    // Направление вывода в файл и в консоль
    try {
    val out = PrintStream(FileOutputStream("out_$localDateNow.log"))
    val dualOut: PrintStream = DualStream(System.out, out)
    System.setOut(dualOut)
    // System.setOut(out);

    val err = PrintStream(FileOutputStream("err_$localDateNow.log"))
    val dualErr = DualStream(System.err, err)
    System.setErr(dualErr)
    // System.setErr(err);
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}