import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.units.qual.s;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import io.github.cdimascio.dotenv.Dotenv;



public class Bot {
    private WebDriver driver;

    public Bot(WebDriver driver){
        this.driver = driver;
    }

    public void mySearch(){
        // Mở Instagram và đăng nhập
        try {
            login();

            // Tới trang cá nhân của bạn (hoặc của người khác)
            this.driver.get("https://www.instagram.com/lokpak.2/");
            this.sleep(5);

            // click vào nút "Following" để mở danh sách người bạn đang follow
            WebElement followingButton = this.driver.findElement(By.xpath("//a[contains(@href, '/following')]"));
            followingButton.click();
            this.sleep(5);

            //scroll to see all following list
            scrollFollowingList();
            
            // Lấy danh sách link các tài khoản mà bạn đang follow
            List<String> links = getAccountsLink();
            System.out.println(links.size());
            System.out.println(links);

            // Lấy danh sách tên các tài khoản đang follow
            List<String> names = getAccountsName();
            System.out.println(names.size());
            System.out.println(names);

            // Lưu dữ liệu vào một file excel
            saveIntoFile(names.size(), links, names);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.driver.quit();
        }
    }

    public void sleep(int seconds){
        System.out.println("Waiting for "+ seconds+" seconds...");
        try{
            Thread.sleep(seconds*1000);
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Finished waiting");
    }

    public void login(){
        Dotenv dotenv = Dotenv.load();
        String IGUserName = dotenv.get("IGUSERNAME");
        String IGPassWord = dotenv.get("IGPASSWORD");

        this.driver.get("https://www.instagram.com/accounts/login/");
        this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Điền thông tin đăng nhập
        WebElement username = this.driver.findElement(By.name("username"));
        WebElement password = this.driver.findElement(By.name("password"));
        WebElement loginButton = this.driver.findElement(By.xpath("//button[@type='submit']"));

        username.sendKeys(IGUserName);
        password.sendKeys(IGPassWord);
        loginButton.click();

        this.sleep(10);
    }

    public void scrollFollowingList(){
        // Scroll xuống dưới cùng của trang
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement scrollableDiv = driver.findElement(By.xpath("//div[contains(@class, 'xyi19xy')]"));

        for(int i=0;i<1;++i){
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", scrollableDiv);
            this.sleep(5);
        }
    }

    List<String> getAccountsLink(){
        List<WebElement> accounts = this.driver.findElements(By.xpath("//a[contains(@class, '_a6hd') and not(img)]" ));
        List<String> links = new ArrayList<>();

        for (int i = 24; i < accounts.size(); i++) {
            links.add(accounts.get(i).getAttribute("href"));
        }

        return links;
    }

    List<String> getAccountsName(){
        List<WebElement> spanElements = driver.findElements(By.xpath("//span[@style and .//span[contains(@class, 'x1lliihq')]]"));
        List<String> names = new ArrayList<>();

        for (int i = 16; i < spanElements.size(); i++) {
            names.add(spanElements.get(i).getText());
        }

        return names;
    }

    public void saveIntoFile(int rows, List<String> links, List<String> names){
        // Tạo workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Ghi dữ liệu vào từng dòng
        for (int i = 0; i < rows; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(links.get(i));
            row.createCell(1).setCellValue(names.get(i));
        }

        // Lưu file Excel
        try (FileOutputStream fileOut = new FileOutputStream("E:/2/1 Study/Crawl Data with Java/test.xlsx")) {
            System.out.println("Save Succeed");
            workbook.write(fileOut);
        }
        catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }

        // Đóng workbook
        try {
            System.out.println("Close Succeed");
            workbook.close();
        } catch (IOException e) {
            System.out.println("Lỗi khi đóng workbook: " + e.getMessage());
        }

        // Chờ trang tải xong
        this.sleep(10);
    }
}
