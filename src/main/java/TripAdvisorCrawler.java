import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TripAdvisorCrawler {
    public static void main(String[] args) throws IOException {

        //String file = args[1];
        String serial=args[0];
        String file = "trip_advisor_"+serial+".csv";

        List<String> content = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                content.add(line.split(",")[0]);
            }
        } catch (FileNotFoundException e) {
            //Some error logging
            e.printStackTrace();
        }

        for(String restaurantname:content) {
            //String cuisine = "indonesian";
            int page = 10;
            // Load the web driver for windows
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
            //System.setProperty("webdriver.chrome.driver", args[0]);

            // Initialize the chrome driver instance
            ChromeDriver driver = new ChromeDriver();
            // Wait until the page loads or the timeout is passed
            driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
//        String restaurantname="Summer Pavilion";

            BufferedWriter writer = new BufferedWriter(new FileWriter("data/restaurant-review-crawler-" + restaurantname + ".csv"));
            writer.write("\"restaurant_name\",\"title\",\"description\",\"date\",\"rating\",\"reviewer_destination\",\"restaurant_price\",\"ranking\"");
            writer.newLine();
            String GlobalURL = "";
            driver.get("https://www.google.com/search?query=" + restaurantname + " Singapore Review TripAdvisor");
            //driver.findElementByXPath("//a[contains(@href, 'tripadvisor.com.sg')])[0]").click();
            for (WebElement webElement : driver.findElementsByCssSelector("cite.iUh30")) {
                if (webElement.getText().contains("tripadvisor.com.sg")) {
                    webElement.click();
                    GlobalURL = driver.getCurrentUrl().toString();
                    String currentURL = "";
                    int flag = 0;

                    int iteration = 10;
                    while (flag == 0) {
                        for (WebElement element : driver.findElementsByCssSelector("div.review-container")) {
                            String Reviewtitle = element.findElement(By.cssSelector("span.noQuotes")).getText().replace(",", " ");
                            String Reviewdesc = element.findElement(By.cssSelector("p.partial_entry")).getText().replaceAll("\\.\\.\\.More", "").replace(",", " ");
                            String Reviewdate = element.findElement(By.cssSelector("span.ratingDate")).getText().replace("Reviewed", "").replace(",", " ");
                            String Reviewrating = element.findElement(By.cssSelector("span.ui_bubble_rating")).getAttribute("class").split("_")[3].replace("0", "").replace(",", " ");
                            String ReviewerLocation = "";
                            String Range="";
                            String Ranking ="";
                            try {
                                Range = driver.findElementByCssSelector("span.header_tags.rating_and_popularity").getText().replace(",", " ");
                            }
                            catch(org.openqa.selenium.NoSuchElementException e){
                                Range="$";
                            }

                            if (element.findElements(By.cssSelector("span.expand_inline.userLocation")).size() > 0) {
                                ReviewerLocation = element.findElement(By.cssSelector("span.expand_inline.userLocation")).getText().replace("Reviewed", "").replace(",", " ");

                            } else {
                                ReviewerLocation = "Singapore";
                            }

                            try {

                                Ranking = driver.findElementByCssSelector("span.header_popularity.popIndexValidation").getText().replace(",", " ");

                            }
                            catch(org.openqa.selenium.NoSuchElementException e){
                                Ranking="";
                            }
                            writer.write(restaurantname + "," + Reviewtitle + "," + Reviewdesc + "," + Reviewdate + "," + Reviewrating + "," + ReviewerLocation+","+Range+","+Ranking);
                            //System.out.println(Range);
                            writer.newLine();
//                        writer.close();


                        }
                        currentURL = driver.getCurrentUrl().toString();
                        String append = "-Reviews-or" + iteration + "-";
                        String replaced = currentURL.replaceAll("-Reviews-", append);

                        driver.get(replaced);
                        currentURL = driver.getCurrentUrl().toString();
                        //System.out.println(replaced +" is the currentURL");
                        //System.out.println(GlobalURL +" is the GlobalURL");

                        iteration = iteration + 10;
                        if (currentURL == GlobalURL | currentURL.equals(GlobalURL)) {
                            flag = 1;
                        } else {

                        }


                    }


                }
                break;
            }



            driver.quit();
            writer.close();


            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
