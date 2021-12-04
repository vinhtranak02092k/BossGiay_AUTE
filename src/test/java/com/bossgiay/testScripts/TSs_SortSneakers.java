package com.bossgiay.testScripts;


import com.bossgiay.pageObjects.CollectionPage;
import com.bossgiay.testListeners.TestListeners;
import com.bossgiay.testUtilities.ConfigurationReader;
import com.google.errorprone.annotations.Var;
import lombok.EqualsAndHashCode;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.testng.AssertJUnit.assertTrue;

@Listeners({TestListeners.class})
public class TSs_SortSneakers extends TestAbstractClass {

    @Var
    private ConfigurationReader confReader = new ConfigurationReader();
    private final String ascdPriceCriteria = "Giá: Tăng dần";
    private final String dsdPriceCriteria = "Giá: Giảm dần";
    private final String sneakerCategory = "Sneaker";
    private final String slideSandalCategory = "Slide/Sandal";
    private final String bagCategory = "Bag";
    private final String clothingCategory = "Clothing";
    private final String sneakerTitle = sneakerCategory.toUpperCase(Locale.ROOT);
    private final String slideSandalTitle= slideSandalCategory.toUpperCase(Locale.ROOT);
    private final String bagTitle= bagCategory.toUpperCase(Locale.ROOT);

    private final By LBL_CATEGORY_TITLE_LOCATOR =
            By.xpath("//h1[@class=\"title\"]");
    private final By LBL_SNEAKER_PRICE_CHILD_LOCATOR =
            By.xpath("//p[contains((@class),\"pro-price\") and contains(text(),\"000\")]");
    private final By LBL_SNEAKER_PRICE_PARENT_LOCATOR =
            By.xpath("//div[contains((@class),\"product-list\")]");

    private final By LBL_SLIDE_SANDAL_CHILD_LOCATOR=
            LBL_SNEAKER_PRICE_CHILD_LOCATOR;
    private final By LBL_SLIDE_SANDAL_PARENT_LOCATOR=
            LBL_SNEAKER_PRICE_PARENT_LOCATOR;
    private final By LBL_BAG_PRICE_CHILD_LOCATOR=
            LBL_SNEAKER_PRICE_CHILD_LOCATOR;
    private final By LBL_BAG_PRICE_PARENT_LOCATOR=
            LBL_SNEAKER_PRICE_PARENT_LOCATOR;

    private final By BTN_PAGE_NODE_LOCATOR =
            By.xpath("//div[@id=\"pagination\"]//a[@class=\"page-node\"]");
    private final By BTN_NEXT_LOCATOR =
            By.xpath("//div[@id=\"pagination\"]//a[@class=\"next\"]" +
                    "//following-sibling::*[local-name() = 'svg' and @version and @viewBox]");

    transient boolean checkPointIfPassed;
    transient boolean checkPointIfFailed;
    transient boolean checkPointIfSkipped;
    public int listValue;

    @EqualsAndHashCode.Include
    private final List<String> sneakersPriceList = new ArrayList<String>();
    private final List<Character> sneakersNameList = new ArrayList<Character>();
    private final List<Integer> snkPriceListInt= new ArrayList<Integer>();

    private final List<String> ssPriceList= new ArrayList<String>();
    private final List<Integer> ssPriceListInt= new ArrayList<Integer>();
    private final List<String> bagPriceList= new ArrayList<String>();
    private final List<Integer> bagPriceListInt= new ArrayList<Integer>();

    @BeforeMethod(alwaysRun = true,
            enabled = true,
            description = "facilitate triggering browser")
    public void be4SearchCaseMethod() throws TimeoutException {
        testSetUp(confReader.getWebApplicationBaseURL() + "collections/", "chrome");
    }

    @AfterMethod(alwaysRun = true,
            enabled = true,
            description = "facilitate repelling driver")
    public void afSearchCaseMethod() throws TimeoutException {
        testTearDown();
    }

