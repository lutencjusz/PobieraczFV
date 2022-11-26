package com.example.application.tests;

import com.example.application.Locators;
import com.example.application.model.TestStatus;
import com.example.application.repo.InMemoRep;
import com.example.application.services.Broadcaster;
import com.example.application.utils.CryptoText;
import com.microsoft.playwright.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestFixtures {

    final int STANDARD_DELAY_IN_MIN_SEC = 1500;
    String fileName;
    String screenshotName;
    Playwright playwright;
    Browser browser;

    Locators locators = new Locators();

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(50));
    }

    @AfterAll
    void closeBrowser() {
        playwright.close();
    }

    BrowserContext context;
    Page page;

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("C:/Data/Java/TestVaadin/src/main/resources/public/png/" + screenshotName)));
        context.close();
    }
}

public class InvoicesDownloadTest extends TestFixtures {

    @Autowired
    InMemoRep inMemoRep = new InMemoRep();
    LocalDate today = LocalDate.now();
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources/.env")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();
    private final String fakturowaniaUserName = dotenv.get("FAKTUROWNIA_USER_NAME");
    private final String fakturowaniaPassword = dotenv.get("FAKTUROWNIA_PASSWORD");
    private final String pkoUserName = dotenv.get("PKO_USER_NAME");
    private final String pkoPassword = dotenv.get("PKO_PASSWORD");
    private final String toyotaUserName = dotenv.get("TOYOTA_USER_NAME");
    private final String toyotaPassword = dotenv.get("TOYOTA_PASSWORD");
    private final String tMobileUserName = dotenv.get("T_MOBILE_USER_NAME");
    private final String tMobilePassword = dotenv.get("T_MOBILE_PASSWORD");
    private final String laseLinkPhone = dotenv.get("LEASELINK_USER_NAME");
    private final String microsoftUserName = dotenv.get("MICROSOFT_USER_NAME");
    private final String microsoftPassword = dotenv.get("MICROSOFT_PASSWORD");
    private final String PATH_TO_DROPBOX = dotenv.get("PATH_TO_DROPBOX") + today.toString().substring(0, 7) + "\\";
    private final String PATH_TO_RAPORT = dotenv.get("PATH_TO_RAPORT");

    private void sendProgress(String name, Double progress) {
        inMemoRep.setProgress(name, progress);
        Broadcaster.broadcast(name);
    }

    /**
     * Łączy dwie tablice o tych samych rozmiarach we wspólną listę
     *
     * @param invoicesNumbers   - tablica numerów FV
     * @param invoicesSalesDays - tablica dni sprzedaży odpowiednich FV
     * @return - listę połączonych tablic
     */
    private List<Map<String, String>> tablesToListConverter(List<String> invoicesNumbers, List<String> invoicesSalesDays) {
        List<Map<String, String>> invoices = new ArrayList<>();
        for (int i = 0; i < invoicesNumbers.size(); i++) {
            Map<String, String> invoice = new HashMap<>();
            invoice.put("nr", invoicesNumbers.get(i));
            invoice.put("date", invoicesSalesDays.get(i));
            invoices.add(invoice);
        }
        return invoices;
    }

