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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertContains


/**
Проверка Администрирование групп:
Добавление/удаление пользователя:
- TDMS Web> Администрирование групп > Окно Редактирование групп:
В левом списке «Группы пользователей» выделить «Все пользователи»
Справа кнопка «Создать пользователя» > Окно «Редактирование пользователя»
Описание: Фамилия 1.2.11-6.1.123 25042022
Логин: ФИО
Чекбокс: Разрешить вход в TDMS
Имя: Имя
Отчество: Отчество
Фамилия: Фамилия
Телефон: 1
E-mail: ya@ya.ru
«Добавить профиль пользователю» > окно «Выбор профиля» > выделить «Приемка документации»> OK
check, что в списке «Профиль» есть строка «Приемка документации»> ERR? >
Два раза : сначала отмена и check  затем заполнить еще раз и >OK
Про скролить правый список до конца > check что последняя строка «Фамилия»>  ERR?
Редактирование пользователя:
В правом списке выделить Фамилия 1.2.9-6.1.109 22032022
Справа кнопка Редактировать пользователя
Сверить все 7 полей с введенными ранее
Сверить Чек-бокс Разрешить вход в TDMS
Сверить наличие профиля Приемка документации
Сверить наличие профиля Default profile
Добавить к описанию @ > Отмена
Сверить отсутствие @ в правом списке у пользователя  Фамилия 1.2.9-6.1.109 22032022
В правом списке выделить Фамилия 1.2.9-6.1.109 22032022
Справа кнопка Редактировать пользователя
Добавить к описанию @ > ОК
Сверить Присутствие @ в правом списке у пользователя  Фамилия 1.2.9-6.1.109 22032022 @
ERR? – Проверить отсутствие ошибок в консоли сервера (если есть – в лог)

-	Удаление пользователя:
Выделить в правом списке  «Все пользователи» последнего Фамилия
Кнопка «Удалить пользователя из системы» > popup TDMS «Удалить пользователя "Фамилия "?»
Check что Фамилия == Фамилия > ERR? > Нет
Про скролить правый список до конца > check что последняя строка «Фамилия»>  ERR?
Выделить в правом списке  «Все пользователи» последнего Фамилия
Кнопка «Удалить пользователя из системы» > popup TDMS «Удалить пользователя "Фамилия "?»
Check что Фамилия == Фамилия > ERR? > Да
check что в списке отсутствует строка «Фамилия»>  ERR? > OK
Добавление/удаление группы:
- TDMS Web> Администрирование групп > Окно Редактирование групп:
Слева кнопка «Создать группу» > Окно «Создание новой группы»
Введите название новой группы: Новая TDM > ERR? > Отмена
check что в левом списке отсутствует строка «Новая TDM»>  ERR?
Слева кнопка «Создать группу» > Окно «Создание новой группы»
Введите название новой группы: Новая TDM > ERR? > ОK > Popup Группа "Новая TDM " создана
check что в левом списке присутствует последняя строка «Новая TDM» и одна>  ERR?
Выделить в левом списке «Группы пользователей» последнюю Новая TDM
Кнопка «Удалить группу» > popup TDMS «Удалить группу "Новая TDM"? »
Check что "Новая TDM"?  == "Новая TDM"?  > ERR? > Нет
Выделить в левом списке «Группы пользователей» последнюю Новая TDM
Кнопка «Удалить группу» > popup TDMS «Удалить группу "Новая TDM"? »
Check что "Новая TDM"?  == "Новая TDM"?  > ERR? > Да
check что в левом списке отсутствует строка «Новая TDM»>  ERR?
	OK  закрываем окно Редактирование групп
Проверки журнала ошибок итд.

Можно выйти из Логина и/или закрыть/открыть Browser
PS (что делает Defult profile - неизвестно) польз удалить - удалился (нет-БАГ)
PPS Можно добавить тест: не удалять созданного пользователя,
в созданную группу добавить созданного пользователя
Затем удалить созданную группу
Затем удалить созданного пользователя
Можно еще проверить работоспособность остальных кнопок формы 2 шт.
 */
