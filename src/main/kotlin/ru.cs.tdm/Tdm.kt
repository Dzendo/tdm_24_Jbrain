package ru.cs.tdm

import ru.cs.tdm.code.printDual
import ru.cs.tdm.ui.StartDialog
import ru.cs.tdm.ui.StartTests
import ru.cs.tdm.data.TestsProperties
import javax.swing.SwingUtilities

/**
 * Если стартуют с параметром, то сразу на выполнение
 * Если стартуют без параметра, то окошко выбора Параметров
 */
fun main(args: Array<String>) {
    if (printDual()) println("Output: double to the console and to the file out+err")
    println("Program arguments: ${args.joinToString()}")
    // отключился Chrome 111 - костыль 1 из 2: второй в StartDriver "--remote-allow-origins=*" (достаточно одного)
    System.setProperty("webdriver.http.factory", "jdk-http-client")
    // метод помещает приложение в очередь событий Swing
    if (args.isEmpty()) SwingUtilities.invokeLater {  StartDialog() }   // этот поток называется EDT (поток диспетчеризации событий).
    else {
        TestsProperties.testCases.clear()
        TestsProperties.testCases.addAll(args.toSet())
        val classStart = StartTests()
        classStart.execute()  // СТАРТ циклов тестов
        val tdmSummuryOfErrors = classStart.get()  // нельзя здесь спрашивать - заморожу интерфейс
        println("@@@@@ TDMsummuryOfErrors = $tdmSummuryOfErrors  @@@")
    }
}