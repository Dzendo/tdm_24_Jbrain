package ru.cs.tdm.code

import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.*
import org.openqa.selenium.support.ui.FluentWait
//import org.openqa.selenium.support.ui.WebDriverWait
import ru.cs.tdm.data.TestsProperties
import java.time.Duration
/*
const val fluentInDuration = 3000L
const val pollingInDuration = 500L
const val fluentOutDuration = 7000L //7L
const val pollingOutDuration = 1000L
const val repeateIn = 5
const val repeateOut= 3
*/
class Tools(val driver: WebDriver) {
    private val threadSleep = TestsProperties.threadSleepNomber     // задержки где они есть
    private val DT: Int = TestsProperties.debugPrintNomber          // глубина отладочной информации 0 - ничего не печатать, 9 - все

    private val fluentInDuration = TestsProperties.fluentInDurationNomber
    private val pollingInDuration = TestsProperties.pollingInDurationNomber
    private val fluentOutDuration = TestsProperties.fluentOutDurationNomber
    private val pollingOutDuration = TestsProperties.pollingOutDurationNomber
    private val repeateIn = TestsProperties.repeateInNomber
    private val repeateOut= TestsProperties.repeateOutNomber

    //private val webDriverWait = WebDriverWait(driver, Duration.ofMillis(fluentOutDuration))// Явное ожидание
    //driver.manage().timeouts().implicitlyWait(Duration.ofMillis(fluentDuration))          // Неявное ожидание
    private val fluentOutWait = FluentWait<WebDriver>(driver)                               // Беглое ожидание
                                        .withTimeout(Duration.ofMillis(fluentOutDuration))
                                        .pollingEvery(Duration.ofMillis(pollingOutDuration))
                                        .ignoreAll(listOf( NoSuchElementException::class.java,
                                                ElementNotInteractableException::class.java,
                                                ElementClickInterceptedException::class.java,
                                                StaleElementReferenceException::class.java))
    private val fluentInWait = FluentWait<WebDriver>(driver)                               // Беглое ожидание
                                        .withTimeout(Duration.ofMillis(fluentInDuration))
                                        .pollingEvery(Duration.ofMillis(pollingInDuration))
                                        .ignoreAll(listOf( NoSuchElementException::class.java,
                                                ElementNotInteractableException::class.java,
                                                ElementClickInterceptedException::class.java,
                                                StaleElementReferenceException::class.java))
    fun xpathLast(xpath: String, last: Boolean = true): WebElement? {
        //val xpathHtml = xpath
        val xpathHtml = "//html/body/descendant::${xpath.drop(2)}"
        repeat(repeateIn) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                val listElements = fluentInWait.until(presenceOfAllElementsLocatedBy(By.xpath(xpathHtml))) //"//html/body${xpath.drop(1)}")))
                if ((listElements!=null) and (listElements.size > 0)) {
                    if (DT >7) println("N$it xpathLast найдено ${listElements.size} элементов $xpathHtml")
                    return if (last) listElements.lastOrNull()
                            else listElements.firstOrNull()
                }
                if (DT >6) println("**N$it НЕ найдено элементов $xpathHtml")
               // return null
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("###*##*N$it попытка ####xpathLast не нашлось  поле xpathLast=$xpathHtml #######")
        }
        if (DT >4) println("&&&&&&&&&xpathLast catch TIME OUT за $repeateIn опросов по 1 сек xpathLast=$xpathHtml &&&&&&&&&")
        return null
    }

    fun xpathClickLast(xpath: String): Boolean {

        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                val element = fluentOutWait.until { xpathLast(xpath)?.click() }
                if (element != null) return true
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("####*##*N$it попытка ### xpathClickLast Не нажат  xpathClickLast=$xpath #######")
        }
        if (DT >4) println("&&&&&&&&& xpathClickLast за $repeateOut опросов по 1 сек xpathClickLast=$xpath &&&&&&&&&")
        return false
    }
    fun qtipLast(qtip: String): WebElement? {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                val element = fluentOutWait.until { xpathLast("//*[contains(@data-qtip, '$qtip')]") }
                if (element != null) return element
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("###*##*N$it попытка #### qtipLast Не найден qtipLast=$qtip #######")
        }
        if (DT >4) println("&&&&&&&&& qtipLast за $repeateOut опросов по 1 сек qtipLast=$qtip &&&&&&&&&")
        return null
    }
    fun referenceLast(reference: String): WebElement? {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                val element = fluentOutWait.until { xpathLast("//*[contains(@data-reference, '$reference')]") }
                if (element != null) return element
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("###*##*N$it попытка #### referenceLast Не найден qtipLast=$reference #######")
        }
        if (DT >4) println("&&&&&&&&& referenceLast за $repeateOut опросов по 1 сек referenceLast=$reference &&&&&&&&&")
        return null
    }
    fun qtipLastClass(qtip: String): String? {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
               val element = fluentOutWait.until { qtipLast(qtip) }
                if (element != null) return element.getAttribute("class")
            } catch (_: TimeoutException) {}
              catch (_: StaleElementReferenceException) {}
            if (DT >5) println("###*##*N$it попытка #### qtipLastClass не найден класс  опрос по 1 сек qtipLastClass=$qtip #######")
        }
        if (DT >4) println("&&&&&&&&& qtipLastClass за $repeateOut опросов по 1 сек qtipLastClass=$qtip &&&&&&&&&")
        return null
    }

    fun qtipClickLast(qtip: String): Boolean {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                val element = fluentOutWait.until { qtipLast(qtip)?.click() }
                if (element != null) return true
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("####*##*N$it попытка ### qtipClickLast Не нажат  qtipClickLast=$qtip #######")
        }
        if (DT >4) println("&&&&&&&&& qtipClickLast за $repeateOut опросов по 1 сек qtipClickLast=$qtip &&&&&&&&&")
        return false
    }
    fun referenceClickLast(data_reference: String): Boolean =
        fluentOutWait.until { xpathLast("//*[@data-reference='$data_reference']")?.click()} != null

    fun qtipPressedLast(qtip: String): Boolean = qtipLast(qtip)?.getAttribute("aria-pressed") =="true"

    fun closeXLast() = qtipClickLast("Закрыть диалог")

    fun closeEsc(): Boolean {
        Actions(driver).sendKeys(Keys.ESCAPE).perform()
        return true
    }
    fun closeEsc5(): Boolean {
        if (DT >7) println("ESC закрыть что-либо (окно, поле и.т.д) ESC драйвера ")
        repeat(5) { closeEsc() }
        return true
    }
    fun xpathClickMenu(menu:String): Boolean {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {
                fluentOutWait.until{
                    xpathLast("//span[starts-with(@id, 'menuitem-') and contains(@id, '-textEl') and  contains(text(),'$menu')]/parent::a")
                    }?.click()
                if (DT >7) println(" ClickMenu=$menu ")
                return true
            } catch (_: ElementNotInteractableException) {}
             catch (_: StaleElementReferenceException) {}
            catch (_: TimeoutException) {}
            if (DT >5) println("###N$it попытка #### xpathClickMenu NOT ClickMenu click по 1 сек xpathClickMenu=$menu #######")
        }
        if (DT >4) println("&&&&&&&&&  xpathClickMenu catch NOT ClickMenu за $repeateOut опросов по 1 сек xpathClickMenu=$menu &&&&&&&&&")
        return false
    }

    fun xpathGetText(xpath: String): String = fluentOutWait.until { xpathLast(xpath)?.text ?:"NULL" }
    fun xpathWaitTextTry(xpath: String, text: String): Boolean {
        Thread.sleep(threadSleep / 10)  // нужно
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
           if (xpathGetText(xpath).contains(text)) return true
            if (DT >5) println("###N$it попытка #### xpathGetTextTry за $it sec не взят-$text xpath=$xpath #######")
        }
        if (DT >4) println("&&&&&&&&& xpathWaitTextTry за $repeateOut опросов через 1 сек не взят-$text xpath=$xpath &&&&&&&&&")
        return false
    }
    fun xpathWaitText(xpath: String, text: String): Boolean =
        fluentOutWait.until (textToBePresentInElement(xpathLast(xpath), text)) //{ xpathLast(xpath)?.text ?:"" }
    fun referenceWaitText(reference: String, text: String): Boolean =
        fluentOutWait.until (textToBePresentInElement(xpathLast("//*[@data-reference= '$reference']"), text))
    fun xpathFluentWaitText(xpath: String, text: String): Boolean =
        fluentOutWait.until (textToBePresentInElement(xpathLast(xpath), text))
    fun qtipFluentWaitText(qtip: String, text: String): Boolean =
        fluentOutWait.until (textToBePresentInElement(qtipLast(qtip), text))

    fun titleContain(title: String): Boolean = fluentOutWait.until(titleContains(title))

    fun title(window:String): String =
        xpathLast("//div[starts-with(@id, '$window-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost'))]")?.text?:"NULL"
    fun titleWait(window:String, title: String): Boolean =
        xpathWaitTextTry("//div[starts-with(@id, '$window-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost'))]",title)
    fun windowTitle(): String =
        xpathLast("//div[starts-with(@id, 'window-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost'))]")?.text?:"NULL"
    fun clickOK(OK: String = ""): Boolean {
        //41e 43a 41e 41a 4f 4b 414 430
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            val xpath: String = if (OK.isNotEmpty()) "//span[text() = '$OK']/ancestor::a"
                                else "//span[text() = 'Ок' or text() = 'ОК' or text() = 'OK' or text() = 'Да']/ancestor::a"
            try {
                val element = fluentOutWait.until { xpathLast(xpath)?.click() }
                if (element != null) return true
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("####*##*N$it попытка ### qtipClickLast Не нажат  OK #######")
        }
        if (DT >4) println("&&&&&&&&& qtipClickLast за $repeateOut опросов по 1 сек OK &&&&&&&&&")
        return false
    }
    fun clickButton(name: String): Boolean {
        repeat(repeateOut) {
            Thread.sleep(threadSleep / 10 * it)  // не нужно
            try {  // 41e 41a  41e 43a
                val element = fluentOutWait.until {
                    xpathLast("//span[text() = '$name']/ancestor::a")
                        ?.click() }
                if (element != null) return true
            } catch (_: TimeoutException) {}
            catch (_: StaleElementReferenceException) {}
            if (DT >5) println("####*##*N$it попытка ### $name Не нажат  #######")
        }
        if (DT >4) println("&&&&&&&&& $name за $repeateOut опросов по 1 сек &&&&&&&&&")
        return false
    }
    // // *[@id="messagebox-1379_header-title-textEl"]
    // //div[starts-with(@id, 'messagebox-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost')) and contains(text(),'TDMS')]
    //div[starts-with(@id, 'window-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost')) and contains(text(),"Редактирование групп")]
    fun nomberTitle(window:String, title: String): Int {
        Thread.sleep(threadSleep / 10)  // не нужно
        val element = xpathLast(
            "//div[starts-with(@id, '$window-') and contains(@id, '_header-title-textEl') and not(contains(@id, 'ghost')) and contains(text(),'$title')]"
        ) ?: return -1
        val id: String = element.getAttribute("id")
        if (id.isEmpty()) return 0
        var nomberString = id.drop(window.length+1)
        nomberString = nomberString.substring(0, minOf(nomberString.indexOf("_"),nomberString.indexOf("-")))
        if (nomberString.isEmpty()) return 0
        val nomberInt = nomberString.toIntOrNull()?:0
        if (DT >8) println("Запрошено окно $nomberInt из $nomberString c $window титл $title \n id= $id")
        return nomberInt
    }
    fun idRef(reference:String): String? {
        Thread.sleep(threadSleep / 10)  // не нужно
        val element = referenceLast(reference) ?: return null
        val id: String = element.getAttribute("id")
        if (id.isEmpty()) return ""
        if (DT >8) println("Запрошен элемент reference = $reference получен id= $id")
        return id
    }
    fun dropID(id: String): String = id.substringBefore("-")
    fun nomberID(id: String): String = id.filter { it.isDigit() }
       // string.replace("[^0-9]".toRegex(), "")
    fun headTeg(id: String): String {
        val drop = dropID(id)
        val nomber = nomberID(id)
        return "$drop-$nomber"
    }


    fun byID(id: String) :WebElement? = driver.findElement(By.id(id))


    fun idList() {
        Thread.sleep(threadSleep / 10)  // не нужно
        println("ID List...")
        var _staleElementReferenceException: Int = 0
        val elementList = driver.findElements(By.xpath("//*[contains(@id,'-') or contains(@id,'_')]"))
        val idList: List<String> = elementList.map {
            try {
                it.getAttribute("id")
            }catch (_: StaleElementReferenceException) {
                _staleElementReferenceException++
                "_StaleElementReferenceException"}
        }
        val idName: List<String> = idList.map{ idname -> idname.substringBefore("-") }
        val idSet:Set <String> = idName.sorted().toSet()
        println ("elementlist = ${elementList.size}  _StaleException= $_staleElementReferenceException idSet = ${idSet.size} ")
        for (id in idSet) println(id)

    }
}
