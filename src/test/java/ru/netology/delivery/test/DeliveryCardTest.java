package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryCardTest {

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1920x1080";
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