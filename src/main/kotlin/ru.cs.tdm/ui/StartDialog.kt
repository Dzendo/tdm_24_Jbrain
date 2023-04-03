package ru.cs.tdm.ui

//import com.intellij.openapi.ui.ComboBox
//import com.intellij.ui.components.JBCheckBox
import ru.cs.tdm.data.TestsProperties
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*
import kotlin.system.exitProcess

/**
 * https://zetcode.com/kotlin/swing/
 * https://java-online.ru/swing-layout.xhtml
 */

class StartDialog : JFrame("TDM365 Tests from Elena Ver 1.4"), ActionListener  {
    //private val classStart = StartTests(this)  // перенес вниз чтобы был повторный старт
    //private var summuryOfErrors : Long = 0L
    //private val testCases: MutableSet<String> = mutableSetOf()
    private val contents = JPanel()
    private val buttonStartStop = JButton("START")
    private val buttonPauseResume = JButton("PAUSE")
    private val buttonLog = JButton("LOG")
    private val buttonExit = JButton("EXIT")

    private val passBox = JCheckBox("Change Password", true)
    private val headBox = JCheckBox("Test Head TDM", true)
    private val userBox = JCheckBox("Test Admin User", true)
    private val filterBox = JCheckBox("Test Filters", true)

    private val outBox = JCheckBox("File Out", true)
    private val consBox = JCheckBox("Console", true)
    private val uiBox = JCheckBox("Window", true)
    private val errBox = JCheckBox("Errors", true)

    private val browserBox: JComboBox<String> = JComboBox<String>(TestsProperties.browsers)
    private val serverBox: JComboBox<String> = JComboBox<String>(TestsProperties.servers)
    private val loginBox: JComboBox<String> = JComboBox<String>(TestsProperties.logins)
    private val passwordBox: JComboBox<String> = JComboBox<String>(TestsProperties.passwords)
    // https://jetbrains.design/intellij/

    /*
    private val browserBox: ComboBox<String> = ComboBox<String>(TestsProperties.browsers)
    private val serverBox: ComboBox<String> = ComboBox<String>(TestsProperties.servers)
    private val loginBox: ComboBox<String> = ComboBox<String>(TestsProperties.logins)
    private val passwordBox: ComboBox<String> = ComboBox<String>(TestsProperties.passwords)
    // https://jetbrains.design/intellij/
    */
    private val spinRepeateCases =JSpinner( SpinnerNumberModel(
            3,  //initial value
            1,  //min
            999,  //max
            1   //step
        ))
    private val spinRepeateTests =JSpinner( SpinnerNumberModel(
        1,  //initial value
        1,  //min
        99,  //max
        1   //step
    ))
    private val spinThreadSleep =JSpinner(SpinnerNumberModel(
        3,  //initial value
        0,  //min
        99,  //max
        1   //step
    ))
    private val spinDebugPrint =JSpinner(SpinnerNumberModel(
        5,  //initial value
        1,  //min
        9,  //max
        1   //step
    ))

    private val actionRepeate: JLabel = JLabel("-1")
    private val testRepeate: JLabel = JLabel("1")
    private val actionCase : JLabel = JLabel("NONE")
    private val actionTestStart : JLabel = JLabel("NONE")
    private val actionTestEnd : JLabel = JLabel("NONE")
    private val allErrors : JLabel = JLabel("-1")
    private val testErrors : JLabel = JLabel("-1")

