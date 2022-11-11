package com.example.application;

import lombok.Getter;

@Getter
public class Locators {
//    Fakturownia
    final String fakturowniaLoginButtonLocator = "//a[@class='button-outline']/span[text()='Zaloguj się']";
    final String fakturowniaUserNameLocator = "//*[@id='user_session_login']";
    final String fakturowniaPasswordLocator = "//*[@id='user_session_password']";
    final String fakturowniaSubmitButtonLocator = "//input[@type='submit']";
    final String fakturowniaMenuItemInvoicesLocator = "//li/a[contains(text(),'Faktury')]";
    final String fakturowniaTotalSumLocator = "//td[@id='total_count']";
    final String fakturowniaInvoicesColumnTableLocators = "//tr/td[2]//a";
    final String fakturowniaCogIconLocator = "//a[text()='%s']/../../..//span[@class='caret']";
    final String fakturowniaDownloadLocator = "//a[text()='%s']/../../..//a[text()='Drukuj']";
//    PKO
    final String pkoLoginButtonLocator = "id=Login";
    final String pkoPasswordButtonLocator = "id=Password";
    final String pkoDownloadButtonLocator = "//tr[1]//td[3]//span[contains(@class,'pko-icon-pobierz_PDF')]";
    final String pkoInvoiceNameTextLocator = "//tr[1]//td[1]//a";
//    Toyota
    final String toyotaCookiesButtonLocator = "//span[text()='Akceptuję wszystkie']";
    final String toyotaUserNameLocator = "id=login_layout_txtUName";
    final String toyotPasswordLocator = "id=login_layout_txtPName";
    final String toyotSubmitButtonLocator = "//span[@class='dx-vam' and text()='ZALOGUJ']";
    final String toyotAllInvoicesButtonLocator = "//span[@class='dx-vam' and text()='Wszystkie faktury']";
    final String toyotInvoiceNumberTextLocator = "//tr[contains(@id,'DXDataRow0')]//td[2]";
    final String toyotDownloadButtonLocator = "//tr[contains(@id,'DXDataRow0')]//td/a[contains(@class,'fa-file-pdf')]";
    // T-Mobile
    final String tMobileCookiesButtonLocator = "//button/span[text()='Accept all']";
    final String tMobileOkButtonLocator = "//button[contains(text(),'Ok')]";
    final String tMobileEmailLocator = "id=email";
    final String tMobileNextButtonLocator = "//button[text()='Dalej']";
    final String tMobilePasswordLocator = "id=password";
    final String tMobileSubmitButtonLocator = "//input[@value='Zaloguj się']";
    final String tMobileSmsCodeTextLocator = "//input[@id='otpInput']";
    final String tMobileSmsCodeButtonLocator = "//input[@id='submit1']";
    final String tMobileInvoicesAndPaymentsMenuLocator = "//li//span[contains(text(),'Płatności i faktury')]";
    final String tMobilePaidPaymentsLinkLocator = "//a[text()='zapłacone rachunki']";
    final String tMobileInvoiceNumberTextLocator = "//li[1]//li[1]//div[@class='label']/span[2]";
    final String tMobileDownloadButtonLocator = "//ul/li[1]//li[1]//a[contains(text(),'pobierz')]";
    // LeaseLink
    final String leaseLinkPhoneLocator = "id=CallbackPanel_txtPhoneNumber";
    final String leaseLinkCookiesButtonLocator = "id=cookiescript_accept";
    final String leaseLinkPinButtonLocator = "id=CallbackPanel_btnPin";
    final String leaseLinkPinTextLocator = "id=CallbackPanel_txtPinNumber";
    final String leaseLinkUserNameLocator = "id=CallbackPanel_btnLogin";
    final String leaseLinkLogoLocator = "id=divLogoPortal";
    final String leaseInvoiceNumberLocator = "//tr[contains(@id,'grdFaktury_DXDataRow0')]/td[2]";
    final String leaseDownloadButtonLocator = "//tr[contains(@id,'grdFaktury_DXDataRow0')]//a[contains(@class,'fa-file-pdf')]";



}
