package ru.cs.tdm.cases

import org.openqa.selenium.WebDriver
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated
import ru.cs.tdm.code.Login
import ru.cs.tdm.code.Tools
import ru.cs.tdm.data.startDriver
import ru.cs.tdm.data.TestsProperties

/**
 *
 * data-reference="SUB_SYSADMIN"
 */
// Видимое имя класса при тестах, но нигде не показывается
@DisplayName("Testing Tools Menu-Icons Test")
// Аннотация Junit выполнять тесты по алфавиту. Здесь не обязательно, исследую для сквозного порядка
@TestMethodOrder(MethodOrderer.MethodName::class)
// Класс тестовый в котором лежит все остальное - требование Котлин
class HeadRef {
    // Статический блок - распределяется при старте программы, а не при создании экземпляра
    // BeforeAll и AfterAll должны быть статическими требование Junit
    // и естественно все переменные, которыми они пользуются
    companion object {
        // вынесены переменные, что бы менять их только здесь, а они поменяются там внизу в тестах
        private val threadSleep = TestsProperties.threadSleepNomber        // задержки где они есть
        private val DT: Int = TestsProperties.debugPrintNomber            // глубина отладочной информации 0 - ничего не печатать, 9 - все
        //private val NN:Int = TestsProperties.repeateTestsNomber        // количество повторений тестов
        private const val NN:Int = 1                    // количество повторений тестов
        private val repeateIn: Int = TestsProperties.repeateInNomber
        private val repeateOut: Int = TestsProperties.repeateOutNomber
        // переменная для драйвера
        lateinit var driver: WebDriver

        // объявления переменных на созданные ранее классы-страницы
        lateinit var tools: Tools
        // костыль для диаграммы Ганта повторного ввода пароля
        lateinit var loginGantt: String
        lateinit var passwordGantt: String

        /**
         * Осуществление первоначальной настройки
         * Предупреждение: Не смешивайте неявные и явные ожидания.
         * Это может привести к непредсказуемому времени ожидания.
         * Например, установка неявного ожидания 10 секунд и явного ожидания 15 секунд
         * может привести к таймауту через 20 секунд.
         */
        // Аннтотация Котлин, которая говорит, что эта функция будет статичная и Java совместимая
        @JvmStatic
        // Аннотация Junit выполнять один раз перед всеми тестами
        @BeforeAll
        fun beforeAll() {
            if (DT > 7) println("Вызов BeforeAll")
            // создание экземпляра драйвера (т.к. он объявлен в качестве переменной):
           driver = startDriver()

            // Создаем экземпляры классов и присвоим ссылки на них.
            // В качестве параметра указываем созданный перед этим объект driver,
            tools = Tools(driver)

            val loginpage = TestsProperties.loginpage
            if (DT > 8) println("Открытие страницы $loginpage")
            val login = TestsProperties.login
            val password = TestsProperties.password
            if (DT > 8) println("login= $login   password= $password")
            driver.get(loginpage)
            assertTrue(driver.title == "Tdms", "@@@@ Не открылась страница $loginpage - нет заголовка вкладки Tdms @@")
            Login(driver).loginIn(login, password)

            // Запоминаю логин и пароль для диаграммы Ганта - костыль.
            loginGantt = login
            passwordGantt = password
            if (DT > 7) println("Конец Вызов BeforeAll")
        }

        @JvmStatic
        // Аннотация Junit выполнять один раз после всех тестов
        @AfterAll
        fun afterAll() {
            //tools.idList()
            if (DT > 7) println("Вызов afterAll")
            tools.closeEsc5()
            Login(driver).loginOut()
            driver.quit() //  закрытия окна браузера
            if (DT > 7) println("Конец Вызов afterAll")
        }
    }  //конец companion object

    // верхний срабатывает на все тесты

    // Как обычно, выполняется перед каждым тестом, только он пустой
    @BeforeEach
    fun beforeEach() {
        if (DT > 7) println("Вызов BeforeEach верхний")
        //driver.navigate().refresh()
        if (DT > 7) println("Конец Вызов BeforeEach верхний")
    }

    // верхний срабатывает на все тесты

    // Как обычно, выполняется после каждого теста, только он пустой
    @AfterEach
    fun afterEach() {
        if (DT > 7) println("Вызов AfterEach верхний")
        tools.closeEsc5()
        //driver.navigate().refresh()
        if (DT > 7) println("Конец Вызов AfterEach верхний")
    }

    /**
     * Тестовый метод нажатия на пункты меню в верхней полосе
     */

