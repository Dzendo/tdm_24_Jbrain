package ru.cs.tdm.code

import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary
import ru.cs.tdm.data.TestsProperties.debugPrintNomber
import ru.cs.tdm.ui.CustomTestExecutionListener
import java.io.PrintWriter
import java.time.LocalDateTime


// https://www.programcreek.com/java-api-examples/?api=org.junit.platform.launcher.Launcher
// https://www.javacodemonk.com/junit-5-platform-launcher-api-7dddb7ab

//@JvmStatic
class Runner(private val povtor : Int) {
    //val listener: TestExecutionListener = SummaryGeneratingListener()
    private val summaryGeneratingListener: SummaryGeneratingListener = SummaryGeneratingListener()
    var executionListener = CustomTestExecutionListener()

    private val DT = debugPrintNomber
    fun runTest(testClass: Class<*> ) : Long {

        // Discover and filter tests
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder
            .request()
            .selectors(DiscoverySelectors.selectClass(testClass))
            //.selectors(selectPackage("ru.cs.tdm.cases"))
            //.filters(includeClassNamePatterns(".*Test"))
            .build()
        val launcher: Launcher = LauncherFactory.create()
        //val plan: TestPlan = launcher.discover(request)

        // Executing tests
        if (DT > 4) println("======= run $povtor Test ${testClass.canonicalName}! ======= ${LocalDateTime.now().withNano(0)} ============")
        launcher.registerTestExecutionListeners(summaryGeneratingListener)
        launcher.registerTestExecutionListeners( executionListener)
        //launcher.execute(request, listener)
        launcher.execute(request)

        // Этот оператор берет от слушателя потока теста итоговые результаты (launcher Junit)
        val executionSummary: TestExecutionSummary = summaryGeneratingListener.summary
        val failed: Long = executionSummary.testsFailedCount
        if ((DT > 7) or (failed > 0L)) executionSummary.printTo(PrintWriter(System.out))
        if (failed >0) executionSummary.printFailuresTo(PrintWriter(System.out))

        // полученные результаты на печать - дает перечень результатов
        if (DT > 4) println("======= end $povtor Test ${testClass.canonicalName}! ====== Errors $failed =============")
        return failed
    }
}
