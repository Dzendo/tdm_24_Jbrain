package ru.cs.tdm.data

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

//окно разворачивается на полный второй экран-1500 1500 3000 2000,0
fun startDriver(indexBrowser: Int = TestsProperties.browserIndex, xPoint:Int = 0, yPoint: Int = 0): WebDriver {
    lateinit var driver: WebDriver
    //      0        1        2         3         4        5         6           7        8
    //  "Chrome", "Edge", "Firefox", "Opera", "Brave", "Yandex", "CCleaner", "IntExp", "Safari"
    when(indexBrowser) {
        0 -> {
            driver = WebDriverManager
                .chromedriver()
                // отключился Chrome 111 - костыль 2 из 2: первый в Tdm System.setProperty("webdriver.http.factory", "jdk-http-client") (достаточно одного)
                .capabilities(ChromeOptions().addArguments("--remote-allow-origins=*"))
                .create()
            //WebDriverManager.chromedriver().setup()
            //driver = ChromeDriver()
        }
        1 -> {
            driver = WebDriverManager.edgedriver().create()
           // driver = EdgeDriver()
        }
        2 -> {
            driver = WebDriverManager.firefoxdriver().create()
            //driver = FirefoxDriver()
        }
        3 -> {  // дает лишнюю ошибку на JBrains
            driver = WebDriverManager.operadriver().create()
            //driver = OperaD FirefoxDriver()
        }
        4 -> {
            val option: ChromeOptions  = ChromeOptions()
            option.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe")
            WebDriverManager.chromiumdriver().setup()
            driver = ChromeDriver(option)
        }
        5 -> {   // не работает стартует
            // C:\Users\ASDze\AppData\Local\Yandex\YandexBrowser\Application\browser.exe
            val option: ChromeOptions  = ChromeOptions()
            option.setBinary("C:\\Users\\ASDze\\AppData\\Local\\Yandex\\YandexBrowser\\Application\\browser.exe")
            WebDriverManager.chromiumdriver().setup()
            driver = ChromeDriver(option)
        }
        6 -> {  // не работает - стартует и не открывается
            // "C:\Program Files (x86)\CCleaner Browser\Application\CCleanerBrowser.exe" --check-run=src=desktop
            val option: ChromeOptions  = ChromeOptions()
            option.setBinary("C:\\Program Files (x86)\\CCleaner Browser\\Application\\CCleanerBrowser.exe")
            option.addArguments("--check-run=src=desktop")
            WebDriverManager
                .chromiumdriver()
                //.capabilities(ChromeOptions().addArguments("--check-run=src=desktop"))
                .setup()
            driver = ChromeDriver(option)

        }
        7 -> {
            driver = WebDriverManager.iedriver().create()
            //driver = InternetExplorerDriver()
        }
        8 -> {
            driver = WebDriverManager.safaridriver().create()
            //driver = SafariDriver()
        }
        else -> {}
    }

    driver.manage().window().position = Point(xPoint, yPoint)
    driver.manage().window().maximize()
    return driver
}