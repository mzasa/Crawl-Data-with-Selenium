import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class Main {
    public static void main(String[] args){
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver();

        Bot tnpBOT = new Bot(driver);

        tnpBOT.mySearch();
    }
}
