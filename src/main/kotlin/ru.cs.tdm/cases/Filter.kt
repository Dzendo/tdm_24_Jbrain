package ru.cs.tdm.cases

import org.apache.commons.io.FileUtils.copyFile
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.*
import ru.cs.tdm.code.Login
import ru.cs.tdm.code.Tools
import ru.cs.tdm.data.startDriver
import ru.cs.tdm.data.TestsProperties
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertContains

/**
 * Халтура - недоделки:
 * 1. Поле ответственный открываю нештатно - ждем ОМСК
 * 2. Выпадающие списки (Статус, Отв лицо) не раскрываю, а вношу в поле имеющееся в списке значение
 * 3. Дату не раскрываю + Сегодня, а вношу сразу сегодня
 * 4. Жестко привязался к омшаннику, а если у И.В.
 * 5. Поиск в таблице примитивный и безграмотный
 * 6. Не выпонен кейс от слова совсем
 * 7. Не проверок на то что все правильно внеслось, проверок на рез фильтрации итд
 */

/**
 * data-reference="CMD_CREATE_USER_QUERY" - кнопка создать фмльтр
 * data-reference="CMD_GET_REPORT" - Получить отчет
 * data-reference="CMD_VIEW_ONLY" Посмотреть
 * data-reference="CMD_EDIT_ATTRS" - редактировать
 * data-reference="CMD_DELETE_USER_QUERY" - удалить
 * data-reference="tabbar-FORM_USER_QUERY" закладка фильтр
 * data-reference="tabbar-FORM_USER_QUERY_RESULT" - закладка Результаты фильтрации
 * data-reference="FORM_USER_QUERY" поле закладки фильтр
 * data-reference="FORM_USER_QUERY_RESULT" поле закладки Результаты фильтрации
 * data-reference="QUERY_USER_QUERY" ЕЩЕ поле закладки Результаты фильтрации
 *
 * Засады:
 * Дерево: Встать, открыть, выделить в Дереве
 * Определить таблицу "Результаты фильтрации" - кол-во итд
 * Выбор из раскрывающегося справочника (выделить, ок)
 * Движки-ползунки (дерево, список результатов + горизонтальный, справочники)
 * Выбор даты
 * Системные вкладки - много
 * Получить результаты: Промаргивает вкладкой браузера и в загрузках файл
 * Определение красного в журнале сервера (GMT)
 *
 * Определение "когда тест больше не ждать"
 * Что далее если срыв теста где-то
 * Вызов тестов из Main
 * Вывод результатов тестов
 */

@DisplayName("Filter Test")
@TestMethodOrder(MethodOrderer.MethodName::class)
class Filter {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    //val localDateNow = LocalDate.now().format(formatter)  //LocalDateTime.now()
    //private val localDateNow = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm").format(LocalDateTime.now())
   // private val localDateNow = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now())

