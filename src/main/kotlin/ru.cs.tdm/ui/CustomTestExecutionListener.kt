package ru.cs.tdm.ui

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import ru.cs.tdm.data.TestsProperties
import ru.cs.tdm.data.TestsProperties.startDialog
import kotlin.system.exitProcess


class CustomTestExecutionListener : TestExecutionListener {
    private val DT = TestsProperties.debugPrintNomber

    override fun executionStarted(testIdentifier: TestIdentifier) {
        if (DT >7) println("Launcher Started test:  ${testIdentifier.displayName} ${testIdentifier.source} ")
        startDialog?.showActionTests(testIdentifier.displayName)
        if (TestsProperties.isStartStop > 0) { } // как то можно остановить ??? Обнулить тест план??
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        if (DT >7) println("Launcher Finished test: ${testIdentifier.displayName}  ${testExecutionResult.status}")
        startDialog?.showActionTests(testIdentifier.displayName, testExecutionResult.status.toString())
        if (TestsProperties.isStartStop > 0) { } // как то можно остановить ??? Обнулить тест план??

        if (TestsProperties.isPaused) {
            if (DT >4) println("Launcher Pausing for Resume")
            try {
                while(TestsProperties.isPaused) Thread.sleep(1000L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (DT >4) println("Launcher  Resume")
        }
        if (TestsProperties.isExit) {
            if (DT >4) println("Launcher Exiting test execution")
            exitProcess(0)
        }
    }
}
