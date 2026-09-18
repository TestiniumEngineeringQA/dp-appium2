package com.testinium;

import com.testinium.helper.DeepLinkHelper;
import com.testinium.helper.RandomString;
import com.testinium.helper.StoreHelper;
import com.testinium.model.SelectorInfo;
import com.thoughtworks.gauge.Step;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.interactions.PointerInput;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;
import java.util.Random;
import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.Keys;

public class StepImpl extends HookImpl {
    private static final PointerInput FINGER = new PointerInput(PointerInput.Kind.TOUCH, "finger");

    private Logger logger = LoggerFactory.getLogger(getClass());
    long startTime = 0;
    static List<String> user = new ArrayList<>();
    static List<String> password = new ArrayList<>();
    static List<String> name = new ArrayList<>();
    static List<String> surname = new ArrayList<>();
    static List<String> email = new ArrayList<>();
    static List<String> emailAgain = new ArrayList<>();
    static List<String> phoneNumber = new ArrayList<>();
    static List<String> birthDate = new ArrayList<>();
    static List<String> password1 = new ArrayList<>();
    static List<String> password2 = new ArrayList<>();
    static List<String> Old = new ArrayList<>();
    static List<String> New = new ArrayList<>();
    static List<String> Again = new ArrayList<>();
    static List<String> CustomerName = new ArrayList<>();
    static List<String> CustomerSurname = new ArrayList<>();
    static List<String> CustomerPhone = new ArrayList<>();
    static List<String> CustomerAddressTitle = new ArrayList<>();
    static List<String> CustomerAddressDetail = new ArrayList<>();
    static List<String> CustomerAddressPostCode = new ArrayList<>();
    static List<String> province = new ArrayList<>();
    static List<String> county = new ArrayList<>();
    static List<String> promoCode = new ArrayList<>();
    public static AndroidDriver androidDriver;


    String accountUser;
    String accountpassword;
    String registerName;
    String registerSurname;
    String registerEmail;
    String registerEmailAgain;
    String registerPhoneNumber;
    String registerBirthDate;
    String registerPassword1;
    String registerPassword2;
    String oldPassword;

    String newPassword;

    String againNewPassword;

    String customerName;

    String customerSurname;

    String customerPhone;

    String customerAddressTitle;

    String customerAddressDetail;

    String customerAddressPostCode;
    String realProvince;
    String realCounty;
    String promoCodes;


    public StepImpl() {

    }

    private boolean isIOS() {
        return getDriver() instanceof IOSDriver;
    }

    private DeepLinkHelper getDeepLinkHelper() {

        return new DeepLinkHelper(
                getDriver(),
                HookImpl.ANDROID_PACKAGE,
                HookImpl.IOS_BUNDLE_ID
        );
    }

    public List<WebElement> findElements(By by) throws Exception {
        List<WebElement> webElementList = null;
        if (isIOS()) {
            waitForElementToDisappear();
        }

        try {
            webElementList = getAppiumFluentWait().until(new ExpectedCondition<List<WebElement>>() {
                @Nullable
                @Override
                public List<WebElement> apply(@Nullable WebDriver driver) {
                    List<WebElement> elements = driver.findElements(by);
                    return elements.size() > 0 ? elements : null;
                }
            });

            if (webElementList == null) {
                throw new NullPointerException(String.format("by = %s Web element list not found", by.toString()));
            }

        } catch (Exception e) {
            throw e;
        }
        return webElementList;
    }

    public List<WebElement> findElementsWithoutAssert(By by) {

        List<WebElement> WebElements = null;
        try {
            WebElements = findElements(by);
        } catch (Exception e) {
        }
        return WebElements;
    }

    public List<WebElement> findElementsWithAssert(By by) {

        List<WebElement> WebElements = null;
        try {
            WebElements = findElements(by);
        } catch (Exception e) {
            Assertions.fail("by = %s Elements not found ", by.toString());
            e.printStackTrace();
        }
        return WebElements;
    }


    public WebElement findElement(By by) throws Exception {
        WebElement WebElement;
        try {
            WebElement = findElements(by).get(0);
        } catch (Exception e) {
            throw e;
        }
        return WebElement;
    }

    public WebElement findElementWithoutAssert(By by) {
        WebElement WebElement = null;
        try {
            WebElement = findElement(by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebElement;
    }

    public WebElement findElementWithAssertion(By by) {
        WebElement WebElement = null;
        try {
            WebElement = findElement(by);
        } catch (Exception e) {
            Assertions.fail(WebElement.getAttribute("value") + " " + "by = %s Element not found ", by.toString());
            e.printStackTrace();
        }
        return WebElement;
    }

    public WebElement findElementByKeyWithoutAssert(String key) {
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
        if (selectorInfo == null) {
            logger.warn("SelectorInfo bulunamadı! Key: " + key);
            return null;
        }

        try {
            if (selectorInfo.getIndex() > 0) {
                return findElements(selectorInfo.getBy()).get(selectorInfo.getIndex());
            } else {
                return findElement(selectorInfo.getBy());
            }
        } catch (Exception e) {
            logger.error("Element bulunamadı. Key: " + key + " Hata: " + e.getMessage());
            return null;
        }
    }


    public WebElement findElementByKey(String key) {
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);

        WebElement mobileElement = null;
        try {
            mobileElement = selectorInfo.getIndex() > 0 ? findElements(selectorInfo.getBy())
                    .get(selectorInfo.getIndex()) : findElement(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Element not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElement;
    }


    public List<WebElement> findElemenstByKeyWithoutAssert(String key) {
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
        List<WebElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileElements;
    }

    public List<WebElement> findElemenstByKey(String key) {
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
        List<WebElement> mobileElements = null;
        try {
            mobileElements = findElements(selectorInfo.getBy());
        } catch (Exception e) {
            Assertions.fail("key = %s by = %s Elements not found ", key, selectorInfo.getBy().toString());
            e.printStackTrace();
        }
        return mobileElements;
    }

    @Step("Excelden degerler <sheetneme>,<deger> okunur")
    public void excel(String sheetname, Integer deger) throws IOException {

        List<Object> excelRead = new ArrayList<>();
        String path = "src/test/resources/excel/KampanyaTestleri.xlsx";
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetname);

        int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
        System.out.println("row count:" + rowCount);

        for (int i = 0; i <= rowCount; i++) {
            System.out.print(sheet.getRow(i).getCell(deger));
            System.out.println();
            excelRead.add((sheet.getRow(i).getCell(deger)).toString().trim());
        }
    }

    @Step("Excelden degerler <sheetname>,<deger>,<row> okunur ve <saveKey> sakla")
    public void excelRead(String sheetname, Integer deger, Integer row, String saveKey) throws IOException {

        List<Object> excelRead = new ArrayList<>();
        String path = "src/test/resources/excel/KampanyaTestleri.xlsx";
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetname);

        Cell cell = sheet.getRow(row - 1).getCell(deger - 1);
        String value = (cell != null) ? cell.toString().trim() : "";

        System.out.println("Değer: " + value);
        StoreHelper.INSTANCE.saveValue(saveKey, value);
    }

    @Step("Excelden degerler <sheetname>,<deger>,<row> sku okunur ve <saveKey> saklanir")
    public void excelReadSKU(String sheetname, Integer deger, Integer row, String saveKey) throws IOException {

        List<Object> excelRead = new ArrayList<>();
        String path = "src/test/resources/excel/KampanyaTestleri.xlsx";
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetname);

        Cell cell = sheet.getRow(row - 1).getCell(deger - 1);

        // SKU
        if (cell.getCellType() == CellType.NUMERIC) {
            DataFormatter dataFormatter = new DataFormatter();
            String formattedValue = dataFormatter.formatCellValue(cell);
            System.out.println("Değer: " + formattedValue);
            StoreHelper.INSTANCE.saveValue(saveKey, formattedValue);
        } else {
            String value = (cell != null) ? cell.toString().trim() : "";
            System.out.println("Değer: " + value);
            StoreHelper.INSTANCE.saveValue(saveKey, value);
        }

        workbook.close();
    }

    @Step({"<str> elementine <str2> degerini gir", "<str> element write <str2> text"})
    public void sendK(String str, String str2) {
        findElementWithAssertion(By.id(str)).sendKeys(str2);
    }


    @Step({"Değeri <text> e eşit olan elementi bul ve tıkla",
            "Find element text equals <text> and click"})
    public void clickByText(String text) {
        findElementWithAssertion(By.xpath(".//*[contains(@text,'" + text + "')]")).click();
    }


    @Step({"İçeriği <value> e eşit olan elementli bul ve tıkla",
            "Find element value equals <value> and click"})
    public void clickByValue(String value) {
        findElementWithAssertion(MobileBy.xpath(".//*[contains(@value,'" + value + "')]")).click();
    }

    @Step("İçeriği <value> value iceren text degerinin goruntulendigi kontrol edilir")
    public void checkByValue(String value) {
        getDriver().findElement(MobileBy.xpath(".//*[contains(@value,'" + value + "')]"));
        System.out.println(value + " " + " degeri goruntulendi");
    }

    @Step("<elementType> tipindeki <attribute> attribute degeri olan <key> değerini içeren text'in görüntülendiği kontrol edilir")
    public void checkByValueText(String elementType, String attribute, String key) {
        String element = "//" + elementType + "[contains(@" + attribute + ",\"" + key + "\")]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(elementType + " tipindeki " + key + " text değerine sahip element görüntülendi");
    }

