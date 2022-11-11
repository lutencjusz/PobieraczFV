package com.example.application;

import lombok.Getter;

@Getter
public class Locators {
//    Fakturownia
    private final String fakturowniaLoginButtonLocator = "//a[@class='button-outline']/span[text()='Zaloguj się']";
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

}