    @Test(groups = {"001", "sort", "sneaker"},
            enabled = true,
            priority = -4,
            description =
            "this test script is facilitate verifying the sort function" +
            "whether it works funtionally:" +
                    "1. Navigate to collection site" +
                    "2. Select sort category (sneaker) dynamically" +
                    "3. Select sort criteria (ascending price)" +
                    "4. Create an array of sneaker price" +
                    "5. Assert array of sneaker price whether it is sorted by ascending order"
    )
    public void sortSneakersTest01() throws TimeoutException {
        CollectionPage collect = new CollectionPage(driver);

//        select sort category by sneaker sending a text of Title Attribute (getAttribute)
        collect.selectDynamicSortCategories("title", sneakerCategory, driver);
        log.info("//--**------------ SELECTED SNEAKER CATEGORY -------------**--//" + "\n");

//        select sort sneakers by ascending price sending a text
        collect.selectDynamicSortCriteria(ascdPriceCriteria, driver);
        log.info("//--**------------ SORTED BY ASCENDING PRICE -------------**--//" + "\n");
//        collect.setWaitFor(LBL_CATEGORY_TITLE_LOCATOR);
        String sneakerCategoryGetTxt = driver.findElement(LBL_CATEGORY_TITLE_LOCATOR).getText();
        System.out.println("" + sneakerCategoryGetTxt + "\n");

//        assert sneaker title
        if (sneakerCategoryGetTxt.equalsIgnoreCase(sneakerTitle)) {

//            the sneaker title text matches the expected title => true assertion
            this.checkPointIfPassed = true;
            this.checkPointIfFailed = false;
            this.checkPointIfSkipped = false;
            if (checkPointIfPassed && !checkPointIfFailed && !checkPointIfSkipped) {
                Assert.assertFalse(false);
                System.out.println("Text on web: " + "\"" + sneakerCategoryGetTxt +
                        "\"" + " matched the expected text!" + "\n");
            }
        } else {
            this.checkPointIfPassed = false;
            this.checkPointIfFailed = true;
            this.checkPointIfSkipped = false;
            if (checkPointIfFailed) {
                Assert.assertFalse(true);
                System.out.println("Text on web: " + "\"" + sneakerCategoryGetTxt + "\"" +
                        " did not match the expected text!" + "\n");
            }
        }
//        declare a variable to count the number of click
        int clickIndex = 0;

//        declare a variable to count how many times we have to click
        int pageNodeSize = driver.findElements(BTN_PAGE_NODE_LOCATOR).size();

/*        declare a variable to attach label to each sneaker:
        - the first sneaker: SNEAKER NUMBER 1
        - the second sneaker: SNEAKER NUMBER 2
        , ...
 */
        int sneakerLabel = 1;
        int indexSneakerPriceArray = 0;
        System.out.println("Number of page: " + (pageNodeSize + 1) + "");

//        generate a for loop to verify whether each sneaker name contains the inputted search key
        for (int index = 0; index < pageNodeSize; index++) {

//            get number of sneakers per page
            int listOfSneakersSize = driver.findElements(LBL_SNEAKER_PRICE_CHILD_LOCATOR).size();
            System.out.println("\n" + "Number of sneakers page " + (index + 1) +
                    " is: " + listOfSneakersSize + " sneakers" + "\n");
            for (int sneakerIndex = 0; sneakerIndex < listOfSneakersSize; sneakerIndex++) {
                WebElement listSneakers = driver.findElement(LBL_SNEAKER_PRICE_PARENT_LOCATOR);
                java.util.List<WebElement> childListSneakers = driver.findElements(LBL_SNEAKER_PRICE_CHILD_LOCATOR);
                String sneakerPrice = childListSneakers.get(sneakerIndex - 0).getText();
                System.out.println("PRICE OF SNEAKER NUMBER " + sneakerLabel + ": " + sneakerPrice + "");
                sneakersPriceList.add(sneakerPrice);
                System.out.println(sneakersPriceList);
                sneakerLabel++;
            }
                WebElement nextButton = driver.findElement(BTN_NEXT_LOCATOR);
                ((JavascriptExecutor) driver).executeScript("scroll(0,6000)");
                collect.pauseWithTryCatch(1550);
                nextButton.click();
                clickIndex += 1;
                if (clickIndex - 1 == index && clickIndex == pageNodeSize) {
                    System.out.println("\n" + "\" //-----**------ THE LAST PAGE --------**-------//\"" + "\n");
                    int listOfSneakersLastPageSize = driver.findElements(LBL_SNEAKER_PRICE_CHILD_LOCATOR).size();
                    System.out.println("Number of sneakers page " + (pageNodeSize + 1) +
                            " is: " + listOfSneakersLastPageSize + " sneakers" + "\n");
                    for (int sneakerIndexLastPage = 0; sneakerIndexLastPage < listOfSneakersLastPageSize; sneakerIndexLastPage++) {
                        WebElement listSneakersLastPage = driver.findElement(LBL_SNEAKER_PRICE_PARENT_LOCATOR);
                        java.util.List<WebElement> childListSneakerslastPage = driver.findElements(LBL_SNEAKER_PRICE_CHILD_LOCATOR);
                        String sneakerPriceLastPage = childListSneakerslastPage.get(sneakerIndexLastPage - 0).getText();
                        System.out.println("PRICE OF SNEAKER NUMBER " + sneakerLabel + ": " + sneakerPriceLastPage + "");
                        sneakersPriceList.add(sneakerPriceLastPage);
                        System.out.println(sneakersPriceList);
                        sneakerLabel++;
                    }
                }
            }

        System.out.println("\n"+ "//-------------//"+ "\n");
        System.out.println("\n"+ "LIST OF SNEAKERS PRICE BEFORE PROCESSING: "+ sneakersPriceList+ "\n");
        System.out.println("\n"+ "//-------------//"+ "\n");

        for (int indexInSnkPrList= 0;
             indexInSnkPrList< sneakersPriceList.size();
             indexInSnkPrList++) {
            String priceInList= sneakersPriceList.get(indexInSnkPrList);
//            System.out.println(priceInList+ "\n");
            if (priceInList.contains(" ")==false) {
                if (priceInList.length()< 9) {
//                    System.out.println("less than 9 chars: "+ priceInList);
                    String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                    String[] splitCommaChar= trimLastChar.split(",");
                    String combinePartitions="";
                     for (int i=0; i<2; i++) {
                         combinePartitions+= splitCommaChar[i];
                     }
//                     System.out.println(combinePartitions);
                     snkPriceListInt.add(Integer.parseInt(combinePartitions));
                }
                else {
//                    System.out.println("more than 9 chars: "+ priceInList);
                    String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                    String[] splitCommaChar= trimLastChar.split(",");
                    String combinePartitions="";
                    for (int i=0; i<3; i++) {
                        combinePartitions+= splitCommaChar[i];
                    }
//                     System.out.println(combinePartitions);
                    snkPriceListInt.add(Integer.parseInt(combinePartitions));
                }
            }
            else {  //price in list contains spacer character
//                System.out.println("contain spacer char: "+ priceInList);
                String[] splitSpacerChar= priceInList.split(" ");
                priceInList =splitSpacerChar[0];
                if (priceInList.length()< 9) {
                    String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                    String[] splitCommaChar= trimLastChar.split(",");
                    String combinePartitions="";
                    for (int i=0; i<2; i++) {
                        combinePartitions+= splitCommaChar[i];
                    }
//                     System.out.println(combinePartitions);
                    snkPriceListInt.add(Integer.parseInt(combinePartitions));
                }
                else { // encompass more than 9 chars
                    String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                    String[] splitCommaChar= trimLastChar.split(",");
                    String combinePartitions="";
                    for (int i=0; i<3; i++) {
                        combinePartitions+= splitCommaChar[i];
                    }
//                     System.out.println(combinePartitions);
                    snkPriceListInt.add(Integer.parseInt(combinePartitions));
                }
            }
        } // exit For loop
        System.out.println("\n"+ "//-------------//"+ "\n");
        System.out.println("\n"+ "LIST OF SNEAKERS PRICE AFTER REFINING: "+ snkPriceListInt+ "\n");
        System.out.println("\n"+ "//-------------//"+ "\n");
        for (int indexAssertPriceListInt= 0;
             indexAssertPriceListInt< snkPriceListInt.size();
             indexAssertPriceListInt++) {
            if (snkPriceListInt.get(indexAssertPriceListInt)<= snkPriceListInt.get(indexAssertPriceListInt+1)) {
                log.info("\n\n//-----**------ ASSERTED SUCCESSFULLY " +
                        "SNEAKER PRICE NUMBER "+ (indexAssertPriceListInt+ 1)+ " --------**-------//"+ "\n\n");
                this.checkPointIfPassed=true;
                this.checkPointIfSkipped=false;
                if (this.checkPointIfPassed==true && this.checkPointIfSkipped==false) {
                    Assert.assertFalse(false);
                }
            }
            else { //the next sneaker price is less than the previous sneaker price => wrong functionally
                log.info("\n\n//-----**------ ASSERTED UNSUCCESSFULLY " +
                        "SNEAKER PRICE NUMBER "+ (indexAssertPriceListInt+ 1)+ " --------**-------//"+ "\n\n");
                this.checkPointIfFailed=true;
                this.checkPointIfSkipped=false;
                if (this.checkPointIfFailed==true && this.checkPointIfSkipped==false) {
                    Assert.assertFalse(true);
                }
            }
        }
    }


