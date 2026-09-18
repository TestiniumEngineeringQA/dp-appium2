package com.testinium;

import com.testinium.api.support.ApiSession;
import com.testinium.driver.TestiniumAndroidDriver;
import com.testinium.driver.TestiniumIOSDriver;
import com.testinium.selector.Selector;
import com.testinium.selector.SelectorFactory;
import com.testinium.selector.SelectorType;
import com.testinium.util.TestiniumEnvironment;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;

import static com.testinium.util.Constants.CapabilityConstants.APPIUM_AUTO_GRANT_PERMISSIONS;

public class HookImpl {

    /*
     * ---------------------------------------------------------
     * APP CONFIG
     * ---------------------------------------------------------
     */

    public static final String ANDROID_PACKAGE =
            "com.gratis.android";

    public static final String ANDROID_ACTIVITY =
            "com.gratis.android.GratisMainActivity";

    public static final String IOS_BUNDLE_ID =
            "com.pharos.Gratis";


    /*
     * ---------------------------------------------------------
     * IOS SESSION CONFIG
     * ---------------------------------------------------------
     */

    private static final int IOS_SESSION_MAX_ATTEMPTS = 3;

    private static final long IOS_SESSION_RETRY_DELAY_MS =
            5000L;


    /*
     * ---------------------------------------------------------
     * LOGGER
     * ---------------------------------------------------------
     */

    private final Logger logger =
            LoggerFactory.getLogger(getClass());


    /*
     * ---------------------------------------------------------
     * THREAD LOCAL OBJECTS
     * ---------------------------------------------------------
     */

    public static ThreadLocal<AppiumDriver> driver =
            new ThreadLocal<>();

    public static ThreadLocal<FluentWait<AppiumDriver>>
            appiumFluentWait = new ThreadLocal<>();

    protected ThreadLocal<Boolean> localAndroid =
            ThreadLocal.withInitial(() -> false);

    public ThreadLocal<Boolean> isDeviceAnd =
            ThreadLocal.withInitial(() -> true);

    public static ThreadLocal<Selector> selector =
            new ThreadLocal<>();

    protected ThreadLocal<URL> hubUrl =
            new ThreadLocal<>();


    /*
     * ---------------------------------------------------------
     * GETTERS
     * ---------------------------------------------------------
     */

    public static Selector getSelector() {
        return selector.get();
    }


    public static AppiumDriver getDriver() {
        return driver.get();
    }


    public static FluentWait<AppiumDriver>
    getAppiumFluentWait() {

        return appiumFluentWait.get();
    }


    /*
     * ---------------------------------------------------------
     * PLATFORM HELPERS
     * ---------------------------------------------------------
     */

    public static boolean isAndroidDriver() {

        return getDriver() instanceof AndroidDriver;
    }


    public static boolean isIOSDriver() {

        return getDriver() instanceof IOSDriver;
    }


    /*
     * ---------------------------------------------------------
     * APP FOREGROUND CONTROL
     * ---------------------------------------------------------
     */

    protected void waitUntilAppInForeground(
            AppiumDriver driver,
            String bundleId
    ) {

        new WebDriverWait(
                driver,
                Duration.ofSeconds(60)
        ).until(d -> {

            try {

                ApplicationState state =
                        ((io.appium.java_client.InteractsWithApps) d)
                                .queryAppState(bundleId);

                return state ==
                        ApplicationState.RUNNING_IN_FOREGROUND;

            } catch (Exception e) {

                logger.debug(
                        "App state alınamadı. BundleId: {} Hata: {}",
                        bundleId,
                        e.getMessage()
                );

                return false;
            }
        });
    }


    /*
     * ---------------------------------------------------------
     * IOS DRIVER CREATE RETRY
     * ---------------------------------------------------------
     */

    private TestiniumIOSDriver createIosDriverWithRetry(
            URL hubUrl,
            DesiredCapabilities capabilities
    ) throws Exception {

        SessionNotCreatedException lastFailure = null;

        for (
                int attempt = 1;
                attempt <= IOS_SESSION_MAX_ATTEMPTS;
                attempt++
        ) {

            try {

                logger.info(
                        "iOS session oluşturuluyor (deneme {}/{})",
                        attempt,
                        IOS_SESSION_MAX_ATTEMPTS
                );

                return new TestiniumIOSDriver(
                        hubUrl,
                        capabilities
                );

            } catch (SessionNotCreatedException e) {

                lastFailure = e;

                logger.warn(
                        "iOS session oluşturulamadı "
                                + "(deneme {}/{}): {}",
                        attempt,
                        IOS_SESSION_MAX_ATTEMPTS,
                        e.getMessage()
                );

                if (attempt < IOS_SESSION_MAX_ATTEMPTS) {

                    Thread.sleep(
                            IOS_SESSION_RETRY_DELAY_MS
                    );
                }
            }
        }

        throw lastFailure;
    }