    //fun startDialog()
    init  {
        TestsProperties.startDialog = this
        // Выход из программы при закрытии
        defaultCloseOperation = EXIT_ON_CLOSE
        // Создание панели содержимого с размещением кнопок
        val gridLayout = GridLayout(0,4,5,5)
        contents.layout = gridLayout
        //layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2f);
        // https://java-online.ru/swt-layout.xhtml#GridLayout

        val gcontent : Array<Array<JPanel>> =  Array(9) { Array(4) { JPanel(FlowLayout(FlowLayout.LEFT)) } }    //arrayOf(arrayOf(JPanel()))
        for (i in 0..8)
            for (j in 0..3)
                contents.add(gcontent[i][j])

        contentPane = contents

        gcontent[0][0].add(JLabel("Тесты:"))
        gcontent[0][1].add(JLabel("Повтор:"))
        gcontent[0][2].add(JLabel("Печать:"))
        gcontent[0][3].add(JLabel("Сервер:"))


        gcontent[1][0].add(passBox)
        gcontent[2][0].add(headBox)
        gcontent[3][0].add(userBox)
        gcontent[4][0].add(filterBox)

        gcontent[1][1].add(spinRepeateCases)
        gcontent[1][1].add(JLabel("Case:"))

        gcontent[2][1].add(spinRepeateTests)
        gcontent[2][1].add(JLabel("Test:"))

        gcontent[3][1].add(spinThreadSleep)
        gcontent[3][1].add(JLabel("Задержка:"))

        gcontent[4][1].add(spinDebugPrint)
        gcontent[4][1].add(JLabel("    Печать:"))

        gcontent[1][2].add(outBox)
        gcontent[2][2].add(consBox)
        gcontent[3][2].add(uiBox)
        gcontent[4][2].add(errBox)

        gcontent[1][3].add(browserBox)
        gcontent[2][3].add(serverBox)
        gcontent[3][3].add(loginBox)
        gcontent[4][3].add(passwordBox)

        // Кнопки для создания диалоговых окон
        gcontent[5][0].add(buttonStartStop)
        buttonStartStop.addActionListener(this)
        gcontent[5][1].add(buttonPauseResume)
        buttonPauseResume.addActionListener(this)
        gcontent[5][2].add(buttonLog)
        buttonLog.addActionListener(this)
        gcontent[5][3].add(buttonExit)
        buttonExit.addActionListener(this)


        gcontent[6][0].add(JLabel("allErrors:"))
        gcontent[6][1].add(allErrors)
        gcontent[6][2].add(testErrors)
        gcontent[6][3].add(JLabel(""))
        gcontent[7][0].add(JLabel("Repeat:"))
        gcontent[7][1].add(actionRepeate)
        gcontent[7][2].add(testRepeate)
        gcontent[7][3].add(JLabel(":"))
        gcontent[8][0].add(JLabel("Case:"))
        gcontent[8][1].add(actionCase)
        gcontent[8][2].add(actionTestStart)
        gcontent[8][3].add(actionTestEnd)

        showButtons()

       setDefaultLookAndFeelDecorated(true)
       pack()
       setLocationRelativeTo(null)
       setSize(550, 350)
       isVisible = true


    }

    /**
     * Должна высвечивать ход выполнения
     */
    fun showActionCase(actionRepeate : Int, actionCase : String, allErrors: Long ) {

        this.actionRepeate.text = actionRepeate.toString()
        this.actionRepeate.isVisible = true
        this.actionRepeate.revalidate()
        this.actionRepeate.repaint()

        this.actionCase.text = actionCase
        this.actionCase.isVisible = true
        this.actionCase.revalidate()
        this.actionCase.repaint()

        this.allErrors.text = allErrors.toString()
        this.allErrors.isVisible = true
        this.allErrors.revalidate()
        this.allErrors.repaint()
    }
    fun showActionTests(displayName: String,  testStatus: String = "Start" ) {

        if (displayName.startsWith("repetition")) {
            this.testRepeate.text = displayName.drop(11)
            this.testRepeate.isVisible = true
            this.testRepeate.revalidate()
            this.testRepeate.repaint()
        }
        else {
            val cat = displayName.lastIndex
            val displayCat = if (cat < 20 ) displayName.substring(0, cat) else  displayName.substring(0, 20)
            this.actionTestStart.text = displayCat
            this.actionTestStart.isVisible = true
            this.actionTestStart.revalidate()
            this.actionTestStart.repaint()

            this.actionTestEnd.text = if (cat < 20 ) "" else displayName.substring(20)
            this.actionTestEnd.isVisible = true
            this.actionTestEnd.revalidate()
            this.actionTestEnd.repaint()
        }

        if (testStatus == "Start" ) return

        this.testErrors.text = testStatus
        this.allErrors.isVisible = true
        this.allErrors.revalidate()
        this.allErrors.repaint()
    }

