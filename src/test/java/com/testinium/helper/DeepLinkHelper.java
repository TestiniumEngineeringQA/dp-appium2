package com.testinium.helper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DeepLinkHelper {

    private static final Logger logger =
            LoggerFactory.getLogger(DeepLinkHelper.class);

    private static final int APP_OPEN_TIMEOUT = 20;

    private static final String IOS_SAFARI_BUNDLE_ID =
            "com.apple.mobilesafari";

    private final AppiumDriver driver;
    private final String androidPackage;
    private final String iosBundleId;


    public DeepLinkHelper(
            AppiumDriver driver,
            String androidPackage,
            String iosBundleId
    ) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "Appium driver null olamaz."
            );
        }

        this.driver = driver;
        this.androidPackage = androidPackage;
        this.iosBundleId = iosBundleId;
    }


    /*
     * =========================================================
     * PUBLIC METHODS
     * =========================================================
     */

    /*
     * ESKİ KULLANIMI KORUYORUZ.
     *
     * openAndVerify(url)
     *
     * APP olarak çalışmaya devam eder.
     */
    public void openAndVerify(String url) {

        openAndVerify(url, "APP");
    }


    /*
     * YENİ KULLANIM:
     *
     * APP
     * BROWSER
     */
    public void openAndVerify(
            String url,
            String openType
    ) {

        validateUrl(url);

        String expectedOpenType =
                normalizeOpenType(openType);

        long startTime =
                System.currentTimeMillis();

        try {

            logger.info(
                    "[DEEPLINK][TEST START] "
                            + "URL: {}, Beklenen açılma tipi: {}",
                    url,
                    expectedOpenType
            );


            /*
             * =================================================
             * APP
             * =================================================
             */
            if ("APP".equals(expectedOpenType)) {

                logger.info(
                        "[DEEPLINK][APP] "
                                + "Uygulamada açılma testi başlıyor. URL: {}",
                        url
                );

                openForApp(url);

                verifyAppIsForeground();
            }


            /*
             * =================================================
             * BROWSER
             * =================================================
             */
            else if ("BROWSER".equals(expectedOpenType)) {

                logger.info(
                        "[DEEPLINK][BROWSER] "
                                + "Tarayıcıda açılma testi başlıyor. URL: {}",
                        url
                );

                openForBrowser(url);

                verifyBrowserIsForeground();
            }


            long duration =
                    System.currentTimeMillis() - startTime;

            logger.info(
                    "[DEEPLINK][SUCCESS] "
                            + "Deeplink testi başarılı. "
                            + "URL: {}, Beklenen: {}, Süre: {} ms",
                    url,
                    expectedOpenType,
                    duration
            );

        } catch (AssertionError e) {

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.error(
                    "[DEEPLINK][ASSERTION FAILED] "
                            + "URL: {}, Beklenen: {}, "
                            + "Süre: {} ms, Hata: {}",
                    url,
                    expectedOpenType,
                    duration,
                    e.getMessage(),
                    e
            );

            throw e;

        } catch (Exception e) {

            long duration =
                    System.currentTimeMillis() - startTime;

            logger.error(
                    "[DEEPLINK][ERROR] "
                            + "URL: {}, Beklenen: {}, "
                            + "Süre: {} ms, "
                            + "Exception: {}, Message: {}",
                    url,
                    expectedOpenType,
                    duration,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "Deeplink testi başarısız."
                            + "\nURL      : " + url
                            + "\nBeklenen : " + expectedOpenType
                            + "\nHata     : " + e.getMessage(),
                    e
            );
        }
    }


    /*
     * =========================================================
     * OPEN DISPATCHER
     * =========================================================
     */

    private void openForApp(String url) {

        if (driver instanceof AndroidDriver) {

            /*
             * Android tarafında ESKİ ÇALIŞAN
             * davranış korunuyor.
             */
            openAndroid(url);

        } else if (driver instanceof IOSDriver) {

            /*
             * iOS APP tarafında bundleId veriyoruz.
             * Eski çalışan davranış.
             */
            openIOSForApp(url);

        } else {

            throw new IllegalStateException(
                    "Desteklenmeyen driver tipi: "
                            + driver.getClass().getName()
            );
        }
    }


    private void openForBrowser(String url) {

        if (driver instanceof AndroidDriver) {

            /*
             * Android URL resolution yine işletim
             * sistemine bırakılıyor.
             *
             * Sonrasında gerçekten browser açıldı mı
             * kontrol ediyoruz.
             */
            openAndroid(url);

        } else if (driver instanceof IOSDriver) {

            /*
             * Browser istendiği için Safari hedefleniyor.
             */
            openIOSForBrowser(url);

        } else {

            throw new IllegalStateException(
                    "Desteklenmeyen driver tipi: "
                            + driver.getClass().getName()
            );
        }
    }


    /*
     * =========================================================
     * VERIFY DISPATCHER
     * =========================================================
     */

    public void verifyAppIsForeground() {

        if (driver instanceof AndroidDriver) {

            verifyAndroidAppIsForeground();

        } else if (driver instanceof IOSDriver) {

            verifyIOSAppIsForeground();

        } else {

            throw new IllegalStateException(
                    "Desteklenmeyen driver tipi: "
                            + driver.getClass().getName()
            );
        }
    }


    private void verifyBrowserIsForeground() {

        if (driver instanceof AndroidDriver) {

            verifyAndroidBrowserIsForeground();

        } else if (driver instanceof IOSDriver) {

            verifyIOSBrowserIsForeground();

        } else {

            throw new IllegalStateException(
                    "Browser kontrolü desteklenmeyen driver tipi: "
                            + driver.getClass().getName()
            );
        }
    }


    /*
     * =========================================================
     * ANDROID - OPEN
     * =========================================================
     *
     * BURASI MEVCUT ÇALIŞAN DAVRANIŞ.
     *
     * APP / BROWSER için package zorlamıyoruz.
     * Android URL resolution mekanizması çalışıyor.
     *
     * APP ise sonrasında Gratis kontrol edilir.
     * BROWSER ise sonrasında browser kontrol edilir.
     */

    private void openAndroid(String url) {

        try {

            logger.info(
                    "[DEEPLINK][ANDROID] "
                            + "mobile:deepLink deneniyor. URL: {}",
                    url
            );

            Map<String, Object> args =
                    new HashMap<>();

            /*
             * Package özellikle VERİLMİYOR.
             *
             * Mevcut Android davranışı korunuyor.
             */
            args.put("url", url);

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "mobile: deepLink",
                            args
                    );

            logger.info(
                    "[DEEPLINK][ANDROID] "
                            + "mobile:deepLink komutu gönderildi."
            );

        } catch (Exception deepLinkException) {

            logger.warn(
                    "[DEEPLINK][ANDROID][FALLBACK] "
                            + "mobile:deepLink başarısız. "
                            + "ADB shell fallback deneniyor. "
                            + "Exception: {}, Message: {}",
                    deepLinkException.getClass().getSimpleName(),
                    deepLinkException.getMessage(),
                    deepLinkException
            );

            openAndroidViaShell(url);
        }
    }


    /*
     * =========================================================
     * ANDROID - SHELL FALLBACK
     * =========================================================
     */

    private void openAndroidViaShell(String url) {

        try {

            Map<String, Object> shellArgs =
                    new HashMap<>();

            shellArgs.put(
                    "command",
                    "am"
            );

            shellArgs.put(
                    "args",
                    Arrays.asList(
                            "start",
                            "-W",
                            "-a",
                            "android.intent.action.VIEW",
                            "-d",
                            url
                    )
            );

            logger.info(
                    "[DEEPLINK][ANDROID][SHELL] "
                            + "ADB shell ile URL açılıyor. URL: {}",
                    url
            );

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "mobile: shell",
                            shellArgs
                    );

            logger.info(
                    "[DEEPLINK][ANDROID][SHELL] "
                            + "ADB shell komutu başarıyla gönderildi."
            );

        } catch (Exception shellException) {

            logger.error(
                    "[DEEPLINK][ANDROID][SHELL ERROR] "
                            + "ADB shell fallback başarısız. "
                            + "URL: {}, Exception: {}, Message: {}",
                    url,
                    shellException.getClass().getSimpleName(),
                    shellException.getMessage(),
                    shellException
            );

            throw new RuntimeException(
                    "Android deeplink açılamadı."
                            + "\nURL: " + url,
                    shellException
            );
        }
    }


    /*
     * =========================================================
     * ANDROID - APP VERIFY
     * =========================================================
     */

    private void verifyAndroidAppIsForeground() {

        AndroidDriver androidDriver =
                (AndroidDriver) driver;

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(APP_OPEN_TIMEOUT)
                );

        String actualPackage = null;

        try {

            logger.info(
                    "[DEEPLINK][ANDROID][APP VERIFY] "
                            + "Gratis foreground kontrolü başladı. "
                            + "Beklenen package: {}",
                    androidPackage
            );

            wait.until(d -> {

                try {

                    String currentPackage =
                            androidDriver.getCurrentPackage();

                    logger.debug(
                            "[DEEPLINK][ANDROID][APP VERIFY] "
                                    + "Aktif package: {}",
                            currentPackage
                    );

                    return androidPackage.equals(
                            currentPackage
                    );

                } catch (Exception e) {

                    logger.debug(
                            "[DEEPLINK][ANDROID][APP VERIFY] "
                                    + "Aktif package henüz alınamadı: {}",
                            e.getMessage()
                    );

                    return false;
                }
            });

            actualPackage =
                    androidDriver.getCurrentPackage();

            assertEquals(
                    androidPackage,
                    actualPackage,
                    "Deeplink sonrası beklenen Android uygulaması açılmadı."
            );

            logger.info(
                    "[DEEPLINK][ANDROID][APP SUCCESS] "
                            + "Gratis uygulaması foreground'da. "
                            + "Beklenen package: {}, Aktif package: {}",
                    androidPackage,
                    actualPackage
            );

        } catch (TimeoutException e) {

            try {

                actualPackage =
                        androidDriver.getCurrentPackage();

            } catch (Exception packageException) {

                logger.warn(
                        "[DEEPLINK][ANDROID][APP] "
                                + "Timeout sonrası aktif package alınamadı. "
                                + "Hata: {}",
                        packageException.getMessage(),
                        packageException
                );
            }

            logger.error(
                    "[DEEPLINK][ANDROID][APP TIMEOUT] "
                            + "Gratis uygulaması {} saniye içinde "
                            + "foreground'a gelmedi. "
                            + "Beklenen package: {}, Aktif package: {}",
                    APP_OPEN_TIMEOUT,
                    androidPackage,
                    actualPackage,
                    e
            );

            fail(
                    "Deeplink sonrası Gratis uygulaması açılmadı."
                            + "\nPlatform         : Android"
                            + "\nBeklenen         : APP"
                            + "\nBeklenen package : " + androidPackage
                            + "\nAktif package    : " + actualPackage
                            + "\nTimeout          : "
                            + APP_OPEN_TIMEOUT
                            + " saniye"
            );

        } catch (AssertionError e) {

            logger.error(
                    "[DEEPLINK][ANDROID][APP ASSERTION FAILED] "
                            + "Package doğrulaması başarısız. "
                            + "Beklenen: {}, Gerçek: {}",
                    androidPackage,
                    actualPackage,
                    e
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[DEEPLINK][ANDROID][APP UNEXPECTED ERROR] "
                            + "Foreground kontrolünde beklenmeyen hata. "
                            + "Beklenen package: {}, "
                            + "Aktif package: {}, "
                            + "Exception: {}, Message: {}",
                    androidPackage,
                    actualPackage,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            fail(
                    "Android deeplink doğrulaması sırasında hata oluştu."
                            + "\nException: "
                            + e.getClass().getSimpleName()
                            + "\nMessage: "
                            + e.getMessage()
            );
        }
    }


    /*
     * =========================================================
     * ANDROID - BROWSER VERIFY
     * =========================================================
     */

    private void verifyAndroidBrowserIsForeground() {

        AndroidDriver androidDriver =
                (AndroidDriver) driver;

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(APP_OPEN_TIMEOUT)
                );

        final String[] actualPackage = {null};

        try {

            logger.info(
                    "[DEEPLINK][ANDROID][BROWSER VERIFY] "
                            + "Browser foreground kontrolü başladı."
            );

            wait.until(d -> {

                try {

                    actualPackage[0] =
                            androidDriver.getCurrentPackage();

                    logger.debug(
                            "[DEEPLINK][ANDROID][BROWSER VERIFY] "
                                    + "Aktif package: {}",
                            actualPackage[0]
                    );

                    return isAndroidBrowserPackage(
                            actualPackage[0]
                    );

                } catch (Exception e) {

                    logger.debug(
                            "[DEEPLINK][ANDROID][BROWSER VERIFY] "
                                    + "Aktif package alınamadı. Hata: {}",
                            e.getMessage()
                    );

                    return false;
                }
            });

            logger.info(
                    "[DEEPLINK][ANDROID][BROWSER SUCCESS] "
                            + "Deeplink browser'da açıldı. "
                            + "Aktif package: {}",
                    actualPackage[0]
            );

        } catch (TimeoutException e) {

            try {

                actualPackage[0] =
                        androidDriver.getCurrentPackage();

            } catch (Exception packageException) {

                logger.warn(
                        "[DEEPLINK][ANDROID][BROWSER] "
                                + "Timeout sonrası aktif package alınamadı. "
                                + "Hata: {}",
                        packageException.getMessage(),
                        packageException
                );
            }

            logger.error(
                    "[DEEPLINK][ANDROID][BROWSER TIMEOUT] "
                            + "Browser {} saniye içinde foreground'a gelmedi. "
                            + "Aktif package: {}, Gratis package: {}",
                    APP_OPEN_TIMEOUT,
                    actualPackage[0],
                    androidPackage,
                    e
            );

            fail(
                    "Deeplink browser'da açılmadı."
                            + "\nPlatform       : Android"
                            + "\nBeklenen       : BROWSER"
                            + "\nAktif package  : " + actualPackage[0]
                            + "\nGratis package : " + androidPackage
                            + "\nTimeout        : "
                            + APP_OPEN_TIMEOUT
                            + " saniye"
            );
        }
    }


    /*
     * Android'de yaygın browser package'ları.
     *
     * Testinium cihazında farklı bir browser package
     * görürsek buraya ekleyebiliriz.
     */
    private boolean isAndroidBrowserPackage(
            String packageName
    ) {

        if (packageName == null) {
            return false;
        }

        return packageName.equals("com.android.chrome")
                || packageName.equals("com.google.android.apps.chrome")
                || packageName.equals("org.chromium.chrome")
                || packageName.equals("com.sec.android.app.sbrowser")
                || packageName.equals("org.mozilla.firefox")
                || packageName.equals("com.microsoft.emmx")
                || packageName.equals("com.opera.browser");
    }


    /*
     * =========================================================
     * IOS - APP OPEN
     * =========================================================
     *
     * BURASI ESKİ ÇALIŞAN DAVRANIŞA GERİ DÖNDÜ.
     *
     * APP istendiğinde Gratis bundleId'si gönderiliyor.
     */

    private void openIOSForApp(String url) {

        try {

            logger.info(
                    "[DEEPLINK][IOS][APP] "
                            + "Deeplink Gratis uygulamasında açılıyor. "
                            + "URL: {}, BundleId: {}",
                    url,
                    iosBundleId
            );

            Map<String, Object> args =
                    new HashMap<>();

            args.put(
                    "url",
                    url
            );

            /*
             * ÖNEMLİ:
             * Eski çalışan APP davranışı.
             */
            args.put(
                    "bundleId",
                    iosBundleId
            );

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "mobile: deepLink",
                            args
                    );

            logger.info(
                    "[DEEPLINK][IOS][APP OPEN SUCCESS] "
                            + "Deeplink komutu başarılı. "
                            + "URL: {}, BundleId: {}",
                    url,
                    iosBundleId
            );

        } catch (Exception e) {

            logger.error(
                    "[DEEPLINK][IOS][APP OPEN ERROR] "
                            + "Deeplink açılamadı. "
                            + "URL: {}, BundleId: {}, "
                            + "Exception: {}, Message: {}",
                    url,
                    iosBundleId,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "iOS APP deeplink açılamadı."
                            + "\nURL      : " + url
                            + "\nBundleId : " + iosBundleId
                            + "\nException: "
                            + e.getClass().getSimpleName()
                            + "\nMessage  : "
                            + e.getMessage(),
                    e
            );
        }
    }


    /*
     * =========================================================
     * IOS - BROWSER OPEN
     * =========================================================
     */

    private void openIOSForBrowser(String url) {

        try {

            logger.info(
                    "[DEEPLINK][IOS][BROWSER] "
                            + "URL Safari'de açılıyor. "
                            + "URL: {}, Safari BundleId: {}",
                    url,
                    IOS_SAFARI_BUNDLE_ID
            );

            Map<String, Object> args =
                    new HashMap<>();

            args.put(
                    "url",
                    url
            );

            args.put(
                    "bundleId",
                    IOS_SAFARI_BUNDLE_ID
            );

            ((JavascriptExecutor) driver)
                    .executeScript(
                            "mobile: deepLink",
                            args
                    );

            logger.info(
                    "[DEEPLINK][IOS][BROWSER OPEN SUCCESS] "
                            + "URL Safari'ye gönderildi. URL: {}",
                    url
            );

        } catch (Exception e) {

            logger.error(
                    "[DEEPLINK][IOS][BROWSER OPEN ERROR] "
                            + "URL Safari'de açılamadı. "
                            + "URL: {}, Exception: {}, Message: {}",
                    url,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "iOS browser deeplink açılamadı."
                            + "\nURL: " + url
                            + "\nException: "
                            + e.getClass().getSimpleName()
                            + "\nMessage: "
                            + e.getMessage(),
                    e
            );
        }
    }


    /*
     * =========================================================
     * IOS - APP VERIFY
     * =========================================================
     */

    private void verifyIOSAppIsForeground() {

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(APP_OPEN_TIMEOUT)
                );

        ApplicationState actualState = null;

        try {

            logger.info(
                    "[DEEPLINK][IOS][APP VERIFY] "
                            + "Gratis foreground kontrolü başladı. "
                            + "BundleId: {}",
                    iosBundleId
            );

            wait.until(d -> {

                try {

                    ApplicationState state =
                            getIOSApplicationState();

                    logger.debug(
                            "[DEEPLINK][IOS][APP VERIFY] "
                                    + "BundleId: {}, AppState: {}",
                            iosBundleId,
                            state
                    );

                    return state ==
                            ApplicationState.RUNNING_IN_FOREGROUND;

                } catch (Exception e) {

                    logger.debug(
                            "[DEEPLINK][IOS][APP VERIFY] "
                                    + "AppState henüz alınamadı. "
                                    + "BundleId: {}, Hata: {}",
                            iosBundleId,
                            e.getMessage()
                    );

                    return false;
                }
            });

            actualState =
                    getIOSApplicationState();

            assertEquals(
                    ApplicationState.RUNNING_IN_FOREGROUND,
                    actualState,
                    "Deeplink sonrası Gratis iOS uygulaması foreground'da değil."
            );

            logger.info(
                    "[DEEPLINK][IOS][APP SUCCESS] "
                            + "Gratis uygulaması foreground'da. "
                            + "BundleId: {}, State: {}",
                    iosBundleId,
                    actualState
            );

        } catch (TimeoutException e) {

            try {

                actualState =
                        getIOSApplicationState();

            } catch (Exception stateException) {

                logger.warn(
                        "[DEEPLINK][IOS][APP] "
                                + "Timeout sonrası Gratis AppState alınamadı. "
                                + "Hata: {}",
                        stateException.getMessage(),
                        stateException
                );
            }

            ApplicationState safariState = null;

            try {

                safariState =
                        getIOSApplicationState(
                                IOS_SAFARI_BUNDLE_ID
                        );

            } catch (Exception ignored) {
            }

            logger.error(
                    "[DEEPLINK][IOS][APP TIMEOUT] "
                            + "Gratis {} saniye içinde foreground'a gelmedi. "
                            + "Gratis State: {}, Safari State: {}",
                    APP_OPEN_TIMEOUT,
                    actualState,
                    safariState,
                    e
            );

            fail(
                    "Deeplink sonrası Gratis uygulaması açılmadı."
                            + "\nPlatform       : iOS"
                            + "\nBeklenen       : APP"
                            + "\nBundleId       : " + iosBundleId
                            + "\nGratis State   : " + actualState
                            + "\nSafari State   : " + safariState
                            + "\nExpected State : "
                            + ApplicationState.RUNNING_IN_FOREGROUND
                            + "\nTimeout        : "
                            + APP_OPEN_TIMEOUT
                            + " saniye"
            );

        } catch (AssertionError e) {

            logger.error(
                    "[DEEPLINK][IOS][APP ASSERTION FAILED] "
                            + "AppState doğrulaması başarısız. "
                            + "Beklenen: {}, Gerçek: {}, BundleId: {}",
                    ApplicationState.RUNNING_IN_FOREGROUND,
                    actualState,
                    iosBundleId,
                    e
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[DEEPLINK][IOS][APP UNEXPECTED ERROR] "
                            + "Foreground kontrolünde hata oluştu. "
                            + "BundleId: {}, AppState: {}, "
                            + "Exception: {}, Message: {}",
                    iosBundleId,
                    actualState,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            fail(
                    "iOS APP deeplink doğrulaması sırasında hata oluştu."
                            + "\nException: "
                            + e.getClass().getSimpleName()
                            + "\nMessage: "
                            + e.getMessage()
            );
        }
    }


    /*
     * =========================================================
     * IOS - BROWSER VERIFY
     * =========================================================
     */

    private void verifyIOSBrowserIsForeground() {

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(APP_OPEN_TIMEOUT)
                );

        ApplicationState safariState = null;
        ApplicationState gratisState = null;

        try {

            logger.info(
                    "[DEEPLINK][IOS][BROWSER VERIFY] "
                            + "Safari foreground kontrolü başladı. "
                            + "BundleId: {}",
                    IOS_SAFARI_BUNDLE_ID
            );

            wait.until(d -> {

                try {

                    ApplicationState state =
                            getIOSApplicationState(
                                    IOS_SAFARI_BUNDLE_ID
                            );

                    logger.debug(
                            "[DEEPLINK][IOS][BROWSER VERIFY] "
                                    + "Safari State: {}",
                            state
                    );

                    return state ==
                            ApplicationState.RUNNING_IN_FOREGROUND;

                } catch (Exception e) {

                    logger.debug(
                            "[DEEPLINK][IOS][BROWSER VERIFY] "
                                    + "Safari AppState henüz alınamadı. "
                                    + "Hata: {}",
                            e.getMessage()
                    );

                    return false;
                }
            });

            safariState =
                    getIOSApplicationState(
                            IOS_SAFARI_BUNDLE_ID
                    );

            assertEquals(
                    ApplicationState.RUNNING_IN_FOREGROUND,
                    safariState,
                    "Deeplink sonrası Safari foreground'da değil."
            );

            logger.info(
                    "[DEEPLINK][IOS][BROWSER SUCCESS] "
                            + "Safari foreground'da. State: {}",
                    safariState
            );

        } catch (TimeoutException e) {

            try {

                safariState =
                        getIOSApplicationState(
                                IOS_SAFARI_BUNDLE_ID
                        );

            } catch (Exception ignored) {
            }

            try {

                gratisState =
                        getIOSApplicationState();

            } catch (Exception ignored) {
            }

            logger.error(
                    "[DEEPLINK][IOS][BROWSER TIMEOUT] "
                            + "Safari {} saniye içinde foreground'a gelmedi. "
                            + "Safari State: {}, Gratis State: {}",
                    APP_OPEN_TIMEOUT,
                    safariState,
                    gratisState,
                    e
            );

            fail(
                    "Deeplink Safari'de açılmadı."
                            + "\nPlatform     : iOS"
                            + "\nBeklenen     : BROWSER"
                            + "\nSafari State : " + safariState
                            + "\nGratis State : " + gratisState
                            + "\nTimeout      : "
                            + APP_OPEN_TIMEOUT
                            + " saniye"
            );

        } catch (AssertionError e) {

            logger.error(
                    "[DEEPLINK][IOS][BROWSER ASSERTION FAILED] "
                            + "Safari doğrulaması başarısız. "
                            + "Safari State: {}, Gratis State: {}",
                    safariState,
                    gratisState,
                    e
            );

            throw e;

        } catch (Exception e) {

            logger.error(
                    "[DEEPLINK][IOS][BROWSER UNEXPECTED ERROR] "
                            + "Browser kontrolünde hata oluştu. "
                            + "Safari State: {}, Gratis State: {}, "
                            + "Exception: {}, Message: {}",
                    safariState,
                    gratisState,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );

            fail(
                    "iOS browser deeplink doğrulaması sırasında hata oluştu."
                            + "\nException: "
                            + e.getClass().getSimpleName()
                            + "\nMessage: "
                            + e.getMessage()
            );
        }
    }


    /*
     * =========================================================
     * IOS APP STATE
     * =========================================================
     */

    private ApplicationState getIOSApplicationState() {

        return getIOSApplicationState(
                iosBundleId
        );
    }


    private ApplicationState getIOSApplicationState(
            String bundleId
    ) {

        return ((InteractsWithApps) driver)
                .queryAppState(bundleId);
    }


    /*
     * =========================================================
     * VALIDATION
     * =========================================================
     */

    private void validateUrl(String url) {

        if (url == null
                || url.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Deeplink URL boş olamaz."
            );
        }

        if (!url.startsWith("http://")
                && !url.startsWith("https://")) {

            throw new IllegalArgumentException(
                    "Geçersiz deeplink URL: "
                            + url
            );
        }
    }


    private String normalizeOpenType(
            String openType
    ) {

        if (openType == null
                || openType.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Deeplink açılma tipi boş olamaz. "
                            + "Kullanılabilir değerler: APP, BROWSER"
            );
        }

        String normalized =
                openType.trim().toUpperCase();

        if (!normalized.equals("APP")
                && !normalized.equals("BROWSER")) {

            throw new IllegalArgumentException(
                    "Geçersiz deeplink açılma tipi: "
                            + openType
                            + ". Kullanılabilir değerler: APP, BROWSER"
            );
        }

        return normalized;
    }
}