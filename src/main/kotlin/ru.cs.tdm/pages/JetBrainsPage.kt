package ru.cs.tdm.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

// https://www.jetbrains.com/
/**
 * Обычный котлиновский класс который здесь выполняет функцию обращения по ссылкам к сайту
 */
class JetBrainsPage(driver: WebDriver) {
    private val webDriverWait = WebDriverWait(driver, Duration.ofSeconds(20))
    /**
     * Статический блок в котлине, который выполняется первым при инициализации класса
     */
    init {
        // Специальная процедура, которая инициализирует - связывает переменные Котлина с полями сайта
        // Применяется паттерн Фабрика - надо, что бы это работало
        PageFactory.initElements(driver, this)
    }
    //@FindBy(css = "[data-test='search-input']")
    @FindBy(xpath = "//input[@data-test='search-input']")
    lateinit var searchField : WebElement

    /**
     * Нажатие на Advanced search Ctrl+K в выпадающем списке под поиском
     */
    //@FindBy(css = "button[data-test='full-search-button']")
    @FindBy(xpath = "//button[@data-test='full-search-button']")
    lateinit var submitButton: WebElement

    /**
     * Поле поиск в новом отрывшемся окне
     */
    //@FindBy(css ="input[data-test='search-input']")
    @FindBy(xpath ="//input[@data-test='search-input']")
    lateinit var searchPageField: WebElement

    // <a href="/products/" class="wt-link wt-link_hardness_average wt-link_theme_dark">All products</a>
    //@FindBy(css = "a.wt-button_mode_primary")
    @FindBy(xpath ="//a[text()='All products']")
    lateinit var seeAllToolsButton: WebElement

    @FindBy(xpath = "//div[@data-test='main-menu-item' and @data-test-marker = 'Developer Tools']")
    lateinit var toolsMenu: WebElement

    @FindBy(css ="div[data-test='main-submenu']")
    lateinit var menuPopup: WebElement

    //*[@id="js-site-header"]/div/div/div[2]/div[1]/div/div/div/div[1]/button
    //*[@data-test='site-header-search-action']

    /**
     * Объявляется переменная searchButton типа WebElement !!! (selenium-java) с отложенным присвоением
     * @FindBy (selenium) - указывает с чем связать переменную.
     * Самое простое - это применить фабрику и указать связь переменной с локатором
     * Если так делать, то все остальное сделает сам selenium, т.е.
     * При обращении к переменной он обратиться через драйвер к браузеру
     * Отдаст ему локатор, если надо, то подождет 10 сек
     * После этого в программе мы можем просто обращаться к Котлиновской переменной типа WebElement
     * и командовать им, а selenium через эту связь будет туда - обратно гонять.
     * Паттерн он надевает уздечку на коня а мы потом за нее дергаем направо-налево
     */
    //@FindBy(css = "[data-test='site-header-search-action']")  JB ошибочно и через CSS
    @FindBy(xpath = "//button[@data-test='site-header-search-action']")
    lateinit var searchButton: WebElement

    @FindBy(id = "products-page" )
    lateinit var productsList: WebElement
}

