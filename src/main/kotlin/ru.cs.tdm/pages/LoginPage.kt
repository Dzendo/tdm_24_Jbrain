package ru.cs.tdm.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * При использовании Page Object элементы страниц,
 * а также методы непосредственного взаимодействия с ними,
 * выносятся в отдельный класс.
 */
/**
 * Класс LoginPage, который будет содержать локацию элементов страницы логина
 * и методы для взаимодействия с этими элементами.
 * ancestor - предок
 * following-sibling - следующий брат-сестра
 * descendant - потомок = дети и их внуки и правнуки и т.д.
 */
class LoginPage(driver: WebDriver) {
    private val webDriverWait = WebDriverWait(driver, Duration.ofSeconds(20))
    /**
     * конструктор класса, занимающийся инициализацией полей класса
     */
    init {
        PageFactory.initElements(driver, this)
    }

    /**
     * определение локатора поля ввода логина
     */
    // data-errorqtip="<ul class="x-list-plain"><li>Это поле обязательно для заполнения</li></ul>"
    //@FindBy(xpath = "//input [contains(@id, 'textfield-1054-inputEl')]")
    // @FindBy(xpath = "//input[contains(@placeholder,'Пользователь')]")  // Тема 2
    //@FindBy(xpath = "//span[contains(text(),'Пользователь:')]/../../..//input")
    //@FindBy(xpath = "//input [contains(@type, 'text') and contains(@class, 'x-form-field') and contains(@class, 'x-form-required-field')]")
    // Локатор элемента для ввода логина связывается с loginField
    // Ищем тег span который содержит текст Пользователь:
    // <span id="textfield-1054-labelTextEl" data-ref="labelTextEl" class="x-form-item-label-text">Пользователь:</span>
    // Можно написать проще xpath = "//span[text()='Пользователь:']", но это жестко и при изменении упадет
    // от него ancestor - предок с тегом label и от него ищем следующий брат или сестра с тегом div
    // и внутри него наконец ищем input (он там должен быть один, если два, то крах)
    // последнее можно записать культурнее //descendant::input - от текущей позиции
    // отобрать среди потомков и их внуков элемент с тегом input (Должне быть один, иначе будет массив)
    // "//span[contains(text(),'Пользователь:')]/ancestor::label/following-sibling::div//descendant::input")

    // Пользователь:
    // <span class="ActionContainer_fieldLable__0c8wo">Пользователь:</span>
    //@FindBy(xpath = "//span[contains(text(),'Пользователь:')]/ancestor::label/following-sibling::div//input")
    // client
    @FindBy(xpath = "//span[contains(text(),'Пользователь:')]/ancestor::label/following-sibling::div//descendant::input")
    // React
    //@FindBy(xpath = "//span[contains(text(),'Пользователь:')]/ancestor::label//descendant::input")
    //@FindBy(xpath = "//input[contains(@data-errorqtip, 'Это поле обязательно для заполнения')]")
    private lateinit var  loginField: WebElement

    /**
     * определение локатора поля ввода пароля
     */
    //@FindBy(xpath = "//input [contains(@id,'textfield-1055-inputEl')]")
    // @FindBy(xpath = "//input[contains(@placeholder,'Пароль')]")  // Тема 2
    //@FindBy(xpath = "//span[contains(text(),'Пароль:')]/ancestor::label/following-sibling::div//descendant::input")
    // Здесь ищем input у которого есть параметр type="password" и он единственный на этой странице (внимание @!!)
    @FindBy(xpath = "//input[contains(@type, 'password')]") // Тема 1,2
    private lateinit var passwdField: WebElement

    /**
     * определение кнопки входа
     */
    //@FindBy(xpath = "//*[@id="button-1060"]")
    //@FindBy(xpath = "//span [contains(@id,'button-1060-btnInnerEl')]/ancestor::a")
    //@FindBy(xpath = "//span [contains(text(), 'Войти')]/../../..")
    // Ищем спан с текстом Войти и его первого предка с тегом а
    // React
    //<button class="Authorization_button__o5Hi7">Войти</button>
    //@FindBy(xpath = "//button [contains(text(), 'Войти')]")
    // Client
    @FindBy(xpath = "//span [contains(text(), 'Войти')]/ancestor::a")
    private lateinit var loginBtn: WebElement

    @FindBy(xpath ="//span[text()='Да']/ancestor::a")
    private lateinit var yesBtn: WebElement

    /**
     * метод для ввода логина
     */
    fun inputLogin(login: String) = webDriverWait.until(elementToBeClickable(loginField)).sendKeys(login)

    /**
     * метод для ввода пароля
     */
    fun inputPasswd(passwd: String) = webDriverWait.until(elementToBeClickable(passwdField)).sendKeys(passwd)

    /**
     * метод для осуществления нажатия кнопки входа в аккаунт
     */
    // Мы получаем веб элемент и даем ему команду нажать
    fun clickLoginBtn() = webDriverWait.until(elementToBeClickable(loginBtn)).click()

    /**
     * метод для осуществления нажатия кнопки подтверждения выхода из TDMS
     */
    fun ClickYesBtn() = webDriverWait.until(elementToBeClickable(yesBtn)).click()
}