    @Test(groups = {"001", "sort", "slide/sandal"},
            enabled = true,
            priority = -4,
            description =
                    "this test script is facilitate verifying the sort function" +
                            "whether it works funtionally:" +
                            "1. Navigate to collection site" +
                            "2. Select sort category (slide/sandal) dynamically" +
                            "3. Select sort criteria (ascending price)" +
                            "4. Create an array of slide/sandal price" +
                            "5. Assert array of slide/sandal price whether it is sorted by ascending order"
    )
    public void sortSlideSandalTest01() throws TimeoutException,
            InterruptedException,
            NoSuchElementException,
            CommandLine.ExecutionException {

//        assign list value= 1 when list of slides/sandals is less than one page - absence of pagination
//        assign list value= 2 when list of slides/sandals is more than one page - presence of pagination
        this.listValue= 1;
        CollectionPage collect = new CollectionPage(driver);

//        select sort category by slide/sandal sending a text of Title Attribute (getAttribute)
        collect.selectDynamicSortCategories("title", slideSandalCategory, driver);
        log.info("//--**------------ SELECTED SLIDE/SANDAL CATEGORY -------------**--//" + "\n");

//        select sort bags by ascending price sending a text
        collect.selectDynamicSortCriteria(ascdPriceCriteria, driver);
        log.info("//--**------------ SORTED BY ASCENDING PRICE -------------**--//" + "\n");
//        collect.setWaitFor(LBL_CATEGORY_TITLE_LOCATOR);
        String slideSandalCategoryGetTxt = driver.findElement(LBL_CATEGORY_TITLE_LOCATOR).getText();
        System.out.println("" + slideSandalCategoryGetTxt + "\n");

        if (slideSandalCategoryGetTxt.equalsIgnoreCase(slideSandalTitle)) {

//            the slide/sandal title text matches the expected title => true assertion
            this.checkPointIfPassed = true;
            this.checkPointIfFailed = false;
            this.checkPointIfSkipped = false;
            if (checkPointIfPassed && !checkPointIfFailed && !checkPointIfSkipped) {
                Assert.assertFalse(false);
                System.out.println("Text on web: " + "\"" + slideSandalCategoryGetTxt +
                        "\"" + " matched the expected text!" + "\n");
            }
        } else {
            this.checkPointIfPassed = false;
            this.checkPointIfFailed = true;
            this.checkPointIfSkipped = false;
            if (checkPointIfFailed) {
                Assert.assertFalse(true);
                System.out.println("Text on web: " + "\"" + slideSandalCategoryGetTxt + "\"" +
                        " did not match the expected text!" + "\n");
            }
        }
        if (listValue==1) {

/*        declare a variable to attach label to each slide/sandal:
        - the first slide/sandal: SLIDE/SANDAL NUMBER 1
        - the second slide/sandal: SLIDE/SANDAL NUMBER 2
        , ...
 */
        int ssLabel = 1;
        int indexSSPriceArray = 0;
            int listOfSSsSize = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR).size();
            for (int ssIndex = 0; ssIndex < listOfSSsSize; ssIndex++) {
                org.openqa.selenium.WebElement listSSs =
                        driver.findElement(LBL_SLIDE_SANDAL_PARENT_LOCATOR);
                java.util.List<WebElement> childListSSs = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR);
                String ssPrice = childListSSs.get(ssIndex).getText();
                System.out.println("PRICE OF SLIDE/SANDAL NUMBER " + ssLabel + ": " + ssPrice + "");
                ssPriceList.add(ssPrice);
                System.out.println(ssPriceList);
                ssLabel++;
            }
            System.out.println("\n" + "//-------------//" + "\n");
            System.out.println("\n" + "LIST OF SLIDES/SANDALS PRICE BEFORE PROCESSING: " + ssPriceList + "\n");
            System.out.println("\n" + "//-------------//" + "\n");