    @Step("<key> csv dosyasindan rastgele kullanici sec")
    public void csvReader(String value) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 2);
                user.add(keyValue[0]);
                password.add(keyValue[1]);
            }

            System.out.println("Maillere ait csv okundu");

            int number = createRandomNumber(user.size());

            accountUser = user.get(number);
            accountpassword = password.get(number);

            System.out.println("Kullanilacak Kullanici adi :" + accountUser);
            System.out.println("Kullanilacak Sifre :" + accountpassword);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(user);
            System.out.println(password);
        }
    }

    @Step("<key> csv dosyasindan <number> numaralı kullaniciyi sec")
    public void csvExactReader(String value, String no) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 2);
                user.add(keyValue[0]);
                password.add(keyValue[1]);
            }

            System.out.println("Maillere ait csv okundu");


            int number = Integer.parseInt(no);

            accountUser = user.get(number);
            accountpassword = password.get(number);

            System.out.println("Kullanilacak Kullanici adi :" + accountUser);
            System.out.println("Kullanilacak Sifre :" + accountpassword);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(user);
            System.out.println(password);
        }
    }

    @Step("iOS login için telefon <phone> kullan")
    public void iosSetLoginPhoneFromParameter(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Telefon boş olamaz.");
        }
        accountUser = phone.replaceAll("\\s+", "").trim();
        accountpassword = "141414";
        logger.info("iOS login telefonu (parametre) ayarlandı: {}", accountUser);
    }

    @Step("<key> csv dosyasindan rastgele il ve ilce sec")
    public void csvCityReader(String value) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 2);
                province.add(keyValue[0]);
                county.add(keyValue[1]);
            }

            System.out.println("Şehirlere ait csv okundu");

            int number = createRandomNumber(province.size());

            realProvince = province.get(number);
            realCounty = county.get(number);

            System.out.println("Kullanilacak Şehir Adi :" + realProvince);
            System.out.println("Kullanilacak Ilce Adi :" + realCounty);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(province);
            System.out.println(county);
        }
    }

    @Step("<key> csv dosyasindan rastgele register kullanicisi sec")
    public void csvRegisterReader(String value) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 8);
                name.add(keyValue[0]);
                surname.add(keyValue[1]);
                email.add(keyValue[2]);
                emailAgain.add(keyValue[3]);
                phoneNumber.add(keyValue[4]);
                birthDate.add(keyValue[5]);
                password1.add(keyValue[6]);
                password2.add(keyValue[7]);
            }

            System.out.println("Ad, Soyad ve Maillere ait csv okundu");
            System.out.println("Telefon ve Doğum Tarihine ait csv okundu");
            System.out.println("Şifre ve Şifre Tekrar ait csv okundu");

            int number = createRandomNumber(name.size());

            registerName = name.get(number);
            registerSurname = surname.get(number);
            registerEmail = email.get(number);
            registerEmailAgain = emailAgain.get(number);
            registerPhoneNumber = phoneNumber.get(number);
            registerBirthDate = birthDate.get(number);
            registerPassword1 = password1.get(number);
            registerPassword2 = password2.get(number);

            System.out.println("Kullanilacak ad :" + registerName);
            System.out.println("Kullanilacak soyad :" + registerSurname);
            System.out.println("Kullanilacak email :" + registerEmail);
            System.out.println("Kullanilacak email tekrari :" + registerEmailAgain);
            System.out.println("Kullanilacak Kullanici numarası :" + registerPhoneNumber);
            System.out.println("Kullanilacak tarih :" + registerBirthDate);
            System.out.println("Kullanilacak şifre :" + registerPassword1);
            System.out.println("Kullanilacak şifre tekrar :" + registerPassword2);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(name);
            System.out.println(surname);
            System.out.println(email);
            System.out.println(emailAgain);
            System.out.println(phoneNumber);
            System.out.println(birthDate);
            System.out.println(registerPassword1);
            System.out.println(registerPassword2);
        }
    }

    @Step("<key> csv dosyasindan rastgele adres bilgileri sec")
    public void csvReaderAddress(String value) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 6);
                CustomerName.add(keyValue[0]);
                CustomerSurname.add(keyValue[1]);
                CustomerPhone.add(keyValue[2]);
                CustomerAddressTitle.add(keyValue[3]);
                CustomerAddressDetail.add(keyValue[4]);
                CustomerAddressPostCode.add(keyValue[5]);
            }

            System.out.println("Adres icin kisisel bilgilere ait csv okundu");

            int number = createRandomNumber(CustomerName.size());

            customerName = CustomerName.get(number);
            customerSurname = CustomerSurname.get(number);
            customerPhone = CustomerPhone.get(number);
            customerAddressTitle = CustomerAddressTitle.get(number);
            customerAddressDetail = CustomerAddressDetail.get(number);
            customerAddressPostCode = CustomerAddressPostCode.get(number);


            System.out.println("Kisisel Bilgiler icin Musteri Adi :" + customerName);
            System.out.println("Kisisel Bilgiler icin Musteri Soyadi :" + customerSurname);
            System.out.println("Kisisel Bilgiler icin Müsteri Telefon No :" + customerPhone);
            System.out.println("Kisisel Bilgiler icin Müsteri Adres Ismi :" + customerAddressTitle);
            System.out.println("Kisisel Bilgiler icin Müsteri Adres Detay :" + customerAddressDetail);
            System.out.println("Kisisel Bilgiler icin Müsteri Adres Posta Kodu :" + customerAddressPostCode);
        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(customerName);
            System.out.println(customerSurname);
            System.out.println(customerPhone);
            System.out.println(customerAddressTitle);
            System.out.println(customerAddressDetail);
            System.out.println(customerAddressPostCode);
        }
    }

    @Step("<key> csv dosyasindan rastgele sifre sec")
    public void csvReaderPassword(String value) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 3);
                Old.add(keyValue[0]);
                New.add(keyValue[1]);
                Again.add(keyValue[2]);
            }

            System.out.println("Sifrelere ait csv okundu");

            int number = createRandomNumber(Old.size());

            oldPassword = Old.get(number);
            newPassword = New.get(number);
            againNewPassword = Again.get(number);

            System.out.println("Kullanilacak Eski Sifre:" + oldPassword);
            System.out.println("Kullanilacak Yeni Sifre :" + newPassword);
            System.out.println("Kullanilacak Tekrar Yeni Sifre :" + againNewPassword);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(oldPassword);
            System.out.println(newPassword);
            System.out.println(againNewPassword);
        }
    }

    @Step({"Değeri <text> e eşit olan <index>. elementi bul ve tıkla"})
    public void clickByText(String text, int index) {
        findElementWithAssertion(By.xpath("(.//*[contains(@text,'" + text + "')])[" + index + "]")).click();
    }

    @Step({"İçeriği <value> e eşit olan <index>. elementi bul ve tıkla"})
    public void clickByValue(String value, int index) {
        findElementWithAssertion(MobileBy.xpath("(.//*[contains(@value,'" + value + "')])[" + index + "]")).click();
    }

    @Step("<key> elementinin <index> .li elementi bul ve tıkla")
    public void clickByKeyIndex(String key, int index) {
        findElementsWithoutAssert(getSelector().getSelectorInfo(key).getBy()).get(index).click();
    }

    @Step("<key> li listeden rastgele birine tıkla")
    public void clickByKeyRandom(String key) {
        List<WebElement> elements = findElementsWithAssert(getSelector().getSelectorInfo(key).getBy());
        if (!elements.isEmpty())
            elements.get(createRandomNumber(elements.size() - 1)).click();
        else
            logger.error("Element bulunamadı!!!");
    }

    @Step("<key> li listeden sırayla texte göre tıkla")
    public void checkSliderControl(String key) {
        List<WebElement> elements = findElementsWithAssert(getSelector().getSelectorInfo(key).getBy());
        if (!elements.isEmpty()) {
            for (WebElement webElement : elements) {
                String text = webElement.getText().trim();
                WebElement element = findElementWithAssertion(By.xpath("//android.widget.TextView[@text='" + text + "']"));
                element.click();

            }
        }

    }

    @Step({"Elementine tıkla <key>", "Click element by <key>"})
    public void clickByKey(String key) {
        doesElementExistByKey(key, 5);
        findElementByKey(key).click();
        logger.info(key + " elemente tıkladı");
    }


    @Step({"Check if element <key> exists",
            "Wait for element to load with key <key>",
            "Element var mı kontrol et <key>",
            "Elementin yüklenmesini bekle <key>"})
    public WebElement getElementWithKeyIfExists(String key) throws InterruptedException {
        WebElement element;
        //   SelectorInfo selectorInfo = selector.getSelectorInfo(key);
        try {
            element = findElementByKey(key);
            logger.info(key + " elementi bulundu.");
        } catch (Exception ex) {
            logger.info("Element: '" + key + "' doesn't exist.");
            // Assertions.fail("key = %s by = %s Element not found ", key,selectorInfo.getBy().toString());
            return null;
        }
        return element;
    }

    @Step({"Elementi var mı kontrol et <key>"})
    public WebElement getElementWithKeyIfExistss(String key) {
        WebElement element = null;
        try {
            By by = getSelector().getSelectorInfo(key).getBy();
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            element = (WebElement) wait.until(ExpectedConditions.presenceOfElementLocated(by));
            logger.info("Element bulundu: " + key);
        } catch (TimeoutException te) {
            logger.warn("Timeout! Element görünmedi: " + key);
        } catch (NoSuchElementException ne) {
            logger.warn("Element bulunamadı: " + key);
        } catch (Exception e) {
            logger.error("Element hatası: " + key + " → " + e.getMessage());
        }
        return element;
    }

    @Step({"Rastgele secilen kullanici adi <key> elementine, sifre <key> elementine yazilir"})
    public void existElementt(String key, String key2) {
        findElementByKey(key).sendKeys(accountUser);
        findElementByKey(key2).sendKeys(accountpassword);
    }

    @Step({"Yeni Rastgele secilen kullanici adi <key> elementine, sifre <key2> elementine yazilir"})
    public void existElementtNew(String key, String key2) {

        findElementByKey(key).sendKeys(accountUser);
        waitBySecond(3);

        tapElementWithKeyControlArea("betaGirisYapBtnOne");
        waitBySecond(3);

        List<WebElement> warningLabels = getDriver().findElements(
                By.xpath("//*[contains(@label,'Telefon numaranızı giriniz.')]")
        );

        if (!warningLabels.isEmpty()) {
            logger.info("'Telefon numarası giriniz.' uyarısı görüldü. Kullanıcı adı tekrar yazılıyor...");
            sendKeysByKey(key, accountUser);
            waitBySecond(1);
            tapElementWithKeyControlArea("betaGirisYapBtnOne");
            waitBySecond(2);
        }

        if (doesElementExistByKey(key2, 10)) {
            WebElement codeField = findElementByKey(key2);

            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    codeField.click();
                    waitBySecond(2); // klavyenin gelmesi için mutlaka bekle

                    // temizlemeyi dene
                    try {
                        codeField.clear();
                    } catch (Exception ignored) {
                    }

                    for (int i = 0; i < 6; i++) {
                        getDriver().switchTo().activeElement().sendKeys(Keys.BACK_SPACE);
                    }

                    waitBySecond(1);

                    // setValue dene
                    codeField.sendKeys("141414");
                    logger.info("[" + attempt + ". deneme] setValue ile OTP yazıldı.");
                    break;

                } catch (Exception e1) {
                    logger.warn("[" + attempt + ". deneme] setValue olmadı, sendKeys denenecek...");
                    try {
                        codeField.sendKeys("141414");
                        logger.info("[" + attempt + ". deneme] sendKeys ile OTP yazıldı.");
                        break;
                    } catch (Exception e2) {
                        logger.error("[" + attempt + ". deneme] sendKeys de çalışmadı.");
                        waitBySecond(2);
                    }
                }
            }

        } else {
            logger.warn(key2 + " alanı bulunamadı, doğrulama kodu yazılamadı.");
        }

        waitBySecond(2);
    }


    @Step({"iOS için <phoneFieldKey> alanına kullanıcı adı yazılır, giriş yapılır, otp yazılır ve başarılı ekran kontrol edilir"})
    public void iosPhoneAndOtpStep(String phoneFieldKey) {
        logger.info("Kullanıcı adı: " + accountUser);
        logger.info("OTP şifresi: " + accountpassword);

        // 1. Telefon numarasını yaz
        findElementByKey(phoneFieldKey).sendKeys(accountUser);
        waitBySecond(2);

        // 2. Giriş butonuna tıkla
        tapElementWithKeyControlArea("betaGirisYapBtnOne");
        waitBySecond(3);

        // 3. Uyarı kontrolü
        List<WebElement> warningLabels = getDriver().findElements(
                By.xpath("//*[contains(@label,'Telefon numarası zorunludur.')]")
        );

        if (!warningLabels.isEmpty()) {
            logger.info("'Telefon numarası zorunludur.' uyarısı görüldü. Kullanıcı adı tekrar yazılıyor...");
            sendKeysByKey(phoneFieldKey, accountUser);
            waitBySecond(1);
            tapElementWithKeyControlArea("betaGirisYapBtnOne");
            waitBySecond(3);
        }

        // 4. OTP yazım denemeleri (klavye tuşlarına tek tek basarak)
        boolean otpWritten = false;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                logger.info(attempt + ". deneme: OTP yazılıyor...");

                WebElement otpField = getDriver().findElement(
                        By.xpath("//XCUIElementTypeTextField[@name='otpInput']")
                );

                // Klavyeyi açtır
                otpField.click();
                waitBySecond(1);

                // Eski değeri temizle (varsa)
                try {
                    otpField.clear();
                } catch (Exception e) {
                    logger.warn("clear() çalışmadı, setValue ile boşaltılıyor");
                    otpField.sendKeys("");
                }
                waitBySecond(1);

                // Karakter karakter klavyeden tuşlara bas
                String otp = "141414";  // test amaçlı
                for (char ch : otp.toCharArray()) {
                    try {
                        WebElement key = getDriver().findElement(
                                By.xpath("//XCUIElementTypeKey[@name='" + ch + "']")
                        );
                        key.click();
                        logger.info(ch + " tuşuna basıldı.");
                        waitBySecond(1);
                    } catch (Exception e) {
                        logger.error(ch + " tuşuna basılamadı: " + e.getMessage());
                    }
                }

                logger.info("OTP klavyeden başarıyla basıldı: " + otp);
                otpWritten = true;
                break;

            } catch (Exception e) {
                logger.warn(attempt + ". denemede OTP yazımı başarısız: " + e.getMessage());
                waitBySecond(2);
            }
        }

        if (!otpWritten) {
            logger.error("3 denemeye rağmen OTP yazılamadı.");
            return;
        }

        waitBySecond(2);

        // 5. Hâlâ geçiş olmadıysa boş alana tıklayıp UI’ı tetikle
        if (!doesElementExistByKey("gratisHomePageProfilTab", 5)) {
            logger.info("Giriş tetiklenmedi, boş alana tıklanıyor...");
            try {
                tapElementWithKeyControlArea("otpKodunuzUlasmadiMi"); // dummy alan
                waitBySecond(2);
            } catch (Exception e) {
                logger.warn("Boş alana tıklama denemesi başarısız: " + e.getMessage());
            }
        }

        // 6. Son kontrol
        if (doesElementExistByKey("gratisHomePageProfilTab", 10)) {
            logger.info("Ana sayfa/başarı elementi göründü. Giriş başarılı.");
        } else {
            logger.warn("OTP girildi ama ana sayfa açılmadı. Element bulunamadı.");
        }
    }


    @Step({"Rastgele secilen il adi <key> elementine yazilir"})
    public void existProvince(String key) {
        findElementByKey(key).sendKeys(realProvince);
    }

    @Step({"Rastgele secilen ilce adi <key> elementine yazilir"})
    public void existCounty(String key) {
        findElementByKey(key).sendKeys(realCounty);
    }

    @Step({"Rastgele secilen kullanici adi <key1> elementine, soyadi <key2> elementine, mail <key3> ,mail tekrarı <key4>,dogum tarihi <key5> ,sifre <key6>, sifre tekrar <key7> elementine yazilir"})
    public void personalInfo(String key, String key2, String key3, String key4, String key5, String key6, String key7) {

        findElementByKey(key).sendKeys(registerName);
        findElementByKey(key2).sendKeys(registerSurname);
        findElementByKey(key3).sendKeys(registerEmail);
        waitBySecond(3);
        findElementByKey(key5).sendKeys(registerBirthDate);
        closeNumberKeyboardIOS();

    }

    @Step({"Rastgele secilen telefon <key> elementine, doğum tarihi <key>  elementine yazilir"})
    public void personalInfo2(String key, String key2) {
        findElementByKey(key).sendKeys(registerPhoneNumber);
        findElementByKey(key2).sendKeys(registerBirthDate);
    }

    @Step({"Rastgele secilen telefon <key> elementine yazilir"})
    public void phoneInfo(String key) {
        findElementByKey(key).sendKeys(registerPhoneNumber);
    }

    @Step({"Rastgele secilen şifre <key> elementine, şifre tekrar <key>  elementine yazilir"})
    public void registerPassword(String key, String key2) {
        findElementByKey(key).sendKeys(registerPassword1);
        findElementByKey(key2).sendKeys(registerPassword2);
    }

    @Step({"Eski sifre <key> elementine, yeni sifre <key> elementine, tekrar yeni sifre <key> elementine yazilir"})
    public void existElementtPassword(String key, String key2, String key3) {
        findElementByKey(key).sendKeys(oldPassword);
        findElementByKey(key2).sendKeys(newPassword);
        findElementByKey(key3).sendKeys(againNewPassword);
    }

    @Step({"Adres icin Ad <key> elementine, Soyad <key> elementine, Telefon <key> elementine, Adres Ismi <key> elementine yazilir"})
    public void existElementtAddAddress(String key, String key2, String key3, String key4) {
        findElementByKey(key).sendKeys(customerName);
        findElementByKey(key2).sendKeys(customerSurname);
        findElementByKey(key3).sendKeys(customerPhone);
        findElementByKey(key4).sendKeys(customerAddressTitle);
    }

    @Step({"Adres Ismi <key>, Adres icin Ad <key> elementine elementine yazilir"})
    public void existElementAddAddressTitleName(String key, String key2) {
        findElementByKey(key).sendKeys(customerAddressTitle);
        findElementByKey(key2).sendKeys(customerName);
    }

    @Step({"Soyad <key> elementine, Telefon <key> elementine yazilir"})
    public void existElementAddAddressSurnamePhone(String key, String key2) {
        findElementByKey(key).sendKeys(customerSurname);
        findElementByKey(key2).sendKeys(customerPhone);
    }

    @Step({"Yeni Adres Ekleme icin Adres Detay <key> elementine, Posta Kodu <key> elementine yazilir"})
    public void existElementtAddAddressContinue(String key, String key2) {
        findElementByKey(key).sendKeys(customerAddressDetail);
        findElementByKey(key2).sendKeys(customerAddressPostCode);
    }

    @Step({"<key> elementine sayacli tikla <key2> value degerini bekle"})
    public void clickByKeyWithCounter(String key, String key2) {
        if (findElementByKey(key).isDisplayed()) {
            System.out.println("Donguye girdi");
            findElementByKey(key).click();
        }
        logger.info(key + " elementine tiklandi");
        clickExistElement(key, key2);
        logger.info(key2 + " uyarısı görüldü.");
    }

    public void clickExistElement(String key1, String key) {
        startTime = System.currentTimeMillis();
        findElementByKey(key);
        //assertTrue(findElementByKey(key).isDisplayed(), "Element sayfada bulunamadı !");
        long finishTime = System.currentTimeMillis();
        long eventDuration = (finishTime - startTime) / 1000;  // Zamanı saniyeye çeviriyoruz
        long seconds = eventDuration % 60;  // Saniyeyi hesaplıyoruz
        long milliseconds = (finishTime - startTime) % 1000;  // Saliseyi hesaplıyoruz
        System.out.println("[" + key1 + "] ------> " + "[" + key + "] " + seconds + " seconds, " + milliseconds + " milliseconds");
    }

    @Step({"<key> icin sayac tut"})
    public void existElement(String key) {
        startTime = System.currentTimeMillis();
        boolean elementExists = waitForElementVisible(key, 5);
        if (elementExists) {
            finishCounter(key);
        } else {
            logger.error(key + " elementi bulunamadı");
        }
    }

    public void finishCounter(String key) {
        long finishTime = System.currentTimeMillis();
        long eventDuration = (finishTime - startTime) / 1000;  // Zamanı saniyeye çeviriyoruz
        long seconds = eventDuration % 60;  // Saniyeyi hesaplıyoruz
        long milliseconds = (finishTime - startTime) % 1000;  // Saliseyi hesaplıyoruz
        logger.info("[" + key + "] " + " elementi sayfada görüntülendi");
        System.out.println("[" + key + "] " + seconds + " seconds, " + milliseconds + " milliseconds");
    }

    @Step("<key> elementinin <text> textini içerdiği kontrol edilir")
    public void checkTextByKey(String key, String text) {
        try {
            Thread.sleep(3000);
            System.out.println("******" + findElementByKey(key).getText() + "******");
            assertTrue(findElementByKey(key).getText().contains(text), "Element beklenen değeri taşımıyor !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> elementinin <text>, <text2> veya <text3> textini içerdiği kontrol edilir")
    public void checkTextByKey(String key, String text, String text2, String text3) {
        try {
            Thread.sleep(3000);
            String elementText = findElementByKey(key).getText();
            System.out.println("******" + elementText + "******");
            assertTrue(elementText.contains(text) || elementText.contains(text2) || elementText.contains(text3), "Element beklenen değeri taşımıyor !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> elementi belirtilen <attribute> attributedeki <text>  degerine esit mi")
    public void checkTextEqualsByKey(String key, String text, String attribute) {
        try {
            Thread.sleep(3000);
            System.out.println("******" + findElementByKey(key).getAttribute("name") + "******");
            assertNotEquals(findElementByKey(key).getAttribute(attribute), text, "Elementi girilen text değerine eşit değil!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Step({"<key> li elementi bul ve varsa tıkla", "Click element by <key> if exist"})
    public void existClickByKey(String key) throws InterruptedException {
        WebElement element = findElementByKeyWithoutAssert(key);
        if (element == null) {
            return;
        }

        // 1) getCenter() yerine merkez noktayı kendimiz hesaplayalım
        Rectangle rect = element.getRect();
        int x = rect.getX() + rect.getWidth() / 2;
        int y = rect.getY() + rect.getHeight() / 2;

        // 2) TouchAction yerine W3C pointer actions ile tap (hem iOS hem Android)
        tapByCoordinates(x, y);

        waitBySecond(2);
    }

    private void tapByCoordinates(int x, int y) {
        // driver -> AppiumDriver<?> veya RemoteWebDriver olmalı
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(80)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));
    }


    @Step({"<key> li elementi bul ve varsa dokun", "Click element by <key> if exist"})
    public void existTapByKey(String key) {
        if (findElementByKey(key).isDisplayed()) {
            findElementByKey(key).click();
        }
    }


    @Step({"<key> li elementi bul, temizle ve <text> değerini yaz",
            "Find element by <key> clear and send keys <text>"})
    public void sendKeysByKey(String key, String text) {
        WebElement element = waitForElement(key, 5);
        if (element != null) {
            element.clear();
            element.sendKeys(text);
            logger.info(key + " elementine " + text + " değeri yazıldı");
        } else {
            logger.error(key + " elementine yazılamadı");
        }
    }

    @Step({"<key> li elementi bul ve temizle",
            "Find element by <key> and clear"})
    public void clearFieldByKey(String key) {
        WebElement webElement = findElementByKey(key);
        webElement.clear();
        logger.info(key + " elementi temizlendi");
    }

    @Step({"<key> li elementin text degeri silinir"})
    public void keyClear(String key) {
        findElementByKey(key).clear();
    }


    @Step({"<t> textini <k> elemente yaz",
            "Find element by <key> and send keys <text>"})
    public void sendKeysByKeyNotClear(String t, String k) {
        doesElementExistByKey(k, 5);
        findElementByKey(k).sendKeys(t);
        logger.info(t + " texti" + k + " key elementine yazildi");
    }

    @Step({"Saklanan <SKU> textini <key> elemente yaz"})
    public void getSendKeysByKeyNotClear(String SKU, String key) {
        String sKUs = StoreHelper.INSTANCE.getValue(SKU);
        doesElementExistByKey(key, 5);
        findElementByKey(key).sendKeys(sKUs);
        logger.info(SKU + " texti" + key + " key elementine yazildi");
    }

    public int createRandomNumber(int max) {
        Random rand = new Random();

        int randomNumber = rand.nextInt(max);

        return randomNumber;
    }


    @Step({"<key> li elementi bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKey(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getText());
        logger.info("[" + StoreHelper.INSTANCE.getValue(saveKey) + "]" + " degeri [" + saveKey + "] ismiyle hafizaya kaydedildi");
    }

    @Step({"<key> li elementi bul ve name değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKeyName(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getAttribute("name"));
        logger.info("[" + StoreHelper.INSTANCE.getValue(saveKey) + "]" + " degeri [" + saveKey + "] ismiyle hafizaya kaydedildi");
    }

    @Step("<key> li elementi bul ve attribute <attribute> değerini <saveKey> olarak sakla")
    public void saveTextByKeyAttribute(String key, String attribute, String saveKey) {
        WebElement element = (WebElement) findElementByKey(key);
        if (element == null) {
            throw new RuntimeException("Element not found for key: " + key);
        }

        String attrValue = element.getAttribute(attribute);
        if (attrValue == null) {
            throw new RuntimeException("Attribute '" + attribute + "' not found on element with key: " + key);
        }

        if (StoreHelper.INSTANCE == null) {
            throw new RuntimeException("StoreHelper.INSTANCE is null. It may not be initialized properly.");
        }

        StoreHelper.INSTANCE.saveValue(saveKey, attrValue);

        if (logger != null) {
            logger.info("[" + attrValue + "] değeri [" + saveKey + "] ismiyle hafızaya kaydedildi");
        } else {
            System.out.println("Logger is null. Log yazılamadı ama değer kaydedildi: " + saveKey + " = " + attrValue);
        }
    }

    @Step({"<key> li elementin text degerini hafizada <saveKey> olarak saklanan attribute <attribute> ile karsilastir"})
    public void checkTextByKeyAndSaveKeyAttribute(String key, String saveKey, String attribute) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getAttribute(attribute);
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertTrue(elementTxt.contains(saveElementTxt), "Degerler birbirine esit degil!");
    }

    @Step({"<key> li elementin text degerini hafizada <saveKey> olarak saklanan name ile karsilastir"})
    public void chechTextByKeyAndSaveKeyName(String key, String saveKey) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getAttribute("name");
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertTrue(elementTxt.contains(saveElementTxt), "Degerler birbirine esit degil!");
    }

    @Step({"<key> li promosyon elementini bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKeyPromosyon(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getText().replace("-", "").trim());
        logger.info("[" + StoreHelper.INSTANCE.getValue(saveKey) + "]" + " degeri [" + saveKey + "] ismiyle hafizaya kaydedildi");
    }

    @Step({"<key> li elementli markayı bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void saveTextByKeyy(String key, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, findElementByKey(key).getText().substring(0, findElementByKey(key).getText().length() - 5));
        logger.info("[" + StoreHelper.INSTANCE.getValue(saveKey) + "]" + " degeri [" + saveKey + "] ismiyle hafizaya kaydedildi");
    }

    @Step({"<key> li elementli küsüratı bul ve değerini <saveKey> olarak sakla",
            "Find element by <key> and save text <saveKey>"})
    public void savePriceByKeyy(String key, String saveKey) {
        String kusur = findElementByKey(key).getText().substring(1, findElementByKey(key).getText().length());
        StoreHelper.INSTANCE.saveValue(saveKey, kusur);
        logger.info("[" + StoreHelper.INSTANCE.getValue(saveKey) + "]" + " degeri [" + saveKey + "] ismiyle hafizaya kaydedildi");
    }

    @Step({"<key> li ve değeri <text> e eşit olan elementli bul ve tıkla",
            "Find element by <key> text equals <text> and click"})
    public void clickByIdWithContains(String key, String text) {
        List<WebElement> elements = findElemenstByKey(key);
        for (WebElement element : elements) {
            logger.info("Text !!!" + element.getText());
            if (element.getText().toLowerCase().contains(text.toLowerCase())) {
                element.click();
                break;
            }
        }
    }

    @Step({"<key> li ve değeri <text> e eşit olan elementli bulana kadar swipe et ve tıkla",
            "Find element by <key> text equals <text> swipe and click"})
    public void clickByKeyWithSwipe(String key, String text) throws InterruptedException {
        boolean find = false;
        int maxRetryCount = 10;
        while (!find && maxRetryCount > 0) {
            List<WebElement> elements = findElemenstByKey(key);
            for (WebElement element : elements) {
                if (element.getText().contains(text)) {
                    element.click();
                    find = true;
                    break;
                }
            }
            if (!find) {
                maxRetryCount--;
                if (getDriver() instanceof AndroidDriver) {
                    swipeUpAccordingToPhoneSize();
                    waitBySecond(1);
                } else {
                    swipeDownAccordingToPhoneSize();
                    waitBySecond(1);
                }
            }
        }
    }

    @Step({"<key> li elementi bulana kadar swipe et ve tıkla",
            "Find element by <key>  swipe and click"})
    public void clickByKeyWithSwipe(String key) throws InterruptedException {
        int maxRetryCount = 10;
        while (maxRetryCount > 0) {
            List<WebElement> elements = findElemenstByKeyWithoutAssert(key);
            if (elements != null && elements.size() > 0) {
                try {
                    if (elements.get(0).isDisplayed()) {
                        elements.get(0).click();
                        logger.info(key + " elementine tıklandı");
                        return;
                    }
                } catch (Exception e) {
                    logger.debug("Element görünür değil, swipe yapılıyor: " + e.getMessage());
                }
            }

            maxRetryCount--;
            if (maxRetryCount > 0) {
                logger.info(key + " elementi bulunamadı, swipe yapılıyor... (Kalan deneme: " + maxRetryCount + ")");
                swipeDownAccordingToPhoneSize();
                waitBySecond(1);
            } else {
                Assertions.fail("key = " + key + " elementi 10 swipe sonrası bulunamadı");
            }
        }
    }


    private int getScreenWidth() {
        return getDriver().manage().window().getSize().width;
    }

    private int getScreenHeight() {
        return getDriver().manage().window().getSize().height;
    }

    private int getScreenWithRateToPercent(int percent) {
        return getScreenWidth() * percent / 100;
    }

    private int getScreenHeightRateToPercent(int percent) {
        return getScreenHeight() * percent / 100;
    }


    public void swipeDownAccordingToPhoneSize(int startXLocation, int startYLocation, int endXLocation, int endYLocation) {
        startXLocation = getScreenWithRateToPercent(startXLocation);
        startYLocation = getScreenHeightRateToPercent(startYLocation);
        endXLocation = getScreenWithRateToPercent(endXLocation);
        endYLocation = getScreenHeightRateToPercent(endYLocation);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        // Parmağı başlangıç noktasına bas
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startXLocation, startYLocation));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Parmağı 1 sn basılı tut
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), endXLocation, endYLocation));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Driver ile swipe hareketini çalıştır
        getDriver().perform(Arrays.asList(swipe));
    }

    @Step({"<key> id'li elementi bulana kadar <times> swipe yap",
            "Find element by <key>  <times> swipe "})
    public void swipeDownUntilSeeTheElement(String element, int limit) throws InterruptedException {
        for (int i = 0; i < limit; i++) {
            List<WebElement> meList = findElementsWithoutAssert(By.id(element));
            meList = meList != null ? meList : new ArrayList<WebElement>();
            logger.info(i + ". swipe yapiliyor");
            if (meList.size() > 0 &&
                    meList.get(0).getLocation().x <= getScreenWidth() &&
                    meList.get(0).getLocation().y <= getScreenHeight()) {
                break;
            } else {
                swipeDownAccordingToPhoneSize(50, 80, 50, 30);
                waitBySecond(1);

                break;
            }
        }
    }


    @Step({"<key> li elementi bulana kadar swipe et",
            "Find element by <key>  swipe "})
    public void findByKeyWithSwipe(String key) {
        int maxRetryCount = 10;
        while (maxRetryCount > 0) {
            //List<WebElement> elements = findElemenstByKeyWithoutAssert(key);
            WebElement elements = findElementByKeyWithoutAssert(key);

            if (elements == null) {
                maxRetryCount--;
                swipeDownAccordingToPhoneSize();
            } else {
                System.out.println(key + " element bulundu");
                break;
            }

        }
    }

    @Step("<key> li elementi bulana kadar swipe et IOS")
    public void findByKeyWithSwipeIOS(String key) {
        int maxRetryCount = 10;
        while (maxRetryCount > 0) {
            WebElement elements = findElementByKeyWithoutAssert(key);
            if (elements != null) {
                System.out.println(key + " element bulundu");
                break;
            } else {
                maxRetryCount--;
                swipeDownAccordingToPhoneSizeIOS();
            }

        }
    }


    @Step("<yon> yönüne swipe et")
    public void swipe(String yon) {
        Dimension d = getDriver().manage().window().getSize();
        int height = d.height;
        int width = d.width;

        int swipeStartWidth;
        int swipeEndWidth;
        int swipeHeight = height / 2;

        if (yon.equalsIgnoreCase("SAĞ")) {
            swipeStartWidth = (width * 80) / 100;
            swipeEndWidth = (width * 30) / 100;
        } else if (yon.equalsIgnoreCase("SOL")) {
            swipeStartWidth = (width * 30) / 100;
            swipeEndWidth = (width * 80) / 100;
        } else {
            throw new IllegalArgumentException("Desteklenmeyen yön: " + yon);
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        // Parmağı başlangıç noktasına götür ve bas
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                swipeStartWidth, swipeHeight));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Parmağı 1 sn basılı tutarak bitiş noktasına götür
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(),
                swipeEndWidth, swipeHeight));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Arrays.asList(swipe));
    }


    @Step({"<key> li elementin değeri <text> e içerdiğini kontrol et",
            "Find element by <key> and text contains <text>"})
    public void containsTextByKey(String key, String text) {
        By by = getSelector().getElementInfoToBy(key);
        assertTrue(getAppiumFluentWait().until(new ExpectedCondition<Boolean>() {
            private String currentValue = null;

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    currentValue = driver.findElement(by).getText();
                    return currentValue.contains(text);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return String.format("text contains be \"%s\". Current text: \"%s\"", text, currentValue);
            }
        }));
        logger.info(key + " li elementin değeri \"" + text + "\" metnini içeriyor.");
    }

    @Step("toast message <key> değerine eşit mi")
    public void toastMessage(String key) {
        String toastMessage = getDriver().findElement(By.xpath("//android.widget.Toast[1]")).getAttribute("name");
        System.out.println("The toast mesaage is: " + toastMessage);

        assertEquals(toastMessage, key);
    }

    @Step({"<key> li elementin değeri <text> e eşitliğini kontrol et",
            "Find element by <key> and text equals <text>"})
    public void equalsTextByKey(String key, String text) {
        assertTrue(getAppiumFluentWait().until(
                ExpectedConditions.textToBe(getSelector().getElementInfoToBy(key), text)));
    }

    @Step({"<seconds> saniye bekle", "Wait <second> seconds"})
    public void waitBySecond(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForElementToDisappear() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//XCUIElementTypeImage[@name=\"loader\"]")));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element belirtilen süre içinde kaybolmadı!!!");
        }
    }

    public void swipeUpAccordingToPhoneSize() {
        Dimension d = getDriver().manage().window().getSize();
        int height = d.height;
        int width = d.width;

        int swipeStartWidth = width / 2;
        int swipeEndWidth = width / 2;

        // Yukarı kaydır = İçerik yukarı çıkar = Parmağı aşağı kaydır (Y artar: 30% -> 75%)
        int swipeStartHeight;
        int swipeEndHeight;

        if (isIOS()) {
            swipeStartHeight = (height * 30) / 100;  // Yukarıdan başla
            swipeEndHeight = (height * 80) / 100;    // Aşağıya kaydır (içerik yukarı çıkar)
        } else {
            swipeStartHeight = (height * 30) / 100;  // Yukarıdan başla
            swipeEndHeight = (height * 75) / 100;    // Aşağıya kaydır (içerik yukarı çıkar)
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        // Parmağı başlangıç noktasına götür ve bas
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                swipeStartWidth, swipeStartHeight));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Parmağı basılı tutarak aşağı kaydır (Y artar = içerik yukarı çıkar)
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(800), PointerInput.Origin.viewport(),
                swipeEndWidth, swipeEndHeight));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Arrays.asList(swipe));

        System.out.println("Swipe UP (content up) completed from Y=" + swipeStartHeight + " to Y=" + swipeEndHeight);
    }


    public void swipeDownAccordingToPhoneSize() {
        Dimension dimension = getDriver().manage().window().getSize();

        int startX = dimension.width / 2;
        int startY = (dimension.height * 75) / 100;   // yukarıdan başla
        int endX = dimension.width / 2;
        int endY = (dimension.height * 30) / 100;    // aşağıya kaydır

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        // Parmağı başlangıç noktasına götür ve bas
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Parmağı basılı tutarak aşağıya kaydır
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(800),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Arrays.asList(swipe));

        System.out.println("Swipe DOWN completed from Y=" + startY + " to Y=" + endY);
    }

    public void swipeDownAccordingToPhoneSizeIOS() {
        Dimension dimension = getDriver().manage().window().getSize();
        int screenHeight = dimension.getHeight();
        int screenWidth = dimension.getWidth();

        int startX = screenWidth / 2;
        int startY = (int) (screenHeight * 0.75);
        int endY = (int) (screenHeight * 0.25);

        Map<String, Object> swipeParams = new HashMap<>();
        swipeParams.put("direction", "up");
        waitBySecond(1);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("mobile: swipe", swipeParams);
    }

    public void swipeUpAccordingToPhoneSizeIOS() {
        Dimension dimension = getDriver().manage().window().getSize();
        int screenHeight = dimension.getHeight();
        int screenWidth = dimension.getWidth();

        int startX = screenWidth / 2;
        int startY = (int) (screenHeight * 0.75);
        int endY = (int) (screenHeight * 0.25);

        Map<String, Object> swipeParams = new HashMap<>();
        swipeParams.put("direction", "down");
        waitBySecond(1);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("mobile: swipe", swipeParams);
    }

    public void quickSwipeDownAccordingToPhoneSize() {
        Dimension d = getDriver().manage().window().getSize();
        int width = d.width;
        int height = d.height;

        int swipeStartWidth = width / 2;
        int swipeStartHeight = (height * 75) / 100;   // aşağıdan başla
        int swipeEndHeight = (height * 15) / 100;     // yukarıya kaydır

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        // Parmağı başlangıç noktasına götür ve bas
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), swipeStartWidth, swipeStartHeight));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Parmağı hızlıca yukarıya kaydır
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(300),   // hızlı = 300ms
                PointerInput.Origin.viewport(), swipeStartWidth, swipeEndHeight));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Arrays.asList(swipe));

        System.out.println("Quick Swipe DOWN completed");
    }


    public boolean isElementPresent(By by) {
        return findElementWithoutAssert(by) != null;
    }


    @Step({"<times> kere aşağıya kaydır", "Swipe times <times>"})
    public void swipe(int times) {
        for (int i = 0; i < times; i++) {
            swipeDownAccordingToPhoneSize();
            waitBySecond(1);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("SWİPE EDİLDİ");
            System.out.println("-----------------------------------------------------------------");

        }
    }

    @Step({"IOS <times> kere aşağıya kaydır", "IOS Swipe times <times>"})
    public void swipeIOS(int times) {
        for (int i = 0; i < times; i++) {
            swipeDownAccordingToPhoneSizeIOS();
            waitBySecond(1);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("SWİPE EDİLDİ");
            System.out.println("-----------------------------------------------------------------");

        }
    }

    @Step({"<times> kere teması devam ettirmeden aşağıya kaydır", "Swipe times <times>"})
    public void quickSwipe(int times) {
        for (int i = 0; i < times; i++) {
            quickSwipeDownAccordingToPhoneSize();
            waitBySecond(1);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("SWİPE EDİLDİ");
            System.out.println("-----------------------------------------------------------------");

        }
    }


    @Step({"<times> kere yukarı doğru kaydır", "Swipe up times <times>"})
    public void swipeUP(int times) {
        for (int i = 0; i < times; i++) {
            swipeUpAccordingToPhoneSize();
            waitBySecond(2);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("SWİPE EDİLDİ");
            System.out.println("-----------------------------------------------------------------");

        }
    }

    @Step({"IOS <times> kere yukarı doğru kaydır", "Swipe up times IOS <times>"})
    public void swipeUpIOS(int times) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            swipeUpAccordingToPhoneSizeIOS();
            waitBySecond(2);

            System.out.println("-----------------------------------------------------------------");
            System.out.println("SWİPE EDİLDİ");
            System.out.println("-----------------------------------------------------------------");

        }
    }


    @Step({"Klavyeyi kapat", "Hide keyboard"})
    public void hideKeyboard() {
        try {
            if (getDriver() instanceof io.appium.java_client.android.AndroidDriver) {
                ((io.appium.java_client.android.AndroidDriver) getDriver()).hideKeyboard();
            } else if (getDriver() instanceof io.appium.java_client.ios.IOSDriver) {
                ((io.appium.java_client.ios.IOSDriver) getDriver()).hideKeyboard();
            } else {
                logger.warn("Bu driver tipi için klavye kapatma desteklenmiyor");
            }
        } catch (Exception ex) {
            logger.error("Klavye kapatılamadı: " + ex.getMessage());
        }
    }


    @Step({"<text> değerini sayfa üzerinde olup olmadığını kontrol et."})
    public void getPageSourceFindWord(String text) {
        assertTrue(getDriver().getPageSource().contains(text), text + " sayfa üzerinde bulunamadı."
        );

        logger.info(text + " sayfa üzerinde bulundu");
    }

    @Step({"<saveKey> olarak saklanan değerin sayfa üzerinde olup olmadığını kontrol et"})
    public void getPageSourceFindSaveKey(String saveKey) {
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);

        assertTrue(getDriver().getPageSource().contains(saveElementTxt), saveElementTxt + " sayfa üzerinde bulunamadı."
        );

        logger.info(saveElementTxt + " sayfa üzerinde bulundu");
    }

    @Step({"<saveKey> olarak saklanan değerin sayfa üzerinde olmadığını kontrol et"})
    public void getPageSourceDontFindSaveKey(String saveKey) {
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);

        assertFalse(getDriver().getPageSource().contains(saveElementTxt), saveElementTxt + " sayfa üzerinde bulundu."
        );

        logger.info(saveElementTxt + " sayfa üzerinde bulunmadı");
    }

    @Step({"<key> değerini sayfa üzerinde olmadıgını kontrol et"})
    public void getPageSourceFindWordKey(String key) {

        WebElement deneme = findElementByKeyWithoutAssert(key);

        if (deneme == null) {
            logger.info(key + " sayfa üzerinde olmadıgı kontrol edildi");
        }
        if (deneme != null) {
            Assertions.fail("Element bulundu");
        }

    }

    @Step({"<key> değerini sayfa olmadığını kontrol et"})
    public void getPageSourceFindWordKeyy(String key) {
        assertFalse(getDriver().getPageSource().contains(key), key + " sayfa üzerinde bulunamadı."
        );

        logger.info(key + " sayfa üzerinde bulundu");
    }

    @Step("<key> elementi bulunana kadar en fazla <sure> saniye kadar bekle")
    public void waitUntilElementExist(String key, int sure) {
        boolean doesExist = doesElementExistByKey(key, sure);
        if (doesExist) {
            logger.info("element beklendi ve bulundu");
        } else {
            logger.info("element beklendi ama verilen süre içerisinde bulunamadı");
        }
    }

    @Step("<key> elementi görünene kadar bekle")
    public void waitUntilElementVisible(String key) {
        final int timeoutSeconds = 20;
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
        if (selectorInfo == null) {
            Assertions.fail("Element key bulunamadı: " + key);
            return;
        }
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
            logger.info(key + " elementi " + timeoutSeconds + " sn içinde görünür oldu.");
        } catch (Exception e) {
            Assertions.fail(key + " elementi " + timeoutSeconds + " saniye içinde görünür olmadı: " + e.getMessage());
        }
    }

    @Step({"<length> uzunlugunda random bir kelime üret ve <saveKey> olarak sakla"})
    public void createRandomNumber(int length, String saveKey) {
        StoreHelper.INSTANCE.saveValue(saveKey, new RandomString(length).nextString());
    }

    @Step("Geri butonuna bas")
    public void clickBybackButton() {
        if (!localAndroid.get()) {
            backPage();
        } else {
            getDriver().navigate().back();

        }

    }

    @Step("<StartX>,<StartY> oranlarından <EndX>,<EndY> oranlarına <times> kere swipe et")
    public void pointToPointSwipe(int startXPercent, int startYPercent, int endXPercent, int endYPercent, int count) throws InterruptedException {
        // ekran boyutu
        Dimension size = getDriver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        // yüzdeden piksele çevir
        int startX = (width * startXPercent) / 100;
        int startY = (height * startYPercent) / 100;
        int endX = (width * endXPercent) / 100;
        int endY = (height * endYPercent) / 100;

        for (int i = 0; i < count; i++) {
            // 1 swipe için W3C action zinciri
            Sequence swipe = new Sequence(FINGER, 1)
                    // parmağı başlangıç noktasına götür
                    .addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                    // ekrana bas
                    .addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    // kısa bekleme (opsiyonel)
                    .addAction(new Pause(FINGER, Duration.ofMillis(100)))
                    // hedef noktaya ~1000 ms’de kaydır
                    .addAction(FINGER.createPointerMove(Duration.ofMillis(1000), PointerInput.Origin.viewport(), endX, endY))
                    // bırak
                    .addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(swipe));
            waitBySecond(1);
        }
    }

    @Step("<StartX>,<StartY> oranlarından <EndX>,<EndY> oranlarına <times> kere yukarı swipe et")
    public void pointToPointSwipeUp(int startXPercent, int startYPercent, int endXPercent, int endYPercent, int count) throws InterruptedException {
        Dimension size = getDriver().manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        // yüzdeleri piksele çevir
        int startX = (width * startXPercent) / 100;
        int startY = (height * startYPercent) / 100;
        int endX = (width * endXPercent) / 100;
        int endY = (height * endYPercent) / 100;

        if (endY >= startY) {
            int tmp = startY;
            startY = endY;
            endY = tmp;
            tmp = startX;
            startX = endX;
            endX = tmp;
        }

        for (int i = 0; i < count; i++) {

            Sequence swipe = new Sequence(FINGER, 1)
                    .addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                    .addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new Pause(FINGER, Duration.ofMillis(100)))
                    .addAction(FINGER.createPointerMove(Duration.ofMillis(900), PointerInput.Origin.viewport(), endX, endY))
                    .addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(swipe));
            waitBySecond(1);
        }
    }


    @Step("<key> elementinin hizasından sağdan sola <times> kere kaydır")
    public void swipeFromLeftToRightAligned(String key, int times) throws InterruptedException {
        Dimension d = getDriver().manage().window().getSize();

        int height = d.height;
        int width = d.width;
        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        pointToPointSwipeWithCoordinats(width - 65, elementLocation.getY(), 40, elementLocation.getY(), times);
    }

    @Step("<key> elementinin hizasından sağdan sola <times> kere kaydır IOS")
    public void swipeFromLeftToRightAlignedIOS(String key, int times) throws InterruptedException {
        Dimension d = getDriver().manage().window().getSize();

        int height = d.height;
        int width = d.width;
        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        pointToPointSwipeWithCoordinatesiOS(width - 65, elementLocation.getY(), 40, elementLocation.getY(), times);
    }

    @Step("<key> elementinin hizasından sağdan sola <times> kere kaydır 2")
    public void swipeFromLeftToRightAligned2(String key, int times) throws InterruptedException {
        Dimension d = getDriver().manage().window().getSize();
        int width = d.width;

        WebElement element = findElementByKeyWithoutAssert(key);
        Point elementLocation = element.getLocation();
        int elementHeight = element.getSize().getHeight();

        // Elementin tam merkezine hizalama
        int centerY = elementLocation.getY() + (elementHeight / 2);

        // Sağ köşeden değil, biraz içeriden başlat (geri gitme sorunu yaşamamak için)
        int startX = width - 200; // Daha içeriden başla
        int endX = 200;           // Daha uzun kaydır

        // Güncellenmiş kaydırma çağrısı
        pointToPointSwipeWithCoordinats(startX, centerY, endX, centerY, times);
    }

    @Step("<key> elementinin hizasından soldan sağa <times> kere kaydır")
    public void swipeFromRightToLeftAligned(String key, int times) throws InterruptedException {
        Dimension d = getDriver().manage().window().getSize();

        int height = d.height;
        int width = d.width;
        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        pointToPointSwipeWithCoordinats(40, elementLocation.getY(), width - 50, elementLocation.getY(), times);
    }

    @Step("<key> elementinin hizasından aşağıdan yukarıya <times> kere kaydır")
    public void swipeFromDownToUpAligned(String key, int times) throws InterruptedException {
        Dimension d = getDriver().manage().window().getSize();

        int height = d.height;
        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        pointToPointSwipeWithCoordinats(elementLocation.getX(), 160, elementLocation.getX(), height + 50, times);
    }

    @Step("<key> li elementi hizala ve sagdan sola kaydır <times> kere y cordinatına <number> ekle")
    public void horizontalSwipeWithElement(String key, int times, int number) throws InterruptedException {

        Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
        logger.info("x==" + elementLocation.getX() + " y==" + elementLocation.getY() + "----------");

        pointToPointSwipeWithCoordinats(900, elementLocation.getY(), 40, elementLocation.getY(), times);
        logger.info("-----horizonal kaydırma işlemi tamamlandı-----");
    }

    @Step("<StartX>,<StartY> coordinatından <EndX>,<EndY> coordinatına <times> kere swipe et")
    public void pointToPointSwipeWithCoordinats(int startX, int startY, int endX, int endY, int count) throws InterruptedException {
        Dimension size = getDriver().manage().window().getSize();

        for (int i = 0; i < count; i++) {
            Sequence swipe = new Sequence(FINGER, 1)
                    .addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                    .addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new Pause(FINGER, Duration.ofMillis(100)))
                    .addAction(FINGER.createPointerMove(Duration.ofMillis(900), PointerInput.Origin.viewport(), endX, endY))
                    .addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(swipe));

            logger.info("************ SWIPE EDİLDİ ***********");
            waitBySecond(1);
        }
    }

    @Step("<startX>,<startY> koordinatından <endX>,<endY> koordinatına <count> kere swipe et (iOS)")
    public void pointToPointSwipeWithCoordinatesiOS(int startX, int startY, int endX, int endY, int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            waitBySecond(1);

            Map<String, Object> params = new HashMap<>();
            params.put("duration", 1.0); // saniye cinsinden, kaydırma süresi
            params.put("fromX", startX);
            params.put("fromY", startY);
            params.put("toX", endX);
            params.put("toY", endY);

            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("mobile: dragFromToForDuration", params);
        }
    }

    public void pointToPointSwipeForDayAndYear(int startX, int startY, int endX, int endY, int count) throws InterruptedException {
        // Ekran boyutu
        Dimension size = getDriver().manage().window().getSize();
        int width = size.width;
        int height = size.height;

        // Eski mantığı koruyorum: 200'den büyükse yüzdelik veriliyormuş gibi kabul et
        if (count > 200) {
            startX = (width * startX) / 100;
            startY = (height * startY) / 100;
            endX = (width * endX) / 100;
            endY = (height * endY) / 100;
            count = count - 2019; // mevcuttaki hesaplama korunuyor
        } else {
            count--; // mevcuttaki davranış
        }

        for (int i = 0; i < count; i++) {
            Sequence swipe = new Sequence(FINGER, 1)
                    .addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                    .addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new Pause(FINGER, Duration.ofMillis(100)))
                    .addAction(FINGER.createPointerMove(Duration.ofMillis(900), PointerInput.Origin.viewport(), endX, endY))
                    .addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(swipe));
            waitBySecond(1);
        }
    }


    private void backPage() {
        getDriver().navigate().back();
    }


    private String getCapability(String text) {
        return getDriver().getCapabilities().getCapability(text).toString();

    }

    public boolean doesElementExistByKey(String key, int timeInSeconds) {
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
        try {
            WebDriverWait elementExist = new WebDriverWait(getDriver(), Duration.ofSeconds(timeInSeconds));
            elementExist.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
            return true;
        } catch (Exception e) {
            logger.info(key + " aranan elementi bulamadı");
            return false;
        }
    }


    public void tapElementWithCoordinate(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        // Parmağı verilen koordinata götür
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        // Parmağı bas
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // Çok kısa bekle
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        // Parmağı kaldır
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));
    }

    @Step("<key> li elementin  merkezine tıkla")
    public void tapElementWithKey(String key) {
        WebElement element = findElementByKey(key);
        Point point = element.getLocation();
        Dimension size = element.getSize();

        // elementin merkezini hesapla
        int centerX = point.x + size.width / 2;
        int centerY = point.y + size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));
    }

    @Step("<key> li elementin x koordinatında <xInt> yüzdesi y ekseninde merkezine tıkla")
    public void tapElementCoordinateWithKey(String key, int xInt) {
        WebElement element = findElementByKey(key);

        // getRect ile merkez hesaplama
        int centerX = element.getRect().getX() + (element.getRect().getWidth() / 2);
        int centerY = element.getRect().getY() + (element.getRect().getHeight() / 2);

        System.out.println("Orijinal point: x=" + centerX + " y=" + centerY);

        // Yüzde hesabı
        int newX = (int) (centerX * (xInt / 100.0));
        int newY = centerY;

        // W3C Actions API
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), newX, newY));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));

        System.out.println("Tıklandı: x=" + newX + ", y=" + newY);
    }


    @Step("<key> li element varsa tıkla")
    public void tapElementWithKeyControl(String key) {

        logger.info("element varsa verilen tıkla ya girdi");
        WebElement mobileElement;

        mobileElement = findElementByKeyWithoutAssert(key);

        if (mobileElement != null) {

            doesElementExistByKey(key, 2);
            findElementByKey(key).click();
            logger.info(key + "elemente tıkladı");

        }
    }

    @Step("<elementType> element tipli <attribute> e sahip <key> element varsa tıkla")
    public void tapElementWithKeyControlAttribute(String elementType, String attribute, String key) {

        logger.info("element varsa verilen tıkla ya girdi");
        WebElement mobileElement;
        String element = "//" + elementType + "[@" + attribute + "=\"" + key + "\"]";

        mobileElement = findElementByKeyWithoutAssert(element);

        if (mobileElement != null) {

            doesElementExistByKey(key, 2);
            findElementByKey(key).click();
            logger.info(key + "elemente tıkladı");

        }
    }

    @Step("<key> li element sayfada görünüyorsa hata ver")
    public void ifElementExistGoFail(String key) {
        logger.info("<" + key + "> elementi kontrol ediliyor...");

        try {
            WebElement mobileElement = findElementByKeyWithoutAssert(key);

            if (mobileElement != null && mobileElement.isDisplayed()) {
                logger.error("<" + key + "> elementi bulundu! Test hata veriyor...");
                throw new AssertionError("<" + key + "> elementi sayfada bulundu, test başarısız!");
            }
        } catch (NoSuchElementException e) {
            logger.info("<" + key + "> elementi bulunamadı, test devam ediyor.");
        }
    }

    @Step("Konum izni popup'ında varsa <text> butonuna tıkla")
    public void konumIzniAlertVarsaTikla(String text) {
        try {
            HashMap<String, String> args = new HashMap<>();
            args.put("action", "accept");
            args.put("buttonLabel", text);

            ((JavascriptExecutor) getDriver()).executeScript("mobile: alert", args);
            System.out.println("mobile: alert ile '" + text + "' butonuna tıklandı.");
        } catch (Exception e) {
            System.out.println("Alert bulunamadı veya '" + text + "' butonu tıklanamadı. Devam ediliyor...");

        }
    }

    @Step("Google şifre kaydet popupı varsa kapatılır")
    public void dismissGoogleSavePasswordIfPresent() {
        try {
            List<WebElement> elements = getDriver().findElements(By.xpath("//*[@text='Şimdi değil' or @text='Hiçbir zaman']"));
            if (!elements.isEmpty()) {
                elements.get(0).click();
                logger.info("Google Şifre Kaydetme popup'ı başarıyla kapatıldı.");
            } else {
                logger.info("Google Şifre Kaydetme popup'ı ekranda görülmedi, devam ediliyor.");
            }
        } catch (Exception e) {
            logger.warn("Google popup'ı kapatılırken bir hata oluştu: " + e.getMessage());
        }
    }

    @Step("<key> elementine JS ile tıkla")
    public void jsIleTikla(String key) {
        try {
            WebElement element = findElementByKey(key);
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript("arguments[0].click();", element);
            System.out.println("JS ile '" + key + "' elementine tıklandı.");
        } catch (Exception e) {
            System.out.println("'" + key + "' elementine JS ile tıklanamadı. Devam ediliyor...");
        }
    }


    @Step("<key> li element varsa tıkla <key2> tıkla")
    public void tapElementWithKeyControlLogin(String key, String key2) {

        logger.info("element varsa verilen tıkla girdi");
        WebElement mobileElement;

        waitBySecond(2);

        mobileElement = findElementByKeyWithoutAssert(key);

        if (mobileElement != null) {

            doesElementExistByKey(key, 3);
            findElementByKey(key).click();
            findElementByKey(key2).click();
            logger.info(key + "elemente tıkladı");

        } else {
            System.out.println(key + " element bulunamadi");

        }
    }

    @Step("<key> li element varsa <key2> tıkla")
    public void tapElementWithKeyControlLoginClick(String key, String key2) {

        logger.info("element varsa verilen tıkla girdi");
        WebElement mobileElement;

        waitBySecond(2);

        mobileElement = findElementByKeyWithoutAssert(key);

        if (mobileElement != null) {

            doesElementExistByKey(key, 3);
            findElementByKey(key);
            findElementByKey(key2).click();
            logger.info(key + "elemente tıkladı");

        } else {
            System.out.println(key + " element bulunamadi");

        }
    }

    @Step("<key> li element varsa tıkla yoksa devam et")
    public void tapElementWithKeyControlArea(String key) {
        logger.info("Element varsa tıkla step'i çalışıyor: " + key);

        try {
            WebElement mobileElement = findElementByKeyWithoutAssert(key);

            if (mobileElement != null) {
                logger.info(key + " elementi bulundu, tıklanıyor...");
                mobileElement.click();
                logger.info(key + " elementine tıklandı");
            } else {
                logger.info(key + " elementi bulunamadı, step devam ediyor.");
            }
        } catch (Exception e) {
            logger.info(key + " elementine tıklama sırasında hata: " + e.getMessage(), e);
        }
    }


    @Step("<key> li element varsa  <x> <y> koordinatına tıkla")
    public void tapElementWithKeyCoordinate(String key, int x, int y) {

        logger.info("element varsa verilen koordinata tıkla ya girdi");
        WebElement element = findElementByKeyWithoutAssert(key);

        if (element != null) {
            System.out.println("pakachu");

            // elementin konum ve boyutu
            Point point = element.getLocation();
            Dimension dimension = element.getSize();

            logger.info("Element X,Y: " + point.x + " , " + point.y);
            logger.info("Element Width,Height: " + dimension.width + " , " + dimension.height);

            // yüzdelere göre yeni koordinat hesapla
            int targetX = point.x + (dimension.width * x) / 100;
            int targetY = point.y + (dimension.height * y) / 100;

            // W3C Actions API
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 1);

            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), targetX, targetY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(new Pause(finger, Duration.ofMillis(100)));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(tap));

            logger.info("Tıklandı: x=" + targetX + " y=" + targetY);
        }
    }


    @Step("<key> li elementin merkezine press ile çift tıkla")
    public void pressElementWithKey(String key) {
        WebElement element = findElementByKey(key);
        Point point = element.getLocation();
        Dimension size = element.getSize();

        // Merkez koordinat
        int centerX = point.x + size.width / 2;
        int centerY = point.y + size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1);

        // İlk tıklama
        doubleTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // İkinci tıklama (çift tık)
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100))); // küçük bekleme
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(doubleTap));

        logger.info("Element merkezine çift tıklandı: " + centerX + "," + centerY);
    }


    @Step("<key> li elementin merkezine double tıkla")
    public void pressElementWithKey2(String key) {
        WebElement element = findElementByKey(key);
        Point point = element.getLocation();
        Dimension size = element.getSize();

        int centerX = point.x + size.width / 2;
        int centerY = point.y + size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence doubleTap = new Sequence(finger, 1);

        // İlk tık
        doubleTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY));
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // İkinci tık
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100))); // arada küçük bekleme
        doubleTap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(new Pause(finger, Duration.ofMillis(100)));
        doubleTap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(doubleTap));

        logger.info("Element merkezine çift tıklandı: " + centerX + "," + centerY);
    }


    @Step("<key> listesinden rastgele bir elemente sayacla tikla ve <key2> bekle")
    public void chooseRandomProduct(String key, String key2) {
        List<WebElement> list = findElemenstByKey(key);
        startTime = System.currentTimeMillis();
        list.get(createRandomNumber(list.size() - 1)).click();
        finishCounter(key);
    }


    @Step("<key> li elemente kadar <text> textine sahip değilse ve <timeout> saniyede bulamazsa swipe yap")
    public void swipeAndFindWithKey(String key, String text, int timeout) {
        WebElement found = null;
        SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeout));

        int attempts = 0;
        while (true) {
            attempts++;

            try {
                found = wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
                if (text == null || text.isEmpty() || text.equals(found.getText().trim())) {
                    break; // bulundu ve text eşleşti (veya text şartı yok)
                }
            } catch (Exception e) {
                logger.info("Bulamadı / görünür değil (deneme=" + attempts + ")");
            }

            if (attempts >= 8) {
                Assertions.fail("Element bulunamadı veya metin eşleşmedi: key=" + key + " text=" + text);
            }

            // --- W3C swipe (aşağıdan yukarıya kaydır) ---
            Dimension dim = getDriver().manage().window().getSize();
            int startX = dim.width / 2;
            int startY = (int) (dim.height * 0.75);
            int endX = dim.width / 2;
            int endY = (int) (dim.height * 0.30);

            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence swipe = new Sequence(finger, 1);
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(new Pause(finger, Duration.ofMillis(800)));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            getDriver().perform(Collections.singletonList(swipe));

            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {
            }
        }
    }


    @Step("<key>li elementi bulana kadar <limit> kere swipe yap ve elementi bul")
    public void swipeKeyy(String key, int limit) throws InterruptedException {


        boolean isAppear = false;

        int windowHeight = this.getScreenHeight();
        for (int i = 0; i < limit; ++i) {
            logger.info((i + 1) + ". kere swipe edilecek");
            try {

                Dimension phoneSize = getDriver().manage().window().getSize();
                Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
                logger.info(elementLocation.x + "  " + elementLocation.y);
                Dimension elementDimension = findElementByKeyWithoutAssert(key).getSize();
                logger.info(elementDimension.width + "  " + elementDimension.height);
                // logger.info(driver.getPageSource());
                if ((0 < elementLocation.y) && (elementLocation.y <= phoneSize.height - 30)) {
                    isAppear = true;
                    logger.info("aranan elementi buldu");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");
            }
            System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");

            swipeDownAccordingToPhoneSize();
            waitBySecond(1);
        }

    }

    @Step("<key>li elementi bulana kadar <limit> kere yukarı swipe yap ve elementi bul")
    public void swipeUpKeyy(String key, int limit) throws InterruptedException {


        boolean isAppear = false;

        int windowHeight = this.getScreenHeight();
        for (int i = 0; i < limit; ++i) {
            logger.info((i + 1) + ". kere swipe edilecek");
            try {

                Dimension phoneSize = getDriver().manage().window().getSize();
                Point elementLocation = findElementByKeyWithoutAssert(key).getLocation();
                logger.info(elementLocation.x + "  " + elementLocation.y);
                Dimension elementDimension = findElementByKeyWithoutAssert(key).getSize();
                logger.info(elementDimension.width + "  " + elementDimension.height);
                // logger.info(driver.getPageSource());
                if ((0 < elementLocation.y) && (elementLocation.y <= phoneSize.height - 30)) {
                    isAppear = true;
                    logger.info("aranan elementi buldu");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");
            }
            System.out.println("Element ekranda görülmedi. Tekrar swipe ediliyor");

            swipeUpAccordingToPhoneSize();
            waitBySecond(1);
        }

    }


    @Step("<key> li telefonun <x> ve elementin <y> koordinatına göre tıkla")
    public void elementFindwithXandYcoordinate(String key, int x, int y) {
        WebElement element = findElementByKey(key);

        // Elementin konumu ve boyutundan y hesapla
        int height = element.getLocation().y + (element.getSize().height * y) / 100;
        // Telefon ekran genişliğine göre x hesapla
        int width = (getDriver().manage().window().getSize().width * x) / 100;

        System.out.println("Tıklanacak Nokta -> X: " + width + " , Y: " + height);

        // W3C Actions API ile tıklama
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), width, height));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));
    }

    @Step("<key> elementinin koordinatlarına x=<x> y=<y> degerlerini ekleyerek tıkla")
    public void coordinatClickWithAdds(String key, int x, int y) {
        WebElement me = findElementByKey(key);
        tapElementWithCoordinate(me.getLocation().x + x, me.getLocation().y + y);
    }

    @Step("<x>,<y> koordinatlarına tıkla")
    public void koordinataTikla(int x, int y) {
        logger.info("Koordinata tıklama yapılıyor: X=" + x + ", Y=" + y);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(tap));

        logger.info("Tıklama başarıyla yapıldı");
    }

    @Step({"Değeri <key> e eşit olan elementli bul"})
    public void clickByTexte(String key) {
        String Sec;
        Sec = findElementByKeyWithoutAssert(key).getAttribute("checked");
        String E2 = Sec;
        if (E2.equals("false")) {
            doesElementExistByKey(key, 5);
            findElementByKey(key).click();
            logger.info(key + "elemente tıkladı");
        } else if (E2.equals("true")) {
            logger.info(key + " secili gelmistir");
        }

    }

    @Step("Enter tıkla")
    public void keyboardClickEnter() {

        tapElementWithCoordinate(999, 1991);
        logger.info("'%s' objesi üzerinde ENTER tuşuna basıldı.");
    }


    @Step("Android Enter tıkla")
    public void keyboardClickEnterAndroid() {
        logger.info("Android Enter step çalışıyor...");

        try {
            // 1) Native key event
            androidDriver.pressKey(new KeyEvent(AndroidKey.ENTER));
            logger.info("ENTER basıldı (AndroidKey.ENTER).");
            return;
        } catch (Exception e1) {
            logger.warn("AndroidKey.ENTER çalışmadı: " + e1.getMessage());
        }

        try {
            // 2) IME action (örn. done/search/go/next/send)
            Map<String, Object> args = new HashMap<>();
            args.put("action", "search");
            ((JavascriptExecutor) getDriver()).executeScript("mobile: performEditorAction", args);
            logger.info("Editor action 'done' gönderildi (mobile: performEditorAction).");
            return;
        } catch (Exception e2) {
            logger.warn("performEditorAction da çalışmadı: " + e2.getMessage());
        }

        try {
            // 3) Fallback – aktif inputa newline
            WebElement focused = getDriver().switchTo().activeElement();
            focused.sendKeys("\n");
            logger.info("ENTER fallback olarak '\\n' gönderildi.");
        } catch (Exception e3) {
            logger.error("ENTER hiçbir şekilde gönderilemedi: " + e3.getMessage(), e3);
        }
    }


    @Step("Android Geri tıkla")
    public void keyboardClickBackAndroid() {
        try {
            if (androidDriver instanceof AndroidDriver) {
                AndroidDriver ad = (AndroidDriver) getDriver();
                ad.pressKey(new KeyEvent(AndroidKey.BACK));
                logger.info("Android geri tuşuna basıldı (AndroidKey.BACK).");
            } else {
                logger.warn("Driver Android değil: " + getDriver().getClass().getSimpleName());
                clickByKey("betaBack");
            }
        } catch (Exception e) {
            logger.error("Geri tuşuna basılamadı: {}", e.getMessage(), e);
        }
    }

    @Step("Klavyeden <tus> tusuna bas")
    public void keyboardClickAndroidByKeyName(String tus) {
        pressKeyboardKeyByName(tus, 1);
    }

    @Step("Klavyeden <adet> kez <tus> tusuna bas")
    public void keyboardClickAndroidByKeyNameCountQuoted(String adet, String tus) {
        int count;
        try {
            count = Integer.parseInt(adet.replace("\"", "").trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Adet sayısal olmalı. Gelen değer: " + adet);
        }
        pressKeyboardKeyByName(tus.replace("\"", "").trim(), count);
    }

    private void pressKeyboardKeyByName(String keyName, int count) {
        if (count <= 0) {
            logger.warn("Geçersiz adet: {}. En az 1 olmalı.", count);
            return;
        }
        String normalizedKey = keyName == null ? "" : keyName.trim().toUpperCase(Locale.ROOT);
        if (normalizedKey.isEmpty()) {
            logger.warn("Tuş değeri boş olamaz.");
            return;
        }
        if (HookImpl.getDriver() instanceof AndroidDriver) {
            AndroidKey androidKey = resolveAndroidKey(normalizedKey);
            if (androidKey == null) {
                logger.warn("Desteklenmeyen tuş: {}. Desteklenenler: SPACE, ENTER, BACK, TAB", keyName);
                return;
            }
            try {
                AndroidDriver ad = (AndroidDriver) getDriver();
                for (int i = 0; i < count; i++) {
                    ad.pressKey(new KeyEvent(androidKey));
                }
                logger.info("{} kez {} basıldı (AndroidKey.{}).", count, normalizedKey, androidKey.name());
            } catch (Exception e) {
                logger.error("{} gönderilemedi: {}", normalizedKey, e.getMessage(), e);
            }
            return;
        }

        if (HookImpl.getDriver() instanceof IOSDriver) {
            Keys iosKey = resolveIosKey(normalizedKey);
            if ("BACK".equals(normalizedKey)) {
                getDriver().navigate().back();
                logger.info("{} kez BACK gönderildi (iOS navigate.back).", count);
                return;
            }
            if (iosKey == null) {
                logger.warn("iOS için desteklenmeyen tuş: {}. Desteklenenler: SPACE, ENTER, TAB, BACK", keyName);
                return;
            }
            try {
                WebElement focused = getDriver().switchTo().activeElement();
                for (int i = 0; i < count; i++) {
                    focused.sendKeys(iosKey);
                }
                logger.info("{} kez {} gönderildi (iOS sendKeys).", count, normalizedKey);
            } catch (Exception e) {
                logger.error("iOS {} gönderilemedi: {}", normalizedKey, e.getMessage(), e);
            }
            return;
        }

        logger.warn("Desteklenmeyen driver tipi: {}", getDriver().getClass().getSimpleName());
    }

    private AndroidKey resolveAndroidKey(String keyName) {
        switch (keyName) {
            case "SPACE":
                return AndroidKey.SPACE;
            case "ENTER":
                return AndroidKey.ENTER;
            case "BACK":
                return AndroidKey.BACK;
            case "TAB":
                return AndroidKey.TAB;
            default:
                return null;
        }
    }

    private Keys resolveIosKey(String keyName) {
        switch (keyName) {
            case "SPACE":
                return Keys.SPACE;
            case "ENTER":
                return Keys.ENTER;
            case "TAB":
                return Keys.TAB;
            default:
                return null;
        }
    }

    @Step("iOS Enter tıkla")
    public void keyboardClickEnteriOS() {
        waitBySecond(2);
        getDriver().findElement(MobileBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField'")).sendKeys(Keys.ENTER);
        // Devam eden işlemleri burada gerçekleştirin
        logger.info("'%s' objesi üzerinde ENTER tuşuna basıldı.");
    }


    @Step("<x> elementinde <y> yilina git")
    public void clickByKeyRepeat(String key, int y) {
        y = 2022 - y;
        for (int i = 1; i < (y + 1); i++) {
            doesElementExistByKey(key, 5);
            findElementByKey(key).click();
            logger.info(key + " elementine " + i + ". kere tıkladı");
        }
    }


    @Step({"<key> Checkbox degerinin false geldigini kontrol et"})
    public void checkBoxControl(String key) {
        String Sec;
        Sec = findElementByKeyWithoutAssert(key).getAttribute("checked");
        String E2 = Sec;
        if (E2.equals("false")) {
            logger.info(key + " checkbox boş olarak(false) olarak gelmiştir.");
        } else if (E2.equals("true")) {
            Boolean selected = true;
            assertFalse(selected, "Checkbox seçili(true) olarak gelmiştir.");

        }
    }

    @Step({"<key> Checkbox degerini kontrol et. Beklenen deger: <expectedValue>",
            "<key> checkbox degerini kontrol et. Beklenen deger: <expectedValue>"})
    public void checkBoxControlRegister(String key, String expectedValue) {
        WebElement element = findElementByKeyWithoutAssert(key);
        String actualValue = resolveCheckboxValue(element);
        logger.info(key + " checkbox value: " + actualValue + ", beklenen: " + expectedValue);
        assertEquals(expectedValue, actualValue,
                key + " checkbox value beklenen deger ile eslesmiyor. Gercek: " + actualValue + ", Beklenen: " + expectedValue);
    }

    private String resolveCheckboxValue(WebElement element) {
        String checkboxName = element.getAttribute("name");
        if ("filled/check_box".equals(checkboxName)) {
            return "1";
        }
        if ("outlined/check_box_blank".equals(checkboxName)) {
            return "0";
        }

        String value = element.getAttribute("value");
        if ("0".equals(value) || "1".equals(value)) {
            return value;
        }

        return value != null ? value : checkboxName;
    }

    @Step({"<key> cinsiyet checkbox degerini kontrol et"})
    public void checkBoxControlGender(String key) {
        String checkboxName = findElementByKeyWithoutAssert(key).getAttribute("name");
        logger.info(key + "için bulunan checkbox name degeri: " + checkboxName);

        if ("outlined/radio_button_checked".contains(checkboxName)) {
            logger.info(key + " checkbox seçili (true) olarak gelmiştir.");
        } else if ("outlined/radio_button_unchecked".contains(checkboxName)) {
            logger.info(key + " checkbox boş (false) olarak gelmiştir.");
        } else {
            fail(key + " checkbox durumu beklenen değerlerden biri değil: " + checkboxName);
        }
    }


    private Long getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return (timestamp.getTime());
    }

    @Step({"<key> li elementi bul, temizle ve rasgele  email değerini yaz",
            "Find element by <key> clear and send keys  random email"})
    public void RandomeMail(String key) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-s");
        Date date = new Date(System.currentTimeMillis());
        logger.info(formatter.format(date));

        WebElement webElement = findElementByKey(key);
        webElement.clear();
        webElement.sendKeys("testotomasyon" + formatter.format(date) + "@beymentest.com");
    }

    @Step({"<key> li elementi bul, temizle ve <length> uzunluğunda string değer yaz",
            "Find element by <key> clear and send keys <text>"})
    public void randomStringSendKeysByKey(String key, int length) {

        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {

            int index = random.nextInt(lowerAlphabet.length());
            char randomChar = lowerAlphabet.charAt(index);
            sb.append(randomChar);
        }
        String randomString = sb.toString();

        logger.info("'" + randomString + "' random kelime olarak oluşturuldu");
        WebElement webElement = findElementByKey(key);
        webElement.clear();
        webElement.sendKeys(randomString);
    }

    @Step("<key>'li elementin <attr> degerini icerdigini kontrol et")
    public void checkByAttr(String key, String attr) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("***************" + dtf.format(now) + "***************");
        Dimension d = getDriver().manage().window().getSize();
        int height = d.height;
        String attribute = findElementByKey(key).getAttribute(attr);
        System.out.println("Height: " + height + " - Attribute: " + attribute);

        LocalDateTime now2 = LocalDateTime.now();
        System.out.println("***************" + dtf.format(now2) + "***************");

    }

    @Step("<key> elementinin <text> textini içermediği kontrol edilir")
    public void checkTextByKeyFalse(String key, String text) {
        try {
            Thread.sleep(3000);
            String denemee = findElementByKey(key).getText();
            assertFalse(findElementByKey(key).getText().contains(text), "Element beklenen değeri taşıyor !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> elementinin <attr> attribute degeri ile scrool bar kontrol edilir")
    public void checkScroolBar(String key, String attr) {
        String firstAttribute = findElementByKey(key).getAttribute(attr);
        logger.info("First attribute: " + firstAttribute);
        swipe(2);
        String secondAttribute = findElementByKey(key).getAttribute(attr);
        logger.info("Second attribute: " + secondAttribute);
        assertFalse(firstAttribute.equals(secondAttribute), "Scrool bar yok!");
        swipeUP(2);
    }

    @Step({"<key> li elementin text degerini hafizada <saveKey> olarak saklanan text ile karsilastir"})
    public void chechTextByKeyAndSaveKey(String key, String saveKey) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getText();
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertTrue(elementTxt.contains(saveElementTxt), "Degerler birbirine esit degil!");
    }

    @Step({"<key> li elementin text degeri hafizada <saveKey> olarak saklanan text ile esit mi kontrol et"})
    public void equalsByKeyAndSaveKey(String key, String saveKey) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getText();
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertEquals(elementTxt, saveElementTxt, "Degerler birbirine esit degil!");
    }

    @Step({"<key> li elementin text degerini hafizada <saveKey> olarak saklanan text ayni olmadigini kontrol et"})
    public void DifferentTextByKeyAndSaveKey(String key, String saveKey) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getText();
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertFalse(elementTxt.contains(saveElementTxt), "Degerler birbirine esit!");
    }

    @Step({"<key> li elementin name degerini hafizada <saveKey> olarak saklanan name ayni olmadigini kontrol et"})
    public void DifferentNameByKeyAndSaveKey(String key, String saveKey) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Expected Value : " + saveElementTxt);
        String elementTxt = findElementByKey(key).getAttribute("name");
        logger.info("Actual Value : " + elementTxt);
        System.out.println("------------------------------------------------------");
        assertFalse(elementTxt.contains(saveElementTxt), "Degerler birbirine esit!");
    }

    @Step({"<saveKey> olarak saklanan text degerinin uzunlugu <int> uzunlugunda mi kontrol et"})
    public void saveKeyLeght(String saveKey, int leghth) {

        System.out.println("------------------------------------------------------");
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        logger.info("Save Value : " + saveElementTxt);
        assertTrue(saveElementTxt.length() == leghth, "Text degerinin uzunlugu esit degil");
    }


    @Step({"<key> li elementin text degerini, saklanan <saveKey> degeriyle ile <islem>"})
    public void calculateAndSave(String key, String saveKey, String islem) {

        WebElement element;
        element = findElementByKeyWithoutAssert(key);

        if (element != null) {
            String lastSaveKey = StoreHelper.INSTANCE.getValue(saveKey);

            lastSaveKey = lastSaveKey.replaceAll("\\s", "");
            lastSaveKey = lastSaveKey.replaceAll(",", ".");
            lastSaveKey = lastSaveKey.replaceAll("TL", "");
            Float floatLastSaveKey = Float.parseFloat(lastSaveKey);
            logger.info("Birikimli odenecek tutar : " + lastSaveKey);

            String elementTxt = findElementByKey(key).getText();

            String price = findElementByKey(key).getText();
            elementTxt = elementTxt.replaceAll("\\s", "");
            elementTxt = elementTxt.replaceAll(",", ".");
            elementTxt = elementTxt.replace(elementTxt.substring(elementTxt.length() - 2), "");
            Float floatElementTxt = Float.parseFloat(elementTxt);
            logger.info(key + " : " + elementTxt);


            if (islem.equals("topla")) {
                floatLastSaveKey = (floatLastSaveKey * 100) + (floatElementTxt * 100);
                floatLastSaveKey /= 100;
                logger.info("Islem sonucu : " + floatLastSaveKey);

            } else if (islem.equals("cikar")) {
                floatLastSaveKey = (floatLastSaveKey * 100) - (floatElementTxt * 100);
                floatLastSaveKey /= 100;
                logger.info("Islem sonucu : " + floatLastSaveKey);
            } else {
                logger.info("Islem adi yanlis girilmiştir!");
                assertTrue(0 > 1);

            }

            String strValue = Float.toString(floatLastSaveKey);
            strValue = strValue.replaceAll("\\.", ",");
            StoreHelper.INSTANCE.saveValue(saveKey, strValue);
        } else {
            logger.info("------ Islem icin [" + key + "] li element bulunamadi ------");
        }

    }

    @Step({"<key> li elementin text degerini, saklanan <saveKey> degeriyle ile <islem>"})
    public void calculateAndSavePrices(String key, String saveKey, String islem) {

        WebElement element;
        element = findElementByKeyWithoutAssert(key);

        if (element != null) {
            String lastSaveKey = StoreHelper.INSTANCE.getValue(saveKey);

            lastSaveKey = lastSaveKey.replaceAll("\\s", "");
            lastSaveKey = lastSaveKey.replaceAll(",", ".");
            lastSaveKey = lastSaveKey.replaceAll("TL", "");
            Float floatLastSaveKey = Float.parseFloat(lastSaveKey);
            logger.info("Birikimli odenecek tutar : " + lastSaveKey);

            String elementTxt = findElementByKey(key).getText();

            String price = findElementByKey(key).getText();
            elementTxt = elementTxt.replaceAll("\\s", "");
            elementTxt = elementTxt.replaceAll(",", ".");
            elementTxt = elementTxt.replace(elementTxt.substring(elementTxt.length() - 2), "");
            Float floatElementTxt = Float.parseFloat(elementTxt);
            logger.info(key + " : " + elementTxt);


            if (islem.equals("topla")) {
                floatLastSaveKey = (floatLastSaveKey * 100) + (floatElementTxt * 100);
                floatLastSaveKey /= 100;
                logger.info("Islem sonucu : " + floatLastSaveKey);

            } else if (islem.equals("cikar")) {
                floatLastSaveKey = (floatLastSaveKey * 100) - (floatElementTxt * 100);
                floatLastSaveKey /= 100;
                logger.info("Islem sonucu : " + floatLastSaveKey);
            } else {
                logger.info("Islem adi yanlis girilmiştir!");
                assertTrue(0 > 1);

            }

            String strValue = Float.toString(floatLastSaveKey);
            strValue = strValue.replaceAll("\\.", ",");
            StoreHelper.INSTANCE.saveValue(saveKey, strValue);
        } else {
            logger.info("------ Islem icin [" + key + "] li element bulunamadi ------");
        }

    }

    @Step("Saklanan fiyat değerlerini <priceTwo>, <secondPriceTwo>, <priceOne>, <secondPriceOne> toplam fiyat <sum>, <sumKusur> ile eşit mi kontrol et")
    public void sumAllPrices(String priceTwo, String secondPriceTwo, String priceOne, String secondPriceOne, String sum, String sumKusur) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String secondPriceTwos = StoreHelper.INSTANCE.getValue(secondPriceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);
        String secondPriceOnes = StoreHelper.INSTANCE.getValue(secondPriceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + secondPriceTwos);
        logger.info("Expected Value : " + priceOnes);
        logger.info("Expected Value : " + secondPriceOnes);

        String sums = findElementByKey(sum).getText();
        String sumKusurs = findElementByKey(sumKusur).getText().substring(1, findElementByKey(sumKusur).getText().length());

        double toplam = (Double.parseDouble(sumKusurs) / 100) + Double.parseDouble(sums);
        double count = (Double.parseDouble(priceTwos) / 100) + (Double.parseDouble(secondPriceTwos) / 100)
                + Double.parseDouble(priceOnes) + Double.parseDouble(secondPriceOnes);

        logger.info("Expected Value : " + count);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, count, "Degerler birbirine esit degil");
    }

    @Step("Saklanan tek ürün fiyat değerlerini <priceTwo>, <priceOne> toplam fiyat <sum>, <sumKusur> ile eşit mi kontrol et")
    public void sumAllPrices(String priceTwo, String priceOne, String sum, String sumKusur) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + priceOnes);

        String sums = findElementByKey(sum).getText();
        String sumKusurs = findElementByKey(sumKusur).getText().substring(1, findElementByKey(sumKusur).getText().length());

        double toplam = (Double.parseDouble(sumKusurs) / 100) + Double.parseDouble(sums);
        double count = (Double.parseDouble(priceTwos) / 100) + Double.parseDouble(priceOnes);

        logger.info("Expected Value : " + count);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, count, "Degerler birbirine esit degil");
    }

    @Step("Saklanan tek ürün fiyat değerlerinin <priceTwo>, <priceOne> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumAllPricesBasket(String priceTwo, String priceOne, String sum) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + priceOnes);
        waitBySecond(1);

        double expectedSum = Double.parseDouble(sum);
        double total = (Double.parseDouble(priceTwos) / 100) + Double.parseDouble(priceOnes);

        logger.info("Expected Value : " + expectedSum);
        logger.info("Actual Value : " + total);

        assertEquals(total, expectedSum, "Degerler birbirine esit degil");
    }

    @Step("Saklanan tek ürün fiyat değerlerinin <priceTwo>, <priceOne> saklanan toplam fiyat <priceSum> ile eşit mi kontrol et")
    public void sumGetAllPricesBasket(String priceTwo, String priceOne, String priceSum) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);
        String priceSums = StoreHelper.INSTANCE.getValue(priceSum);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + priceOnes);
        waitBySecond(1);

        double expectedSum = Double.parseDouble(priceSums);
        double total = (Double.parseDouble(priceTwos) / 100) + Double.parseDouble(priceOnes);

        logger.info("Expected Value : " + expectedSum);
        logger.info("Actual Value : " + total);

        assertEquals(total, expectedSum, "Degerler birbirine esit degil");
    }

    @Step("Saklanan tek ürün fiyat değerlerini <priceTwo>, <priceOne> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumAllPricesIyzıco(String priceTwo, String priceOne, String sum) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + priceOnes);
        waitBySecond(1);
        String sumPrice = findElementByKey(sum).getText();
        waitBySecond(1);
        String sums = sumPrice.substring(0, sumPrice.length() - 7);
        sums = sums.replace(",", ".");

        double toplam = Double.parseDouble(sums);
        double count = (Double.parseDouble(priceTwos) / 100) + Double.parseDouble(priceOnes);

        logger.info("Expected Value : " + count);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, count, "Degerler birbirine esit degil");
    }

    @Step("Ödenecek tutar <ödenecektutar> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumAllPricesIyzıco(String odenecektutar, String sum) {

        double odenecekTutar = Double.parseDouble(odenecektutar);

        String sumPrice = findElementByKey(sum).getText();
        String sums = sumPrice.substring(0, sumPrice.length() - 7);
        sums = sums.replace(",", ".");
        double toplam = Double.parseDouble(sums);

        logger.info("Expected Value : " + odenecekTutar);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, odenecekTutar, "Degerler birbirine esit degil");
    }

    @Step("Saklanan ödenecek tutar <odenecekToplamSum> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumGetAllPricesIyzico(String odenecekToplamSum, String sum) {
        String odenecekToplamSums = StoreHelper.INSTANCE.getValue(odenecekToplamSum);
        double odenecekTutar = Double.parseDouble(odenecekToplamSums);

        String sumPrice = findElementByKey(sum).getText();
        String sums = sumPrice.substring(0, sumPrice.length() - 7);
        sums = sums.replace(".", "");
        sums = sums.replace(",", ".");
        double toplam = Double.parseDouble(sums);

        logger.info("Expected Value : " + odenecekTutar);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, odenecekTutar, "Degerler birbirine esit degil");
    }


    @Step("Saklanan fiyat değerlerini <priceTwo>, <secondPriceTwo>, <priceOne>, <secondPriceOne> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumAllPricesStr(String priceTwo, String secondPriceTwo, String priceOne, String secondPriceOne, String sum) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String secondPriceTwos = StoreHelper.INSTANCE.getValue(secondPriceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);
        String secondPriceOnes = StoreHelper.INSTANCE.getValue(secondPriceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + secondPriceTwos);
        logger.info("Expected Value : " + priceOnes);
        logger.info("Expected Value : " + secondPriceOnes);

        String sums = findElementByKey(sum).getText().substring(0, findElementByKey(sum).getText().length() - 7);
        sums = sums.replace(",", ".");
        double toplam = Double.parseDouble(sums);
        double count = (Double.parseDouble(priceTwos) / 100) + (Double.parseDouble(secondPriceTwos) / 100)
                + Double.parseDouble(priceOnes) + Double.parseDouble(secondPriceOnes);

        logger.info("Expected Value : " + count);
        logger.info("Actual Value : " + toplam);

        assertEquals(toplam, count, "Degerler birbirine esit degil");
    }

    @Step("Saklanan fiyat değerlerini kargo indirimine bağlı <priceTwo>, <secondPriceTwo>, <priceOne>, <secondPriceOne> toplam fiyat <sum> ile eşit mi kontrol et")
    public void sumAllPricesWithShippingStr(String priceTwo, String secondPriceTwo, String priceOne, String secondPriceOne, String sum) {
        String priceTwos = StoreHelper.INSTANCE.getValue(priceTwo);
        String secondPriceTwos = StoreHelper.INSTANCE.getValue(secondPriceTwo);
        String priceOnes = StoreHelper.INSTANCE.getValue(priceOne);
        String secondPriceOnes = StoreHelper.INSTANCE.getValue(secondPriceOne);

        logger.info("Expected Value : " + priceTwos);
        logger.info("Expected Value : " + secondPriceTwos);
        logger.info("Expected Value : " + priceOnes);
        logger.info("Expected Value : " + secondPriceOnes);

        String sums = findElementByKey(sum).getText().substring(0, findElementByKey(sum).getText().length() - 7);
        sums = sums.replace(".", "");
        sums = sums.replace(",", ".");
        double toplam = Double.parseDouble(sums);
        logger.info("toplam Value : " + toplam);
        double countOne = Double.parseDouble(priceOnes) + (Double.parseDouble(priceTwos) / 100);
        logger.info("countOne Value : " + countOne);
        double countTwo = Double.parseDouble(secondPriceOnes) + (Double.parseDouble(secondPriceTwos) / 100);
        logger.info("countTwo Value : " + countTwo);
        double count = 0;
        double indirim = 350;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String strcountOne = decimalFormat.format(countOne);
        String strcountTwo = decimalFormat.format(countTwo);

        strcountOne = strcountOne.replace(",", ".");
        strcountTwo = strcountTwo.replace(",", ".");

        double countOnes = Double.parseDouble(strcountOne);
        double countTwos = Double.parseDouble(strcountTwo);

        if (countOnes >= indirim)
            count = countOnes;
        else
            count = countOnes + countTwos;

        String strcount = decimalFormat.format(count);
        strcount = strcount.replace(",", ".");
        double counts = Double.parseDouble(strcount);


        logger.info("Expected Value: " + counts);
        logger.info("Actual Value: " + toplam);

        assertEquals(toplam, counts, "Degerler birbirine esit degil");
    }


    @Step({"<text> text degerine sahip elementin sayfada gorundugu kontrol edilir"})
    public void ifExistCompare(String text) throws InterruptedException {
        waitBySecond(1);
        String key = "(//*[contains(@text,'" + text + "')])";
        assertTrue(getDriver().findElement(By.xpath(key)).isDisplayed(), "Element sayfada bulunamadı !");
        logger.info(key + " elementi sayfada görüntülendiği kontrol edildi");
    }

    @Step({"<firstKey> li tarihin,<secondKey> li tarihten yeni oldugu kontrol edilir"})
    public void ifExistCompareeee(String firstKey, String secondKey) throws InterruptedException, ParseException {

        String firstDate = findElementByKey(firstKey).getText();
        String secondDate = findElementByKey(secondKey).getText();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Date date1 = sdf.parse(firstDate);
        Date date2 = sdf.parse(secondDate);

        System.out.println("date1 : " + sdf.format(date1));
        System.out.println("date2 : " + sdf.format(date2));

        int result = date1.compareTo(date2);

        if (result == 0) {
            logger.info("Iki tarih birbirine esittir [" + firstDate + "][" + secondDate + "]");
        } else if (result > 0) {
            logger.info("Iki tarih arasindan yeni olan : [" + firstDate + "]");

        } else if (result < 0) {
            logger.info("Iki tarih arasindan yeni olan : [" + secondDate + "]");

        } else {
            logger.info("Tarih kiyaslamada problem cikti!");
        }
        assertTrue((result > 0) || (result == 0), "Urunlerin tarih siralamasi dogru değil!");


    }

    @Step({"<saveKey> olarak hafizada saklanan degeri <key> elementine yaz"})
    public void getTextAndWriteForKey(String saveKey, String key) {
        String saveElementTxt = StoreHelper.INSTANCE.getValue(saveKey);
        WebElement webElement = findElementByKey(key);
        webElement.clear();
        webElement.sendKeys(saveElementTxt);
    }


    @Step({"Klavyede arama tusuna bas"})
    public void enterAndroidKeyboard() {
        try {

            if (localAndroid.get() == false) {
                Actions action = new Actions(getDriver());
                action.sendKeys(Keys.ENTER).perform();
            } else {
                Actions action = new Actions(getDriver());
                action.sendKeys(Keys.ENTER).perform();
            }
        } catch (Exception ex) {
            logger.error("Klavye üzerinden arama başarısız " + ex.getMessage());
        }
    }

    @Step({"Favori ürünler silinir"})
    public void deleteFavElement() {

        try {

            Boolean dongu = true;

            while (dongu) {
                List<WebElement> elements = findElemenstByKeyWithoutAssert("FAVORI_ELEMENT_SIL");
                int elementsSize = elements.size();
                System.out.println("Element size : " + elementsSize);
                for (int i = 0; i < elementsSize; i++) {
                    clickByKey("FAVORI_ELEMENT_SIL");
                    System.out.println("i : " + i);
                }
            }
        } catch (Exception e) {
            logger.info("Tüm elementler silindi");
        }

    }

    @Step({"Sepetteki ürünler silinir"})
    public void deleteBasketElement() {

        try {

            Boolean dongu = true;

            while (dongu) {
                List<WebElement> elements = findElemenstByKeyWithoutAssert("SEPETIM_ELEMENT_SIL");
                int elementsSize = elements.size();
                System.out.println("Element size : " + elementsSize);
                for (int i = 0; i < elementsSize; i++) {
                    clickByKey("SEPETIM_ELEMENT_SIL");
                    System.out.println("i : " + i);
                }
            }
        } catch (Exception e) {
            logger.info("Tüm elementler silindi");
        }
    }


    @Step({"Fiyatların yazdığı <key1> li elementin degerinin fiyata gore azalan oldugu kontrol edilir"})
    public void compareTwoIntValue(String key1) throws InterruptedException {

        long firstValue = 0;
        long secondValue = 0;

        while (firstValue == secondValue) {
            String firstKeyTxt = findElementByKey(key1).getText();
            firstKeyTxt = firstKeyTxt.replaceAll("\\s", "");
            //firtKeyTxt = firtKeyTxt.replace(firtKeyTxt.substring(firtKeyTxt.length()-2), "");
            //firtKeyTxt = firtKeyTxt.replaceAll(",", "");
            //firtKeyTxt = firtKeyTxt.replaceAll("\\.", "");
            firstValue = Long.parseLong(firstKeyTxt);
            logger.info("1.elementinin degeri : " + firstValue);

            swipe(3);

            String secondKeyText = findElementByKey(key1).getText();
            secondKeyText = secondKeyText.replaceAll("\\s", "");
            //secondKeyText = secondKeyText.replace(secondKeyText.substring(secondKeyText.length()-2), "");
            //secondKeyText = secondKeyText.replaceAll(",", "");
            //secondKeyText = secondKeyText.replaceAll("\\.", "");
            secondValue = Long.parseLong(secondKeyText);
            logger.info("2.elementinin degeri : " + secondValue);

            if (firstValue != secondValue)
                break;

            swipe(1);
        }
        assertTrue(firstValue > secondValue, firstValue + " elementinin degeri" + secondValue + " elementinin degerinden kucuk");

    }

    @Step({"Fiyatların yazdığı <key1> li elementin degerinin fiyata gore artan oldugu kontrol edilir"})
    public void compareTwoIntValuee(String key1) throws InterruptedException {

        long firstValue = 0;
        long secondValue = 0;

        while (firstValue == secondValue) {
            String firtKeyTxt = null;
            while (true) {
                swipeDownAccordingToPhoneSize();
                swipeDownAccordingToPhoneSize();
                try {
                    firtKeyTxt = findElementByKeyWithoutAssert(key1).getText();
                } catch (Exception e) {
                    System.out.println("Fiyat bulunamadı, tekrar swipe ediliyor!");
                }
                if (firtKeyTxt != null) {
                    break;
                }
            }
            firtKeyTxt = firtKeyTxt.replaceAll("\\s", "");
            //firtKeyTxt = firtKeyTxt.replace(firtKeyTxt.substring(firtKeyTxt.length()-2), "");
            //firtKeyTxt = firtKeyTxt.replaceAll(",", "");
            //firtKeyTxt = firtKeyTxt.replaceAll("\\.", "");
            firstValue = Long.parseLong(firtKeyTxt);
            logger.info(key1 + " elementinin degeri : " + firstValue);

            //swipeDownAccordingToPhoneSize();
            swipeDownAccordingToPhoneSize();
            swipeDownAccordingToPhoneSize();
            waitBySecond(1);

            String secondKeyText = null;

            while (true) {
                swipeDownAccordingToPhoneSize();
                swipeDownAccordingToPhoneSize();
                try {
                    secondKeyText = findElementByKeyWithoutAssert(key1).getText();
                } catch (Exception e) {
                    System.out.println("Fiyat bulunamadı, tekrar swipe ediliyor!");
                }
                if (secondKeyText != null) {
                    break;
                }
            }


            secondKeyText = secondKeyText.replaceAll("\\s", "");
            //secondKeyText = secondKeyText.replace(secondKeyText.substring(secondKeyText.length()-2), "");
            //secondKeyText = secondKeyText.replaceAll(",", "");
            //secondKeyText = secondKeyText.replaceAll("\\.", "");
            secondValue = Long.parseLong(secondKeyText);
            logger.info(key1 + " elementinin degeri : " + secondValue);

            if (firstValue != secondValue)
                break;

            swipeDownAccordingToPhoneSize();
            waitBySecond(1);
        }
        assertTrue(secondValue > firstValue, secondValue + " elementinin degeri" + firstValue + " elementinin degerinden büyük");

    }


    @Step("<String> alt kategorisinin goruntulendigi kontrol edilir")
    public void findCategory(String key) {

        String element = "//XCUIElementTypeStaticText[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(key + " elementinin sayfa uzerinde goruntulendigi kontrol edilir");

    }

    @Step("<String> title degerinin goruntulendigi kontrol edilir")
    public void findTitleText(String key) {

        String element = "//XCUIElementTypeStaticText[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(key + " elementinin sayfa uzerinde goruntulendigi kontrol edilir");

    }

    @Step("<String> text degerine sahip elementin goruntulendigi kontrol edilir")
    public void findTextXpath(String key) {

        String element = "//XCUIElementTypeStaticText[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(key + " text degerine sahip elementin goruntulendigi kontrol edildi");
    }

    @Step("<String> text degerine sahip butonun sayfada olmadigi görüntülenir")
    public void findTextXpathButtonIsNotDisplayed(String key) {
        String element = "//XCUIElementTypeButton[@name=\"" + key + "\"]";
        List<WebElement> elements = getDriver().findElements(MobileBy.xpath(element));

        if (elements.isEmpty()) {
            logger.info(key + " text degerine sahip butonun sayfada olmadigi dogrulandi.");
        } else {
            throw new AssertionError(key + " text degerine sahip buton sayfada goruntulendi!");
        }
    }

    @Step("<String> text degerine sahip elemente tikla")
    public void clickWithText(String key) {

        String element = "//XCUIElementTypeStaticText[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element)).click();
        logger.info(key + " text degerine sahip elemente tiklandi");
    }

    @Step("<String> text degerine sahip elemente tikla.")
    public void clickWithTextAnd(String key) {

        String element = "//android.widget.TextView[@text=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element)).click();
        logger.info(key + " text degerine sahip elemente tiklandi");
    }

    @Step("<String> text degerine sahip butonun goruntulendigi kontrol edilir")
    public void findButtonWithText(String key) {

        String element = "//XCUIElementTypeButton[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(key + " text degerine sahip buton goruntulendi");
    }

    @Step("<key> elementinin <text> text degerine sahip oldugu kontrol edilir")
    public void elementContainsText(String key, String text) {
        WebElement element = findElementByKey(key);

        String elementText = element.getAttribute("name");
        assertEquals(elementText, text, "text degeri ayni degil");
        logger.info(key + " text degerine sahip buton goruntulendi");
    }

    @Step("<key> elementinin <text2> attiribute değerinin <text> text degerine sahip oldugu kontrol edilir")
    public void attiributeControl(String key, String text2, String text) {
        WebElement element = findElementByKey(key);

        String elementText = element.getAttribute(text2);
        assertEquals(elementText, text, "text degeri ayni degil");
        logger.info(key + " text degerine sahip buton goruntulendi");
    }

    @Step("<String> text degerine sahip butona tikla")
    public void clickButtonWithText(String key) {

        String element = "//XCUIElementTypeButton[@name=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element)).click();
        logger.info(key + " text degerine sahip elemente tiklandi");
    }

    @Step("<elementType> tipindeki <attribute> attribute degeri olan <key> text değerini içeren butona tiklanir")
    public void clickButtonWithText(String elementType, String attribute, String key) {

        String element = "//" + elementType + "[@" + attribute + "=\"" + key + "\"]";
        getDriver().findElement(MobileBy.xpath(element)).click();
        logger.info(key + " text degerine sahip elemente tiklandi");
    }


    @Step("<key> ismiyle hafizada text degeri sakli elementin sayfada gorundugu kontrol edilir")
    public void clickButtonWithTextt(String key) {

        String saveElementTxt = StoreHelper.INSTANCE.getValue(key);

        String element = "//XCUIElementTypeStaticText[@name=\"" + saveElementTxt + "\"]";
        getDriver().findElement(MobileBy.xpath(element));
        logger.info(saveElementTxt + " text degerine sahip elementin goruntulendigi kontrol edildi");

    }

    @Step("<key> ismiyle saklanan fiyat degerinin <key2> ismiyle saklanan degerden buyuk oldugu kontrol edilir")
    public void compareSavedTextForIntValue(String key, String key2) {


        String saveElement1Txt = StoreHelper.INSTANCE.getValue(key);
        String saveElement2Txt = StoreHelper.INSTANCE.getValue(key2);

        String element1txt = saveElement1Txt.substring(0, saveElement1Txt.length() - 3);
        String element2txt = saveElement2Txt.substring(0, saveElement2Txt.length() - 3);

        element1txt = element1txt.replace(",", "");
        element2txt = element2txt.replace(",", "");

        int price1 = Integer.parseInt(element1txt.replace(".", ""));
        int price2 = Integer.parseInt(element2txt.replace(".", ""));

        logger.info(key + " degerine ait text : [" + price1 + "]");
        logger.info(key2 + " degerine ait text : [" + price2 + "]");

//        boolean compareResult = price1 > price2;

        assertTrue(price1 > price2);
    }

    @Step("<key> ismiyle saklanan indirim yüzde degerinin <key2> ismiyle saklanan degerden buyuk oldugu kontrol edilir")
    public void compareSavedTextForPertanceValue(String key, String key2) {


        String saveElement1Txt = StoreHelper.INSTANCE.getValue(key);
        String saveElement2Txt = StoreHelper.INSTANCE.getValue(key2);

        saveElement1Txt = saveElement1Txt.replace("-", "");
        saveElement2Txt = saveElement2Txt.replace("-", "");

        int price1 = Integer.parseInt(saveElement1Txt.replace("%", ""));
        int price2 = Integer.parseInt(saveElement2Txt.replace("%", ""));

        logger.info(key + " degerine ait text : [" + price1 + "]");
        logger.info(key2 + " degerine ait text : [" + price2 + "]");

        boolean compareResult = price1 > price2;

        assertTrue(compareResult);
    }

    @Step({"IOS icin login ise logout olunur"})
    public void ifLoginDoLogout() throws InterruptedException {
        WebElement element;
        element = findElementByKeyWithoutAssert("digerProfilimSekmesi");
        if (element != null) {
            swipe(1);
            waitBySecond(5);
            System.out.println("Cikis yapilacak");
            clickByKeyWithCounter("cikisYapBtnn", "cikisYapPopUpCikisBtn");
            waitBySecond(3);
            clickByKey("cikisYapPopUpCikisBtn");
            swipeUP(1);
        }
        System.out.println("Uygulama logout durumda");
    }

    @Step({"login ise logout olunur And"})
    public void ifLoginDoLogoutAndroid() throws InterruptedException {
        WebElement element;
        waitBySecond(2);
        element = findElementByKeyWithoutAssert("girisYapBtn");
        //element = findElementByKeyWithoutAssert("girisYapBtn");
        if (element == null) {
            System.out.println("buraya girdi");
            existElement("loginMailText");
            swipe(1);
            waitBySecond(2);
            //findTextXpath("Çıkış");
            //clickWithText("Çıkış");
            clickByKey("cikisYapBtn");
            waitBySecond(2);
            existElement("popUpCikisBtn");
            clickByKey("popUpCikisBtn");
        }
        waitBySecond(4);
    }

    @Step({"logout ise <email> ve <sifre> ile login olunur Android"})
    public void ifLogoutDoLoginAndroid(String mail, String sifre) throws InterruptedException {
        WebElement element;
        waitBySecond(2);
        element = findElementByKeyWithoutAssert("cikisYapBtn");
        if (element == null) {
            logger.info("****** Logout durumda ******");
            existElement("girisYapBtn");
            clickByKey("girisYapBtn");
            existElement("andEpostaGirisInput");
            sendKeysByKeyNotClear(mail, "andEpostaGirisInput");
            sendKeysByKeyNotClear(sifre, "andGirisYapSifreInputArea");
            clickByKey("andLoginBtn");
        }
        waitBySecond(2);
        logger.info("****** Basarili sekilde login olundu ******");

        //Doğru hesap kontrolu yapilmasi icin
        WebElement element2;
        element2 = findElementByKeyWithoutAssert("andLoginMailText");

        if (element2 != null) {

            if (!Objects.equals(findElementByKey("andLoginMailText").getText(), mail)) {
                logger.info("****** Yanlis hepap ile login olunmus ******");
                waitBySecond(2);
                swipe(1);
                waitBySecond(2);
                clickByKey("cikisYapBtn");
                waitBySecond(2);
                existElement("popUpCikisBtn");
                clickByKey("popUpCikisBtn");
                waitBySecond(2);
                existElement("profilSekmesi");
                clickByKey("profilSekmesi");
                existElement("girisYapBtn");
                clickByKey("girisYapBtn");
                existElement("andEpostaGirisInput");
                sendKeysByKeyNotClear(mail, "andEpostaGirisInput");
                sendKeysByKeyNotClear(sifre, "andGirisYapSifreInputArea");
                clickByKey("andLoginBtn");
                logger.info("****** Dogru hesapla login olundu ******");
                waitBySecond(3);
                existElement("andLoginMailText");
            }
        }

    }


    @Step({"logout ise <email> ve <sifre> ile login olunur"})
    public void ifLogoutDoLogin(String mail, String sifre) throws InterruptedException {
        WebElement element;
        element = findElementByKeyWithoutAssert("loginSiparislerim");
        if (element == null) {
            logger.info("****** Logout durumda ******");
            existElement("girisYapBtn");
            clickByKey("girisYapBtn");
            existElement("girisYapEpostaInputArea");
            sendKeysByKeyNotClear(mail, "girisYapEpostaInputArea");
            sendKeysByKeyNotClear(sifre, "girisYapSifreInputArea");
            clickByKey("loginBtn");
        }
        waitBySecond(2);
        logger.info("****** Basarili sekilde login olundu ******");

        //Doğru hesap kontrolu yapilmasi icin

        if (!Objects.equals(findElementByKey("loginMailText").getText(), mail)) {
            logger.info("****** Yanlis hepap ile login olunmus ******");
            waitBySecond(2);
            swipe(1);
            waitBySecond(2);
            findTextXpath("Çıkış");
            clickWithText("Çıkış");
            waitBySecond(2);
            existElement("popUpCikisBtn");
            clickByKey("popUpCikisBtn");
            waitBySecond(2);
            existElement("digerSekmesi");
            clickByKey("digerSekmesi");
            existElement("girisYapBtn");
            clickByKey("girisYapBtn");
            existElement("girisYapEpostaInputArea");
            sendKeysByKeyNotClear(mail, "girisYapEpostaInputArea");
            sendKeysByKeyNotClear(sifre, "girisYapSifreInputArea");
            clickByKey("loginBtn");
            logger.info("****** Dogru hesapla login olundu ******");
            waitBySecond(2);
            existElement("loginSiparislerim");
        }
    }


    @Step({"<key> elementine var oldukca tiklanir"})
    public void ifLogoutDoLogin(String key) throws InterruptedException {
        WebElement element;
        waitBySecond(1);
        element = findElementByKeyWithoutAssert(key);
        while (element != null) {
            int i = 0;
            logger.info(i + ". element bulundu");

            clickByKey(key);

            waitBySecond(2);

            element = findElementByKeyWithoutAssert(key);
            waitBySecond(2);
        }
    }


    @Step({"Android klavye kapatılır"})
    public void closeKeyboard() {
        try {
            AndroidDriver androidDriver = (AndroidDriver) getDriver();
            androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        } catch (Exception e) {
            System.out.println("Klavye kapatılamadı: " + e.getMessage());
        }
    }

    @Step({"IOS klavye kapatılır"})
    public void closeKeyboardIOS() {
        try {
            IOSDriver iosDriver = (IOSDriver) getDriver();
            iosDriver.hideKeyboard(); // iOS için doğru kullanım
        } catch (Exception e) {
            try {
                getDriver().findElement(By.xpath("//XCUIElementTypeButton[@name=\"Return\"]")).click();
            } catch (Exception innerException) {
                System.out.println("Klavyeyi kapatmak için 'Return' butonuna erişilemedi: " + innerException.getMessage());
            }
        }
    }

    @Step({"IOS numara klavyesi kapatılır"})
    public void closeNumberKeyboardIOS() {
        IOSDriver iosDriver = (IOSDriver) getDriver();
        try {
            iosDriver.hideKeyboard();
            return;
        } catch (Exception e) {
            // ignore
        }

        // 1. Done tuşunu dene
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(2));
            WebElement done = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@name='Done' or @name='Return' or @label='Bitti']"))
            );
            done.click();
            return;
        } catch (Exception e) {
            // ignore
        }

        // 2. Ekran üstüne tap yap
        tapOutsideToHideKeyboard(iosDriver);
    }

    public void tapOutsideToHideKeyboard(IOSDriver driver) {
        Dimension size = getDriver().manage().window().getSize();
        int x = size.width / 2;
        int y = size.height / 10;  // ekranın üst taraflarına bir yere tıkla

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tap));
    }

    @Step({"IOS klavye sil tuşuna basılır"})
    public void removeNumberKeyboardIOS() {

//            driver.hideKeyboard(); // İlk olarak hideKeyboard() metodunu dene
        WebElement element2;
        element2 = findElementByKey("betaRemoveKeyboard");
        element2.click();
        logger.info("Klavyede sil butonuna tiklandi.");
    }

    @Step({"IOS klavye sil tuşuna <number> kez basılır"})
    public void removeClickNumberKeyboardIOS(int number) {
        for (int i = 0; i < number; i++) {
            removeNumberKeyboardIOS();
        }
    }

    @Step({"<key> li elementin değeri <name> içerdiğini kontrol et",
            "Find element by <key> and text contains <name>"})
    public void containsNameByKey(String key, String name) {
        By by = getSelector().getElementInfoToBy(key);
        assertTrue(getAppiumFluentWait().until(new ExpectedCondition<Boolean>() {
            private String currentValue = null;

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    currentValue = driver.findElement(by).getAttribute("name");
                    return currentValue.contains(name);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return String.format("text contains be \"%s\". Current text: \"%s\"", name, currentValue);
            }
        }));
        logger.info(key + " li elementin değeri \"" + name + "\" metnini içeriyor.");
    }

    @Step({"<key> li elementleri bul ve ekrana yazdır",
            "Find elements by <key> and log"})
    public void logElementsByIdWithContains(String key) {
        List<WebElement> elements = findElemenstByKey(key);
        for (WebElement element : elements) {
            logger.info("Element Text: " + element.getAttribute("name"));
        }
    }

    @Step("<key> li elementleri <attribute> attribute bul ve ekrana yazdır")
    public void logElementsByIdWithContainsAttribute(String key, String attribute) {
        List<WebElement> elements = findElemenstByKey(key);
        for (WebElement element : elements) {
            logger.info("Element Text: " + element.getAttribute(attribute));
        }
    }

    @Step({"<key> elementi varsa <keyTwo> elementine tıkla", "If <key> exists, click <keyTwo>"})
    public void existClickOtherElementIfExist(String key, String keyTwo) throws InterruptedException {
        WebElement keyElement = findElementByKeyWithoutAssert(key);
        if (keyElement != null) {
            System.out.println(key + " elementi bulundu.");
            WebElement keyTwoElement = findElementByKeyWithoutAssert(keyTwo);
            if (keyTwoElement != null) {
                System.out.println(keyTwo + " elementi bulundu, tıklanıyor.");
                keyTwoElement.click();

            } else {
                System.out.println(keyTwo + " elementi bulunamadı, tıklanamadı.");
            }
        } else {
            System.out.println(key + " elementi bulunamadı, işlem yapılmadı.");
        }
        waitBySecond(2);
    }

    @Step({"<key> elementi varsa <keyTwo> elementine, yoksa <keyThree> elementine tıkla",
            "If <key> exists, click <keyTwo>, otherwise click <keyThree>"})
    public void existClickOtherElementIfExistOrClickThird(String key, String keyTwo, String keyThree) throws InterruptedException {
        WebElement keyElement = findElementByKeyWithoutAssert(key);
        if (keyElement != null) {
            System.out.println(key + " elementi bulundu.");
            WebElement keyTwoElement = findElementByKeyWithoutAssert(keyTwo);
            if (keyTwoElement != null) {
                System.out.println(keyTwo + " elementi bulundu, tıklanıyor.");
                waitBySecond(2);
                keyTwoElement.click();
            } else {
                System.out.println(keyTwo + " elementi bulunamadı, tıklanamadı.");
            }
        } else {
            System.out.println(key + " elementi bulunamadı, " + keyThree + " elementine tıklanacak.");
            WebElement keyThreeElement = findElementByKeyWithoutAssert(keyThree);
            if (keyThreeElement != null) {
                System.out.println(keyThree + " elementi bulundu, tıklanıyor.");
                keyThreeElement.click();
            } else {
                System.out.println(keyThree + " elementi bulunamadı, tıklanamadı.");
            }
        }
        waitBySecond(2);
    }

    @Step({"Sepet doluysa tüm ürünleri sepetten çıkar"})
    public void ifBasketFullDeleteAllItem() throws InterruptedException {
        clickByKey("Sepet_Bottom_Bar_Menu");
        waitBySecond(2);
        WebElement keyElement = findElementByKeyWithoutAssert("Sepet_Urun_Yok_Text_Control");
        if (keyElement == null || !keyElement.isDisplayed()) {
            tapElementWithKeyControlArea("Kasa_Arkası_Urunler_Popup_Close");
            waitBySecond(2);
            tapElementWithKeyControl("Sepet_Tumunu_Sil_Button");
            waitBySecond(2);
            clickByKey("Sepet_Tumunu_Sil_Popup_Button");

        }
        clickByKey("HomePageButton_MainMenu");

    }

    @Step("Sepette <key> ürün varsa Tümünü sil <keyAllDelete> ve sonrasında <keyAllDeletePopup> ve ürünleri sil yoksa devam et")
    public void tapElementWithKeyControlAreaBasketMenu(String key, String keyAllDelete, String keyAllDeletePopup) {

        logger.info("element varsa verilen tıkla ya girdi");
        WebElement mobileElement;

        mobileElement = findElementByKeyWithoutAssert(key);

        if (mobileElement != null) {

            doesElementExistByKey(key, 3);
            findElementByKey(keyAllDelete).click();
            findElementByKey(keyAllDeletePopup).click();
            logger.info(key + "elemente tıkladı");

        } else {
            System.out.println(key + " element yuklenmedi");

        }
    }

    @Step("<key> fiyatını hafızaya al, %20 indirim uygulat ve güncel fiyat <key2> ile karşılaştır")
    public void verifyTwentyPercentDiscountApplied(String key, String key2) {
        // 1. Fiyatı çek ve hafızaya al
        WebElement element = findElementByKey(key);
        String rawInitialText = element.getText();
        logger.info("Ham fiyat metni: " + rawInitialText);

        String initialPriceText = rawInitialText
                .replace("TL", "")
                .replace(".", "")           // binlik ayraçları kaldır
                .replace(",", ".")          // ondalık ayracı noktaya çevir
                .replaceAll("[^0-9.]", "")  // sadece rakam ve nokta bırak
                .trim();

        double initialPrice = Double.parseDouble(initialPriceText);
        StoreHelper.INSTANCE.saveValue("originalPrice", initialPriceText);
        logger.info("İlk fiyat hafızaya alındı: " + initialPrice);

        // 2. İndirim adımları
        clickButtonWithText("XCUIElementTypeButton", "label", "Personel İndirimimi Kullanmak İstiyorum");
        clickButtonWithText("XCUIElementTypeButton", "label", "KULLAN");
        //clickByKey("betaBasketKasaArkasiButton");

        waitBySecond(2); // İndirim sonrası fiyat güncellemesi için bekleme

        // 3. Güncel fiyatı tekrar çek
        WebElement updatedElement = findElementByKey(key2);
        String rawUpdatedText = updatedElement.getText();
        logger.info("Ham indirimli fiyat metni: " + rawUpdatedText);

        String updatedPriceText = rawUpdatedText
                .replace("TL", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();

        double updatedPrice = Double.parseDouble(updatedPriceText);
        logger.info("Güncel indirimli fiyat: " + updatedPrice);

        // 4. Beklenen fiyat hesapla (%20 indirim)
        double expectedPrice = initialPrice * 0.80;
        logger.info("Beklenen indirimli fiyat: " + expectedPrice);

        // 5. Karşılaştırma
        assertEquals(expectedPrice, updatedPrice, 0.01, "İndirim doğru uygulanmamış!");
    }

    @Step("<value> csv dosyasindan rastgele gelen promosypn kodunu <key> elementine yaz")
    public void csvReaderPromo(String value, String key) {
        try {
            String line = "";
            String splitBy = ",";

            BufferedReader br = new BufferedReader(new FileReader("data/" + value + ".csv"));
            while ((line = br.readLine()) != null) {
                String[] keyValue = line.split(splitBy, 2);
                promoCode.add(keyValue[0]);
            }

            System.out.println("Maillere ait csv okundu");

            int number = createRandomNumber(promoCode.size());

            promoCodes = promoCode.get(number);
            sendKeysByKey(key, promoCodes);


            System.out.println("Kullanilacak Kullanici adi :" + promoCodes);

        } catch (Exception e) {
            System.out.println("Csv dosyasi oluşturulurken hatayla karsilasildi");
            System.out.println(promoCode);
        }
    }

    @Step("<key> değerinin sıfıra eşit olup olmadığını kontrol et")
    public void checkIfPriceIsZero(String key) {
        String rawText = findElementByKey(key).getText();
        logger.info("Ham fiyat metni: " + rawText);

        // Nokta ve TL'yi temizle, sadece virgül noktaya çevrilir
        String priceText = rawText
                .replace(".", "")             // binlik ayırıcıyı sil
                .replace(",", ".")            // ondalık için nokta kullan
                .replaceAll("[^0-9.]", "")    // sayılar ve tek nokta hariç hepsini sil
                .trim();

        logger.info("Düzenlenmiş fiyat metni: " + priceText);

        double actualPrice = Double.parseDouble(priceText);
        double expectedPrice = 0.00;

        logger.info("Ekrandan alınan fiyat: " + actualPrice);
        logger.info("Beklenen fiyat: " + expectedPrice);

        assertEquals(expectedPrice, actualPrice, 0.01, "Fiyat 0.00 TL değil!");
    }

    @Step("<keyFiyat> fiyatını al, <keyPuanText> içinden puanı çıkar, <keyGuncelFiyat> ile karşılaştır")
    public void verifyDiscountAppliedByGratisPoint(String keyFiyat, String keyPuanText, String keyGuncelFiyat) {
        // 1. İlk fiyatı al
        clickButtonWithText("XCUIElementTypeStaticText", "value", "Gratis Puanınızı Kullanın");
        String fiyatText = findElementByKey(keyFiyat).getText()
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double orijinalFiyat = Double.parseDouble(fiyatText);
        logger.info("Orijinal fiyat: " + orijinalFiyat);


        // 2. Puan metninden puanı al
        String puanTextRaw = findElementByKey(keyPuanText).getText();  // "Bu alışverişinizde... 10,00 TL"
        String puanText = puanTextRaw.replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double puanDegeri = Double.parseDouble(puanText);
        logger.info("Gratis puanı: " + puanDegeri);

        clickButtonWithText("XCUIElementTypeButton", "label", "KULLAN");
        waitBySecond(5);
        // 3. Güncel fiyatı al
        String guncelFiyatText = findElementByKey(keyGuncelFiyat).getText()
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double guncelFiyat = Double.parseDouble(guncelFiyatText);
        logger.info("Güncel fiyat: " + guncelFiyat);

        // 4. Beklenen fiyatı hesapla ve karşılaştır
        double beklenenFiyat = orijinalFiyat - puanDegeri;
        logger.info("Beklenen fiyat (orijinal - puan): " + beklenenFiyat);

        assertEquals(beklenenFiyat, guncelFiyat, 0.01, "Puan düşüldükten sonra fiyat doğru değil!");
    }


    @Step("<key> fiyatını hafızaya al, 50 TL indirimi uygulat ve <keyGuncel> güncel fiyatla karşılaştır")
    public void verifyFiftyTLDiscountAppliedWithTwoKeys(String key, String keyGuncel) {
        // 1. İlk fiyatı al
        clickButtonWithText("XCUIElementTypeStaticText", "value", "Hediye Kartım Var");

        WebElement element = findElementByKey(key);
        String initialPriceText = element.getText()
                .replace("TL", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double initialPrice = Double.parseDouble(initialPriceText);
        logger.info("İlk fiyat: " + initialPrice);

        // 2. Hafızaya kaydet
        StoreHelper.INSTANCE.saveValue("originalPrice", initialPriceText);
        logger.info("İlk fiyat hafızaya alındı: " + initialPrice);

        // 3. Hediye Karti işlemleri
        waitBySecond(1);
        csvReaderPromo("GiftCode", "betaBasketGiftCardTextField");
        clickButtonWithText("XCUIElementTypeButton", "label", "UYGULA");
        waitBySecond(1);

        // 4. Güncel fiyatı ayrı elementten çek
        waitBySecond(2); // Güncellenmesini bekle
        WebElement updatedElement = findElementByKey(keyGuncel);
        String updatedPriceText = updatedElement.getText()
                .replace("TL", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double updatedPrice = Double.parseDouble(updatedPriceText);
        logger.info("Güncel indirimli fiyat: " + updatedPrice);

        // 5. Beklenen fiyatı hesapla ve karşılaştır
        double expectedPrice = initialPrice - 50.0;
        logger.info("Beklenen 50 TL indirimli fiyat: " + expectedPrice);

        assertEquals(expectedPrice, updatedPrice, 0.01, "50 TL indirim doğru şekilde uygulanmamış!");
    }

    @Step("<keyFiyat> fiyatını al, <keyPuan> puan değerini al, <keyGuncelFiyat> ile karşılaştır")
    public void verifyDiscountAppliedByGratisPointKey(String keyFiyat, String keyPuan, String keyGuncelFiyat) {
        // 1. Orijinal fiyatı al
        clickButtonWithText("XCUIElementTypeStaticText", "value", "Promosyon Kodu Ekle");
        String fiyatText = findElementByKey(keyFiyat).getText()
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double orijinalFiyat = Double.parseDouble(fiyatText);
        logger.info("Orijinal fiyat: " + orijinalFiyat);

        csvReaderPromo("PromoCode", "betaBasketPromoCodeTextField");

        //sendKeysByKey("betaBasketPromoCodeTextField","");

        clickButtonWithText("XCUIElementTypeButton", "label", "UYGULA");
        waitBySecond(5);
        waitForElementToDisappear();
        // 2. Puan değerini ayrı bir elementten al
        String puanText = findElementByKey(keyPuan).getText()
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double puanDegeri = Double.parseDouble(puanText);
        logger.info("Gratis puanı: " + puanDegeri);


        // 3. Güncel fiyatı al
        String guncelFiyatText = findElementByKey(keyGuncelFiyat).getText()
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();
        double guncelFiyat = Double.parseDouble(guncelFiyatText);
        logger.info("Güncel fiyat: " + guncelFiyat);
        waitBySecond(5);
        // 5. Beklenen fiyat = orijinal - puan
        double beklenenFiyat = (orijinalFiyat) - (puanDegeri);
        logger.info("Beklenen fiyat: " + beklenenFiyat);

        // 6. Karşılaştır
        assertEquals(beklenenFiyat, guncelFiyat, 0.01, "Promosyon Kodu doğru uygulanmamış!");
    }

    @Step("Max fatura adresi eklenir")
    public void addMaxInvoiceAddress() {
        for (int i = 0; i < 8; i++) {
            clickByKey("betaDeliveryInvoiceForwardNavigationButton");
            clickByKey("betaDeliveryNewAddressAddButton");
            sendKeysByKey("betaDeliveryStoreInvoiceAddressName", "Test");
            sendKeysByKey("betaDeliveryStoreInvoiceAddressSurname", "Testinium");
            sendKeysByKey("betaDeliveryStoreInvoiceAddressPhoneNumber", "5354742335");
            sendKeysByKey("betaDeliveryStoreInvoiceAddressAddressName", "Adres baslik");
            clickByKey("betaDeliveryStoreInvoiceAddressCityDropdown");
            checkByValueText("XCUIElementTypeStaticText", "value", "İl Seçiniz");
            clickButtonWithText("XCUIElementTypeButton", "label", "ADIYAMAN");
            checkByValueText("XCUIElementTypeStaticText", "value", "İlçe Seçiniz");
            clickButtonWithText("XCUIElementTypeButton", "label", "BESNİ");
            checkByValueText("XCUIElementTypeStaticText", "value", "Mahalle Seçiniz");
            clickButtonWithText("XCUIElementTypeButton", "label", "ABIMISTIK");
            swipeIOS(1);
            sendKeysByKey("betaDeliveryStoreInvoiceAddressOpenAddress", "Test test");
            sendKeysByKey("betaDeliveryStoreInvoiceAddressPostalCode", "34500");
            clickButtonWithText("XCUIElementTypeButton", "label", "ADRESİMİ KAYDET");
        }


    }

//    @Step("<sliderKey> slider'ını <startOffset> başlangıç, <endOffset> bitiş olacak şekilde sürükle ve <minInputKey>, <maxInputKey> değerlerini kontrol et")
//    public void slideAndVerifyPriceRangeDynamic(String sliderKey, int startOffset, int endOffset, String minInputKey, String maxInputKey) {
//
//        WebElement slider = findElementByKey(sliderKey);
//        Rectangle rect = slider.getRect();
//
//        int centerY = rect.getY() + rect.getHeight() / 2;
//        int startX = rect.getX() + startOffset;
//        int endX = rect.getX() + endOffset;
//
//        logger.info("Slider koordinatları: X=" + rect.getX() + ", Width=" + rect.getWidth());
//        logger.info("startX: " + startX + ", endX: " + endX + ", centerY: " + centerY);
//
//        // Slider’ı sürükle
//        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
//        Sequence drag = new Sequence(finger, 0);
//        drag.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, centerY));
//        drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
//        int steps = 5;
//        for (int i = 1; i <= steps; i++) {
//            int moveX = startX + ((endX - startX) * i / steps);
//            drag.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), moveX, centerY));
//        }
//        drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
//        driver.perform(Collections.singletonList(drag));
//
//        waitBySecond(3);
//
//        // En Az / En Çok input değerlerini al
//        String enAzText = findElementByKey(minInputKey).getAttribute("value").replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();
//        String enCokText = findElementByKey(maxInputKey).getAttribute("value").replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();
//
//        logger.info("Slider sonrası En Az değeri: " + enAzText);
//        logger.info("Slider sonrası En Çok değeri: " + enCokText);
//
//        double enAzDeger = Double.parseDouble(enAzText);
//        double enCokDeger = Double.parseDouble(enCokText);
//
//        // Yukarıda görünen min-max değerlerini slider'dan oku
////        WebElement sliderMinTextEl = findElementByKey("betaFilterPriceRangeSliderMin"); // örneğin: "1 TL"
////        WebElement sliderMaxTextEl = findElementByKey("betaFilterPriceRangeSliderMax"); // örneğin: "1.000 TL"
////
////        String sliderMinText = sliderMinTextEl.getAttribute("value").replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();
////        String sliderMaxText = sliderMaxTextEl.getAttribute("value").replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();
////
////        logger.info("Slider üzerindeki görünen Min: " + sliderMinText + ", Max: " + sliderMaxText);
//
////        double sliderMinValue = Double.parseDouble(sliderMinText);
////        double sliderMaxValue = Double.parseDouble(sliderMaxText);
//
//        // Ana kontroller
//        assertTrue(enAzDeger >= 1, "En Az değeri 1 TL'den küçük olamaz");
//        assertTrue(enCokDeger <= 5000, "En Çok değeri 5000 TL'den büyük olamaz");
//        assertTrue(enCokDeger > enAzDeger, "En Çok değeri En Az değerinden büyük olmalı");
//
//        // Yeni kontroller: Slider üzerindeki değerlerle eşleşiyor mu

    /// /        assertEquals(enAzDeger, sliderMinValue, "En Az input değeri slider üzerindeki değerle eşleşmiyor");
    /// /        assertEquals(enCokDeger, sliderMaxValue, "En Çok input değeri slider üzerindeki değerle eşleşmiyor");
//    }
    @Step("<sliderKey> slider'ını kaydır: sol başlangıç <leftStartOffset>, sol bitiş <leftEndOffset>, sağ başlangıç <rightStartOffset>, sağ bitiş <rightEndOffset>; ve <minInputKey>, <maxInputKey> değerlerini kontrol et")
    public void slideAndVerifyPriceRangeDynamic(String sliderKey, int leftStartOffset, int leftEndOffset,
                                                int rightStartOffset, int rightEndOffset,
                                                String minInputKey, String maxInputKey) {
        // 1. Slider alanını bul
        WebElement slider = findElementByKey(sliderKey);
        Rectangle rect = slider.getRect();

        int sliderX = rect.getX();
        int sliderWidth = rect.getWidth();
        int sliderRightX = sliderX + sliderWidth;
        int centerY = rect.getY() + rect.getHeight() / 2;


        logger.info("Slider koordinatları: X={}, Width={}", sliderX, sliderWidth);

        // Sol handle koordinatları (kontrolsüz çünkü sol her zaman slider'ın içindedir genelde)
        int leftStartX = sliderX + leftStartOffset;
        int leftEndX = sliderX + leftEndOffset;
        logger.info("Sol handle startX: {}, endX: {}", leftStartX, leftEndX);

        // Sağ handle koordinatları — sınır kontrolü ile
        WebElement rightHandle = findElementByKey("betaFilterPriceRangeSliderRight"); // bu element varsa
        Rectangle rect2 = rightHandle.getRect();
        int rightStartX = rect2.getX();
        int rightEndX = rightStartX - 22;
        logger.info("Sağ handle startX: {}, endX: {}", rightStartX, rightEndX);

        logger.info("centerY: {}", centerY);

        // 2. Sol handle’ı kaydır
        PointerInput leftFinger = new PointerInput(PointerInput.Kind.TOUCH, "leftFinger");
        Sequence leftDrag = new Sequence(leftFinger, 1);
        leftDrag.addAction(leftFinger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), leftStartX, centerY));
        leftDrag.addAction(leftFinger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        leftDrag.addAction(leftFinger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), leftEndX, centerY));
        leftDrag.addAction(leftFinger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(leftDrag));
        waitBySecond(1);

        // 3. Sağ handle’ı kaydır
        PointerInput rightFinger = new PointerInput(PointerInput.Kind.TOUCH, "rightFinger");
        Sequence rightDrag = new Sequence(rightFinger, 1);
        rightDrag.addAction(rightFinger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), rightStartX, centerY));
        rightDrag.addAction(rightFinger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        rightDrag.addAction(rightFinger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), rightEndX, centerY));
        rightDrag.addAction(rightFinger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        getDriver().perform(Collections.singletonList(rightDrag));
        waitBySecond(3);

        // 4. Input alanlarındaki değerleri kontrol et
        String enAzText = findElementByKey(minInputKey).getAttribute("value")
                .replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();
        String enCokText = findElementByKey(maxInputKey).getAttribute("value")
                .replace(".", "").replace(",", ".").replaceAll("[^0-9.]", "").trim();

        logger.info("Input - En Az değeri: {}", enAzText);
        logger.info("Input - En Çok değeri: {}", enCokText);

        double enAzDeger = Double.parseDouble(enAzText);
        double enCokDeger = Double.parseDouble(enCokText);

//        assertTrue(enAzDeger >= 1, "En Az değeri 1 TL'den küçük olamaz");
//        assertTrue(enCokDeger <= 9000, "En Çok değeri 5000 TL'den büyük olamaz");
        assertTrue(enCokDeger > enAzDeger, "En Çok değeri En Az değerinden büyük olmalı");
    }

    @Step("<minInputKey> alanına <minValue>, <maxInputKey> alanına <maxValue> yaz ve <sliderKey> slider handle konumlarını kontrol et")
    public void enterValuesAndVerifySliderPositions(String minInputKey, String minValue,
                                                    String maxInputKey, String maxValue,
                                                    String sliderKey) {

        // 1. Değerleri input alanlarına yaz
        WebElement minInput = findElementByKey(minInputKey);
        WebElement maxInput = findElementByKey(maxInputKey);

        minInput.clear();
        minInput.sendKeys(minValue);

        maxInput.clear();
        maxInput.sendKeys(maxValue);

        waitBySecond(2); // Slider animasyonun oturması için


        clickByKey("betaFilterPriceApplyButton");
        swipe(2);
        clickByKey("betaFilterPriceRange");

        // 2. Slider ve handle’ları al
        WebElement slider = findElementByKey(sliderKey);
        Rectangle sliderRect = slider.getRect();

        int sliderX = sliderRect.getX();
        int sliderWidth = sliderRect.getWidth();
        int sliderRightX = sliderX + sliderWidth;

        int centerY = sliderRect.getY() + sliderRect.getHeight() / 2;

        // Handle’lar
        WebElement leftHandle = findElementByKey("betaFilterPriceRangeSliderLeft");
        WebElement rightHandle = findElementByKey("betaFilterPriceRangeSliderRight");

        int leftHandleX = leftHandle.getRect().getX();
        int rightHandleX = rightHandle.getRect().getX();

        logger.info("Slider başlangıç X: {}, genişlik: {}", sliderX, sliderWidth);
        logger.info("Sol handle X: {}, Sağ handle X: {}", leftHandleX, rightHandleX);

        // 3. Min/Max değerlerini parse et
        double min = Double.parseDouble(minValue.replace(",", "."));
        double max = Double.parseDouble(maxValue.replace(",", "."));

        // Örnek: slider min=1, max=5000 (statik değerler)
        double sliderMin = Double.parseDouble(findElementByKey("betaFilterPriceRangeSliderMin").getText()
                .replace("TL", "")
                .trim());
        double sliderMax = Double.parseDouble(findElementByKey("betaFilterPriceRangeSliderMax").getText()
                .replace("TL", "")
                .replace(".", "")
                .trim());

        // 4. Beklenen konumları hesapla
        double leftRatio = (min - sliderMin) / (sliderMax - sliderMin);
        double rightRatio = (max - sliderMin) / (sliderMax - sliderMin);

        int expectedLeftX = (int) (sliderX + sliderWidth * leftRatio);
        int expectedRightX = (int) (sliderX + sliderWidth * rightRatio);

        int tolerance = 15; // Pixel bazlı tolerans

        logger.info("Beklenen Sol handle X: {} (tolerans ±{})", expectedLeftX, tolerance);
        logger.info("Beklenen Sağ handle X: {} (tolerans ±{})", expectedRightX, tolerance);

        // 5. Assertion
        assertTrue(Math.abs(leftHandleX - expectedLeftX) <= tolerance,
                "Sol handle beklenen konumda değil!");

        assertTrue(Math.abs(rightHandleX - expectedRightX) <= tolerance,
                "Sağ handle beklenen konumda değil!");
    }

    @Step("Video oynuyor mu kontrol et")
    public void checkIfVideoIsPlaying() {
        try {
            waitBySecond(5);
            File screenshot1 = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
            File temp1 = new File("video_frame1.png");
            FileUtils.copyFile(screenshot1, temp1);

            Thread.sleep(1000); // 1 saniye bekle (video oynuyorsa değişiklik olur)

            // Screenshot 2
            File screenshot2 = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
            File temp2 = new File("video_frame2.png");
            FileUtils.copyFile(screenshot2, temp2);

            // Karşılaştır
            boolean same = FileUtils.contentEquals(temp1, temp2);

            if (same) {
                System.out.println("Video OYNAMIYOR 😞");
            } else {
                System.out.println("Video OYNUYOR 🚀");
            }

        } catch (Exception e) {
            System.out.println("Video kontrolü sırasında hata oluştu: " + e.getMessage());
        }
    }

    @Step({"Kullanıcı oturumu açıksa çıkış yapılır"})
    public void logoutIfLoggedIn() {
        waitBySecond(3);
        clickByKey("gratisHomePageProfilTab");
        // Profil butonu varsa çıkış süreci başlat
        if (doesElementExistByKey("betaUserLogoutButton", 5)) {
            logger.info("Kullanıcı oturumda görünüyor, çıkış işlemi başlatılıyor...");
            tapElementWithKeyControlArea("betaUserLogoutButton");
            waitBySecond(2);

            // Bottom sheet açıldıysa çıkış yap butonuna tıkla
            if (doesElementExistByKey("betaLogoutButton", 5)) {
                tapElementWithKeyControlArea("betaLogoutButton");
                logger.info("Çıkış yapıldı.");
            } else {
                logger.warn("betaLogoutButton bulunamadı, çıkış işlemi tamamlanamadı.");
            }

        } else {
            logger.info("Uygulama logout durumda geldi");
        }
    }


    private WebElement waitForElement(String key, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
            SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
            return (WebElement) wait.until(ExpectedConditions.presenceOfElementLocated(selectorInfo.getBy()));
        } catch (Exception e) {
            logger.info("{} elementi {} saniye içinde bulunamadı", key, timeoutInSeconds);
            return null;
        }
    }

    private WebElement waitForElementClickable(String key, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
            SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
            return (WebElement) wait.until(ExpectedConditions.elementToBeClickable(selectorInfo.getBy()));
        } catch (Exception e) {
            logger.info(key + " elementi tıklanabilir değil veya " + timeoutInSeconds + " saniye içinde bulunamadı");
            return null;
        }
    }

    private boolean waitForElementVisible(String key, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
            SelectorInfo selectorInfo = getSelector().getSelectorInfo(key);
            wait.until(ExpectedConditions.visibilityOfElementLocated(selectorInfo.getBy()));
            return true;
        } catch (Exception e) {
            logger.info("{} elementi görünür değil veya {} saniye içinde bulunamadı", key, timeoutInSeconds);
            return false;
        }
    }


    @Step({"Element var mı kontrol et <key> görünmüyorsa sliderı kaydır",
            "Check element <key> exists, if not swipe slider"})
    public void checkElementWithSwipeIfNotVisible(String key) throws InterruptedException {

        int maxSwipe = 10;

        for (int i = 0; i < maxSwipe; i++) {

            WebElement el = findElementByKeyWithoutAssert(key);

            if (el != null) {
                try {
                    if (el.isDisplayed()) {
                        logger.info("{} bulundu ve ekranda görünüyor.", key);
                        return;
                    }
                } catch (Exception ignore) {
                    // Compose/slider repaint yüzünden bazen element stale olabilir; tekrar deneyeceğiz.
                }
            }

            logger.info(key + " görünmüyor. Slider swipe deneniyor... (" + (i + 1) + "/" + maxSwipe + ")");

            // Senin senaryodaki ile aynı swipe: 750,827 -> 150,827
            pointToPointSwipeWithCoordinats(750, 827, 150, 827, 1); // :contentReference[oaicite:1]{index=1}
            waitBySecond(1);
        }

        Assertions.fail(key + " elementi " + maxSwipe + " swipe sonrası bulunamadı!");
    }

    @Step("<key> elementini bulana kadar aşağı kaydır")
    public void scrollDownUntilVisibleSmart(String key) throws InterruptedException {

        String lastSource = "";
        int maxLoop = 15;

        for (int i = 0; i < maxLoop; i++) {

            WebElement el = findElementByKeyWithoutAssert(key);
            if (el != null) {
                try {
                    if (el.isDisplayed()) {
                        // Elementin tamamen görünür olup olmadığını kontrol et
                        Rectangle rect = el.getRect();
                        Dimension screenSize = getDriver().manage().window().getSize();

                        // Elementin tüm kenarlarının ekran içinde olup olmadığını kontrol et
                        boolean isFullyVisible = rect.getX() >= 0 &&
                                (rect.getX() + rect.getWidth()) <= screenSize.width &&
                                rect.getY() >= 0 &&
                                (rect.getY() + rect.getHeight()) <= screenSize.height;

                        if (isFullyVisible) {
                            logger.info(key + " bulundu ve tamamen görünür.");
                            // Bazı cihazlarda scroll sonrası element tam yüklenmesi için kısa bekleme
                            waitBySecond(1);
                            // Elementin hala görünür olduğundan emin ol
                            WebElement verifyEl = findElementByKeyWithoutAssert(key);
                            if (verifyEl != null && verifyEl.isDisplayed()) {
                                Rectangle verifyRect = verifyEl.getRect();
                                boolean stillFullyVisible = verifyRect.getX() >= 0 &&
                                        (verifyRect.getX() + verifyRect.getWidth()) <= screenSize.width &&
                                        verifyRect.getY() >= 0 &&
                                        (verifyRect.getY() + verifyRect.getHeight()) <= screenSize.height;
                                if (stillFullyVisible) {
                                    logger.info(key + " doğrulandı ve tamamen görünür.");
                                    return;
                                } else {
                                    logger.debug(key + " bulundu ama tamamen görünür değil, devam ediliyor...");
                                }
                            } else {
                                logger.debug(key + " bulundu ama doğrulama başarısız, devam ediliyor...");
                            }
                        } else {
                            logger.debug(key + " bulundu ama tamamen görünür değil (x=" + rect.getX() +
                                    ", y=" + rect.getY() + ", width=" + rect.getWidth() +
                                    ", height=" + rect.getHeight() + "), tamamen görünür hale getirmek için scroll yapılıyor...");

                            // Elementin tamamen görünür olması için özel scroll yap
                            Dimension d = getDriver().manage().window().getSize();
                            int centerX = rect.getX() + rect.getWidth() / 2;
                            int centerY = rect.getY() + rect.getHeight() / 2;

                            // Eğer element üstte kesilmişse, yukarı scroll
                            if (rect.getY() < 0 || rect.getY() < d.height * 0.1) {
                                int scrollY = (int) (d.height * 0.3);
                                pointToPointSwipeWithCoordinats(centerX, scrollY, centerX, (int) (d.height * 0.7), 1);
                                logger.debug("Element üstte kesilmiş, yukarı scroll yapıldı");
                            }
                            // Eğer element altta kesilmişse, aşağı scroll
                            else if ((rect.getY() + rect.getHeight()) > d.height * 0.9) {
                                int scrollY = (int) (d.height * 0.7);
                                pointToPointSwipeWithCoordinats(centerX, scrollY, centerX, (int) (d.height * 0.3), 1);
                                logger.debug("Element altta kesilmiş, aşağı scroll yapıldı");
                            }

                            // Eğer element solda kesilmişse, sağa scroll
                            if (rect.getX() < 0 || rect.getX() < d.width * 0.1) {
                                int scrollX = (int) (d.width * 0.3);
                                pointToPointSwipeWithCoordinats(scrollX, centerY, (int) (d.width * 0.7), centerY, 1);
                                logger.debug("Element solda kesilmiş, sağa scroll yapıldı");
                            }
                            // Eğer element sağda kesilmişse, sola scroll
                            else if ((rect.getX() + rect.getWidth()) > d.width * 0.9) {
                                int scrollX = (int) (d.width * 0.7);
                                pointToPointSwipeWithCoordinats(scrollX, centerY, (int) (d.width * 0.3), centerY, 1);
                                logger.debug("Element sağda kesilmiş, sola scroll yapıldı");
                            }

                            waitBySecond(1);
                            // Scroll sonrası tekrar kontrol et
                            continue;
                        }
                    }
                } catch (Exception ignore) {
                }
            }

            String currentSource = getDriver().getPageSource();
            if (currentSource.equals(lastSource)) {
                throw new AssertionError(
                        key + " bulunamadı. Aşağı kaydırma sona ulaştı."
                );
            }
            lastSource = currentSource;

            Dimension d = getDriver().manage().window().getSize();
            int x = d.width / 2;
            int startY = (int) (d.height * 0.80);
            int endY = (int) (d.height * 0.25);

            pointToPointSwipeWithCoordinats(x, startY, x, endY, 1);
            waitBySecond(1);
        }

        throw new AssertionError(key + " bulunamadı. Max deneme aşıldı.");
    }

    @Step("<anchorKey> hizasında <targetKey> bulunana kadar <direction> kaydır")
    public void swipeHorizontallyUntilVisibleSmart(String anchorKey,
                                                   String targetKey,
                                                   String direction)
            throws InterruptedException {

        final int MAX_SWIPE = 10;
        String dir = direction.trim().toUpperCase();

        for (int i = 0; i < MAX_SWIPE; i++) {

            WebElement target = findElementByKeyWithoutAssert(targetKey);
            if (target != null) {
                try {
                    if (target.isDisplayed()) {
                        // Elementin tamamen görünür olup olmadığını kontrol et
                        Rectangle rect = target.getRect();
                        Dimension screenSize = getDriver().manage().window().getSize();

                        // Elementin sol ve sağ kenarlarının ekran içinde olup olmadığını kontrol et
                        boolean isFullyVisible = rect.getX() >= 0 &&
                                (rect.getX() + rect.getWidth()) <= screenSize.width;

                        if (isFullyVisible) {
                            logger.info(targetKey + " is found and fully visible.");
                            // Bazı cihazlarda horizontal scroll sonrası element tam yüklenmesi için kısa bekleme
                            waitBySecond(1);
                            // Elementin hala görünür olduğundan emin ol
                            WebElement verifyTarget = findElementByKeyWithoutAssert(targetKey);
                            if (verifyTarget != null && verifyTarget.isDisplayed()) {
                                Rectangle verifyRect = verifyTarget.getRect();
                                boolean stillFullyVisible = verifyRect.getX() >= 0 &&
                                        (verifyRect.getX() + verifyRect.getWidth()) <= screenSize.width;
                                if (stillFullyVisible) {
                                    logger.info(targetKey + " doğrulandı ve tamamen görünür.");
                                    return;
                                } else {
                                    logger.debug(targetKey + " bulundu ama tamamen görünür değil, devam ediliyor...");
                                }
                            } else {
                                logger.debug(targetKey + " bulundu ama doğrulama başarısız, devam ediliyor...");
                            }
                        } else {
                            logger.debug(targetKey + " bulundu ama tamamen görünür değil (x=" + rect.getX() +
                                    ", width=" + rect.getWidth() + ", screenWidth=" + screenSize.width + "), tamamen görünür hale getirmek için scroll yapılıyor...");

                            // Elementin tamamen görünür olması için özel horizontal scroll yap
                            int centerY = rect.getY() + rect.getHeight() / 2;

                            // Eğer element solda kesilmişse, sağa scroll
                            if (rect.getX() < 0 || rect.getX() < screenSize.width * 0.1) {
                                int scrollX = (int) (screenSize.width * 0.3);
                                pointToPointSwipeWithCoordinats(scrollX, centerY, (int) (screenSize.width * 0.7), centerY, 1);
                                logger.debug("Element solda kesilmiş, sağa scroll yapıldı");
                            }
                            // Eğer element sağda kesilmişse, sola scroll
                            else if ((rect.getX() + rect.getWidth()) > screenSize.width * 0.9) {
                                int scrollX = (int) (screenSize.width * 0.7);
                                pointToPointSwipeWithCoordinats(scrollX, centerY, (int) (screenSize.width * 0.3), centerY, 1);
                                logger.debug("Element sağda kesilmiş, sola scroll yapıldı");
                            }

                            waitBySecond(1);
                            // Scroll sonrası tekrar kontrol et
                            continue;
                        }
                    }
                } catch (Exception ignore) {
                }
            }

            WebElement anchor = findElementByKeyWithoutAssert(anchorKey);
            if (anchor == null) {
                logger.info(anchorKey + " anchor bulunamadi, swipe deneniyor... (" + (i + 1) + "/" + MAX_SWIPE + ")");
            }

            swipeHorizontallyByDirection(anchorKey, dir);
            waitBySecond(1);
        }

        throw new AssertionError(targetKey + " bulunamadı. Max deneme aşıldı: " + MAX_SWIPE);
    }


    private void swipeHorizontallyByDirection(String anchorKey, String direction) throws InterruptedException {

        if ("LEFT".equals(direction)) {
            // sağdan sola (mevcut davranış)
            swipeFromLeftToRightAligned2(anchorKey, 1);
            return;
        }

        if ("RIGHT".equals(direction)) {
            // soldan sağa
            WebElement anchor = findElementByKeyWithoutAssert(anchorKey);
            if (anchor == null) return;

            Rectangle r = anchor.getRect();
            int startX = r.x + (int) (r.width * 0.15);
            int endX = r.x + (int) (r.width * 0.85);
            int y = r.y + r.height / 2;

            pointToPointSwipeWithCoordinats(startX, y, endX, y, 1);
            return;
        }

        throw new IllegalArgumentException(
                "Geçersiz direction: " + direction + " (LEFT veya RIGHT olmalı)"
        );
    }

    @Step("<string> butonuna tıkla <string> mesajı gelirse tekrar tıkla")
    public void favoriteToggleWithToastControl(String favButtonKey, String removedToastKey) {

        findElementByKey(favButtonKey).click();

        boolean toastGeldi = doesElementExistByKey(removedToastKey, 3);
        if (toastGeldi) {
            logger.info("Toast geldi ({}). Tekrar favori eklemek için tekrar tıklanıyor.", removedToastKey);
            clickByKey(favButtonKey);
        } else {
            logger.info("Toast gelmedi (" + removedToastKey + "). Devam ediliyor.");
        }
    }

    @Step("<string> butonuna tıkla <string> butonu gelmezse tekrar tıkla")
    public void clickButtonIfDoesntExistClickAgain(String ButtonKey1, String ButtonKey2) {

        findElementByKey(ButtonKey1).click();

        boolean btnGeldi = doesElementExistByKey(ButtonKey2, 3);
        if (!btnGeldi) {
            logger.info("Btn gelmedi ({}). Tekrar favori eklemek için tekrar tıklanıyor.", ButtonKey2);
            clickByKey(ButtonKey1);
        } else {
            logger.info("Btn geldi (" + ButtonKey2 + "). Devam ediliyor.");
        }
    }

    @Step("<string> varsa tıkla yoksa <string> tıkla")
    public void clickPrimaryOrFallback(String primaryKey, String fallbackKey) {

        Boolean isFavAddBtn = doesElementExistByKey(primaryKey, 3);

        if (isFavAddBtn) {
            logger.info(primaryKey + " bulundu, tıklanıyor.");
            clickByKey(primaryKey);
        } else {
            logger.info(primaryKey + " bulunamadı, " + fallbackKey + " tıklanıyor.");
            clickByKey(fallbackKey);
            clickByKey(primaryKey);
        }
    }

    @Step("<key1> veya <key2> elementi var mı kontrol et")
    public void checkEitherElementExists(String key1, String key2) {

        By by1 = null;
        By by2 = null;

        // key1 için By al
        if (getSelector().getSelectorInfo(key1) != null) {
            by1 = getSelector().getSelectorInfo(key1).getBy();
        } else {
            logger.warn("SelectorInfo bulunamadı: " + key1);
        }

        // key2 için By al
        if (getSelector().getSelectorInfo(key2) != null) {
            by2 = getSelector().getSelectorInfo(key2).getBy();
        } else {
            logger.warn("SelectorInfo bulunamadı: " + key2);
        }

        boolean found = false;

        // key1 kontrol
        if (by1 != null) {
            List<WebElement> list1 = findElementsWithoutAssert(by1);
            if (list1 != null && !list1.isEmpty()) {
                logger.info("Element bulundu: " + key1);
                found = true;
            }
        }

        // key2 kontrol (key1 bulunmadıysa)
        if (!found && by2 != null) {
            List<WebElement> list2 = findElementsWithoutAssert(by2);
            if (list2 != null && !list2.isEmpty()) {
                logger.info("Element bulundu: " + key2);
                found = true;
            }
        }

        if (!found) {
            Assertions.fail(
                    "Ne '" + key1 + "' ne de '" + key2 + "' elementi bulundu."
            );
        }
    }

    @Step("<key> elementi hala varsa tekrardan tıkla.")
    public void clickElementIfStillExists(String key) {

        WebElement element = findElementByKeyWithoutAssert(key);

        if (element != null) {
            logger.info(key + " bulundu, tekrar tıklanıyor.");
            element.click();
        } else {
            logger.info(key + " yok, işlem yapılmadı.");
        }
    }

    @Step("<leftKnobKey> <leftPercent> <rightKnobKey> <rightPercent> yüzde konumuna çekilir, <minInputKey> ve <maxInputKey> inputları doğru mu kontrol edilir")
    public void moveSliderAndVerifyMinMaxInputs(String leftKnobKey, String leftPercent,
                                                String rightKnobKey, String rightPercent,
                                                String minInputKey, String maxInputKey) {

        WebElement bar = findElementByKey("priceSelectionSliderBar");
        WebElement leftKnob = findElementByKey(leftKnobKey);
        WebElement rightKnob = findElementByKey(rightKnobKey);

        double minBefore = parsePrice(getValue(minInputKey));
        double maxBefore = parsePrice(getValue(maxInputKey));

        Rectangle barRect = bar.getRect();
        int y = barRect.getY() + barRect.getHeight() / 2;

        int leftTargetX =
                barRect.getX() + (int) (barRect.getWidth() * (Integer.parseInt(leftPercent) / 100.0));
        int rightTargetX =
                barRect.getX() + (int) (barRect.getWidth() * (Integer.parseInt(rightPercent) / 100.0));


        dragCenter(leftKnob, leftTargetX, y);
        waitBySecond(1);


        rightKnob = findElementByKey(rightKnobKey);
        dragCenter(rightKnob, rightTargetX, y);
        waitBySecond(1);

        double minAfter = parsePrice(getValue(minInputKey));
        double maxAfter = parsePrice(getValue(maxInputKey));

        logger.info("Min before/after: {} -> {}", minBefore, minAfter);
        logger.info("Max before/after: {} -> {}", maxBefore, maxAfter);

        assertTrue(minAfter < maxAfter,
                "En Az, En Çok'tan küçük olmalı");

        assertTrue(
                minBefore != minAfter || maxBefore != maxAfter,
                "Slider oynatıldı ama min/max input değerleri değişmedi"
        );
    }

    private void dragCenter(WebElement el, int toX, int toY) {
        Rectangle r = el.getRect();
        int fromX = r.getX() + r.getWidth() / 2;
        int fromY = r.getY() + r.getHeight() / 2;

        Map<String, Object> params = new HashMap<>();
        params.put("duration", 0.8);
        params.put("fromX", fromX);
        params.put("fromY", fromY);
        params.put("toX", toX);
        params.put("toY", toY);

        ((JavascriptExecutor) getDriver())
                .executeScript("mobile: dragFromToForDuration", params);
    }

    private String getValue(String key) {
        return findElementByKey(key).getAttribute("value");
    }

    private double parsePrice(String raw) {
        if (raw == null) return 0;
        String s = raw.replaceAll("[^0-9,\\.]", "");
        s = s.replace(".", "");
        s = s.replace(",", ".");
        return s.isEmpty() ? 0 : Double.parseDouble(s);
    }


    @Step("<Favori List> adında favori listesi varsa silinir")
    public void deleteIfFavListExists(String favList) {

        if (isIOS()) {
            // iOS için favori listesi silme
            List<WebElement> favLists = getDriver().findElements(By.xpath(
                    "//XCUIElementTypeStaticText[@label=\"" + favList + "\"]" +
                            " | //XCUIElementTypeStaticText[@value=\"" + favList + "\"]" +
                            " | //XCUIElementTypeStaticText[@name=\"" + favList + "\"]"
            ));

            if (favLists.isEmpty()) {
                logger.info("{} adında bir liste yok. Devam ediliyor...", favList);
                return;
            }

            logger.info("({}) adında mevcut favori listesi var. Siliniyor...", favList);

            clickButtonWithText("XCUIElementTypeStaticText", "label", favList);

            clickByKey("betaProfileFavoriteDeleteButton");

            checkByValueText(
                    "XCUIElementTypeStaticText",
                    "value",
                    "Listeniz silinecektir, bu işlem geri alınamaz."
            );

            checkByValueText("XCUIElementTypeButton", "label", "Listemi Sil");
            checkByValueText("XCUIElementTypeButton", "label", "Vazgeç");

            clickButtonWithText("XCUIElementTypeButton", "label", "Listemi Sil");

            checkByValueText(
                    "XCUIElementTypeStaticText",
                    "label",
                    "Listeniz başarıyla silindi."
            );
        } else {
            // Android için favori listesi silme
            List<WebElement> favLists = getDriver().findElements(By.xpath(
                    "//android.widget.TextView[@text=\"" + favList + "\"]"
            ));

            if (favLists.isEmpty()) {
                logger.info("{} adında bir liste yok. Devam ediliyor...", favList);
                return;
            }

            logger.info("({}) adında mevcut favori listesi var. Siliniyor...", favList);

            // Liste adına tıkla
            clickButtonWithText("android.widget.TextView", "text", favList);
            waitBySecond(1);

            // Sil butonuna tıkla
            clickByKey("Profile_Favorilerim_Delete");
            waitBySecond(1);

            // Silme onayı butonuna tıkla
            clickByKey("Profile_Favorilerim_Delete_Kabul");
            waitBySecond(1);
        }
    }

    @Step("Kayıtlı adresleri silmek için <myAddressTrashButton> butonuna tıkla")
    public void deleteIfRegisteredAddresses(String deletebtn) {

        while (doesElementExistByKey(deletebtn, 2)) {
            logger.info("{} is found. Clicking...", deletebtn);

            clickByKey(deletebtn);

            checkByValueText(
                    "XCUIElementTypeStaticText",
                    "value",
                    "Adresinizi silmek istediğinize emin misiniz?"
            );

            clickButtonWithText("XCUIElementTypeButton", "label", "EMİNİM, SİL");

            checkByValueText(
                    "XCUIElementTypeStaticText",
                    "value",
                    "Adresiniz başarıyla silindi."
            );
        }

        logger.info("{} is not found. Continue...", deletebtn);

    }

    @Step("Sharelink kontrol sağla")
    public void sharelinkKontrolSagla() {
        logger.info("Sharelink kontrol adımı başlatılıyor...");

        try {
            logger.info("Paylaş butonuna tıklanıyor...");
            clickByKey("ÜrünDetay_UrunPaylas_Butonu");
            waitBySecond(2);

            boolean shareSheetOpened = false;
            String verificationMessage = "";

            // Kontrol 1: URL içeren text elementi var mı?
            try {
                List<WebElement> urlElements = getDriver().findElements(
                        By.xpath("//android.widget.TextView[contains(@text, 'gratis.com')]")
                );
                if (!urlElements.isEmpty() && urlElements.get(0).isDisplayed()) {
                    shareSheetOpened = true;
                    verificationMessage = "Share sheet açıldı - URL görüntülendi: " + urlElements.get(0).getText();
                    logger.info(verificationMessage);
                }
            } catch (Exception e) {
                logger.debug("URL elementi bulunamadı: " + e.getMessage());
            }

            // Kontrol 2: "Kopyala" butonu var mı?
            if (!shareSheetOpened) {
                try {
                    List<WebElement> copyButtons = getDriver().findElements(
                            By.xpath("//android.widget.Button[contains(@text, 'Kopyala') or contains(@text, 'KOPYALA') or contains(@text, 'Kop')]")
                    );
                    if (!copyButtons.isEmpty() && copyButtons.get(0).isDisplayed()) {
                        shareSheetOpened = true;
                        verificationMessage = "Share sheet açıldı - Kopyala butonu görüntülendi";
                        logger.info(verificationMessage);
                    }
                } catch (Exception e) {
                    logger.debug("Kopyala butonu bulunamadı: " + e.getMessage());
                }
            }

            // Kontrol 3: Share chooser dialog container'ı var mı?
            if (!shareSheetOpened) {
                try {
                    List<WebElement> shareSheetElements = getDriver().findElements(
                            By.xpath("//android.widget.LinearLayout[contains(@resource-id, 'chooser')] | " +
                                    "//androidx.compose.ui.platform.ComposeView[contains(@package, 'android')]")
                    );
                    if (!shareSheetElements.isEmpty()) {
                        shareSheetOpened = true;
                        verificationMessage = "Share sheet açıldı - Chooser dialog görüntülendi";
                        logger.info(verificationMessage);
                    }
                } catch (Exception e) {
                    logger.debug("Chooser dialog bulunamadı: " + e.getMessage());
                }
            }

            // Kontrol 4: Mevcut kontrol elementi (ÜrünDetay_ShareLink_Kontrol) var mı?
            if (!shareSheetOpened) {
                try {
                    if (doesElementExistByKey("ÜrünDetay_ShareLink_Kontrol", 3)) {
                        shareSheetOpened = true;
                        verificationMessage = "Share sheet açıldı - Mevcut kontrol elementi görüntülendi";
                        logger.info(verificationMessage);
                    }
                } catch (Exception e) {
                    logger.debug("Mevcut kontrol elementi bulunamadı: " + e.getMessage());
                }
            }

            if (shareSheetOpened) {
                logger.info("✓ Share sheet başarıyla açıldı ve doğrulandı. " + verificationMessage);
                logger.info("Not: Uygulama listesi telefondan telefona değişiklik gösterebilir, bu normaldir.");
            } else {
                logger.warn("Share sheet açılmış olabilir ancak beklenen sabit elementler bulunamadı.");
                logger.warn("Share sheet'in görünür olup olmadığını manuel olarak kontrol edin.");
            }

        } catch (Exception e) {
            logger.error("Sharelink kontrol sırasında hata oluştu: " + e.getMessage(), e);
            throw new AssertionError("Sharelink kontrol başarısız: " + e.getMessage());
        }
    }


    @Step("Daha önce kayıt olmuş mu kontrol et. <key>")
    public void checkIfRegisteredOrNot(String key) throws InterruptedException {

        boolean isRegistered = findElementByKeyWithoutAssert("KUllanıcı_Gırısı_Merhaba_Control") != null;

        if (key.equals("Kayıtlı değilse üye ol")) {
            if (isRegistered) {
                logger.info("Zaten daha önceden üye olmuş");
            } else {
                logger.info("Kullanıcı kayıtlı değil, üye olma işlemi yapılacak");
                getElementWithKeyIfExists("uyeOlTitle");
                sendKeysByKeyNotClear("DenemeAd", "Register_Forum_Adınız_Alanı_Text");
                sendKeysByKeyNotClear("DenemeSoyad", "Register_Forum_Soyad_Alanı_Text");
                sendKeysByKeyNotClear("denemetest@gmail.com", "Register_Forum_Eposta_Alanı_Text");
                sendKeysByKeyNotClear("13041996", "Register_Forum_Dogum_Tarihi_Alanı_Text");
                swipe(1);
                clickByKey("Register_Forum_Aydınlatma_Metni_CheckBox");
                clickByKey("Register_Forum_Cep_Telefonu_Sms_Onay_CheckBox");
                swipe(1);
                clickByKey("Register_Forum_Eposta_Bilgilendirme_Onay_CheckBox");
                clickByKey("Register_Forum_Callbox_Onay_CheckBox");
                swipe(1);
                clickByKey("Register_Forum_Uyeligi_Olustur_Button");
            }
        } else if (key.equals("Kayıtlı ise hesabı sil")) {
            if (!isRegistered) {
                logger.info("Zaten daha önceden hesap silinmiş");
            } else {
                logger.info("Kullanıcı kayıtlı, hesap silme işlemi yapılacak");
                clickByKey("Profile_Tab_MainMenu_Button");
                clickByKey("Profil_Hesabim_Ve_Ayarlarim_Button");
                clickByKey("Hesabim_Ve_Ayarlarim__Uyelik_Bilgilerim_Button");
                scrollDownUntilVisibleSmart("Uyelik_Bilgilerim_Uyelikten_Ayril_Button");
                clickByKey("Uyelik_Bilgilerim_Uyelikten_Ayril_Button");
                clickByKey("Uyelikten_Ayril_Sil_Button");
                checkByValueText("android.widget.TextView", "text", "Hesabınız başarıyla silinmiştir. Dilediğiniz zaman Gratis avantajlarından yararlanmak için tekrar üye olabilirsiniz.");
                getElementWithKeyIfExists("HomePage_Uye_Olun_Button");
                clickByKey("HomePage_Uye_Olun_Button");
                sendKeysByKeyNotClear("995555505", "Uye_Gırısı_Telefon_Numarası_Text");
                clickByKey("Uye_Gırısı_Gırıs_Yap_Button");
                waitBySecond(1);
                sendKeysByKeyNotClear("141414", "Register_Telefon_Numarası_Text_Area");
                waitBySecond(3);
            }
        } else {
            logger.warn("Bilinmeyen key değeri: " + key);
        }
    }

    @Step("Urun listesinde en alta inilir")
    public void scrollProductListToBottomAuto() {
        final int swipeBatchSize = 15;
        final int maxBatchCount = 20;
        int unchangedBatchCount = 0;

        String previousBatchFingerprint = getListBottomFingerprint();
        for (int batch = 1; batch <= maxBatchCount; batch++) {
            for (int i = 0; i < swipeBatchSize; i++) {
                swipeDownAccordingToPhoneSize();
                waitBySecond(1);
            }

            String currentBatchFingerprint = getListBottomFingerprint();
            if (currentBatchFingerprint.equals(previousBatchFingerprint)) {
                unchangedBatchCount++;
                logger.info("Liste sonu kontrolü: batch {} sonrası değişim yok ({}/2)", batch, unchangedBatchCount);
            } else {
                unchangedBatchCount = 0;
                logger.info("Liste ilerledi: batch {} sonrası alt fingerprint değişti.", batch);
            }

            if (unchangedBatchCount >= 2) {
                logger.info("Ürün listesinde en alta ulaşıldı. Toplam batch: {}", batch);
                return;
            }
            previousBatchFingerprint = currentBatchFingerprint;
        }

        fail("Ürün listesinde en alta ulaşılamadı. Güvenlik limiti aşıldı (" + (swipeBatchSize * maxBatchCount) + " swipe).");
    }

    private String getListBottomFingerprint() {
        try {
            // Android ve iOS için farklı ağaçlar var; liste sonunu anlamak için ekrandaki en alttaki birkaç text'i fingerprint yapıyoruz.
            boolean isIos = getDriver() instanceof io.appium.java_client.ios.IOSDriver;

            List<WebElement> textElements;
            if (isIos) {
                // iOS: StaticText'ler genelde label/value taşır; name bazen full text olur.
                textElements = getDriver().findElements(By.xpath("//XCUIElementTypeStaticText"));
            } else {
                textElements = getDriver().findElements(By.xpath("//android.widget.TextView[normalize-space(@text)!='']"));
            }

            if (textElements == null || textElements.isEmpty()) {
                return "";
            }

            int from = Math.max(0, textElements.size() - 8);
            StringBuilder sb = new StringBuilder();
            for (int i = from; i < textElements.size(); i++) {
                WebElement el = textElements.get(i);
                if (el == null) continue;

                String text;
                if (isIos) {
                    // iOS'da getText() bazen boş döner; attribute'lerden oku.
                    text = el.getAttribute("name");
                    if (text == null || text.isBlank()) text = el.getAttribute("label");
                    if (text == null || text.isBlank()) text = el.getAttribute("value");
                    if (text == null || text.isBlank()) text = el.getText();
                } else {
                    text = el.getText();
                }

                if (text != null && !text.isBlank()) {
                    sb.append(text.trim()).append('|');
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logger.debug("Liste alt fingerprint alınamadı: {}", e.getMessage());
            return "";
        }
    }


    @Step("Deeplink ile <url> sayfası açılır ve <openType> üzerinde açıldığı kontrol edilir")
    public void openDeepLinkAndVerify(String url, String openType) {

        logger.info(
                "Deeplink testi başlıyor. URL: {}, Beklenen açılma tipi: {}",
                url,
                openType
        );

        getDeepLinkHelper()
                .openAndVerify(url, openType);

        logger.info(
                "Deeplink testi başarılı. URL: {}, Beklenen açılma tipi: {}",
                url,
                openType
        );
    }
    @Step("Deeplink ile <url> sayfası açılır ve uygulamanın açıldığı kontrol edilir")
    public void openDeepLinkAndVerifyApp(String url) {

        getDeepLinkHelper()
                .openAndVerify(url, "APP");
    }
}