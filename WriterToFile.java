import java.io.IOException;
import java.util.List;
import java.io.PrintWriter;


public class WriterToFile 
{
    public WriterToFile()
    {

    }

    /** Запись результатов в TXT файл 
     * filename - имя файла
     * allMatches - список куда сохранится скачанная информация
    */
    public void writeResultsToFile(String filename, List<String> allMatches) throws IOException 
    {
        PrintWriter f = new PrintWriter(filename);
        for (int i = 0; i < allMatches.size(); i++) 
        {
            f.println(i+1 + ": " + allMatches.get(i));
        }
        f.close();
    }
}
