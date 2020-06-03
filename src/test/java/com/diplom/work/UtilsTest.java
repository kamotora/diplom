package com.diplom.work;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.exceptions.NumberParseException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

public class UtilsTest {

    @Test
    public void testCheckSigns() {
        final String ID = "ONOFIQNEOIFNQO142EIFNGQEPIN";
        final String key = "OQNFOI5213QNEFOI135QNEOFINQE";
        final NumberInfo numberInfo = new NumberInfo();
        numberInfo.setDomain("873427.20.rt.ru");
        numberInfo.setFrom_number("79131234567");
        numberInfo.setRequest_number("79112233445");
        final String correctSign = "f8f23ae57b0355cdb43a856574ed89d7fdb9b0108ccb45845a77ad9d9c538172";
        try {
            HttpHeaders headers = ControllerUtils.getHeaders(numberInfo, ID, key);
            Assert.assertNotNull(headers);
            Assert.assertTrue(headers.containsKey("X-Client-ID"));
            Assert.assertTrue(headers.containsKey("X-Client-Sign"));
            Assert.assertEquals(1, Objects.requireNonNull(headers.get("X-Client-Sign")).size());
            Assert.assertEquals(correctSign, Objects.requireNonNull(headers.get("X-Client-Sign")).get(0));
        } catch (Exception e) {
            Assert.fail("Было вызвано исключение " + e.getMessage());
        }
    }

    @Test
    public void testParseNumber() {
        final String correctStr1 = "sip:79131234567@domain.com";
        final String number1 = "79131234567";
        final String correctStr2 = "sip:+79131238899@domain.com";
        final String number2 = "+79131238899";
        final String correctStr3 = "sip:89131234567@do";
        final String number3 = "89131234567";

        final String uncorrectStr1 = "sip:";
        final String uncorrectStr2 = "79131234567";
        final String nullStr = null;
        final String emptyStr = "";
        final String blankStr = "sip:     @";
        try {
            Assert.assertEquals(ControllerUtils.parseNumberFromSip(correctStr1), number1);
            Assert.assertEquals(ControllerUtils.parseNumberFromSip(correctStr2), number2);
            Assert.assertEquals(ControllerUtils.parseNumberFromSip(correctStr3), number3);
        } catch (Exception e) {
            Assert.fail("Было вызвано исключение " + e.getMessage());
        }
        Assert.assertThrows(NumberParseException.class, () -> ControllerUtils.parseNumberFromSip(uncorrectStr1));
        Assert.assertThrows(NumberParseException.class, () -> ControllerUtils.parseNumberFromSip(uncorrectStr2));
        Assert.assertThrows(NullPointerException.class, () -> ControllerUtils.parseNumberFromSip(nullStr));
        Assert.assertThrows(NumberParseException.class, () -> ControllerUtils.parseNumberFromSip(blankStr));
    }
}
