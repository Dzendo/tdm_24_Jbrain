package ru.cs.tdm.ui

import ru.cs.tdm.cases.*
import ru.cs.tdm.code.Runner
import ru.cs.tdm.data.TestsProperties
import java.time.LocalDateTime
import javax.swing.SwingWorker
/**
 * Computes a result, or throws an exception if unable to do so.
 * Вычисляет результат или выдает исключение, если это невозможно сделать.
 *
 * Note that this method is executed only once.
 * Обратите внимание, что этот метод выполняется только один раз.
 *
 * Note: this method is executed in a background thread.
 * Примечание: этот метод выполняется в фоновом потоке.
 *
 * @return the computed result
 * @throws Exception if unable to compute a result
 */
class StartTests(private val startDialog: StartDialog? = null) : SwingWorker<Long, Int>() {
    override fun process(chunk: List<Int>) {
        super.process(chunk)
        // get last result
        val counterChunk = chunk[chunk.size - 1]
        println( "counterChunk = $counterChunk")
    }
    //@Throws(Exception::class)
    override fun doInBackground(): Long {
        if (TestsProperties.testCases.isEmpty()) TestsProperties.testCases.add("JetBrains")
        println("startTests ${LocalDateTime.now().withNano(0)} arguments: ${TestsProperties.testCases.joinToString()}")
        if (TestsProperties.debugPrintNomber > 1) println("Повторов ${TestsProperties.repeateCasesNomber} Задержка ${TestsProperties.threadSleepNomber} Печать ${TestsProperties.debugPrintNomber}")
        if (TestsProperties.debugPrintNomber > 1) println("Открытие страницы ${TestsProperties.loginpage}")
        if (TestsProperties.debugPrintNomber > 1) println("Браузер = ${TestsProperties.browsers[TestsProperties.browserIndex]} login= ${TestsProperties.login}   password= ${TestsProperties.password}")

        var allSumErrors: Long = 0L     // сумма ошибок для всех тестов для всех повторов
        for (repeat in 1..TestsProperties.repeateCasesNomber) {
            var repeatSumErrors: Long = 0L     // сумма ошибок для всех тестов для текущего повтора repeat
                    if (TestsProperties.isStartStop == 2) break

             for (test in TestsProperties.testCases) {

                if (TestsProperties.isStartStop == 2) break         // STOP
                while(TestsProperties.isPaused) Thread.sleep(1000L)     // PAUSE

                startDialog?.showActionCase(repeat, test, allSumErrors)

                 // process(chunk:
                publish(repeat)

                var caseErrors: Long = 0L        // сумма ошибок для текущего теста test при текущем повторе repeat
                if (TestsProperties.debugPrintNomber > 7) println("-------------- старт $test Повтор $repeat ------------")

                when (test) {
                    "Pass" -> {
                        caseErrors += Runner(repeat).runTest(ChangePass::class.java)
                    }

                    "Head" -> {
                        caseErrors += Runner(repeat).runTest(HeadRef::class.java)
                    }

                    "User" -> {
                        caseErrors += Runner(repeat).runTest(AdminUser::class.java)
                    }

                    "Filter" -> {
                        caseErrors += Runner(repeat).runTest(Filter::class.java)
                    }
                    "JetBrains" -> {
                        caseErrors += Runner(repeat).runTest(JetBrainsTest::class.java)
                    }

                    "ALL" -> {
                        caseErrors += Runner(repeat).runTest(ChangePass::class.java)
                        caseErrors += Runner(repeat).runTest(HeadRef::class.java)
                        caseErrors += Runner(repeat).runTest(AdminUser::class.java)
                        caseErrors += Runner(repeat).runTest(Filter::class.java)
                    }

                    else -> { println("Test $test! Unknowns") }
                }  // конец when вызова теста

                if (TestsProperties.debugPrintNomber > 7) println("-------------- стоп $test Повтор $repeat  --------------")
                repeatSumErrors += caseErrors
                startDialog?.showActionCase(repeat, test, allSumErrors)

            }
            if (repeatSumErrors > 0) println("@@@@@ $repeat repeateSumErrors = $repeatSumErrors  @@@")
            allSumErrors += repeatSumErrors
            startDialog?.showActionCase(repeat, "ALL", allSumErrors)
        }
        TestsProperties.isStartStop = -2        // т.е. START
        startDialog?.showButtons()
        if (allSumErrors > 0) println("@@@@@ Tests: allSumErrors = $allSumErrors  @@@")
        TestsProperties.summuryOfErrors += allSumErrors
        startDialog?.showActionCase(TestsProperties.repeateCasesNomber, "ALL", allSumErrors)
        return allSumErrors
    }  // end doInBackground()
}