    /*
     * ---------------------------------------------------------
     * BEFORE SCENARIO
     * ---------------------------------------------------------
     */

    @BeforeScenario
    public void beforeScenario() throws Exception {

        localAndroid.set(false);

        /*
         * -----------------------------------------------------
         * LOCAL
         * -----------------------------------------------------
         */

        if (StringUtils.isEmpty(System.getenv("key"))) {

            /*
             * -------------------------------------------------
             * LOCAL ANDROID
             * -------------------------------------------------
             */

            if (localAndroid.get()) {

                isDeviceAnd.set(true);

                logger.info(
                        "Local Android testi başlatılıyor."
                );

                DesiredCapabilities overridden =
                        new DesiredCapabilities();

                overridden.setCapability(
                        MobileCapabilityType.PLATFORM_NAME,
                        MobilePlatform.ANDROID
                );

                overridden.setCapability(
                        MobileCapabilityType.DEVICE_NAME,
                        "RFCY51GBYTL"
                );

                overridden.setCapability(
                        MobileCapabilityType.AUTOMATION_NAME,
                        "uiautomator2"
                );

                overridden.setCapability(
                        AndroidMobileCapabilityType.APP_PACKAGE,
                        ANDROID_PACKAGE
                );

                overridden.setCapability(
                        AndroidMobileCapabilityType.APP_ACTIVITY,
                        ANDROID_ACTIVITY
                );

                overridden.setCapability(
                        "skipDeviceInitialization",
                        true
                );

                overridden.setCapability(
                        "skipServerInstallation",
                        true
                );

                overridden.setCapability(
                        "enforceXPath1",
                        true
                );

                overridden.setCapability(
                        "disableIdLocatorAutocompletion",
                        true
                );

                overridden.setCapability(
                        "autoGrantPermissions",
                        true
                );

                overridden.setCapability(
                        "unicodeKeyboard",
                        true
                );

                overridden.setCapability(
                        "waitForIdleTimeout",
                        150
                );

                overridden.setCapability(
                        MobileCapabilityType.NO_RESET,
                        false
                );

                overridden.setCapability(
                        MobileCapabilityType.FULL_RESET,
                        false
                );

                overridden.setCapability(
                        MobileCapabilityType.NEW_COMMAND_TIMEOUT,
                        3000
                );

                URL url =
                        new URL(
                                "http://127.0.0.1:4723/"
                        );

                driver.set(
                        new AndroidDriver(
                                url,
                                overridden
                        )
                );


                /*
                 * -------------------------------------------------
                 * LOCAL IOS
                 * -------------------------------------------------
                 */

            } else {

                isDeviceAnd.set(false);

                logger.info(
                        "Local iOS testi başlatılıyor."
                );

                DesiredCapabilities overridden =
                        new DesiredCapabilities();

                overridden.setCapability(
                        MobileCapabilityType.PLATFORM_NAME,
                        MobilePlatform.IOS
                );

                overridden.setCapability(
                        MobileCapabilityType.AUTOMATION_NAME,
                        "XCUITest"
                );

                overridden.setCapability(
                        MobileCapabilityType.UDID,
                        "463a05d668b1e9221c77ddd337f72c5eb7ce5fab"
                );

                overridden.setCapability(
                        IOSMobileCapabilityType.BUNDLE_ID,
                        IOS_BUNDLE_ID
                );

                overridden.setCapability(
                        MobileCapabilityType.DEVICE_NAME,
                        "devtestinium iPhone X"
                );

                overridden.setCapability(
                        MobileCapabilityType.PLATFORM_VERSION,
                        "16.7.10"
                );

                overridden.setCapability(
                        MobileCapabilityType.NEW_COMMAND_TIMEOUT,
                        300
                );

                URL url =
                        new URL(
                                "http://127.0.0.1:4723/"
                        );

                driver.set(
                        new IOSDriver(
                                url,
                                overridden
                        )
                );

                waitUntilAppInForeground(
                        driver.get(),
                        IOS_BUNDLE_ID
                );
            }


            /*
             * -----------------------------------------------------
             * TESTINIUM
             * -----------------------------------------------------
             */

        } else {

            logger.info(
                    "Testinium testi başlatılıyor."
            );

            logger.info(
                    "isAndroid: {}",
                    TestiniumEnvironment
                            .isPlatformAndroid()
            );


            /*
             * -------------------------------------------------
             * TESTINIUM ANDROID
             * -------------------------------------------------
             */

            if (
                    localAndroid.get()
                            || TestiniumEnvironment
                            .isPlatformAndroid()
            ) {

                isDeviceAnd.set(true);

                DesiredCapabilities overridden =
                        new DesiredCapabilities();

                overridden.setCapability(
                        "key",
                        System.getenv("key")
                );

                overridden.setCapability(
                        "appium:noReset",
                        false
                );

                overridden.setCapability(
                        "appium:fullReset",
                        false
                );

                overridden.setCapability(
                        APPIUM_AUTO_GRANT_PERMISSIONS,
                        true
                );

                overridden.setCapability(
                        "unicodeKeyboard",
                        true
                );

                overridden.setCapability(
                        "resetKeyboard",
                        true
                );

                overridden.setCapability(
                        "appium:settings[waitForIdleTimeout]",
                        500
                );

                overridden.setCapability(
                        "appium:skipDeviceInitialization",
                        true
                );

                overridden.setCapability(
                        "appium:skipServerInstallation",
                        true
                );

                overridden.setCapability(
                        "appium:ignoreUnimportantViews",
                        false
                );

                overridden.setCapability(
                        "appium:enforceXPath1",
                        true
                );

                overridden.setCapability(
                        "appium:disableIdLocatorAutocompletion",
                        true
                );

                hubUrl.set(
                        new URL(
                                "http://192.168.1.89:4723/"
                        )
                );

                driver.set(
                        new TestiniumAndroidDriver(
                                hubUrl.get(),
                                overridden
                        )
                );


                /*
                 * -------------------------------------------------
                 * TESTINIUM IOS
                 * -------------------------------------------------
                 */

            } else {

                isDeviceAnd.set(false);

                logger.info(
                        "iOS Test başlıyor."
                );

                DesiredCapabilities overridden =
                        new DesiredCapabilities();

                overridden.setCapability(
                        "platformName",
                        "iOS"
                );

                overridden.setCapability(
                        "appium:automationName",
                        "XCUITest"
                );

                overridden.setCapability(
                        "appium:noReset",
                        false
                );

                overridden.setCapability(
                        "appium:fullReset",
                        true
                );

                overridden.setCapability(
                        "key",
                        System.getenv("key")
                );

                overridden.setCapability(
                        "appium:bundleId",
                        IOS_BUNDLE_ID
                );

                hubUrl.set(
                        new URL(
                                "http://hub-devcluster.testinium.io:4444/wd/hub"
                        )
                );

                driver.set(
                        createIosDriverWithRetry(
                                hubUrl.get(),
                                overridden
                        )
                );

                waitUntilAppInForeground(
                        driver.get(),
                        IOS_BUNDLE_ID
                );
            }
        }


        /*
         * ---------------------------------------------------------
         * SELECTOR
         * ---------------------------------------------------------
         */

        selector.set(
                SelectorFactory.createElementHelper(
                        (
                                localAndroid.get()
                                        || TestiniumEnvironment
                                        .isPlatformAndroid()
                        )
                                ? SelectorType.ANDROID
                                : SelectorType.IOS
                )
        );


        /*
         * ---------------------------------------------------------
         * IMPLICIT WAIT
         * ---------------------------------------------------------
         */

        getDriver()
                .manage()
                .timeouts()
                .implicitlyWait(
                        Duration.ofSeconds(5)
                );


        /*
         * ---------------------------------------------------------
         * FLUENT WAIT
         * ---------------------------------------------------------
         */

        FluentWait<AppiumDriver> wait =
                new FluentWait<>(getDriver())
                        .withTimeout(
                                Duration.ofSeconds(8)
                        )
                        .pollingEvery(
                                Duration.ofMillis(350)
                        )
                        .ignoring(
                                NoSuchElementException.class
                        );

        appiumFluentWait.set(wait);

        logger.info(
                "Driver hazır. Platform: {} Session: {}",
                getDriver()
                        .getCapabilities()
                        .getPlatformName(),
                getDriver()
                        .getSessionId()
        );
    }


    /*
     * ---------------------------------------------------------
     * AFTER SCENARIO
     * ---------------------------------------------------------
     */

    @AfterScenario
    public void afterScenario() {

        ApiSession.clear();

        if (getDriver() != null) {

            try {

                logger.info(
                        "Driver kapatılıyor. Session: {}",
                        getDriver().getSessionId()
                );

                getDriver().quit();

            } catch (Exception e) {

                logger.error(
                        "Driver quit sırasında hata oluştu "
                                + "(Timeout vb.): {}",
                        e.getMessage()
                );

            } finally {

                driver.remove();
                selector.remove();
                appiumFluentWait.remove();
                localAndroid.remove();
                isDeviceAnd.remove();
                hubUrl.remove();

                logger.info(
                        "ThreadLocal driver bilgileri temizlendi."
                );
            }
        }
    }
}