            for (int indexInSSPrList = 0;
                 indexInSSPrList < ssPriceList.size(); indexInSSPrList++) {
                String priceInList = ssPriceList.get(indexInSSPrList);
//            System.out.println(priceInList+ "\n");
                if (priceInList.contains(" ") == false) {
                    if (priceInList.length() < 9) {
//                    System.out.println("less than 9 chars: "+ priceInList);
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 2; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    } else {
//                    System.out.println("more than 9 chars: "+ priceInList);
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 3; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                } else {  //price in list contains spacer character
//                System.out.println("contain spacer char: "+ priceInList);
                    String[] splitSpacerChar = priceInList.split(" ");
                    priceInList = splitSpacerChar[0];
                    if (priceInList.length() < 9) {
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 2; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    } else { // encompass more than 9 chars
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 3; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
            }// exit For loop
            System.out.println("\n" + "//-------------//" + "\n");
            System.out.println("\n" + "LIST OF SLIDES/SANDALS PRICE AFTER REFINING: " + ssPriceListInt + "\n");
            System.out.println("\n" + "//-------------//" + "\n");
            for (int indexAssertPriceListInt = 0;
                 indexAssertPriceListInt < ssPriceListInt.size();
                 indexAssertPriceListInt++) {
                if (ssPriceListInt.get(indexAssertPriceListInt) <= ssPriceListInt.get(indexAssertPriceListInt + 1)) {
                    log.info("\n\n//-----**------ ASSERTED SUCCESSFULLY " +
                            "SLIDE/SANDAL PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfPassed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfPassed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(false);
                    }
                } else { //the next slide/sandal price is less than the previous slide/sandal price => wrong functionally
                    log.info("\n\n//-----**------ ASSERTED UNSUCCESSFULLY " +
                            "SLIDE/SANDAL PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfFailed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfFailed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(true);
                    }
                }
            }
        }
        else if (listValue> 1){

//        declare a variable to count the number of click
            int clickIndex = 0;

//        declare a variable to count how many times we have to click
            int pageNodeSize = driver.findElements(BTN_PAGE_NODE_LOCATOR).size();

/*        declare a variable to attach label to each slide/sandal:
        - the first slide/sandal: SLIDE/SANDAL NUMBER 1
        - the second slide/sandal: SLIDE/SANDAL NUMBER 2
        , ...
 */
            int ssLabel = 1;
            int indexSSPriceArray = 0;
            System.out.println("Number of page: " + (pageNodeSize + 1) + "");

//        generate a for loop to verify whether each slide/sandal name contains the inputted search key
            for (int index = 0; index < pageNodeSize; index++) {

//            get number of slides/sandals per page
                int listOfSSSize = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR).size();
                System.out.println("\n" + "Number of slides/sandals page " + (index + 1) +
                        " is: " + listOfSSSize + " slides/sandals" + "\n");
                for (int ssIndex = 0; ssIndex < listOfSSSize; ssIndex++) {
                    WebElement listSS = driver.findElement(LBL_SLIDE_SANDAL_PARENT_LOCATOR);
                    java.util.List<WebElement> childListSS = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR);
                    String ssPrice = childListSS.get(ssIndex - 0).getText();
                    System.out.println("PRICE OF SLIDE/SANDAL NUMBER " + ssLabel + ": " + ssPrice + "");
                    ssPriceList.add(ssPrice);
                    System.out.println(ssPriceList);
                    ssLabel++;
                }
                WebElement nextButton = driver.findElement(BTN_NEXT_LOCATOR);
                ((JavascriptExecutor) driver).executeScript("scroll(0,6000)");
                collect.pauseWithTryCatch(1550);
                nextButton.click();
                clickIndex += 1;
                if (clickIndex - 1 == index && clickIndex == pageNodeSize) {
                    System.out.println("\n" + "\" //-----**------ THE LAST PAGE --------**-------//\"" + "\n");
                    int listOfSSLastPageSize = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR).size();
                    System.out.println("Number of slides/sandals page " + (pageNodeSize + 1) +
                            " is: " + listOfSSLastPageSize + " slides/sandals" + "\n");
                    for (int ssIndexLastPage = 0; ssIndexLastPage < listOfSSLastPageSize; ssIndexLastPage++) {
                        WebElement listSSLastPage = driver.findElement(LBL_SLIDE_SANDAL_PARENT_LOCATOR);
                        java.util.List<WebElement> childListSSLastPage = driver.findElements(LBL_SLIDE_SANDAL_CHILD_LOCATOR);
                        String ssPriceLastPage = childListSSLastPage.get(ssIndexLastPage).getText();
                        System.out.println("PRICE OF SLIDE/SANDAL NUMBER " + ssLabel + ": " + ssPriceLastPage + "");
                        ssPriceList.add(ssPriceLastPage);
                        System.out.println(ssPriceList);
                        ssLabel++;
                    }
                }
            }