    public void fakturownia(LocalDate date) {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("Fakturownia", 0.0);
        List<String> nrFvFiltered = new ArrayList<>();
        boolean isFoundAnyInvoice = false;
        launchBrowser();
        createContextAndPage();
        screenshotName = "fakturownia.png";
        try {
            page.navigate("https://fakturownia.pl/");
            sendProgress("Fakturownia", 0.1);
            page.locator(locators.getFakturowniaLoginButtonLocator()).click();
            assert fakturowaniaUserName != null;
            sendProgress("Fakturownia", 0.2);
            page.locator(locators.getFakturowniaUserNameLocator()).fill(CryptoText.decodeDES(fakturowaniaUserName));
            sendProgress("Fakturownia", 0.3);
            assert fakturowaniaPassword != null;
            page.locator(locators.getFakturowniaPasswordLocator()).fill(CryptoText.decodeDES(fakturowaniaPassword));
            sendProgress("Fakturownia", 0.4);
            page.locator(locators.getFakturowniaSubmitButtonLocator()).click();
            assertEquals("https://sopim.fakturownia.pl/", page.url());
            sendProgress("Fakturownia", 0.5);
            page.getByText("Przychody ").click();
            sendProgress("Fakturownia", 0.6);
            page.locator(locators.getFakturowniaMenuItemInvoicesLocator()).first().click();
            page.locator(locators.getFakturowniaTotalSumLocator()).waitFor();
            Locator rowLocatorInvoicesNumbers = page.locator(locators.getFakturowniaInvoicesColumnTableLocators());
            Locator rowLocatorInvoicesSalesDates = page.locator(locators.getFakturowniaInvoicesSalesDayColumnTableLocators());
            sendProgress("Fakturownia", 0.7);
            List<String> invoicesSalesDays = rowLocatorInvoicesSalesDates.allTextContents();
            List<String> invoicesNumbers = rowLocatorInvoicesNumbers.allTextContents();
            List<Map<String, String>> invoices = tablesToListConverter(invoicesNumbers, invoicesSalesDays);
            int month = date.getMonthValue();
            System.out.println("Wybrałeś miesiąc: " + month);
            int year = date.getYear();
            System.out.println("Wybrałeś rok: " + year);
            sendProgress("Fakturownia", 0.8);
            for (Map<String, String> invoice : invoices) {
                int monthSubStr = Integer.parseInt(invoice.get("date").substring(5, 7));
                int yearSubStr = Integer.parseInt(invoice.get("date").substring(0, 4));
                if (monthSubStr == month && yearSubStr == year) {
                    System.out.println("Pobieram FV nr: " + invoice.get("nr"));
                    page.locator(String.format(locators.getFakturowniaCogIconLocator(), invoice.get("nr"))).last().click();
                    Download download = page.waitForDownload(() -> page.locator(String.format(locators.getFakturowniaDownloadLocator(), invoice.get("nr"))).click());
                    fileName = "Fakturownia_" + invoice.get("nr").replace("/", "-") + ".pdf";
                    download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
                    download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
                    System.out.println("Pobieram pliki do scieżki: " + fileName);
                    nrFvFiltered.add(invoice.get("nr"));
                    isFoundAnyInvoice = true;
                }
            }
            if (!isFoundAnyInvoice) {
                throw new Exception("Nie znaleziono faktur z tego okresu (dzień sprzedaży)");
            } else {
                closeContext();
                closeBrowser();
                endTest = LocalDateTime.now();
                inMemoRep.updateTestData("Fakturownia", nrFvFiltered, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
            }
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
            endTest = LocalDateTime.now();
            inMemoRep.setStatus("Fakturownia", TestStatus.fail);
            inMemoRep.setDuration("Fakturownia", Duration.between(beginTest, endTest));
            Broadcaster.broadcast("Fakturownia");
            return;
        }
        Broadcaster.broadcast("Fakturownia");
        sendProgress("Fakturownia", 1.0);
    }

    public void pko() {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("PKO", 0.0);
        String invoiceName;
        launchBrowser();
        createContextAndPage();
        try {
            screenshotName = "pko.png";
            page.navigate("https://portal.pkoleasing.pl/Common/Authentication/Login");
            sendProgress("PKO", 0.1);
            assert pkoUserName != null;
            page.fill(locators.getPkoLoginButtonLocator(), CryptoText.decodeDES(pkoUserName));
            sendProgress("PKO", 0.2);
            assert pkoPassword != null;
            page.fill(locators.getPkoPasswordButtonLocator(), CryptoText.decodeDES(pkoPassword));
            page.press(locators.getPkoPasswordButtonLocator(), "Enter");
            sendProgress("PKO", 0.3);
            Locator invoices = page.getByText("Faktury").first();
            invoices.waitFor();
            sendProgress("PKO", 0.5);
            Download download = page.waitForDownload(() -> page.click(locators.getPkoDownloadButtonLocator()));
            sendProgress("PKO", 0.8);
            invoiceName = page.innerText(locators.getPkoInvoiceNameTextLocator());
            fileName = "PKO_" + invoiceName.replace("/", "-");
            fileName = fileName.substring(0, fileName.length() - 1).trim() + ".pdf";
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
            sendProgress("PKO", 0.9);
        } catch (Exception e) {
            endTest = LocalDateTime.now();
            inMemoRep.setDuration("PKO", Duration.between(beginTest, endTest));
            inMemoRep.setStatus("PKO", TestStatus.fail);
            Broadcaster.broadcast("PKO");
            return;
        }
        closeContext();
        closeBrowser();
        endTest = LocalDateTime.now();
        inMemoRep.updateTestData("PKO", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
        Broadcaster.broadcast("PKO");
        sendProgress("PKO", 1.0);
    }


    public void toyota() {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("Toyota", 0.0);
        launchBrowser();
        createContextAndPage();
        screenshotName = "toyota.png";
        String invoiceName;
        try {
            page.navigate("https://portal.toyotaleasing.pl/Login");
            if (page.locator(locators.getToyotaCookiesButtonLocator()).isVisible()) {
                page.locator(locators.getToyotaCookiesButtonLocator()).click();
            }
            sendProgress("Toyota", 0.1);
            assert toyotaUserName != null;
            page.locator(locators.getToyotaUserNameLocator()).fill(CryptoText.decodeDES(toyotaUserName));
            sendProgress("Toyota", 0.2);
            assert toyotaPassword != null;
            page.locator(locators.getToyotPasswordLocator()).fill(CryptoText.decodeDES(toyotaPassword));
            sendProgress("Toyota", 0.3);
            page.locator(locators.getToyotSubmitButtonLocator()).click();
            sendProgress("Toyota", 0.4);
            Locator allInvoicesButton = page.locator(locators.getToyotAllInvoicesButtonLocator());
            allInvoicesButton.waitFor();
            allInvoicesButton.click();
            sendProgress("Toyota", 0.6);
            Locator InvoiceNumber = page.locator(locators.getToyotInvoiceNumberTextLocator());
            InvoiceNumber.waitFor();
            invoiceName = InvoiceNumber.innerText();
            sendProgress("Toyota", 0.8);
            Download download = page.waitForDownload(() -> page.locator(locators.getToyotDownloadButtonLocator()).click());
            fileName = "Toyota_" + invoiceName.replace("/", "-") + ".pdf";
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
            sendProgress("Toyota", 0.9);
        } catch (Exception e) {
            endTest = LocalDateTime.now();
            inMemoRep.setDuration("Toyota", Duration.between(beginTest, endTest));
            inMemoRep.setStatus("Toyota", TestStatus.fail);
            Broadcaster.broadcast("Toyota");
            return;
        }
        closeContext();
        closeBrowser();
        endTest = LocalDateTime.now();
        inMemoRep.updateTestData("Toyota", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
        Broadcaster.broadcast("Toyota");
        sendProgress("Toyota", 1.0);
    }


    public void tMobile() {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("T-Mobile", 0.0);
        launchBrowser();
        createContextAndPage();
        screenshotName = "t-mobile.png";
        Scanner scanner = new Scanner(System.in);
        String invoiceName;
        try {
            page.navigate("https://nowymoj.t-mobile.pl/");
            if (page.isVisible(locators.getTMobileCookiesButtonLocator())) {
                page.click(locators.getTMobileCookiesButtonLocator());
            }
            sendProgress("T-Mobile", 0.1);
            if (page.isVisible(locators.getTMobileOkButtonLocator())) {
                page.click(locators.getTMobileOkButtonLocator());
            }
            sendProgress("T-Mobile", 0.2);
            assert tMobileUserName != null;
            page.fill(locators.getTMobileEmailLocator(), CryptoText.decodeDES(tMobileUserName));
            page.click(locators.getTMobileNextButtonLocator());
            sendProgress("T-Mobile", 0.3);
            assert tMobilePassword != null;
            page.fill(locators.getTMobilePasswordLocator(), CryptoText.decodeDES(tMobilePassword));
            page.click(locators.getTMobileSubmitButtonLocator());

            sendProgress("T-Mobile", 0.4);
            System.out.println("Podaj kod otrzymany SMS'em od T-mobile: ");
            String SmsCode = scanner.nextLine();
            sendProgress("T-Mobile", 0.5);

            page.type(locators.getTMobileSmsCodeTextLocator(), SmsCode);
            page.click(locators.getTMobileSmsCodeButtonLocator());
            sendProgress("T-Mobile", 0.6);
            page.click(locators.getTMobileOkButtonLocator());
            sendProgress("T-Mobile", 0.7);
            Locator menuItem = page.locator(locators.getTMobileInvoicesAndPaymentsMenuLocator());
            menuItem.waitFor();
            menuItem.click();
            page.click(locators.getTMobilePaidPaymentsLinkLocator());
            sendProgress("T-Mobile", 0.8);
            Locator InvoiceNumber = page.locator(locators.getTMobileInvoiceNumberTextLocator());
            InvoiceNumber.waitFor();
            invoiceName = InvoiceNumber.innerText();
            fileName = "T-Mobile_" + invoiceName + ".pdf";
            Download download = page.waitForDownload(() -> page.locator(locators.getTMobileDownloadButtonLocator()).click());
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
            sendProgress("T-Mobile", 0.9);
        } catch (Exception e) {
            endTest = LocalDateTime.now();
            inMemoRep.setDuration("T-Mobile", Duration.between(beginTest, endTest));
            inMemoRep.setStatus("T-Mobile", TestStatus.fail);
            Broadcaster.broadcast("T-Mobile");
            return;
        }
        closeContext();
        closeBrowser();
        endTest = LocalDateTime.now();
        inMemoRep.updateTestData("T-Mobile", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
        sendProgress("T-Mobile", 1.0);
    }


    public void leaseLink() {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("LeaseLink", 0.0);
        launchBrowser();
        createContextAndPage();
        screenshotName = "leaselink.png";
        Scanner scanner = new Scanner(System.in);
        String invoiceName;
        try {
            page.navigate("https://portal.leaselink.pl/");
            assert laseLinkPhone != null;
            page.fill(locators.getLeaseLinkPhoneLocator(), CryptoText.decodeDES(laseLinkPhone));
            sendProgress("LeaseLink", 0.1);
            if (page.isVisible(locators.getLeaseLinkCookiesButtonLocator())) {
                page.locator(locators.getLeaseLinkCookiesButtonLocator()).click();
            }
            page.click(locators.getLeaseLinkPinButtonLocator());

            sendProgress("LeaseLink", 0.2);
            System.out.println("Podaj kod otrzymany SMS'em od LeaseLink: ");
            String SmsCode = scanner.nextLine();
            sendProgress("LeaseLink", 0.4);

            page.fill(locators.getLeaseLinkPinTextLocator(), SmsCode);
            page.click(locators.getLeaseLinkUserNameLocator());
            sendProgress("LeaseLink", 0.5);
            Locator logoPortal = page.locator(locators.getLeaseLinkLogoLocator());
            logoPortal.waitFor();
            sendProgress("LeaseLink", 0.6);
            Locator InvoiceNumber = page.locator(locators.getLeaseLinkInvoiceNumberLocator());
            InvoiceNumber.waitFor();
            invoiceName = InvoiceNumber.innerText();
            sendProgress("LeaseLink", 0.7);
            fileName = "LeaseLink_" + invoiceName.replace("/", "-") + ".pdf";
            Download download = page.waitForDownload(() -> page.click(locators.getLeaseLinkDownloadButtonLocator()));
            sendProgress("LeaseLink", 0.8);
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
            sendProgress("LeaseLink", 0.9);
        } catch (Exception e) {
            endTest = LocalDateTime.now();
            inMemoRep.setDuration("LeaseLink", Duration.between(beginTest, endTest));
            inMemoRep.setStatus("LeaseLink", TestStatus.fail);
            Broadcaster.broadcast("LeaseLink");
            return;
        }
        closeContext();
        closeBrowser();
        endTest = LocalDateTime.now();
        inMemoRep.updateTestData("LeaseLink", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
        Broadcaster.broadcast("LeaseLink");
        sendProgress("LeaseLink", 1.0);
    }


    public void microsoft() {
        LocalDateTime beginTest = LocalDateTime.now();
        LocalDateTime endTest;
        sendProgress("Microsoft", 0.0);
        launchBrowser();
        createContextAndPage();
        screenshotName = "microsoft.png";
        String invoiceName;
        try {
            page.navigate("https://admin.microsoft.com/Adminportal/Home?ref=billoverview/invoice-list#/billoverview/invoice-list");
            assert microsoftUserName != null;
            sendProgress("Microsoft", 0.1);
            page.fill(locators.getMicrosoftUserNameLocator(), CryptoText.decodeDES(microsoftUserName));
            page.press(locators.getMicrosoftUserNameLocator(), "Enter");
            sendProgress("Microsoft", 0.2);
            assert microsoftPassword != null;
            page.fill(locators.getMicrosoftPasswordLocator(), CryptoText.decodeDES(microsoftPassword));
            sendProgress("Microsoft", 0.3);
            Locator staySignedIn = page.locator(locators.getMicrosoftStaySignedButtonLocator());
            sendProgress("Microsoft", 0.4);
            staySignedIn.waitFor();
            staySignedIn.click(new Locator.ClickOptions().setDelay(STANDARD_DELAY_IN_MIN_SEC));
            sendProgress("Microsoft", 0.5);
            Locator moreInfo = page.locator(locators.getMicrosoftMoreInfoButtonLocator());
            moreInfo.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            if (moreInfo.isVisible()) {
                moreInfo.click();
            }
            sendProgress("Microsoft", 0.6);
            staySignedIn.click(new Locator.ClickOptions().setDelay(STANDARD_DELAY_IN_MIN_SEC));
            if (page.locator(locators.getMicrosoftEnableSecurityDefaultsButtonLocator()).isVisible(new Locator.IsVisibleOptions().setTimeout(5000))) {
                page.locator(locators.getMicrosoftEnableSecurityDefaultsButtonLocator()).click();
            }
            sendProgress("Microsoft", 0.7);
            Locator viewport = page.locator(locators.getMicrosoftListInvoicesTableLocator());
            viewport.waitFor();
            Locator InvoiceNumber = page.locator(locators.getMicrosoftInvoiceNumberTextLocator());
            InvoiceNumber.waitFor();
            sendProgress("Microsoft", 0.8);
            invoiceName = InvoiceNumber.innerText();
            InvoiceNumber.click();
            Download download = page.waitForDownload(() -> page.click(locators.getMicrosoftDownloadButtonLocator()));
            fileName = "Microsoft_" + invoiceName + ".pdf";
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
            sendProgress("Microsoft", 0.9);
        } catch (Exception e) {
            endTest = LocalDateTime.now();
            inMemoRep.setDuration("Microsoft", Duration.between(beginTest, endTest));
            inMemoRep.setStatus("Microsoft", TestStatus.fail);
            Broadcaster.broadcast("Microsoft");
            return;
        }
        closeContext();
        closeBrowser();
        endTest = LocalDateTime.now();
        inMemoRep.updateTestData("Microsoft", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass, Duration.between(beginTest, endTest));
        sendProgress("Microsoft", 1.0);
    }
}

