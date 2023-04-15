import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;   // для указания кодировки текста
import java.nio.file.Path;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.concurrent.*;  // ThreadPoolExecutor, Executors

import java.sql.*;

public class JavaDownloadWebPage {

    public static void main(String[] args) throws IOException,  InterruptedException, SQLException, ClassNotFoundException
    {
        List<String>newsList = new ArrayList<String>();

		
		Thread t1 = new Thread(new Runnable(){
			@Override
			public void run(){
				try{
				download_n_WebPagesFromZabguNews(12, newsList);
				}
				catch(IOException e){e.printStackTrace();}
		}});
		
		t1.start();
		t1.join();		

        writeResultsToFile("my_file2.txt", newsList);

        writeResultsToDatabase(newsList);
    }


    //Запись в базу SQLite 
    private static void writeResultsToDatabase(List<String> allMatches) throws IOException, SQLException, ClassNotFoundException
    {
        String DB_URL = "jdbc:sqlite:ZabguNews.db";
        Connection conn = null;

        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            //Очистить таблицу от данных чтобы избежать ошибки с уникальностью ID
            Statement delete = conn.createStatement();
            delete.execute("DELETE FROM news; VACUUM;" );

            //Заполнение таблицы по новой
            Statement insert = conn.createStatement();
            for(int i = 0; i < allMatches.size(); i++)
            {
                insert.execute("INSERT INTO news (id, title) VALUES (" + (i+1) + ", '" + allMatches.get(i) + "')");
            }
        }
        catch (SQLException e )
        {
            throw new RuntimeException(e);
        }
        finally
        { 
            if(conn!= null)
            conn.close();
        }

        conn.close();
    }

    //Запись в файл
    private static void writeResultsToFile(String filename, List<String> allMatches) throws IOException 
    {
        PrintWriter f = new PrintWriter(filename);
        for (int i = 0; i < allMatches.size(); i++) 
        {
            f.println(i+1 + ": " + allMatches.get(i));
        }
        f.close();
    }


    // todo: to new class 

    /** todo: doc */
    private static void download_n_WebPagesFromZabguNews(Integer n, List<String> allMatches) throws IOException 
    {
        //allMatches = new ArrayList<String>();

        ThreadPoolExecutor thread_pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        long start = System.currentTimeMillis();

        for(int p = 1; p <= n; p++)
        {
            final int p2 = p;
        thread_pool.submit( () -> {

            try{
                
            String URL = "https://zabgu.ru/php/news.php?category=1&page="+p2;
            String result = downloadWebPage(URL);            

            Matcher m = Pattern.compile("<div class=.headline.>.*?.<.div>").matcher(result);
            while (m.find()) 
            {
            allMatches.add(m.group());
            }

            for(int i = 0; i < allMatches.size(); i++)
            {
                String tmp = allMatches.get(i);
                tmp = tmp.replace("<div class=\"headline\">", "");
                tmp = tmp.replace("</div>", "");
                tmp = tmp.replace("&quot;", "\"");
                allMatches.set(i, tmp);
            }
        }
        catch(IOException ex)
        {

        }
        } );
    }

        // Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted. I
        thread_pool.shutdown();
        try {
            // пул не завершает все потоки сразу, как только кончились задачи ( Runnable или Callable )
            // потоки пула можно завершить только после таймаута
            // ждём завершения работы всех потоков ИЛИ Long.MAX_VALUE секунд и удаляем потоки
            thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println( e.getStackTrace() );
        }
        long stop = System.currentTimeMillis();
        System.out.println( String.format("parallel algorithm dt: %7d ms", stop-start) );

        for (int i = 0; i < allMatches.size(); i++) 
        {
            System.out.println(i+1 + ": " + allMatches.get(i) + " ");
        }
    }


    //Скачивание одной страницы
    private static String downloadWebPage(String url) throws IOException 
    {
        StringBuilder result = new StringBuilder();
        String line;

        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla");
        urlConnection.setReadTimeout(5000);
        urlConnection.setConnectTimeout(5000);

        try (InputStream is = urlConnection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) 
            {
                while ((line = br.readLine()) != null) 
                {
                    result.append(line);
                }
            }

        return result.toString();
    }
}