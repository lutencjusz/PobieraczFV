package com.example.application.tests;

import com.example.application.Locators;
import com.example.application.model.TestStatus;
import com.example.application.repo.InMemoRep;
import com.example.application.utils.Broadcaster;
import com.example.application.utils.CryptoText;
import com.microsoft.playwright.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

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

    // New instance for each test method.
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

    public void fakturownia(LocalDate date) {
        String invoicesName = "";
        boolean isFoundAnyInvoice = false;
        launchBrowser();
        createContextAndPage();
        screenshotName = "fakturownia.png";
        try {
            page.navigate("https://fakturownia.pl/");
            page.locator(locators.getFakturowniaLoginButtonLocator()).click();
            assert fakturowaniaUserName != null;
            page.locator(locators.getFakturowniaUserNameLocator()).fill(CryptoText.decodeDES(fakturowaniaUserName));
            assert fakturowaniaPassword != null;
            page.locator(locators.getFakturowniaPasswordLocator()).fill(CryptoText.decodeDES(fakturowaniaPassword));
            page.locator(locators.getFakturowniaSubmitButtonLocator()).click();
            assertEquals("https://sopim.fakturownia.pl/", page.url());
            page.getByText("Przychody ").click();
            page.locator(locators.getFakturowniaMenuItemInvoicesLocator()).first().click();
            page.locator(locators.getFakturowniaTotalSumLocator()).waitFor();
            Locator rowLocator = page.locator(locators.getFakturowniaInvoicesColumnTableLocators());
            List<String> invoicesNumbers = rowLocator.allTextContents();
            int month = date.getMonthValue();
            System.out.println("Wybrałeś miesiąc: " + month);
            int year = date.getYear();
            System.out.println("Wybrałeś rok: " + year);
            for (String nr : invoicesNumbers) {
                int monthSubStr = Integer.parseInt(nr.substring(7, 9));
                int yearSubStr = Integer.parseInt(nr.substring(2, 6));
                if (monthSubStr == month && yearSubStr == year) {
                    System.out.println("Pobieram FV nr: " + nr);
                    page.locator(String.format(locators.getFakturowniaCogIconLocator(), nr)).last().click();
                    Download download = page.waitForDownload(() -> page.locator(String.format(locators.getFakturowniaDownloadLocator(), nr)).click());
                    fileName = "Fakturownia_FV" + year + "-" + month + "-" + nr.substring(10) + ".pdf";
                    download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
                    download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
                    System.out.println("Pobieram pliki do scieżki: " + fileName);
                    invoicesName += isFoundAnyInvoice ? invoicesName + ", " + fileName : fileName;
                    isFoundAnyInvoice = true;
                }
            }
            if (!isFoundAnyInvoice) {
                throw new Exception("invoices not found");
            } else {
                inMemoRep.updateTestData("Fakturownia", invoicesName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
            }
        } catch (Exception e) {
            inMemoRep.setStatus("Fakturownia", TestStatus.fail);
        }
        closeContext();
        closeBrowser();
        Broadcaster.broadcast("Fakturownia");
    }

    public void pko() {
        String invoiceName;
        launchBrowser();
        createContextAndPage();
        try {
            screenshotName = "pko.png";
            page.navigate("https://portal.pkoleasing.pl/Common/Authentication/Login");
            assert pkoUserName != null;
            page.fill(locators.getPkoLoginButtonLocator(), CryptoText.decodeDES(pkoUserName));
            assert pkoPassword != null;
            page.fill(locators.getPkoPasswordButtonLocator(), CryptoText.decodeDES(pkoPassword));
            page.press(locators.getPkoPasswordButtonLocator(), "Enter");
            Locator invoices = page.getByText("Faktury").first();
            invoices.waitFor();
            Download download = page.waitForDownload(() -> page.click(locators.getPkoDownloadButtonLocator()));
            invoiceName = page.innerText(locators.getPkoInvoiceNameTextLocator());
            fileName = "PKO_" + invoiceName.replace("/", "-");
            fileName = fileName.substring(0, fileName.length() - 1).trim() + ".pdf";
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
        } catch (Exception e) {
            inMemoRep.setStatus("PKO", TestStatus.fail);
            Broadcaster.broadcast("PKO");
            return;
        }
        closeContext();
        closeBrowser();
        inMemoRep.updateTestData("PKO", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
        Broadcaster.broadcast("PKO");
    }


    public void toyota() {
        launchBrowser();
        createContextAndPage();
        screenshotName = "toyota.png";
        String invoiceName;
        try {
            page.navigate("https://portal.toyotaleasing.pl/Login");
            if (page.locator(locators.getToyotaCookiesButtonLocator()).isVisible()) {
                page.locator(locators.getToyotaCookiesButtonLocator()).click();
            }
            assert toyotaUserName != null;
            page.locator(locators.getToyotaUserNameLocator()).fill(CryptoText.decodeDES(toyotaUserName));
            assert toyotaPassword != null;
            page.locator(locators.getToyotPasswordLocator()).fill(CryptoText.decodeDES(toyotaPassword));
            page.locator(locators.getToyotSubmitButtonLocator()).click();
            Locator allInvoicesButton = page.locator(locators.getToyotAllInvoicesButtonLocator());
            allInvoicesButton.waitFor();
            allInvoicesButton.click();
            Locator InvoiceNumber = page.locator(locators.getToyotInvoiceNumberTextLocator());
            InvoiceNumber.waitFor();
            invoiceName = InvoiceNumber.innerText();
            Download download = page.waitForDownload(() -> page.locator(locators.getToyotDownloadButtonLocator()).click());
            fileName = "Toyota_" + invoiceName.replace("/", "-") + ".pdf";
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
        } catch (Exception e) {
            inMemoRep.setStatus("Toyota", TestStatus.fail);
            Broadcaster.broadcast("Toyota");
            return;
        }
        closeContext();
        closeBrowser();
        inMemoRep.updateTestData("Toyota", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
        Broadcaster.broadcast("Toyota");
    }


    public void tMobile() {
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
            if (page.isVisible(locators.getTMobileOkButtonLocator())) {
                page.click(locators.getTMobileOkButtonLocator());
            }
            assert tMobileUserName != null;
            page.fill(locators.getTMobileEmailLocator(), CryptoText.decodeDES(tMobileUserName));
            page.click(locators.getTMobileNextButtonLocator());
            assert tMobilePassword != null;
            page.fill(locators.getTMobilePasswordLocator(), CryptoText.decodeDES(tMobilePassword));
            page.click(locators.getTMobileSubmitButtonLocator());

            System.out.println("Podaj kod otrzymany SMS'em od T-mobile: ");
            String SmsCode = scanner.nextLine();

            page.type(locators.getTMobileSmsCodeTextLocator(), SmsCode);
            page.click(locators.getTMobileSmsCodeButtonLocator());
            page.click(locators.getTMobileOkButtonLocator());
            Locator menuItem = page.locator(locators.getTMobileInvoicesAndPaymentsMenuLocator());
            menuItem.waitFor();
            menuItem.click();
            page.click(locators.getTMobilePaidPaymentsLinkLocator());
            Locator InvoiceNumber = page.locator(locators.getTMobileInvoiceNumberTextLocator());
            InvoiceNumber.waitFor();
            invoiceName = InvoiceNumber.innerText();
            fileName = "T-Mobile_" + invoiceName + ".pdf";
            Download download = page.waitForDownload(() -> page.locator(locators.getTMobileDownloadButtonLocator()).click());
            download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
            download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
            System.out.println("Pobrano plik: " + fileName);
        } catch (Exception e) {
            inMemoRep.setStatus("T-Mobile", TestStatus.fail);
            Broadcaster.broadcast("T-Mobile");
            return;
        }
        closeContext();
        closeBrowser();
        inMemoRep.updateTestData("T-Mobile", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
        Broadcaster.broadcast("T-Mobile");
    }


    public void leaseLink() {
        launchBrowser();
        createContextAndPage();
        screenshotName = "leaselink.png";
        Scanner scanner = new Scanner(System.in);
        page.navigate("https://portal.leaselink.pl/");
        assert laseLinkPhone != null;
        page.fill(locators.getLeaseLinkPhoneLocator(),CryptoText.decodeDES(laseLinkPhone));
        if (page.isVisible(locators.getLeaseLinkCookiesButtonLocator())) {
            page.locator(locators.getLeaseLinkCookiesButtonLocator()).click();
        }
        page.click(locators.getLeaseLinkPinButtonLocator());
        System.out.println("Podaj kod otrzymany SMS'em od LeaseLink: ");
        String SmsCode = scanner.nextLine();
        page.fill(locators.getLeaseLinkPinTextLocator(),SmsCode);
        page.click(locators.getLeaseLinkUserNameLocator());
        Locator logoPortal = page.locator(locators.getLeaseLinkLogoLocator());
        logoPortal.waitFor();
        Locator InvoiceNumber = page.locator(locators.getLeaseInvoiceNumberLocator());
        InvoiceNumber.waitFor();
        String invoiceName = InvoiceNumber.innerText();
        fileName = "LeaseLink_" + invoiceName.replace("/", "-") + ".pdf";
        Download download = page.waitForDownload(() -> page.click(locators.getLeaseDownloadButtonLocator()));
        download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
        download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
        System.out.println("Pobrano plik: " + fileName);
        closeContext();
        closeBrowser();
        inMemoRep.updateTestData("LeaseLink", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
        Broadcaster.broadcast("LeaseLink");
    }


    public void microsoft() {
        launchBrowser();
        createContextAndPage();
        screenshotName = "microsoft.png";
        page.navigate("https://admin.microsoft.com/Adminportal/Home?ref=billoverview/invoice-list#/billoverview/invoice-list");
        assert microsoftUserName != null;
        page.locator("id=i0116").fill(CryptoText.decodeDES(microsoftUserName));
        page.locator("id=i0116").press("Enter");
        assert microsoftPassword != null;
        page.locator("id=i0118").fill(CryptoText.decodeDES(microsoftPassword));
        Locator staySignedIn = page.locator("id=idSIButton9");
        staySignedIn.waitFor();
        staySignedIn.click(new Locator.ClickOptions().setDelay(STANDARD_DELAY_IN_MIN_SEC));
        Locator moreInfo = page.locator("//a[contains(text(), 'Skip for now')]");
        moreInfo.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        if (moreInfo.isVisible()) {
            moreInfo.click();
        }
        staySignedIn.click(new Locator.ClickOptions().setDelay(STANDARD_DELAY_IN_MIN_SEC));
        if (page.locator("id=btnEnableSecurityDefaults").isVisible(new Locator.IsVisibleOptions().setTimeout(5000))) {
            page.locator("id=btnEnableSecurityDefaults").click();
        }
        Locator viewport = page.locator("//div[@data-automation-id='ListInvoiceList']");
        viewport.waitFor();
        Locator InvoiceNumber = page.locator("//div[@data-list-index='0']//div[@aria-colindex='2']/span");
        InvoiceNumber.waitFor();
        String invoiceName = InvoiceNumber.innerText();
        InvoiceNumber.click();
        Download download = page.waitForDownload(() -> page.locator("//button[text()='Pobierz plik PDF']").click());
        fileName = "Microsoft_" + invoiceName + ".pdf";
        download.saveAs(Paths.get(PATH_TO_DROPBOX + fileName));
        download.saveAs(Paths.get(PATH_TO_RAPORT + fileName));
        System.out.println("Pobrano plik: " + fileName);
        closeContext();
        closeBrowser();
        inMemoRep.updateTestData("Microsoft", invoiceName, PATH_TO_DROPBOX + fileName, TestStatus.pass);
        Broadcaster.broadcast("Microsoft");
    }
}

