import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main
{
    public static void main(String[] args)
    {
        List<String>newsList = new ArrayList<String>(); //Список с заголовками новостей
        String FileName = "Results_v2.txt";
        String DatabaseName = "ZabguNews_v2";

        WebPageDownloader WPD = new WebPageDownloader(); //Загрузчик веб-страниц
        WriterToFile WTF = new WriterToFile();          //Записывание данных в файлы
        WriterToDatabase WTD = new WriterToDatabase(); //Записывание данных в базу данных

        System.out.println(" ");

        try
        {
            WPD.download_n_WebPagesFromZabguNews(10, newsList);
            WTF.writeResultsToFile(FileName, newsList);
            WTD.writeResultsToDatabase(DatabaseName, newsList);
        }
        catch(Exception ex)
        {
            
        }

        System.out.println("Нажмите ENTER для завершения программы...");
        Scanner scanner = new Scanner(System.in); 
        scanner.nextLine();
        scanner.close();
    }
}