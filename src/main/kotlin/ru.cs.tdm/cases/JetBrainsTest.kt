package ru.cs.tdm.cases
/**
 * Selenium - это система управления браузером  - открыть, локация, считать, нажать, вкладки и т.д.
 * jupiter - это система тестирования чего угодно старты тестов, ассерты, отчеты, сетки, и более сложные вещи
 * bonigarcia - левая приблуда, котоая подсовывает актуальный chrome driver
 */
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.WebDriverWait
import ru.cs.tdm.data.TestsProperties
import ru.cs.tdm.data.startDriver
import ru.cs.tdm.pages.JetBrainsPage
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Классический пример теста переданный JB и подшаманенный, т.к. был не рабочий
 */
@DisplayName("Class JetBrainsTest")
@TestMethodOrder(MethodOrderer.MethodName::class)
class JetBrainsTest {

    companion object {
        private lateinit var brainsPage: JetBrainsPage
        // задержки : 0- все сбоят 100 - 1 шт 1000 - 0 шт
        private val threadSleep = TestsProperties.threadSleepNomber     // задержки где они есть
        private val DT: Int = TestsProperties.debugPrintNomber          // глубина отладочной информации 0 - ничего не печатать, 9 - все
        //private val NN:Int = repeateTestsNomber                       // количество повторений тестов
        private const val NN:Int = 3                                    // количество повторений тестов

        // Для разных типов ожиданий; см Wait
        private lateinit var webDriverWait: WebDriverWait
        private lateinit var fluentWait: FluentWait<WebDriver>

        private val localDateNow: String = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss").format(LocalDateTime.now())

        // переменная для драйвера
        lateinit var driver: WebDriver

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            if (DT >7) println("Начало BeforeAll JetBrainsTest")
            // создание экземпляра драйвера (т.к. он объявлен в качестве переменной):
            driver = startDriver()
            brainsPage = JetBrainsPage(driver)
            val loginpage = "https://www.jetbrains.com/" //TestsProperties.loginpage
            if (DT > 8) println("Открытие страницы $loginpage")
            val login = TestsProperties.login
            val password = TestsProperties.password
            if (DT > 8) println("login= $login   password= $password")
            driver.get(loginpage)
            // JetBrains: Essential tools for software developers and teams
            assertTrue(driver.title == "JetBrains: Essential tools for software developers and teams",
                "@@@@ Не открылась страница $loginpage - нет заголовка вкладки JB @@")
            //Login(driver).loginIn(login, password)

            webDriverWait = WebDriverWait(driver, Duration.ofSeconds(10))     // Явное ожидание
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))  // Неявное ожидание
            fluentWait = FluentWait<WebDriver>(driver)                              // Беглое ожидание (сложное)

            if (DT >7) println("Конец Вызов BeforeAll JetBrainsTest")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            if (DT >7) println("Вызов AfterAll JetBrainsTest")
            //Login(driver).loginOut()
            driver.quit() //  закрытия окна браузера
            if (DT >7) println("Конец AfterAll JetBrainsTest")
        }
    }   // конец companion object
    /**
     * Функция которая выполняется перед каждым тестом, в т.ч. перед повторными
     */
    @BeforeEach
    fun setUp() {
        if (DT >7) println("Начало BeforeEach JetBrainsTest")

        // Через драйвер=ChromeDriver() мы говорим браузеру Открой эту страницу
        driver.get("https://www.jetbrains.com/")
        if (DT >7) println("Конец BeforeEach JetBrainsTest")
    }
    /**
     * Функция которая выполняется после каждого теста, в т.ч. после повторного
     */
    @AfterEach
    fun tearDown() {
        if (DT >7) println("Начало AfterEach JetBrainsTest")
        // driver.quit()
        if (DT >7) println("Конец AfterEach JetBrainsTest")
    }
    //    @RepeatedTest(3)
    @Test
    @DisplayName("--Seleium--")
    fun t01_search() {
        if (DT >7) println("Начало теста поиск ++Seleium++")
        // Мы обращаемся к классу brainsPage = JetBrainsPage(driver)
        // Зовем из него переменную searchButton и говорим ему клик
        brainsPage.searchButton.click()
        // Мы обращаемся к классу brainsPage = JetBrainsPage(driver)
        // Зовем из него переменную searchField и посылаем ему строку
        brainsPage.searchField.sendKeys("Selenium")
        // пример явного ожидания ждем до 10 сек пока кнопка не будет Clickable
        // Теоретически нельзя применять с включенным неявным ожиданием
        webDriverWait.until(ExpectedConditions.elementToBeClickable(brainsPage.submitButton))
        // Мы обращаемся к классу brainsPage = JetBrainsPage(driver)
        // Зовем из него переменную submitButton и говорим ему клик
        brainsPage.submitButton.click()
        /**
         * assertEquals - это уже Junit и никакого отношения к selenium не имеет
         * Junit о Selenium не знает и наоборот то же
         * Можно управлять браузером и ничего не спрашивать Junit 5 (jupiter)
         */
        // brainsPage.searchPageField.getAttribute("value") - берем поле и вынимаем его значени
        assertEquals("Seleniumm", brainsPage.searchPageField.getAttribute("value"), "++Seleium++")
        if (DT >7) println("Конец теста поиск ++Seleium++")

    }

    @RepeatedTest(NN)
    @DisplayName("toolsMenu")
    fun t02_toolsMenu() {
        if (DT >7) println("Начало теста нажатие toolsMenu")
        Actions(driver)
            .moveToElement(brainsPage.toolsMenu)
            .perform()

        brainsPage.toolsMenu.click()
        assertTrue(brainsPage.menuPopup.isDisplayed, "Не выскочило Попап Меню")
        if (DT >7) println("Конец теста нажатие toolsMenu")
    }

    @RepeatedTest(NN)
    @DisplayName("All Developer Tools")
    fun t03_navigationToAllTools() {
        if (DT >7) println("Начало теста открытие All Developer Tools")
        brainsPage.seeAllToolsButton.click()
        assertTrue(brainsPage.productsList.isDisplayed, "Не открылся Продакт Лист")
        assertEquals("All Developer Tools and Products by JetBrains", driver.title, "ALL Developer")
        if (DT >7) println("Конец теста открытие All Developer Tools")
    }
}