    fun showButtons() {
        when (TestsProperties.isStartStop) {
            -2 -> buttonStartStop.text = "START"
            -1 -> buttonStartStop.text = "Starting.."
             1 -> buttonStartStop.text = "STOP"
             2 -> buttonStartStop.text = "Stopping"
            else -> buttonStartStop.text = "ELSE"
        }
        buttonStartStop.isVisible = true
        this.buttonStartStop.revalidate()
        this.buttonStartStop.repaint()

        buttonPauseResume.text = if (TestsProperties.isPaused) "Resume" else "PAUSE"
        this.buttonPauseResume.revalidate()
        this.buttonPauseResume.repaint()
    }


    override fun actionPerformed(e: ActionEvent?) {
        if (e == null) return
        when (e.source) {
            buttonStartStop -> {
                    when (TestsProperties.isStartStop) {
                        -2 -> {     // START
                            TestsProperties.isStartStop = -1
                            showButtons()
                            with(TestsProperties) {
                                repeateCasesNomber = spinRepeateCases.value.toString().toInt()
                                repeateTestsNomber = spinRepeateTests.value.toString().toInt()
                                threadSleepNomber = spinThreadSleep.value.toString().toLong() * 1000L
                                debugPrintNomber = spinDebugPrint.value.toString().toInt()

                                browserIndex = browserBox.selectedIndex
                                pageIndex = serverBox.selectedIndex
                                //loginpage = server.selectedItem.toString()
                                loginpage = loginPages[pageIndex]
                                loginIndex = loginBox.selectedIndex
                                //login = login.selectedItem.toString()
                                login = logins[loginIndex]
                                passwordIndex = passwordBox.selectedIndex
                                //password = password.selectedItem.toString()
                                password = passwords[passwordIndex]

                                testCases.clear()
                                if (passBox.isSelected) testCases.add("Pass")
                                if (headBox.isSelected) testCases.add("Head")
                                if (userBox.isSelected) testCases.add("User")
                                if (filterBox.isSelected) testCases.add("Filter")

                                fileOutCheck = outBox.isSelected
                                consOutCheck = consBox.isSelected
                                uiOutCheck = uiBox.isSelected
                                assertOutCheck = errBox.isSelected

                            }
                            //println("@@@@@ actionCase.text = ${actionCase.text}  @@@")
                            // Здесь надо организовывать поток и стартовать в нем + Листенер с указанием туда в поток
                            // #################################################################################################
                            val classStart = StartTests(this)
                            val executeStart = classStart.execute()  // СТАРТ циклов тестов
                            //classStart.cancel(true) - не срабатывает
                            // #################################################################################################
                            with(TestsProperties) {
                                isStartStop = 1
                                // summuryOfErrors = classStart.get()  // нельзя здесь спрашивать - заморожу интерфейс
                                showButtons()
                                println("@@@@@ START summuryOfErrors = ${summuryOfErrors}  @@@")
                            }
                        }

                        1 -> {   // STOP
                            // НЕ РАБОТАЕТ переделать в флажки
                            //classStart.cancel(true)
                            TestsProperties.isStartStop = 2
                            showButtons()
                            println("@@@@@ STOP summuryOfErrors = ${TestsProperties.summuryOfErrors}  @@@")
                        }

                        else -> println("ELSE Нельзя нажимать на ing... ${TestsProperties.isStartStop}")
                    }

            }

            buttonPauseResume -> {
                TestsProperties.isPaused = !TestsProperties.isPaused
                showButtons()
            }
            buttonLog -> {}
            buttonExit -> {
                println("@@@@@ EXIT allSummuryOfErrors = ${TestsProperties.summuryOfErrors}  @@@")
                exitProcess(0)
            }

            else -> {}
        }
    }
}

fun main() {
    SwingUtilities.invokeLater { StartDialog() }  // этот поток называется EDT (поток диспетчеризации событий).
}
/** Функция создания диалогового окна.
 * @param title - заголовок окна
 * @param modal - флаг модальности
 */

/*private fun createDialog(title: String, modal: Boolean): JDialog {
    val dialog = JDialog(this, title, modal)
    dialog.defaultCloseOperation = DISPOSE_ON_CLOSE
    dialog.setSize(180, 90)
    return dialog
}*/