    // Аннотация Junit5 вложенных классов тестов - ничего не дает кроме порядка в тестах
    // HeadMenuTest все тесты нажимающие на верхнее меню - можно запустить отдельно
    @Nested
    @DisplayName("Testing each menu separately")
    inner class HeadMenuTest {

        @BeforeEach
        fun beforeEach() {
            if (DT > 7) println("Вызов inner Head BeforeEach")
            val mainMenu = "Объекты"
            tools.qtipClickLast(mainMenu)
            assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия $mainMenu - нет заголовка вкладки TDM365 @@")
            assertTrue(tools.qtipPressedLast(mainMenu), "@@@@ После нажатия $mainMenu - кнопка Объекты нет утоплена @@")
            if (DT > 7) println("Конец Вызов inner Head BeforeEach")
        }

        @AfterEach
        fun afterEach() {
            if (DT > 7) println("Вызов inner Head AfterEach 5 раз closeEsc")
            tools.closeEsc5()
            if (DT > 7) println("Конец Вызов inner Head AfterEach")
        }

        @RepeatedTest(NN)
        @DisplayName("Объекты")
        fun mainMenuTest() {
            val mainMenu = "Объекты"
            if (DT > 6) println("Test нажатия на $mainMenu TDMS Web")
            // Клик на элемент с data-qtip="Объекты"
            tools.qtipClickLast(mainMenu)
            // Проверяем заголовок закладки Junit
            assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия $mainMenu - нет заголовка вкладки TDM365 @@")
            // Проверяем, что утоплена кнопка Объекты
            assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия $mainMenu - кнопка $mainMenu нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $mainMenu TDMS Web")
        }

        @RepeatedTest(NN)
        @DisplayName("Рабочий стол")
        fun workTableTest() {
            val workTable = "Рабочий стол"
            if (DT > 6) println("Test нажатия на $workTable")
            tools.qtipClickLast(workTable)
            assertTrue(tools.titleContain(workTable), "@@@@ После нажатия $workTable - нет заголовка вкладки $workTable @@")
            assertTrue(tools.qtipPressedLast(workTable), "@@@@ После нажатия $workTable - кнопка $workTable нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $workTable")
        }

        @RepeatedTest(NN)
        @DisplayName("Объекты")
        fun objectsTest() {
            val objects = "Объекты"
            if (DT > 6) println("Test нажатия на $objects")
            tools.qtipClickLast(objects)
            assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия $objects - нет заголовка вкладки TDM365 @@")
            assertTrue(tools.qtipPressedLast(objects), "@@@@ После нажатия $objects - кнопка $objects нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $objects")
        }

        @RepeatedTest(NN)
        @DisplayName("Почта")
        fun mailTest() {
            val mail = "Почта"
            if (DT > 6) println("Test нажатия на $mail")
            tools.qtipClickLast(mail)
            assertTrue(tools.titleContain(mail), "@@@@ После нажатия $mail - нет заголовка вкладки TDM365 @@")
            assertTrue(tools.qtipPressedLast(mail), "@@@@ После нажатия $mail - кнопка $mail нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $mail")
        }

        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Совещания")
        fun meetingTest() {
            var meeting = "Чат-Совещания"
            var title = meeting
            if (DT > 6) println("Test нажатия на $meeting")
            if (driver.findElements(By.xpath("//*[contains(@data-qtip, 'Чат')]")).size > 0) {
                meeting = "Чат"
                title = "Каналы"
            }
            if (driver.findElements(By.xpath("//*[contains(@data-qtip, 'Совещания')]")).size > 0) {
                meeting = "Совещания"
                title = meeting
            }
            if ((meeting == "Совещания") or  (meeting == "Чат")) {
                tools.qtipClickLast(meeting)
                assertTrue(tools.titleContain(title), "@@@@ После нажатия $meeting - нет заголовка вкладки $title @@")
                assertTrue(tools.qtipPressedLast(meeting),
                    "@@@@ После нажатия $meeting - кнопка $meeting нет утоплена @@" )
            }
            else if (DT > 3) println("Отсутствует пункт меню $meeting")
            if (DT > 6) println("Конец Test нажатия на $meeting")
        }

        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Диаграмма Ганта")
        fun ganttchartTest() {  //(repetitionInfo: RepetitionInfo) {

            //if (repetitionInfo.currentRepetition % 10 == 1) driver.navigate().refresh()
            val ganttchart = "Диаграмма Ганта"
            if (DT > 6) println("Test нажатия на $ganttchart")
            if (driver.findElements(By.xpath("//*[contains(@data-qtip, 'Диаграмма Ганта')]")).size > 0) {
                tools.qtipClickLast(ganttchart)
                //assertTrue(tools.titleContain(ganttchart), "@@@@ После нажатия $ganttchart - нет заголовка вкладки $ganttchart @@")  // Нет заголовка
                assertTrue(
                    tools.qtipPressedLast(ganttchart),
                    "@@@@ После нажатия $ganttchart - кнопка $ganttchart нет утоплена @@"
                )
                Thread.sleep(threadSleep)
                driver.navigate().refresh()
                Thread.sleep(threadSleep)
                if (driver.title == "Tdms") Login(driver).loginIn(loginGantt, passwordGantt) // Костыль
            }
            else if (DT > 3) println("Отсутствует пункт меню $ganttchart")
            if (DT > 6) println("Конец Test нажатия на $ganttchart")
        }

        @RepeatedTest(NN)
        @DisplayName("Справка")
        fun helpTest() {
            val help = "Справка"
            if (DT > 6) println("Test нажатия на $help")
            tools.qtipClickLast(help)
            //assertFalse(tools.titleContain(help), "@@@@ После нажатия $help - нет заголовка вкладки $help @@")      // Исправить!!! NOT
            assertTrue(tools.qtipPressedLast(help), "@@@@ После нажатия $help - кнопка $help нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $help")
        }

        @RepeatedTest(NN)
        @DisplayName("Искать")
        fun searchTest() {
            val search = "Искать"
            if (DT > 6) println("Test нажатия на $search")
            tools.qtipClickLast("Рабочий стол")  // Ошибка TDMS - отжимается кнопка !! Убрать
            tools.qtipClickLast("Объекты")
            tools.qtipLast("Введите текст")?.sendKeys("Лебедев")
            Thread.sleep(threadSleep)
            // Из поля с data-qtip="Введите текст" получить значение этого поля и сравнить с Лебедев,
            // если да, то тест прошел, если нет, то упал
            assertEquals("Лебедев", tools.qtipLast("Введите текст")?.getAttribute("value"), "@@@@ После ввода Лебедев в поле поиска в поле поиска не Лебедев @@")
            Thread.sleep(threadSleep)
            val findTrue = tools.qtipClickLast(search)
            if (!findTrue)  println("Не нажал лупу")
            assertTrue(tools.titleContain("Результаты"), "@@@@ После нажатия $search - нет заголовка вкладки Результаты @@")

            tools.qtipClickLast("Очистить")
            // Проверяется, что поле поиска пустое
            assertEquals("", tools.qtipLast("Введите текст")?.getAttribute("value"), "@@@@ После очистки поля поиска в поле поиска не пусто @@")
            if (DT > 6) println("Конец Test нажатия на $search")
        }

        @RepeatedTest(NN)
        @DisplayName("Уведомления")
        fun notificationTest() {
            val notification = "Уведомления"
            if (DT > 6) println("Test нажатия на $notification")
            tools.qtipClickLast(notification)
            // Запрашивает заголовок открывшегося окна и проверяет, что он Окно сообщений
            assertEquals("Окно сообщений", tools.windowTitle(), "@@@@ После нажатия $notification - нет заголовка окна Окно сообщений @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $notification")
        }
    }
    /**
     * Тестовый метод нажатия на иконки инструментов
     * во второй строке под SYSADMIN только ОБЪЕКТЫ
     * системного подменю расположено ниже в отдельном классе
     */
    @Nested
    @DisplayName("Testing Tools Box")
    @TestMethodOrder(MethodOrderer.MethodName::class)
    inner class ToolTest {
        @BeforeEach
        fun beforeEach() {
            if (DT > 7) println("Вызов inner Tools BeforeEach")
            tools.qtipClickLast("Объекты")
            assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия Объекты - нет заголовка вкладки TDM365 @@")
            assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия Объекты - кнопка Объекты нет утоплена @@")
            if (DT > 7) println("Конец inner Tools BeforeEach")
        }

        @AfterEach
        fun afterEach() {
            if (DT > 7) println("Вызов inner Tools AfterEach пять раз closeEsc")
            tools.closeEsc5()
            if (DT > 7) println("Конец inner Tools AfterEach пять раз closeEsc")
        }

        @RepeatedTest(NN)
        @DisplayName("Показать/скрыть дерево")
        // repetitionInfo (Junit) объект, который содержит в т.ч. номер повтора теста repetitionInfo.currentRepetition
        fun open_showTreeTest(){     //(repetitionInfo: RepetitionInfo) {
            // Если номер повтора теста 1,11,21 (остаток от деления на 10 равно 1) и тд, то обновить экран
            // У TDM подмерзает экран если много команд показать-скрыть
            //if (repetitionInfo.currentRepetition % 10 == 1) driver.navigate().refresh() // Костыль проверить убрать
            driver.navigate().refresh()
            val open_showTree = "Показать/скрыть дерево"
            if (DT > 6) println("Test нажатия на $open_showTree")
            assertTrue(tools.qtipPressedLast(open_showTree), "@@@@ После нажатия $open_showTree - кнопка $open_showTree нет утоплена @@") // Исправить на референс
            tools.referenceClickLast("TDMS_COMMAND_COMMON_SHOWTREE")  // Скрыть дерево
            //tools.qtipClickLast(open_showTree)   // Скрыть дерево
            assertFalse(tools.qtipPressedLast(open_showTree), "@@@@ После отжатия $open_showTree - кнопка $open_showTree утоплена @@")
            tools.referenceClickLast("TDMS_COMMAND_COMMON_SHOWTREE")  // Показать дерево
            //tools.qtipClickLast(open_showTree)   // Показать дерево
            assertTrue(tools.qtipPressedLast(open_showTree), "@@@@ После второго нажатия $open_showTree - кнопка $open_showTree нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $open_showTree")
        }

        @RepeatedTest(NN)  // Не сделан reference
        @DisplayName("Показать/скрыть панель предварительного просмотра")
        fun open_showPreviewPanelTest() {   //(repetitionInfo: RepetitionInfo) {
            // if (repetitionInfo.currentRepetition % 10 == 1) driver.navigate().refresh()
            driver.navigate().refresh()
            val open_showPreviewPanel = "Показать/скрыть панель предварительного просмотра"
            if (DT > 6) println("Test нажатия на $open_showPreviewPanel")
            assertTrue(tools.qtipPressedLast(open_showPreviewPanel), "@@@@ После нажатия $open_showPreviewPanel - кнопка $open_showPreviewPanel нет утоплена @@")
            tools.qtipClickLast(open_showPreviewPanel)
            assertFalse(tools.qtipPressedLast(open_showPreviewPanel), "@@@@ После отжатия $open_showPreviewPanel - кнопка $open_showPreviewPanel утоплена @@")
            tools.qtipClickLast(open_showPreviewPanel)
            assertTrue(tools.qtipPressedLast(open_showPreviewPanel), "@@@@ После второго нажатия $open_showPreviewPanel - кнопка $open_showPreviewPanel нет утоплена @@")
            if (DT > 6) println("Конец Test нажатия на $open_showPreviewPanel")

        }

        @RepeatedTest(NN)
        @DisplayName("Создать фильтр")
        fun filterTest(){ //(repetitionInfo: RepetitionInfo) {
            // if (repetitionInfo.currentRepetition % 10 == 1) driver.navigate().refresh()
            driver.navigate().refresh()
            val filter = "Создать фильтр"
            if (DT > 6) println("Test нажатия на $filter")
            tools.referenceClickLast("CMD_CREATE_USER_QUERY")
            //tools.qtipClickLast(filter)
            assertTrue(tools.titleWait("tdmsEditObjectDialog", "Редактирование объекта"),
                "@@@@ После нажатия $filter - нет заголовка окна Редактирование объекта @@")
            assertTrue(tools.referenceWaitText("T_ATTR_USER_QUERY_NAME", "Наименование фильтра"),
                "@@@@ После нажатия $filter и открытия окна с заголовком Редактирование объекта в окне нет поля T_ATTR_USER_QUERY_NAME Наименование фильтра @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $filter")
        }

        @RepeatedTest(NN)
        @DisplayName("Обновить")
        fun renewTest() {  //(repetitionInfo: RepetitionInfo) {
            // if (repetitionInfo.currentRepetition % 10 == 1) driver.navigate().refresh()
            val renew = "Обновить"
            if (DT > 6) println("Test нажатия на $renew")
            tools.referenceClickLast("TDMS_COMMAND_UPDATE")
            //tools.qtipClickLast(renew)
            if (DT > 6) println("Конец Test нажатия на $renew")
        }

        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Администрирование групп")
        fun adminUserTest() {
            val adminUser = "Администрирование групп"
            if (DT > 6) println("Test нажатия на $adminUser")
            tools.referenceClickLast("CMD_GROUP_CHANGE")
            //tools.qtipClickLast(adminUser)
            assertTrue(tools.titleWait("window", "Редактирование групп"),
                "@@@@ После нажатия $adminUser - нет заголовка окна Редактирование групп @@")
            assertTrue(tools.referenceWaitText("STATIC1", "Группы пользователей"),
                "@@@@ После нажатия $adminUser и открытия окна с заголовком Редактирование групп в окне нет поля STATIC1 Группы пользователей @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $adminUser")
        }

        @RepeatedTest(NN)
        @Disabled
        @DisplayName("Импорт пользователей")
        fun importUserTest() {
            val importUser = "Импорт пользователей"
            if (DT > 6) println("Test нажатия на $importUser")
            tools.referenceClickLast("CMD_IMPORT_USERS")
            //tools.qtipClickLast(importUser)
            // Нарвался - открывается окно Windows не имеющее отношения к HTML
            //assertTrue(tools.titleWait("window", "Редактирование групп"))
            //assertTrue(tools.referenceWaitText("STATIC1", "Группы пользователей"))
            //tools.closeXLast()
            tools.closeEsc()
            if (DT > 6) println("Конец Test нажатия на $importUser")
        }

        @RepeatedTest(NN)
        @DisplayName("Создать объект разработки")
        fun createObjectTest() {
            val createObject = "Создать объект разработки"
            if (DT > 6) println("Test нажатия на $createObject")
            tools.referenceClickLast("CMD_OBJECT_STRUCTURE_CREATE")
            //tools.qtipClickLast(createObject)
            assertTrue(tools.titleWait("tdmsEditObjectDialog","Редактирование объекта"),
                "@@@@ После нажатия $createObject - нет заголовка окна Редактирование объекта @@")
            assertTrue(tools.referenceWaitText("T_ATTR_OCC_CODE", "Код объекта"),
                "@@@@ После нажатия $createObject и открытия окна с заголовком Редактирование объекта в окне нет поля T_ATTR_OCC_CODE Код объекта @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $createObject")
        }

        @RepeatedTest(NN)
        @DisplayName("Настройка шаблона уведомлений")
        fun configuringNotificationTest() { //repetitionInfo: RepetitionInfo) {
            val configuringNotification = "Настройка шаблона уведомлений"
            //val nomerTesta: Int = repetitionInfo.currentRepetition
            //if ((nomerTesta % 10 == 1)) driver.navigate().refresh()
            if (DT > 6) println("Test нажатия на $configuringNotification")
            //Thread.sleep(threadSleep *2)
            tools.referenceClickLast("CMD_NOTIFICATIONS_SETTINGS")
            //tools.qtipClickLast(configuringNotification)
            Thread.sleep(threadSleep *2)
            assertTrue(tools.titleWait("tdmsEditObjectDialog", "Редактирование объекта"),
                "@@@@ После нажатия $configuringNotification - нет заголовка окна Редактирование объекта @@")
            assertTrue(tools.referenceWaitText("T_ATTR_NAME", "Наименование"),
                "@@@@ После нажатия $configuringNotification и открытия окна с заголовком Редактирование объекта в окне нет поля T_ATTR_NAME Наименование @@")
            assertTrue(tools.referenceWaitText("T_ATTR_REGULATION_START_TIME", "Время запуска проверки регламента"),
                "@@@@ После нажатия $configuringNotification и открытия окна с заголовком Редактирование объекта в окне нет поля T_ATTR_REGULATION_START_TIME Время запуска проверки регламента @@")
            tools.closeXLast()
            //Thread.sleep(threadSleep *2)
            if (DT > 6) println("Конец Test нажатия на $configuringNotification")
        }
    }  // конец inner - nested Testing Tools Box

