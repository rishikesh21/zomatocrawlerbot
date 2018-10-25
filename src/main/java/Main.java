import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
// chinese 223
// indian 29
// korean 13
// western 39
// japanese 47
// Indonesian 10
public class Main {
    public static void main(String[] args) throws IOException {

        String cuisine="chinese";
        int page=4;
        // Load the web driver for windows
        System.setProperty("webdriver.chrome.driver", "/Users/mac/IntelIjPrograms/zomatocrawlerbot/src/main/resources/driver/chromedriver");
        // Initialize the chrome driver instance
        ChromeDriver driver = new ChromeDriver();
        // Wait until the page loads or the timeout is passed
        driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
        BufferedWriter writer = new BufferedWriter(new FileWriter("restaurant-zomato-crawler.csv"));
        writer.write("\"restaurant_name\",\"rating\",\"address\",\"cuisine\",\"postal_code\"");
        writer.newLine();

        for (int i=1 ;i<=page;i++){
            driver.get("https://www.zomato.com/singapore/restaurants/"+cuisine+"?page="+i);

            driver.findElementsByCssSelector("div.search-snippet-card").forEach(contentElement -> {
                String titleText = contentElement.findElement(By.cssSelector("a.result-title")).getText();
                String ratingText = contentElement.findElement(By.cssSelector("div.rating-popup")).getText();
                String addressText = contentElement.findElement(By.cssSelector("div.search-result-address")).getText().replace(",",";");
                List<String> foodItems = contentElement
                        .findElement(By.cssSelector("span.pl0"))
                        .findElements(By.tagName("a"))
                        .stream()
                        .map(a -> a.getText())
                        .collect(Collectors.toList());
                //foodItems.forEach(System.out::println);
                String postal_code=addressText.split(" ")[(addressText.split(" ").length) -1 ];

                String str=titleText + "," + ratingText + "," + addressText + "," + foodItems.toString()+","+postal_code;

                try {

                    writer.write(str);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            System.out.println(i + " successfully wrote page");
        }
        driver.quit();


        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
