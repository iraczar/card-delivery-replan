package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryCardTest {

    @BeforeAll
    static void setUpAll() {
        // Указываем путь к ChromeDriver только для Windows
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver",
                    System.getProperty("user.dir") + "\\drivers\\chromedriver.exe");
        }
        // На Linux Selenium Manager сам скачает нужный драйвер

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // Указываем путь к chrome.exe только для Windows
        if (os.contains("win")) {
            options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        }
        // На Linux Chrome установлен в стандартном месте

        Configuration.browserCapabilities = options;
        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.timeout = 30000;
        Configuration.pageLoadTimeout = 60000;
    }

    @BeforeEach
    void setUp() {
        open("http://127.0.0.1:9999");
    }

    @Test
    void shouldSuccessfullyReplanMeeting() {
        UserInfo user = DataGenerator.generateUser();
        String firstMeetingDate = DataGenerator.generateDate(3);
        String secondMeetingDate = DataGenerator.generateDate(7);

        // Первая отправка формы
        $("[data-test-id='city'] input").setValue(user.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();

        // Проверка успешного планирования
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));

        // ЗАКРЫВАЕМ первое уведомление через крестик
        $("[data-test-id='success-notification'] .notification__closer").click();

        // Ждем пока первое уведомление полностью исчезнет
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.hidden, Duration.ofSeconds(10));

        // Повторная отправка формы с новой датой
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $(".button").click();

        // Проверка появления окна перепланирования
        $("[data-test-id='replan-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"), Duration.ofSeconds(15));

        // Нажатие кнопки "Перепланировать"
        $("[data-test-id='replan-notification'] button").click();
        // Ждем пока уведомление перепланирования исчезнет
        $("[data-test-id='replan-notification']")
                .shouldBe(Condition.hidden, Duration.ofSeconds(10));

        // Проверка успешного перепланирования
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(15));
    }
}