            System.out.println("\n"+ "//-------------//"+ "\n");
            System.out.println("\n"+ "LIST OF SLIDES/SANDALS PRICE BEFORE PROCESSING: "+ ssPriceList+ "\n");
            System.out.println("\n"+ "//-------------//"+ "\n");

            for (int indexInSSPrList= 0;
                 indexInSSPrList< ssPriceList.size();
                 indexInSSPrList++) {
                String priceInList= ssPriceList.get(indexInSSPrList);
//            System.out.println(priceInList+ "\n");
                if (priceInList.contains(" ")==false) {
                    if (priceInList.length()< 9) {
//                    System.out.println("less than 9 chars: "+ priceInList);
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<2; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                    else {
//                    System.out.println("more than 9 chars: "+ priceInList);
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<3; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
                else {  //price in list contains spacer character
//                System.out.println("contain spacer char: "+ priceInList);
                    String[] splitSpacerChar= priceInList.split(" ");
                    priceInList =splitSpacerChar[0];
                    if (priceInList.length()< 9) {
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<2; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                    else { // encompass more than 9 chars
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<3; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        ssPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
            } // exit For loop
            System.out.println("\n"+ "//-------------//"+ "\n");
            System.out.println("\n"+ "LIST OF SLIDES/SANDALS PRICE AFTER REFINING: "+ ssPriceListInt+ "\n");
            System.out.println("\n"+ "//-------------//"+ "\n");
            for (int indexAssertPriceListInt= 0;
                 indexAssertPriceListInt< ssPriceListInt.size();
                 indexAssertPriceListInt++) {
                if (ssPriceListInt.get(indexAssertPriceListInt) <= ssPriceListInt.get(indexAssertPriceListInt + 1)) {
                    log.info("\n\n//-----**------ ASSERTED SUCCESSFULLY " +
                            "SLIDE/SANDAL PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfPassed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfPassed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(false);
                    }
                } else { //the next sneaker price is less than the previous sneaker price => wrong functionally
                    log.info("\n\n//-----**------ ASSERTED UNSUCCESSFULLY " +
                            "SLIDE/SANDAL PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfFailed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfFailed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(true);
                    }
                }
            }
        }
    }
    @Test(groups = {"001", "sort", "bag"},
            enabled = true,
            priority = -4,
            description =
                    "this test script is facilitate verifying the sort function" +
                            "whether it works funtionally:" +
                            "1. Navigate to collection site" +
                            "2. Select sort category (bag) dynamically" +
                            "3. Select sort criteria (ascending price)" +
                            "4. Create an array of bag price" +
                            "5. Assert array of bag price whether it is sorted by ascending order"
    )
    public void sortBagTest01() throws TimeoutException,
            InterruptedException,
            NoSuchElementException,
            CommandLine.ExecutionException {

//        assign list value= 1 when list of bags is less than one page - absence of pagination
//        assign list value= 2 when list of bags is more than one page - presence of pagination
        this.listValue= 1;
        CollectionPage collect = new CollectionPage(driver);

//        select sort category by bag sending a text of Title Attribute (getAttribute)
        collect.selectDynamicSortCategories("title", bagCategory, driver);
        log.info("//--**------------ SELECTED BAG CATEGORY -------------**--//" + "\n");

//        select sort bags by ascending price sending a text
        collect.selectDynamicSortCriteria(ascdPriceCriteria, driver);
        log.info("//--**------------ SORTED BY ASCENDING PRICE -------------**--//" + "\n");
//        collect.setWaitFor(LBL_CATEGORY_TITLE_LOCATOR);
        String bagCategoryGetTxt = driver.findElement(LBL_CATEGORY_TITLE_LOCATOR).getText();
        System.out.println("" + bagCategoryGetTxt + "\n");

        if (bagCategoryGetTxt.equalsIgnoreCase(bagTitle)) {

//            the bag title text matches the expected title => true assertion
            this.checkPointIfPassed = true;
            this.checkPointIfFailed = false;
            this.checkPointIfSkipped = false;
            if (checkPointIfPassed && !checkPointIfFailed && !checkPointIfSkipped) {
                Assert.assertFalse(false);
                System.out.println("Text on web: " + "\"" + bagCategoryGetTxt +
                        "\"" + " matched the expected text!" + "\n");
            }
        } else {
            this.checkPointIfPassed = false;
            this.checkPointIfFailed = true;
            this.checkPointIfSkipped = false;
            if (checkPointIfFailed) {
                Assert.assertFalse(true);
                System.out.println("Text on web: " + "\"" + bagCategoryGetTxt + "\"" +
                        " did not match the expected text!" + "\n");
            }
        }
        if (listValue==1) {

/*        declare a variable to attach label to each bag:
        - the first bag: BAG NUMBER 1
        - the second bag: BAG NUMBER 2
        , ...
 */
            int bagLabel = 1;
            int indexBagPriceArray = 0;
            int listOfBagSize = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR).size();
            for (int bagIndex = 0; bagIndex < listOfBagSize; bagIndex++) {
                org.openqa.selenium.WebElement listBag =
                        driver.findElement(LBL_BAG_PRICE_PARENT_LOCATOR);
                java.util.List<WebElement> childListBag = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR);
                String bagPrice = childListBag.get(bagIndex).getText();
                System.out.println("PRICE OF BAG NUMBER " + bagLabel + ": " + bagPrice + "");
                bagPriceList.add(bagPrice);
                System.out.println(bagPriceList);
                bagLabel++;
            }
            System.out.println("\n" + "//-------------//" + "\n");
            System.out.println("\n" + "LIST OF BAGS PRICE BEFORE PROCESSING: " + bagPriceList + "\n");
            System.out.println("\n" + "//-------------//" + "\n");

            for (int indexInBagPrList = 0;
                 indexInBagPrList< bagPriceList.size(); indexInBagPrList++) {
                String priceInList = bagPriceList.get(indexInBagPrList);
//            System.out.println(priceInList+ "\n");
                if (priceInList.contains(" ") == false) {
                    if (priceInList.length() < 9) {
//                    System.out.println("less than 9 chars: "+ priceInList);
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 2; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    } else {
//                    System.out.println("more than 9 chars: "+ priceInList);
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 3; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                } else {  //price in list contains spacer character
//                System.out.println("contain spacer char: "+ priceInList);
                    String[] splitSpacerChar = priceInList.split(" ");
                    priceInList = splitSpacerChar[0];
                    if (priceInList.length() < 9) {
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 2; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    } else { // encompass more than 9 chars
                        String trimLastChar = priceInList.substring(0, (priceInList.length() - 1));
                        String[] splitCommaChar = trimLastChar.split(",");
                        String combinePartitions = "";
                        for (int i = 0; i < 3; i++) {
                            combinePartitions += splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
            }// exit For loop
            System.out.println("\n" + "//-------------//" + "\n");
            System.out.println("\n" + "LIST OF BAGS PRICE AFTER REFINING: " + bagPriceListInt + "\n");
            System.out.println("\n" + "//-------------//" + "\n");
            for (int indexAssertPriceListInt = 0;
                 indexAssertPriceListInt < bagPriceListInt.size();
                 indexAssertPriceListInt++) {
                if (bagPriceListInt.get(indexAssertPriceListInt) <= bagPriceListInt.get(indexAssertPriceListInt + 1)) {
                    log.info("\n\n//-----**------ ASSERTED SUCCESSFULLY " +
                            "BAG PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfPassed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfPassed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(false);
                    }
                } else { //the next bag price is less than the previous slide/sandal price => wrong functionally
                    log.info("\n\n//-----**------ ASSERTED UNSUCCESSFULLY " +
                            "BAG PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfFailed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfFailed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(true);
                    }
                }
            }
        }
        else if (listValue> 1){

//        declare a variable to count the number of click
            int clickIndex = 0;

//        declare a variable to count how many times we have to click
            int pageNodeSize = driver.findElements(BTN_PAGE_NODE_LOCATOR).size();

/*        declare a variable to attach label to each bag:
        - the first bag: BAG NUMBER 1
        - the second bag: BAG NUMBER 2
        , ...
 */
            int bagLabel = 1;
            int indexBagPriceArray = 0;
            System.out.println("Number of page: " + (pageNodeSize + 1) + "");

//        generate a for loop to verify whether each bag name contains the inputted search key
            for (int index = 0; index < pageNodeSize; index++) {

//            get number of bags per page
                int listOfBagSize = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR).size();
                System.out.println("\n" + "Number of bags page " + (index + 1) +
                        " is: " + listOfBagSize + " bags" + "\n");
                for (int bagIndex = 0; bagIndex < listOfBagSize; bagIndex++) {
                    WebElement listBag = driver.findElement(LBL_BAG_PRICE_PARENT_LOCATOR);
                    java.util.List<WebElement> childListBag = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR);
                    String bagPrice = childListBag.get(bagIndex - 0).getText();
                    System.out.println("PRICE OF BAG NUMBER " + bagLabel + ": " + bagPrice + "");
                    bagPriceList.add(bagPrice);
                    System.out.println(bagPriceList);
                    bagLabel++;
                }
                WebElement nextButton = driver.findElement(BTN_NEXT_LOCATOR);
                ((JavascriptExecutor) driver).executeScript("scroll(0,6000)");
                collect.pauseWithTryCatch(1550);
                nextButton.click();
                clickIndex += 1;
                if (clickIndex - 1 == index && clickIndex == pageNodeSize) {
                    System.out.println("\n" + "\" //-----**------ THE LAST PAGE --------**-------//\"" + "\n");
                    int listOfBagLastPageSize = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR).size();
                    System.out.println("Number of bags page " + (pageNodeSize + 1) +
                            " is: " + listOfBagLastPageSize + " bags" + "\n");
                    for (int bagIndexLastPage = 0;
                         bagIndexLastPage < listOfBagLastPageSize; bagIndexLastPage++) {
                        WebElement listBagLastPage = driver.findElement(LBL_BAG_PRICE_PARENT_LOCATOR);
                        java.util.List<WebElement> childListBagLastPage = driver.findElements(LBL_BAG_PRICE_CHILD_LOCATOR);
                        String bagPriceLastPage = childListBagLastPage.get(bagIndexLastPage).getText();
                        System.out.println("PRICE OF BAG NUMBER " + bagLabel + ": " + bagPriceLastPage + "");
                        bagPriceList.add(bagPriceLastPage);
                        System.out.println(bagPriceList);
                        bagLabel++;
                    }
                }
            }

            System.out.println("\n"+ "//-------------//"+ "\n");
            System.out.println("\n"+ "LIST OF BAGS PRICE BEFORE PROCESSING: "+ bagPriceList+ "\n");
            System.out.println("\n"+ "//-------------//"+ "\n");

            for (int indexInBagPrList= 0;
                 indexInBagPrList< bagPriceList.size();
                 indexInBagPrList++) {
                String priceInList= bagPriceList.get(indexInBagPrList);
//            System.out.println(priceInList+ "\n");
                if (priceInList.contains(" ")==false) {
                    if (priceInList.length()< 9) {
//                    System.out.println("less than 9 chars: "+ priceInList);
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<2; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                    else {
//                    System.out.println("more than 9 chars: "+ priceInList);
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<3; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
                else {  //price in list contains spacer character
//                System.out.println("contain spacer char: "+ priceInList);
                    String[] splitSpacerChar= priceInList.split(" ");
                    priceInList =splitSpacerChar[0];
                    if (priceInList.length()< 9) {
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<2; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                    else { // encompass more than 9 chars
                        String trimLastChar= priceInList.substring(0, (priceInList.length()-1));
                        String[] splitCommaChar= trimLastChar.split(",");
                        String combinePartitions="";
                        for (int i=0; i<3; i++) {
                            combinePartitions+= splitCommaChar[i];
                        }
//                     System.out.println(combinePartitions);
                        bagPriceListInt.add(Integer.parseInt(combinePartitions));
                    }
                }
            } // exit For loop
            System.out.println("\n"+ "//-------------//"+ "\n");
            System.out.println("\n"+ "LIST OF BAGS PRICE AFTER REFINING: "+ bagPriceListInt+ "\n");
            System.out.println("\n"+ "//-------------//"+ "\n");
            for (int indexAssertPriceListInt= 0;
                 indexAssertPriceListInt< bagPriceListInt.size();
                 indexAssertPriceListInt++) {
                if (bagPriceListInt.get(indexAssertPriceListInt) <= bagPriceListInt.get(indexAssertPriceListInt + 1)) {
                    log.info("\n\n//-----**------ ASSERTED SUCCESSFULLY " +
                            "BAG PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfPassed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfPassed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(false);
                    }
                } else { //the next bag price is less than the previous bag price => wrong functionally
                    log.info("\n\n//-----**------ ASSERTED UNSUCCESSFULLY " +
                            "BAG PRICE NUMBER " + (indexAssertPriceListInt + 1) + " --------**-------//" + "\n\n");
                    this.checkPointIfFailed = true;
                    this.checkPointIfSkipped = false;
                    if (this.checkPointIfFailed == true && this.checkPointIfSkipped == false) {
                        Assert.assertFalse(true);
                    }
                }
            }
        }


    }






}