    companion object {
        private val threadSleep = TestsProperties.threadSleepNomber     // задержки где они есть
        private val DT: Int = TestsProperties.debugPrintNomber          // глубина отладочной информации 0 - ничего не печатать, 9 - все
        //private val NN:Int = TestsProperties.repeateTestsNomber       // количество повторений тестов
        private const val NN:Int = 1                                    // количество повторений тестов
        private val localDateNow = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss").format(LocalDateTime.now())
    // переменная для драйвера
    private lateinit var driver: WebDriver
    // объявления переменных на созданные ранее классы-страницы
    private lateinit var tools: Tools

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
        if (DT >7) println("Вызов BeforeAll FilterTest")
            // создание экземпляра драйвера (т.к. он объявлен в качестве переменной):
            driver = startDriver()

            // Создаем экземпляры классов созданных ранее страниц, и присвоим ссылки на них.
            // В качестве параметра указываем созданный перед этим объект driver,
            tools = Tools(driver)

            val loginpage = TestsProperties.loginpage
            if (DT > 7) println("Открытие страницы $loginpage")
            val login = TestsProperties.login
            val password = TestsProperties.password
            if (DT > 7) println("login= $login   password= $password")
            driver.get(loginpage)
            assertTrue(driver.title == "Tdms", "@@@@ Не открылась страница $loginpage - нет заголовка вкладки Tdms @@")
            Login(driver).loginIn(login, password)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            //tools.idList()
            if (DT >7) println("Вызов AfterAll FilterTest")
            tools.closeEsc5()
            Login(driver).loginOut()
            driver.quit() //  закрытия окна браузера

        }
    }   // конец companion object

    @BeforeEach
    fun beforeEach() {

        if (DT > 7) println("Начало BeforeEach FilterTest")
        val mainMenu = "Объекты"
        if (DT > 7) println("Test нажатия на $mainMenu TDMS Web")
        tools.qtipClickLast(mainMenu)
        assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия $mainMenu - нет заголовка вкладки TDM365 @@")  // сбоит нечасто заголовок страницы на создании
        assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия $mainMenu - кнопка Объекты нет утоплена @@")
        if (DT >7) println("Конец BeforeEach FilterTest")
    }
    // пришлось ввести т.к. при рабочем столе два значка "создать фильтр"
    fun workTable() {
        val workTable = "Рабочий стол"
        if (DT >7) println("Test нажатия на $workTable")
        tools.qtipClickLast(workTable)
        //Thread.sleep(threadSleep)
        tools.xpathClickLast("//span[text()= '$workTable (SYSADMIN)']") // встать в дереве на Рабочий стол (SYSADMIN)
        assertTrue(tools.titleContain(workTable), "@@@@ После нажатия $workTable - нет заголовка вкладки $workTable @@")  // сбоит 1 раз на 100
        assertTrue(tools.qtipPressedLast(workTable), "@@@@ После нажатия $workTable - кнопка $workTable нет утоплена @@")
        // проверить что справа Рабочий стол (SYSADMIN)
        // Здесь проверка дерева и отображения
        tools.xpathClickLast("//span[contains(text(), 'Фильтры')]")
        Thread.sleep(threadSleep)
    }
    private fun clickFilter(nomberFilter: String, clickRef: String = "CMD_EDIT_ATTRS" ) {
        val tipWindow = if (clickRef == "CMD_DELETE_USER_QUERY")  "messagebox" else "tdmsEditObjectDialog"
        val titleWindow = if (clickRef == "CMD_DELETE_USER_QUERY") "TDM365" else
                if (clickRef == "CMD_EDIT_ATTRS") "Редактирование объекта" else "Просмотр свойств"
        if (DT > 6) println("Test нажатия на Фильтр $localDateNow действие: $clickRef")
        Thread.sleep(threadSleep)
        tools.xpathClickLast("//*[contains(text(), 'Фильтр $localDateNow')]")
        Thread.sleep(threadSleep)
        assertContains(tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")?.getAttribute("value") ?: "NONE", "Фильтр $localDateNow",false,
            "@@@@ Проверка наличия имени фильтра после создания не прошла @@")

        Thread.sleep(threadSleep)
        if (tools.referenceLast("CMD_DELETE_USER_QUERY") == null) {
            println("$$$$$$$$$$$$$$$ НЕТ ИНСТРУМЕНТОВ $localDateNow действие: $clickRef")
            screenShot()
        }

        Thread.sleep(threadSleep)
        tools.referenceClickLast(clickRef)
        Thread.sleep(threadSleep)
        assertTrue(tools.titleWait(tipWindow, titleWindow),
            "@@@@ После нажатия $clickRef - окно типа $tipWindow не имеет заголовка $tipWindow @@")
        Thread.sleep(threadSleep)
        val filterText = if (clickRef == "CMD_DELETE_USER_QUERY")
                tools.xpathLast("//div[contains(text(),'Вы действительно хотите удалить объект')]")?.text ?: "NONE"
            else
                tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")?.getAttribute("value") ?: "NONE"
         assertContains(filterText, "Фильтр $localDateNow", false,
             "@@@@ Нет правильного текста Фильтр $localDateNow на всплывающем окне $filterText @@")
    }
    @AfterEach
    fun afterEach(){
        if (DT >7) println("Вызов AfterEach FilterTest")
        //screenShot()
        tools.closeEsc5()
        Thread.sleep(threadSleep)
        driver.navigate().refresh()
    }
    fun screenShot(name: String = "image") {
        val scrFile = (driver as TakesScreenshot).getScreenshotAs<File>(OutputType.FILE)
        val sdf = SimpleDateFormat("ddMMyyyyhhmmss")
        copyFile(scrFile, File("./$name${sdf.format(Date())}.png"))
    }

     /**
     * Общий длинный тест пока : создание, редактирование, получение отчета, удаление
      */
    /**
     *  тест создание нового фильтра
     */
    @RepeatedTest(NN)
    @DisplayName("Создать фильтры")
    fun n01_CreateUserQuery(repetitionInfo: RepetitionInfo) {
        val nomberFilter = "${repetitionInfo.currentRepetition}"
        val createUser = "Создать фильтр"
        if (DT > 6) println("Test нажатия на $createUser")

        tools.referenceClickLast("CMD_CREATE_USER_QUERY")
        assertTrue(tools.titleWait("tdmsEditObjectDialog", "Редактирование объекта"),
            "@@@@ После нажатия $createUser - нет окна с заголовком Редактирование объекта @@")
        //  оставить ждать Омск
        // tools.referenceClickLast("tabbar-FORM_USER_QUERY")
        //  assertTrue(tools.referenceLast("tabbar-FORM_USER_QUERY")?.getAttribute("aria-selected") == "true")
        assertTrue(tools.referenceWaitText("T_ATTR_USER_QUERY_NAME", "Наименование фильтра"),
            "@@@@ На форме фильтра при $createUser - не нашлось текста Наименование фильтра @@")
        tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")  // Наименование фильтра
            ?.sendKeys(" $localDateNow")
        Thread.sleep(threadSleep)
        // Проверить что в поле стоит дата, если нет, то Скрин
        val ATTR_USER_QUERY_NAME = tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")  // Наименование фильтра
            ?.getAttribute("value") ?: "NONE"
         if (ATTR_USER_QUERY_NAME.contains(" $localDateNow").not()){
             if (DT > 0) println("&&&&&&&&&&01&&&&&&&&&&&& Название Фильтра не прописано: $ATTR_USER_QUERY_NAME")
             screenShot()
         }
        tools.clickOK()
        if (DT > 6) println("Конец Test нажатия на $createUser")
    }
    /**
     *  тест создание нового фильтра
     */
    @RepeatedTest(NN)
    //@Disabled
    @DisplayName("Посмотреть фильтр")
    fun n02_ViewUserQuery(repetitionInfo: RepetitionInfo) {
        val nomberFilter = "${repetitionInfo.currentRepetition}"
        val viewUser = "Посмотреть фильтр"
        if (DT > 6) println("Test посмотреть на $viewUser")
        // проверка что фильтр создан, если нет
        workTable()
        clickFilter(nomberFilter, "CMD_VIEW_ONLY")

        // Проверить что в поле стоит дата, если нет, то Скрин
        val ATTR_USER_QUERY_NAME = tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")  // Наименование фильтра
            ?.getAttribute("value") ?: "NONE"
        if (ATTR_USER_QUERY_NAME.contains(" $localDateNow").not()){
            if (DT > 0) println("&&&&&&&&&02&&&&&&&&&&&&& Название Фильтра не прописано: $ATTR_USER_QUERY_NAME")
            screenShot()
        }

        tools.clickOK("Закрыть")
        //tools.xpathClickLast("//*[contains(text(), 'Фильтр $localDateNow')]")
        if (DT > 6) println("Конец Test посмотреть на $viewUser")

    }
        /**
         *  тест заполнение и сохранение фильтра
         */
    @RepeatedTest(NN)
    //@Disabled
    @DisplayName("Заполнение текстовых полей фильтра")
    fun n03_fillingFilterTest(repetitionInfo: RepetitionInfo) {
            workTable()
            val nomberFilter = "${repetitionInfo.currentRepetition}"
            Thread.sleep(threadSleep)
            val fillingUser = "Заполнение текстовых полей фильтра"
            if (DT > 6) println("Test нажатия на $fillingUser")

            clickFilter(nomberFilter)  // встать на фильтр

            val ATTR_USER_QUERY_NAME = tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")
            ATTR_USER_QUERY_NAME?.sendKeys(" #")  // Наименование фильтра
            assertContains(ATTR_USER_QUERY_NAME?.getAttribute("value") ?: "NONE", "#",false,
                "@@@@ В Наименовании фильтра не прописалось # при редактировании @@")

            Thread.sleep(threadSleep)
            val ATTR_QUERY_TechDoc_Num = tools.xpathLast("//*[@data-reference='ATTR_QUERY_TechDoc_Num']/descendant::input")
            ATTR_QUERY_TechDoc_Num?.sendKeys("Обозначение $localDateNow")  // Обозначение
            Thread.sleep(threadSleep)
            assertContains(ATTR_QUERY_TechDoc_Num?.getAttribute("value") ?: "NONE", "Обозначение", false,
                "@@@@ В Обозначение фильтра не прописалось Обозначение при редактировании @@")

            Thread.sleep(threadSleep)
            val ATTR_QUERY_TechDoc_RevNum = tools.xpathLast("//*[@data-reference='ATTR_QUERY_TechDoc_RevNum']/descendant::input")
            ATTR_QUERY_TechDoc_RevNum ?.sendKeys("77") // Изм. №
            assertContains(ATTR_QUERY_TechDoc_RevNum?.getAttribute("value") ?: "NONE", "77", false,
                "@@@@ В Изм. № фильтра не прописалось 77 при редактировании @@")

            Thread.sleep(threadSleep)
            val ATTR_QUERY_TechDoc_Name = tools.xpathLast("//*[@data-reference='ATTR_QUERY_TechDoc_Name']/descendant::input")
            ATTR_QUERY_TechDoc_Name?.sendKeys("Наименование $localDateNow")  // Наименование
            assertContains(ATTR_QUERY_TechDoc_Name?.getAttribute("value") ?: "NONE", "Наименование", false,
                "@@@@ В Наименование фильтра не прописалось Наименование при редактировании @@")

            Thread.sleep(threadSleep)
            val ATTR_DESCRIPTION = tools.xpathLast("//*[@data-reference='ATTR_DESCRIPTION']/descendant::textarea")
            ATTR_DESCRIPTION ?.sendKeys("Описание $localDateNow") // Описание
            assertContains(ATTR_DESCRIPTION?.getAttribute("value") ?: "NONE", "Описание", false,
                "@@@@ В Описание фильтра не прописалось Описание при редактировании @@")
            Thread.sleep(threadSleep)

            tools.clickOK()
            if (DT > 6) println("Конец Test нажатия на $fillingUser")
        }

    /**
         *  тест редактирование фильтра
         */
        @RepeatedTest(NN)
        //@Disabled
        @DisplayName("Заполнение ссылочных полей фильтра")
        fun n04_EditFilterTest(repetitionInfo: RepetitionInfo) {
            workTable()
            val nomberFilter = "${repetitionInfo.currentRepetition}"
            val editFilter = "Заполнение ссылочных полей фильтра"
        if (DT > 6) println("Test нажатия на $editFilter")

        clickFilter(nomberFilter)  // встать на фильтр

            val description =
                tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")  // Описание
            assertTrue(description?.getAttribute("value")!!.contains("Фильтр $localDateNow"),
                "@@@@ В Название фильтра нет названия Фильтр $localDateNow @@")
            description.sendKeys(" @")
            assertTrue(description.getAttribute("value").contains("@"),
                "@@@@ В Наименовании фильтра не прописалось @ при редактировании @@")

            val BUTTON_OBJDEV_SEL = {   // Объект разработки
                tools.referenceClickLast("BUTTON_OBJDEV_SEL")
                assertTrue(tools.titleWait("tdmsSelectObjectGridDialog", "Выбор объекта структуры"),
                    "@@@@ карандашик BUTTON_OBJDEV_SEL на поле Объект разработки : нет справочника с заголовком Выбор объекта структуры @@")

                // 0203 / 051-1007345 Расширение ЕСГ для обеспечения подачи газа в газопровод «Южный поток».1-й этап (Западный коридор), для обеспечения подачи газа в объеме 31,5 млрд. м3/
                tools.xpathLast("//*[contains(text(),'Расширение ЕСГ')]/ancestor::td")?.click()

                Thread.sleep(threadSleep)
                tools.clickOK("Ок")
            }
            BUTTON_OBJDEV_SEL()
            Thread.sleep(threadSleep)
            val attrObjectDev = tools.xpathLast("//*[@data-reference='ATTR_OBJECT_DEV']/descendant::input")
            val getFerma = attrObjectDev?.getAttribute("value")
            assertContains(attrObjectDev?.getAttribute("value") ?: "NONE", "Расширение ЕСГ",
                false, "@@@@ карандашик BUTTON_OBJDEV_SEL : После выбора в поле фильтра объекта разработки из справочника в поле фильтра Объект разработки - пусто, а должно стоять Ферма_омшанник @@")

            tools.referenceClickLast("BUTTON_OBJDEV_ERASE")
            Thread.sleep(threadSleep)
            assertTrue(((attrObjectDev?.getAttribute("value") ?: "NONE").length) == 0,
                "@@@@ крестик BUTTON_OBJDEV_ERASE(объекта разработки) : После удаления поля фильтра поле не пусто, а должно быть пусто @@")

            BUTTON_OBJDEV_SEL()  // Всавляем еще раз омшанник

            val BUTTON_PROJECT_SEL = {      // Проект
                tools.referenceClickLast("BUTTON_PROJECT_SEL")
                assertTrue(tools.titleWait("tdmsSelectObjectGridDialog", "Выбор проекта"),
                    "@@@@ карандашик BUTTON_PROJECT_SEL на поле Проект : нет справочника с заголовком Выбор проекта @@")
        // 0203.001.001 / 0203.001.001 1-Й ЭТАП (ВОСТОЧНЫЙ КОРИДОР), ДЛЯ ОБЕСПЕЧЕНИЯ ПОДАЧИ ГАЗА В ОБЪЕМЕ ДО 63 МЛРД.М3/ГОД Этап 2.1. Линейная часть. Участок «Починки-Анапа», км 0 –км 347,5 (км 0 – км 181, км 181 – км 295,7, км 295,7 – км 347,5)
                tools.xpathLast("//*[contains(text(),'1-Й ЭТАП (ВОСТОЧНЫЙ КОРИДОР)')]/ancestor::td")
                    ?.click()                   //tr  tbody  table

                Thread.sleep(threadSleep)
                tools.clickOK("Ок")
            }
            BUTTON_PROJECT_SEL()
            Thread.sleep(threadSleep)
            val ATTR_RefToProject = tools.xpathLast("//*[@data-reference='ATTR_RefToProject']/descendant::input")
            assertContains(
                ATTR_RefToProject?.getAttribute("value") ?: "NONE",
                "1-Й ЭТАП (ВОСТОЧНЫЙ КОРИДОР)", false,
                "@@@@ карандашик BUTTON_PROJECT_SEL : После выбора в поле Проекта разработки из справочника в поле фильтра Проект - пусто, а должно стоять Название проекта из справочника @@"
            )
            tools.referenceClickLast("BUTTON_ERASE_PROJECT")
            Thread.sleep(threadSleep)
            assertTrue(((ATTR_RefToProject?.getAttribute("value") ?: "NONE").length) == 0,
                "@@@@ крестик BUTTON_ERASE_PROJECT(Проект) : После удаления поля фильтра поле не пусто, а должно быть пусто @@")
            BUTTON_PROJECT_SEL()  // Всавляем еще раз

            val BUTTON_TYPE_DOC = {     // Тип документации из справочника Типы технической документации
                tools.referenceClickLast("BUTTON_TYPE_DOC")
                assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Типы технической документации"),
                    "@@@@ карандашик BUTTON_TYPE_DOC на поле Тип документации : нет справочника с заголовком Типы технической документации @@")
                //   Разделы документации / Марки РД - ни один нельзя присвоить надо раскрывать слева дерево до последнего
                // Только SRV1: слева Марки РД - Справа АР Архитектурные решения (галочку) и ОК

               // tools.xpathLast("//a[contains(text(),'Марки РД')]/ancestor::tr")?.click()
                // table body tdr td div div+div(>)+img+span
                tools.xpathLast("//span[contains(text(),'Марки РД')]/ancestor::tr")?.click()
                Thread.sleep(threadSleep)
                // Ошибка Firefox
                // Element: [[FirefoDriver: firefo on WINDOWS (1f8f6504-78cc-4d5e-9c4e-1642e0a2cf62)] -> xpath: //html/body/descendant::a[contains(text(),'АР Архитектурные решения')]/ancestor::tr]
                // org.openqa.selenium.ElementNotInteractableException: Element <tr class="  x-grid-row"> could not be scrolled into view
                //tools.xpathLast("//a[contains(text(),'АР Архитектурные решения')]/ancestor::tr")?.click()
                tools.xpathLast("//a[contains(text(),'АР Архитектурные решения')]/ancestor::td/preceding-sibling::td")?.click()
                Thread.sleep(threadSleep)
                tools.clickOK("Ок")
            }

            BUTTON_TYPE_DOC()
            Thread.sleep(threadSleep)
            val ATTR_TechDoc_Sort = tools.xpathLast("//*[@data-reference='ATTR_TechDoc_Sort']/descendant::input")

            assertContains(ATTR_TechDoc_Sort?.getAttribute("value") ?: "NONE", "АР Архитектурные решения",false,
                "@@@@ поле фильтра Тип документации не содержит значение АР Архитектурные решения после выбора @@")
            tools.referenceClickLast("BUTTON_ERASE_TTD")
            Thread.sleep(threadSleep)
            assertTrue(((ATTR_TechDoc_Sort?.getAttribute("value") ?: "NONE").length) == 0,
                "@@@@ крестик BUTTON_ERASE_TTD(Тип документации) : После удаления поля фильтра поле не пусто, а должно быть пусто @@")
            BUTTON_TYPE_DOC()  // Всавляем еще раз

            val BUTTON_OBJ_STR = {      // Объект структуры
                tools.referenceClickLast("BUTTON_OBJ_STR")
                assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Объекты структуры"),
                    "@@@@ карандашик BUTTON_OBJ_STR на поле Объект структуры : нет справочника с заголовком Объекты структуры @@")
                // Для всех 0203 / 051-1007345 Структура объекта "Расширение ЕСГ для обеспечения подачи газа в газопровод «Южный поток».1-й этап (Западный коридор), для обеспечения подачи газа в объеме 31,5 млрд. м3/"
                //  Слева 051-1007345 Структура объекта "Расшир..." тогда 0203.КТО.001 Комплекс термического обезвреживания отходов  (справа)
                //  или слева тогда 0203.КТО.001.4701- 4799.007 Линии электропередач воздушные и электротехнические коммуникации
                //tools.xpathLast("//a[contains(text(),'Структура объекта')]/ancestor::tr")?.click()
                // Firefox сбоит Element <tr class="x-grid-tree-node-expanded  x-grid-row"> could not be scrolled into view
                //tools.xpathLast("//span[contains(text(),'Расширение ЕСГ')]/ancestor::tr")?.click()
                tools.xpathLast("//span[contains(text(),'Расширение ЕСГ')]/preceding-sibling::img")?.click()
                Thread.sleep(threadSleep)
                tools.clickOK("Ок")
            }
            BUTTON_OBJ_STR()
            Thread.sleep(threadSleep)
            val ATTR_OCC = tools.xpathLast("//*[@data-reference='ATTR_OCC']/descendant::input")
            assertContains(ATTR_OCC?.getAttribute("value") ?: "NONE", "Расширение ЕСГ",false,
                "@@@@ поле фильтра Объект структуры не содержит значение Расширение ЕСГ @@")
            tools.referenceClickLast("BUTTON_ERASE_OS")
            Thread.sleep(threadSleep)
            assertTrue(((ATTR_OCC?.getAttribute("value") ?: "NONE").length) == 0,
                "@@@@ крестик BUTTON_ERASE_OS(Объект структуры) : После удаления поля фильтра поле не пусто, а должно быть пусто @@")
            BUTTON_OBJ_STR()  // Всавляем еще раз

            val BUTTON_ORG_SEL = {      // Организация  ГПП  Газпромпроектирование для всех
                tools.referenceClickLast("BUTTON_ORG_SEL")
                assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Организации/Подразделения"),
                    "@@@@ карандашик BUTTON_ORG_SEL на поле Организация/Подразд. : нет справочника с заголовком Организации/Подразделения @@")
                //FireFox не работает Element <tr class="  x-grid-row"> could not be scrolled into view  td входит в ссылку
                //tools.xpathLast("//a[contains(text(),'Газпромпроектирование')]/ancestor::tr")?.click()
                tools.xpathLast("//a[contains(text(),'Газпромпроектирование')]/ancestor::td/preceding-sibling::td")?.click()  // на квадратик слева
                Thread.sleep(threadSleep)
                tools.clickOK("Ок")
            }
            BUTTON_ORG_SEL()
            Thread.sleep(threadSleep *3)
            val ATTR_ORGANIZATION_LINK =
                tools.xpathLast("//*[@data-reference='ATTR_ORGANIZATION_LINK']/descendant::input")
            assertContains(ATTR_ORGANIZATION_LINK?.getAttribute("value") ?: "NONE", "Газпромпроектирование",false,
                "@@@@ поле фильтра Организация/Подразд. не содержит значение Газпромпроектирование после выбора @@")
            tools.referenceClickLast("BUTTON_ERASE_ORG")
            Thread.sleep(threadSleep)
            assertTrue(((ATTR_ORGANIZATION_LINK?.getAttribute("value") ?: "NONE").length) == 0,
                "@@@@ крестик BUTTON_ERASE_ORG(Организация/Подразд.) : После удаления поля фильтра поле не пусто, а должно быть пусто @@")
            BUTTON_ORG_SEL()  // Всавляем еще раз

            tools.clickOK()
        if (DT > 6) println("Конец Test нажатия на $editFilter")
        }

    @RepeatedTest(NN)
    //@Disabled
    @DisplayName("Заполнение дат и выпадающих фильтра")
    fun n06_EditFilterTest(repetitionInfo: RepetitionInfo) {
        workTable()
        val nomberFilter = "${repetitionInfo.currentRepetition}"
        val editFilter = "Заполнение дат и выпадающих фильтра"
        if (DT > 6) println("Test нажатия на $editFilter")

        clickFilter(nomberFilter)  // встать на фильтр

        val description =
            tools.xpathLast("//*[@data-reference='ATTR_USER_QUERY_NAME']/descendant::input")  // Описание
        assertTrue(description?.getAttribute("value")!!.contains("Фильтр $localDateNow"),
            "@@@@ Нет в списке фильтра с именем Фильтр $localDateNow @@")
        description.sendKeys(" @@")
        assertTrue(description.getAttribute("value").contains("@@"),
            "@@@@ В Наименовании фильтра не прописалось @@ при редактировании @@")



            tools.xpathLast("// *[@data-reference='ATTR_DATE_START']/descendant::input")
               // ?.sendKeys(localDateNow)
                ?.sendKeys(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now()))
            tools.xpathLast("// *[@data-reference='ATTR_DATE_END']/descendant::input")
                //?.sendKeys(localDateNow)
                ?.sendKeys(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now()))

            tools.xpathLast("// *[@data-reference='ATTR_DATE_RELEASE_DOCUMENT']/descendant::div[contains(@id, 'picker')]")?.click()
            Thread.sleep(threadSleep)
            tools.xpathLast("//span[text()='Сегодня']")?.click()
            Thread.sleep(threadSleep)
            val ATTR_DATE_RELEASE_DOCUMENT = tools.xpathLast("//*[@data-reference='ATTR_DATE_RELEASE_DOCUMENT']/descendant::input")
            val ATTR_DATE = ATTR_DATE_RELEASE_DOCUMENT?.getAttribute("value")
            assertEquals(ATTR_DATE_RELEASE_DOCUMENT?.getAttribute("value") ?:"NONE",
                DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now()),
                "@@@@ В Дата не стоит сегодняшняя дата после занесения из выбора Сегодня $ATTR_DATE @@")

            tools.xpathLast("// *[@data-reference='ATTR_USER_QUERY_STATUS']/descendant::input")
                ?.sendKeys("В разработке")
            tools.xpathLast("// *[@data-reference='ATTR_DOC_AUTHOR']/descendant::input")
                ?.sendKeys("SYSADMIN")

            // Только на SRV1 после выкачки-закачки схемы
            //tools. referenceClickLast("ATTR_RESPONSIBLE_USER")   ОШИБОЧНОЕ ПОЛЕ Ответственный
            //tools.xpathLast("// *[@data-reference='ATTR_RESPONSIBLE_USER']/descendant::input")
            //    ?.sendKeys("SYSADMIN")
            // Можно очистить BUTTON_ERASE_RESP_USER ??

            tools. qtipClickLast("Выбрать объект")
            assertTrue(tools.titleWait("tdmsSelectObjectDialog", "Выбор объектов"),
                "@@@@ стрелочка ATTR_RESPONSIBLE_USER (Ответственный) на поле Ответственный : нет справочника с заголовком Выбор объектов @@")
        // Ошибка Firefox  Element <tr class="  x-grid-row"> could not be scrolled into view
            //tools.xpathLast("//a[contains(text(),'SYSADMIN')]/ancestor::tr")?.click()
            tools.xpathLast("//a[contains(text(),'SYSADMIN')]/ancestor::td/preceding-sibling::td")?.click()
            Thread.sleep(threadSleep)
            tools.clickOK("Ок")

        // Надо поставить проверку всех заполненных полей

            tools.clickOK()
        if (DT > 6) println("Конец Test нажатия на $editFilter")
    }
    @RepeatedTest(NN)
    @Disabled
    @DisplayName("Очистка фильтров")
    fun n08_clearUserQuery(repetitionInfo: RepetitionInfo) {
        workTable()
        val nomberFilter = "${repetitionInfo.currentRepetition}"
        val clearFilter = "Очистка фильтров"
        if (DT > 6) println("Test нажатия на $clearFilter")

        clickFilter(nomberFilter)  // встать на фильтр
    }

    /**
     *  тест Удаление фильтра
     */
   @RepeatedTest(NN)
   @DisplayName("Удаление фильтров")
   fun n11_DeleteUserQuery(repetitionInfo: RepetitionInfo) {
        workTable()

       val nomberFilter = "${repetitionInfo.currentRepetition}"
       val deleteFilter = "Удалить фильтр"
       if (DT > 6) println("Test нажатия на $deleteFilter")

        clickFilter(nomberFilter, "CMD_DELETE_USER_QUERY")  // встать на фильтр

       // Вы действительно хотите удалить объект "(Все проекты) Фильтр" из системы?
       tools.clickOK("Да")
        if (DT > 6) println("Конец Test нажатия на $deleteFilter")

   }
}