    /**
     * Тестовый метод нажатия на пункты системного подменю
     * во второй строке под SYSADMIN не только ОБЪЕКТЫ
     * имеет вложенный класс тестирования СЭТД
     */
    @Nested
    @DisplayName("Testing SubSysadmin")
    @TestMethodOrder(MethodOrderer.MethodName::class)
    inner class SubSysadminTest {
        @BeforeEach
        fun beforeEach() {
            if (DT > 7) println("Вызов inner SubSysadmin BeforeEach")
            tools.qtipClickLast("Объекты")
            assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия Объекты - нет заголовка вкладки TDM365 @@")
            assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия Объекты - кнопка Объекты нет утоплена @@")
            if (DT > 7) println("Конец inner SubSysadmin BeforeEach")
        }

        @AfterEach
        fun afterEach() {
            if (DT > 7) println("Вызов inner SubSysadmin AfterEach 5 раз closeEsc")
            tools.closeEsc5()
            if (DT > 7) println("Конец inner SubSysadmin AfterEach")
        }

        // вспомогательная процедура открытия системного меню SubSysadmin и для СЭТД
        private fun openSubSysadmin() {
            if (DT > 7) println("Вызов openSubSysadmin")
            repeat(repeateOut) {
                Thread.sleep(threadSleep * it)
                tools.referenceClickLast("SUB_SYSADMIN")
                ///Thread.sleep(threadSleep)
                if (tools.qtipLastClass("Меню разработчика")?.contains("x-btn-menu-active") ?: false) return
                if (DT > 6) println("####### SUB_SYSADMIN Повтор *##*$it  открытия через $it sec #######")
                repeat(3) { tools.closeEsc() }
                tools.qtipClickLast("Объекты")
            }
            if (DT > 5) println("&&&&&&&&& Не открылось SUB_SYSADMIN за $repeateOut опросов  &&&&&&&&&")
            assertTrue(tools.qtipLastClass("Меню разработчика")?.contains("x-btn-menu-active") ?: false,
                "@@@@ После $repeateOut нажатия Меню разработчика меню все-таки не открылось @@")
            if (DT > 7) println("Конец openSubSysadmin")
        }

        /**
         * Вспомогательная процедура нажатия на пункты системного меню SubSysadmin и СЭТД
         * ожидает открытия попап окна с заданным заголовком
         * вообще годится для любого меню - вынести в Tools.kt
         */
        private fun clickMenu(menu: String, window: String, title: String): Boolean {
            if (DT > 7) println("Вызов clickMenu $menu $window $title")
            repeat(repeateOut) {
                //openSubSysadmin()
                Thread.sleep(threadSleep * it)
                tools.xpathClickMenu(menu)
                ///Thread.sleep(threadSleep)
                if (tools.titleWait(window, title)) return true
                if (DT > 6) println("####### пункт MENU за *##*$it не нажалось  $menu - нет  $title ждем $it sec #######")
                tools.closeEsc()
                tools.qtipClickLast("Объекты")
            }
            if (DT > 5) println("&&&&&&&&& Не нажалось $menu за $repeateOut нажатий $title  &&&&&&&&&")
            assertTrue(tools.titleWait(window, title),
                "@@@@ После клика на меню $menu не открылось окно $window с заголовком $title за $repeateOut попыток @@")
            if (DT > 7) println("Конец clickMenu $menu $window $title")
            return false
        }

        // data-reference="FORM_SYSTEM_SETTINGS"
        @RepeatedTest(NN)
        @DisplayName("Информация о системе")
        fun systemParametersTest() {
            val systemParameters = "Информация о системе"
            if (DT > 6) println("Test нажатия на $systemParameters")
            openSubSysadmin()
            clickMenu(systemParameters, "window", systemParameters)
            //tools.referenceClickLast("CMD_SYSTEM_SETTINGS")
            //tools.qtipClickLast(systemParameters)
            //println("FORM_SYSTEM_SETTING = ${tools.referenceLast("FORM_SYSTEM_SETTINGS")?.text}")
            assertTrue(tools.referenceWaitText("FORM_SYSTEM_SETTINGS", systemParameters),
                "@@@@ После нажатия $systemParameters нет FORM_SYSTEM_SETTINGS  $systemParameters @@")
            assertTrue(tools.titleWait("window", systemParameters),
                "@@@@ После нажатия $systemParameters - нет заголовка окна $systemParameters @@")
            assertTrue(tools.referenceWaitText("T_VER_SERVER", "Версия сервера"),
                "@@@@ После нажатия $systemParameters - в окне $systemParameters Нет T_VER_SERVER Версия сервера @@")
            assertTrue(tools.referenceWaitText("T_VER_TDM365", "Версия TDM365"),
                "@@@@ После нажатия $systemParameters - в окне $systemParameters Нет T_VER_TDM365 Версия TDM365 @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $systemParameters")
        }

        // data-reference="FORM_ATTRS_LIST"
        @RepeatedTest(NN)
        @DisplayName("Системные атрибуты")
        fun sysAttributesTest() {
            val sysAttributes = "Системные атрибуты"
            if (DT > 6) println("Test нажатия на $sysAttributes")
            openSubSysadmin()
            clickMenu(sysAttributes, "window", "Атрибуты")
            //println("FORM_ATTRS_LIST = ${tools.referenceLast("FORM_ATTRS_LIST")?.text}")
            // Скилл Selenium изучаю присутствия элемента в DOM страницы
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='FORM_ATTRS_LIST']")) != null,
                "@@@@ После нажатия $sysAttributes нет FORM_ATTRS_LIST @@")
            assertTrue(tools.referenceWaitText("FORM_ATTRS_LIST", "Атрибуты"),
                "@@@@ После нажатия $sysAttributes - нет FORM_ATTRS_LIST Атрибуты @@")
            assertTrue(tools.titleWait("window", "Атрибуты"),
                "@@@@ После нажатия $sysAttributes - нет заголовка окна Атрибуты @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $sysAttributes")
        }

        // data-reference="FORM_TREE_OBJS"
        // data-reference="TREE"
        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Схема данных")
        fun dataTreeTest() {
            val dataTree = "Схема данных"
            if (DT > 6) println("Test нажатия на $dataTree")
            openSubSysadmin()

            clickMenu(dataTree, "window", dataTree)
            //println("FORM_TREE_OBJS = ${tools.referenceLast("FORM_TREE_OBJS")?.text}")
            //println("TREE = ${tools.referenceLast("TREE")?.text}")
            // Лучше проверять присутствует ли этот элемент в DOM presenceOfElementLocated(By locator)
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='FORM_TREE_OBJS']")) != null,
                "@@@@ После нажатия $dataTree в окне нет FORM_TREE_OBJS @@")
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='TREE']")) != null,
                "@@@@ После нажатия $dataTree в окне нет TREE' @@")

            assertTrue(tools.referenceWaitText("FORM_TREE_OBJS", dataTree),
                "@@@@ После нажатия $dataTree в окне FORM_TREE_OBJS не равно $dataTree @@")
            assertTrue(tools.referenceWaitText("TREE", "Типы объектов"),
                "@@@@ После нажатия $dataTree в окне TREE не равно Типы объектов @@")

            assertTrue(tools.titleWait("window", dataTree),
                "@@@@ После нажатия $dataTree - нет заголовка окна $dataTree @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $dataTree")
        }

        // data-reference="FORM_EVENTS_LOG"
        // data-reference="GRID"
        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Журнал событий")
        fun eventsLogTest() {
            val eventsLog = "Журнал событий"
            if (DT > 6) println("Test нажатия на $eventsLog")
            openSubSysadmin()
            clickMenu(eventsLog, "window", eventsLog)
            //println("FORM_EVENTS_LOG = ${tools.referenceLast("FORM_EVENTS_LOG")?.text}")
            //println("GRID = ${tools.referenceLast("GRID")?.text}")
            // Лучше проверять присутствует ли этот элемент в DOM presenceOfElementLocated(By locator)
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='FORM_EVENTS_LOG']")) != null,
                "@@@@ После нажатия $eventsLog в окне нет FORM_EVENTS_LOG @@")
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='GRID']")) != null,
                "@@@@ После нажатия $eventsLog в окне нет GRID @@")
            assertTrue(tools.referenceWaitText("FORM_EVENTS_LOG", eventsLog),
                "@@@@ После нажатия $eventsLog в окне FORM_EVENTS_LOG не равно $eventsLog @@")
            assertTrue(tools.referenceWaitText("GRID", ""))
            assertTrue(tools.titleWait("window", eventsLog),
                "@@@@ После нажатия $eventsLog в окне заголовок содержит $eventsLog @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $eventsLog")
        }

        // data-reference="FORM_SERVER_LOG"
        // data-reference="QUERY_SERVER_LOG"
        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Журнал сервера")
        fun serverLogTest() {
            val serverLog = "Журнал сервера"
            if (DT > 6) println("Test нажатия на $serverLog")
            openSubSysadmin()
            clickMenu(serverLog, "window", serverLog)
            //println("FORM_SERVER_LOG = ${tools.referenceLast("FORM_SERVER_LOG")?.text}")
            //println("QUERY_SERVER_LOG = ${tools.referenceLast("QUERY_SERVER_LOG")?.text}")
            // Лучше проверять присутствует ли этот элемент в DOM presenceOfElementLocated(By locator)
            Thread.sleep(threadSleep)
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='FORM_SERVER_LOG']")) != null,
                "@@@@ После нажатия $serverLog в окне нет FORM_SERVER_LOG @@")
            assertTrue(presenceOfElementLocated(By.xpath("//*[data-reference='QUERY_SERVER_LOG']")) != null,
                "@@@@ После нажатия $serverLog в окне нет QUERY_SERVER_LOG @@")
            assertTrue(tools.referenceWaitText("FORM_SERVER_LOG", serverLog),
                "@@@@ После нажатия $serverLog в окне FORM_SERVER_LOG не равно $serverLog @@")
            assertTrue(tools.referenceWaitText("QUERY_SERVER_LOG", "Timestamp"),
                "@@@@ После нажатия $serverLog в окне QUERY_SERVER_LOG не равно Timestamp @@")
            assertTrue(tools.titleWait("window", serverLog),
                "@@@@ После нажатия $serverLog в окне заголовок содержит $serverLog @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $serverLog")
        }

        @RepeatedTest(NN)
        @DisplayName("Удалить структуру объектов")
        fun delObjectsTest() {
            val delObjects = "Удалить структуру объектов"
            if (DT > 6) println("Test нажатия на $delObjects")
            openSubSysadmin()
            clickMenu(delObjects, "tdmsSelectObjectDialog", "Удаление структуры объектов")
            assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Удаление структуры объектов"),
                "@@@@ После нажатия $delObjects в окне заголовок не содержит Удаление структуры объектов @@")
            tools.closeXLast()
            if (DT > 6) println("Конец Test нажатия на $delObjects")
        }

        /**
         * Тестовый метод нажатия на пункты подменю СЭТД
         * во второй строке под SYSADMIN и Имитация ИУС СЭТД
         * вложен в верхний класс тестирования меню системщика
         * вложенный класс во вложенный класс пробую и логически это так и есть
         * Можно его запускать отдельно, т.к. он оформлен отдельным классом
         */
        @Nested
        @DisplayName("Testing CETD")
        @TestMethodOrder(MethodOrderer.MethodName::class)
        inner class CETDTest {
            @BeforeEach
            fun beforeEach() {
                if (DT > 7) println("Вызов inner CETD BeforeEach")
                tools.qtipClickLast("Объекты")
                assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия Объекты - нет заголовка вкладки TDM365 @@")
                assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия Объекты - кнопка Объекты нет утоплена @@")
                if (DT > 7) println("Конец inner CETD BeforeEach")
            }

            @AfterEach
            fun afterEach() {
                if (DT > 7) println("Вызов inner CETD AfterEach 5 раз closeEsc")
                tools.closeEsc5()
                if (DT > 7) println("Конец inner CETD AfterEach")
            }

            // Имитация ИУС СЭТД
            private fun openCETD() {
                if (DT > 7) println("Вызов openCETD")
                repeat(repeateOut) {
                    openSubSysadmin()
                    Thread.sleep(threadSleep * it)
                    val click = tools.xpathClickMenu("Имитация ИУС СЭТД")
                    //Thread.sleep(threadSleep)
                    if (click) return
                    if (DT > 6) println("####### ИУС СЭТД Повтор *##*$it  открытия через $it sec #######")
                    repeat(3) { tools.closeEsc() }
                    tools.qtipClickLast("Объекты")
                }
                if (DT > 5) println("&&&&&&&&& Не открылось ИУС СЭТД за $repeateOut опросов  &&&&&&&&&")
                assertTrue(tools.qtipLastClass("СЭТД")?.contains("x-btn-menu-active") ?: false,
                    "@@@@ После клика на подменю Имитация ИУС СЭТД не открылось подменю Имитация ИУС СЭТД  за $repeateOut попыток @@")
                if (DT > 7) println("Конец openCETD")
            }

            @RepeatedTest(NN)
            @DisplayName("Поток - Проверка связи без авторизации")
            fun flow_2Test() {
                val flow0 = "Поток - Проверка связи без авторизации"
                if (DT > 6) println("Test нажатия на $flow0")
                openCETD()
                clickMenu(flow0, "messagebox", "TDMS")
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow0 в окне заголовок не содержит TDMS @@")
                Thread.sleep(threadSleep)
                tools.closeXLast()
                tools.closeEsc()
                if (DT > 6) println("Конец Test нажатия на $flow0")
            }
            // Отправка запроса на сервер - не надо
            // ? Соединение установлено (с авторизацией)
            @RepeatedTest(NN)
            @DisplayName("Поток - Проверка связи с авторизацией")
            fun flow_1Test() {
                val flow0 = "Поток - Проверка связи с авторизацией"
                if (DT > 6) println("Test нажатия на $flow0")
                openCETD()
                clickMenu(flow0, "messagebox", "TDMS")
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow0 в окне заголовок не содержит TDMS @@")
                Thread.sleep(threadSleep)
                tools.closeXLast()
                Thread.sleep(threadSleep)
                tools.closeEsc()
                if (DT > 6) println("Конец Test нажатия на $flow0")
            }

            @RepeatedTest(NN)
            @DisplayName("Поток - Проверка статуса проекта")
            fun flowTest() {
                val flow = "Поток - Проверка статуса проекта"
                if (DT > 6) println("Test нажатия на $flow")
                openCETD()
                //tools.xpathClickMenu(flow)
                clickMenu(flow, "messagebox", "TDMS")
                Thread.sleep(threadSleep)
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow в окне заголовок не содержит TDMS @@")
                //val msgText = tools.xpathGetText("//div[starts-with(@id,'messagebox-') and  contains(@id,'-msg')]")
                //assertTrue(msgText.contains("Да")) // - Ввод GUID проекта вручную"))
                //assertTrue(msgText.contains("Нет")) // - Выбор проекта в системе"))
                tools.closeXLast()
                assertTrue(tools.titleWait("messagebox","Ввод значения"),
                    "@@@@ После следующего нажатия $flow в окне заголовок не содержит Ввод значения @@")
                tools.closeXLast()
                Thread.sleep(threadSleep)
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После последнего нажатия $flow в окне заголовок не содержит Ввод значения @@")
                tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow")
            }

            @RepeatedTest(NN)
            @DisplayName("Поток 0 - Отправка проекта")
            fun flow0Test() {
                val flow0 = "Поток 0 - Отправка проекта"
                if (DT > 6) println("Test нажатия на $flow0")
                openCETD()
                clickMenu(flow0, "messagebox", "TDMS")
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow0 в окне заголовок не содержит TDMS @@")
                Thread.sleep(threadSleep)
                tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow0")
            }

            //@Disabled
            @RepeatedTest(NN)
            @DisplayName("Поток 1 - Отправка передаточного документа")
            fun flow1Test() {
                val flow1 = "Поток 1 - Отправка передаточного документа"
                if (DT > 6) println("Test нажатия на $flow1")
                openCETD()

                clickMenu(flow1, "window", "Выбор сценария")

                assertTrue(tools.titleWait("window", "Выбор сценария"),
                    "@@@@ После нажатия $flow1 в окне заголовок не содержит Выбор сценария @@")
               // tools.idList()
                tools.closeXLast()
                //assertTrue(tools.titleWait("messagebox","TDMS"), "@@@@ После нажатия $flow1 в окне заголовок не содержит TDMS @@")
                //tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow1")
            }

            @RepeatedTest(NN)
            @Disabled
            @DisplayName("Поток 2.1 - Ответ о результате передаче РЗ")
            fun flow2Test() {
                val flow2 = "Поток 2.1 - Ответ о результате передаче РЗ"
                if (DT > 6) println("Test нажатия на $flow2")
                openCETD()
                clickMenu(flow2, "messagebox", "TDMS")
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow2 в окне заголовок не содержит TDMS @@")
                tools.closeXLast()
                assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Выбор Реестра замечаний"),
                    "@@@@ После нажатия $flow2 в очередном окне заголовок не содержит Выбор Реестра замечаний @@")
                tools.closeXLast()
                //Thread.sleep(threadSleep)
                //assertTrue(tools.titleWait("messagebox","TDMS")) // нет в 1.2.18
                //tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow2")
            }

            @RepeatedTest(NN)
            @Disabled
            @DisplayName("Поток 3 - Отправка ответов на замечания")
            fun flow3Test() {
                val flow3 = "Поток 3 - Отправка ответов на замечания"
                if (DT > 6) println("Test нажатия на $flow3")
                openCETD()
                clickMenu(flow3, "messagebox", "TDMS")
                assertTrue(tools.titleWait("messagebox","TDMS"),
                    "@@@@ После нажатия $flow3 в окне заголовок не содержит TDMS @@")
                tools.closeXLast()
                assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Выбор Реестра замечаний"),
                    "@@@@ После нажатия $flow3 в очередном окне заголовок не содержит Выбор Реестра замечаний @@")
                tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow3")
            }
            @RepeatedTest(NN)
            @DisplayName("Поток 5.1 - Отправка окончательного ПД")
            fun flow5Test() {  // tdmsSelectObjectDialog-1593_header-title-textEl
                val flow5 = "Поток 5.1 - Отправка окончательного ПД"
                if (DT > 6) println("Test нажатия на $flow5")
                openCETD()
                clickMenu(flow5, "tdmsSelectObjectDialog", "Выбор Передаточного документа")
                assertTrue(tools.titleWait("tdmsSelectObjectDialog","Выбор Передаточного документа"),
                    "@@@@ После нажатия $flow5 в окне заголовок не содержит Выбор Передаточного документа @@")
                tools.closeXLast()
                if (DT > 6) println("Конец Test нажатия на $flow5")
            }
        }   // конец Имитация ИУС СЭТД
    }  // конец Testing SubSysadmin
}