@DisplayName("Administration users group Test")
@TestMethodOrder(MethodOrderer.MethodName::class)
class AdminUser {
    companion object {
        // задержки : 0- все сбоят 100 - 1 шт 1000 - 0 шт
        private val threadSleep = TestsProperties.threadSleepNomber     // задержки где они есть
        private val DT: Int = TestsProperties.debugPrintNomber          // глубина отладочной информации 0 - ничего не печатать, 9 - все
        //private val NN:Int = repeateTestsNomber                       // количество повторений тестов
        private const val NN:Int = 1                                    // количество повторений тестов

        private val localDateNow: String = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss").format(LocalDateTime.now())

    // переменная для драйвера
    lateinit var driver: WebDriver
    // объявления переменных на созданные ранее классы-страницы
    lateinit var tools: Tools

    /**
     * осуществление первоначальной настройки
     * Предупреждение: Не смешивайте неявные и явные ожидания.
     * Это может привести к непредсказуемому времени ожидания.
     * Например, установка неявного ожидания 10 секунд и явного ожидания 15 секунд
     * может привести к таймауту через 20 секунд.
     */
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
        if (DT >7) println("Вызов BeforeAll AdminUserTest")
            // создание экземпляра драйвера (т.к. он объявлен в качестве переменной):
        driver = startDriver()

            // Создаем экземпляры классов созданных ранее страниц, и присвоим ссылки на них.
            // В качестве параметра указываем созданный перед этим объект driver,
            tools = Tools(driver)

        val loginpage = TestsProperties.loginpage
        if (DT > 8) println("Открытие страницы $loginpage")
        val login = TestsProperties.login
        val password = TestsProperties.password
        if (DT > 8) println("login= $login   password= $password")
        driver.get(loginpage)
        assertTrue(driver.title == "Tdms","@@@@ Не открылась страница $loginpage - нет заголовка вкладки Tdms @@")
        Login(driver).loginIn(login, password)
        if (DT >7) println("Конец Вызов BeforeAll AdminUserTest")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            if (DT >7) println("Вызов AfterAll AdminUserTest")
            tools.closeEsc5()
            Login(driver).loginOut()
            driver.quit() //  закрытия окна браузера

        }
    }   // конец companion object

    @BeforeEach
    fun beforeEach(){
        // data-reference="STATIC1" = Группы пользователей
        // data-reference="GROUP_NAME" - Выделенная группа
        // data-reference="GRID_GROUPS" - левый список групп
        // data-reference="GRID_USERS" - правый список пользователей
        if (DT >7) println("Начало BeforeEach AdminUserTest")
        val mainMenu = "Объекты"
        if (DT >8) println("Test нажатия на $mainMenu TDMS Web")
        tools.qtipClickLast(mainMenu)
        assertTrue(tools.titleContain("TDM365"), "@@@@ Нажали на $mainMenu - нет заголовка вкладки TDM365 @@")
        assertTrue(tools.qtipPressedLast("Объекты"),"@@@@ После нажатия $mainMenu - кнопка Объекты нет утоплена @@")

        val adminUser = "Администрирование групп"
        if (DT >8) println("Test нажатия на $adminUser")
        tools.qtipClickLast(adminUser)
        val nomberWindowAdminUser = tools.nomberTitle("window", "Редактирование групп")
        assertTrue(tools.titleWait("window", "Редактирование групп"),
            "@@@@ После нажатия $adminUser - нет заголовка окна Редактирование групп @@")
        assertTrue(tools.referenceWaitText("STATIC1", "Группы пользователей"),
            "@@@@ В окне Редактирование групп нет обязательного заголовка списка Группы пользователей @@")

       // data-reference="GRID_GROUPS"
        val headTeg = tools.idRef("GRID_GROUPS")
        val allUsers = "Все пользователи"
        if (DT >8) println("Test нажатия на $allUsers")
        // //div[text()= '$allUsers']   //*[@id='$headTeg']/descendant::div[text()= '$allUsers']
        tools.xpathLast("//*[@id='$headTeg']/descendant::div[text()= '$allUsers']")?.click()

        assertTrue(tools.referenceWaitText("GROUP_NAME", allUsers),
            "@@@@ В окне Редактирование групп после выделения $adminUser нет обязательного заголовка списка $adminUser @@")

        //tools.clickOK()

        if (DT >7) println("Конец BeforeEach AdminUserTest")
    }
    @AfterEach
    fun afterEach(){
        if (DT >7) println("Вызов AfterEach AdminUserTest")
        //screenShot()
        tools.closeEsc5()
        // Thread.sleep(threadSleep)
        //driver.navigate().refresh()
        if (DT >7) println("Конец Вызов AfterEach AdminUserTest")
    }
    fun screenShot(name: String = "image") {
        val scrFile = (driver as TakesScreenshot).getScreenshotAs<File>(OutputType.FILE)
        val localDateNow = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm").format(LocalDateTime.now())
        copyFile(scrFile, File("./$name$localDateNow.png"))
        if (DT >5) println("Скрин сохранен ")
    }
    private fun openAllUsers(click: String) {
        val adminUser = "Администрирование групп"
        val mainMenu = "Объекты"
        if (DT > 8) println("Test openAllUsers")
        /*
        tools.qtipClickLast(mainMenu)
        assertTrue(tools.titleContain("TDM365"), "@@@@ После нажатия $mainMenu - нет заголовка вкладки TDM365 @@")
        assertTrue(tools.qtipPressedLast("Объекты"), "@@@@ После нажатия $mainMenu - кнопка Объекты нет утоплена @@")
        if (DT > 8) println("Test нажатия на $adminUser")
        tools.qtipClickLast(adminUser)
        */
        assertTrue(
            tools.titleWait("window", "Редактирование групп"),
            "@@@@ После нажатия $adminUser - нет заголовка окна Редактирование групп @@")
        assertTrue(
            tools.referenceWaitText("STATIC1", "Группы пользователей"),
            "@@@@ В окне Редактирование групп нет обязательного заголовка списка Группы пользователей @@")

        // data-reference="GRID_GROUPS"
        val headTeg = tools.idRef("GRID_GROUPS")
        val allUsers = "Все пользователи"
        if (DT > 8) println("Test нажатия на $allUsers")
        // //div[text()= '$allUsers']   //*[@id='$headTeg']/descendant::div[text()= '$allUsers']
        tools.xpathLast("//*[@id='$headTeg']/descendant::div[text()= '$allUsers']")?.click()
        assertTrue(
            tools.referenceWaitText("GROUP_NAME", allUsers),
            "@@@@ В окне Редактирование групп после выделения $adminUser нет обязательного заголовка списка $adminUser @@")
        if (DT > 7) println("Открыли всех пользователей")

        if (DT >8) println("Test нажатия на $click")
        if (DT >8) println("Редактирование $localDateNow")
        if ((click == "BUTTON_USER_EDIT")  or (click == "BUTTON_USER_DELETE"))
            tools.xpathLast("//div[contains(text(), '$localDateNow')]")?.click()

        //  Редактировать пользователя data-reference="BUTTON_USER_EDIT"
        if ( (click == "NONE").not())
            tools. referenceClickLast(click)
        Thread.sleep(threadSleep)
        if ((click == "BUTTON_USER_CREATE") or (click == "BUTTON_USER_EDIT"))
            assertTrue(
                tools.titleWait("window", "Редактирование пользователя"),
                "@@@@ После нажатия $click - нет заголовка окна Редактирование пользователя @@")
        if (DT > 8) println("Конец Test openAllUsers")
    }
     /**
     * Общий длинный тест пока : создание, редактирование, добавление роли, удаление
      */
    /**
     *  тест создание нового пользователя
     */
    @RepeatedTest(NN)
    @DisplayName("Создать пользователя")
    fun n04_createUserTest(repetitionInfo: RepetitionInfo) {
        val createUser = "Создать пользователя"
        if (DT > 6) println("Test нажатия на $createUser")

        openAllUsers("BUTTON_USER_CREATE")

        Thread.sleep(threadSleep)
        if (DT >8) println("Ура заработало = ${tools.nomberTitle("window", "Редактирование пользователя")}")
        assertTrue(tools.titleWait("window", "Редактирование пользователя"),
            "@@@@ После нажатия BUTTON_USER_CREATE - нет заголовка окна Редактирование пользователя @@")
        tools.xpathLast("//*[@data-reference='ATTR_DESCRIPTION']/descendant::input")  // Описание
            ?.sendKeys("Тестовый $localDateNow")
        tools.xpathLast("//*[@data-reference='ATTR_LOGIN']/descendant::input")  // Логин
            ?.sendKeys("Логин $localDateNow")

        tools.clickOK()
        tools.clickOK()
        if (DT > 6) println("Конец Test нажатия на $createUser")
    }

        /**
         *  тест заполнение и сохранение нового пользователя
         */
    @Test
    @DisplayName("Заполнение нового пользователя")
    fun n05_fillingUserTest() {

            //val testFIO = "Тестовая Фамилия $localDateNow"
            val fillingUser = "Редактирование пользователя"
            if (DT > 6) println("Test нажатия на $fillingUser")

            openAllUsers("BUTTON_USER_EDIT")

            assertTrue(tools.titleWait("window", fillingUser),
                "@@@@ После нажатия BUTTON_USER_EDIT - нет заголовка окна Редактирование пользователя @@")
            // //html/body/descendant::div[@data-reference]
            tools.xpathLast("//*[@data-reference='ATTR_DESCRIPTION']/descendant::input")  // Описание
                ?.sendKeys(" #")
            tools.xpathLast("//*[@data-reference='ATTR_LOGIN']/descendant::input")  // Логин
                ?.sendKeys(" #")

            tools.xpathLast("//*[@data-reference='ATTR_TDMS_LOGIN_ENABLE']/descendant::input")  // Разрешить вход в TDMS
                ?.click()
            tools.xpathLast("//*[@data-reference='ATTR_USER_NAME']/descendant::input")  // Имя
                ?.sendKeys("Имя")
            tools.xpathLast("//*[@data-reference='ATTR_USER_MIDDLE_NAME']/descendant::input")  // Отчество
                ?.sendKeys("Отчество")
            tools.xpathLast("//*[@data-reference='ATTR_USER_LAST_NAME']/descendant::input")  // Фамилия
                ?.sendKeys("Фамилия")
            tools.xpathLast("//*[@data-reference='ATTR_USER_PHONE']/descendant::input")  // Телефон
                ?.sendKeys("9291234567")
            tools.xpathLast("//*[@data-reference='ATTR_USER_EMAIL']/descendant::input")  // E-mail
                ?.sendKeys("ya@ya")
            tools.clickOK()
            tools.clickOK()
            if (DT > 6) println("Конец Test нажатия на $fillingUser")
        }
        /**
         *  тест редактирование пользователя
         */
    /**
     *  тест заполнение и сохранение нового пользователя
     */
    @Test
    @DisplayName("Редактирование пользователя")
    fun n06_EditUserTest() {

        //val testFIO = "Тестовая Фамилия $localDateNow"
        val fillingUser = "Редактирование пользователя"
        if (DT > 6) println("Test нажатия на $fillingUser")

        openAllUsers("BUTTON_USER_EDIT")

        val editUser = "Редактировать пользователя"
        if (DT > 6) println("Test нажатия на $editUser")

        if (DT > 8) println("Редактирование $localDateNow")
        //tools.xpathLast("//div[contains(text(), '$localDateNow')]")?.click()

        //  Редактировать пользователя data-reference="BUTTON_USER_EDIT"
        //tools. referenceClickLast("BUTTON_USER_EDIT")
        //Thread.sleep(threadSleep)
        //assertTrue(tools.titleWait("window", "Редактирование пользователя"))

        val description = tools.xpathLast("//*[@data-reference='ATTR_DESCRIPTION']/descendant::input")  // Описание

        //.sendKeys("Тестовая Фамилия $localDateNow")
        assertTrue(description?.getAttribute("value") == "Тестовый $localDateNow #",
            "@@@@ Нет измененного описания с # @@")
        description?.sendKeys(" @")
        assertTrue(description?.getAttribute("value") == "Тестовый $localDateNow # @",
            "@@@@ Нет измененного описания с # и @ @@")

        tools.clickOK()
        tools.clickOK()
        if (DT > 6) println("Конец Test нажатия на $fillingUser")
    }
        /**
         *  тест добавление роли пользователю
         */
        @Test
        @DisplayName("Добавление роли пользователю")
        fun n07_AddRoleUserTest() {

            //val testFIO = "Тестовая Фамилия $localDateNow"
            val fillingUser = "Редактирование пользователя"
            if (DT > 6) println("Test n07_AddRoleUserTest нажатия на $fillingUser")

            openAllUsers("BUTTON_USER_EDIT")
            //  кнопка Добавить профиль data-reference="BUTTON_PROFILE_ADD"
            tools.referenceClickLast("BUTTON_PROFILE_ADD")
            //assertTrue(tools.selectedGridDialogTitleWait("Выбор профиля"))
            if (DT > 8) println("Ура заработало = ${tools.nomberTitle("tdmsSelectObjectGridDialog", "Выбор профиля")}")
            assertTrue(tools.titleWait("tdmsSelectObjectGridDialog", "Выбор профиля"),
                "@@@@ После нажатия BUTTON_PROFILE_ADD - нет заголовка окна Выбор профиля @@")

            val profileUser = "Руководитель"
            if (DT > 6) println("Test нажатия на $profileUser")
            //tools.idList()
            tools.xpathLast("//span[text()= '$profileUser']/ancestor::td")?.click()
            tools.clickOK()  // закрыть выбор профиля с выбором руководителя

            assertTrue(tools.titleWait("window", "Редактирование пользователя"),
                "@@@@ После выхода из редактирования - нет заголовка окна Редактирование групп @@")
            // проверка что есть профиль руководитель

            val description_new = tools.xpathLast("//*[@data-reference='ATTR_DESCRIPTION']/descendant::input")
            assertTrue(description_new?.getAttribute("value") == "Тестовый $localDateNow # @",
                "@@@@ После редактирования - нет пользователя Тестовый $localDateNow # @  @@")
            tools.clickOK()
            tools.clickOK()
            if (DT > 6) println("Конец Test n07_AddRoleUserTest нажатия на $fillingUser")
        }

        /**
         *  тест Создания Группы
         */

    @Test
    @DisplayName("Создание новой группы")
    fun n08_AddGroupUserTest() {

            val createGroup = "Создание новой группы"
            if (DT > 6) println("Test нажатия на $createGroup")
            //  Создания Группы data-reference="BUTTON_GROUP_CREATE"
            tools.referenceClickLast("BUTTON_GROUP_CREATE")
            Thread.sleep(threadSleep)
            if (DT > 8) println("Ура заработало = ${tools.nomberTitle("messagebox", "Создание новой группы")}")
            assertTrue(tools.titleWait("messagebox", "Создание новой группы"),
                "@@@@ После нажатия $createGroup - нет окна с заголовком Создание новой группы @@")
            val inputGpoup =
                tools.xpathLast("//div[text() = 'Введите название новой группы']/parent::*/descendant::input")
            // // *[@id="messagebox-1194-textfield-inputEl"] - можно получить из "messagebox", "Создание новой группы"
            inputGpoup?.sendKeys("Тестовая $localDateNow")
            tools.clickOK("OK")  // создать Тестовая
            if (DT > 8) println("Ура заработало = ${tools.nomberTitle("messagebox", "TDMS")}")
            Thread.sleep(threadSleep)
            assertTrue(tools.titleWait("messagebox", "TDMS"),
                "@@@@ При $createGroup - нет окна сообщения с заголовком TDMS (группа создана) @@")
            Thread.sleep(threadSleep)
            assertTrue(tools.titleWait("messagebox", "TDMS"),
                "@@@@ Повторная проверка $createGroup - нет окна сообщения с заголовком TDMS (группа создана) @@")
            tools.clickOK("OK")  // создана тестовая
            tools.clickOK("ОК")     // закрыть адимин
            if (DT > 6) println("Конец Test нажатия на $createGroup")
        }
        /**
         *  тест Удаление Группы
         */
        @Test
        @DisplayName("Удаление Группы")
        fun n09_DeleteGroupUserTest() {

            val deleteGroup = "Удаление группы"
            if (DT > 6) println("Test нажатия на $deleteGroup")
            // data-reference="GRID_GROUPS"   data-reference="GRID_USERS"
            tools.xpathLast("//div[contains(text(), 'Тестовая')]")?.click()
            //  Проверить что выделенная группа Тестовая data-reference="GROUP_NAME"
            tools.referenceClickLast("BUTTON_GROUP_DELETE")
            Thread.sleep(threadSleep)
            assertTrue(tools.titleWait("messagebox", "TDMS"),
                "@@@@ При $deleteGroup - нет окна сообщения с заголовком TDMS (удалить группу) @@")
            //val msgGpoup = tools.xpathLast("//div[text() = 'Удалить группу \"Тестовая\"?']")
            // // *[@id="messagebox-1194-textfield-inputEl"] - можно получить из "messagebox", "Создание новой группы"
            val nomberMessage = tools.nomberTitle("messagebox", "TDMS")
            // id="messagebox-1329-msg"
            assertContains(tools.byID("messagebox-$nomberMessage-msg")?.text?: "None", "Удалить группу",false,
                "@@@@ При $deleteGroup - нет в окне сообщения с заголовком TDMS Удалить группу @@")
            tools.clickOK("Да")  // удалить тестовая
            tools.clickOK("ОК")     // закрыть адимин
            if (DT > 6) println("Test нажатия на $deleteGroup")
        }

        /**
         *  тест удаление пользователя
         */
        @Test
        @DisplayName("удаление пользователя")
        fun n10_DeleteUserTest() {
            if (DT > 6) println("Test нажатия на Удаление на $localDateNow")
            openAllUsers("BUTTON_USER_DELETE")
            //assertTrue(tools.titleWait("window", "Редактирование групп"))
        //val testFIO = "Тестовая Фамилия"
            Thread.sleep(threadSleep)
            assertTrue(tools.titleWait("messagebox", "TDMS"),
                "@@@@ При BUTTON_USER_DELETE - нет окна подтверждения с заголовком TDMS (удалить пользователя) @@")
        //tools.xpathLast("//div[contains(text(), '$localDateNow')]")?.click()
        //tools.referenceClickLast("BUTTON_USER_DELETE")  // //  кнопка Удалить пользователя
            val nomberMessage = tools.nomberTitle("messagebox", "TDMS")
            // id="messagebox-1329-msg"
            assertContains(tools.byID("messagebox-$nomberMessage-msg")?.text?: "None", "Удалить пользователя",false,
                "@@@@ При BUTTON_USER_DELETE - нет в окне подтверждения с заголовком TDMS Удалить группу @@")

        tools.clickOK("Да")
        tools.clickOK("ОК")
            if (DT > 6) println("Конец Test нажатия на Удаление на $localDateNow")
    }
    @RepeatedTest(92)
    @Disabled
    @DisplayName("Удаление пользователей")
    fun n11_DeleteUserQuery(repetitionInfo: RepetitionInfo) {
        val user = "Тестовый "
        assertTrue(tools.titleWait("window", "Редактирование групп"),
            "@@@@ После нажатия Администрирование - нет заголовка окна Редактирование групп @@")
        //val testFIO = "Тестовая Фамилия"
        if (DT > 6) println("Удаление на $user")
        tools.xpathLast("//div[contains(text(), '$user')]")?.click()
        tools.referenceClickLast("BUTTON_USER_DELETE")  // //  кнопка Удалить пользователя
        tools.clickOK("Да")
        tools.clickOK("ОК")

    }
}

