import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.concurrent.*;  // ThreadPoolExecutor, Executors


public class WebPageDownloader 
{
   private static String UserAgent = "Mozilla";
   private static int TimeOut = 5000;

   public WebPageDownloader()
   {

   }

   public WebPageDownloader(String _UserAgent, int _TimeOut)
   {
        UserAgent = _UserAgent;
        TimeOut = _TimeOut;
   }


    /** Скачивание одной страницы */
    private String downloadWebPage(String url) throws IOException 
    {
        StringBuilder result = new StringBuilder();
         String line;
   
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("User-Agent", UserAgent);
        urlConnection.setReadTimeout(TimeOut);
        urlConnection.setConnectTimeout(TimeOut);
   
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



    /** Скачивание N страниц новостей 
     * n - количество страниц
     * allMatches - список куда сохранится скачанная информация
    */
    public void download_n_WebPagesFromZabguNews(Integer n, List<String> allMatches) throws IOException 
    {
        ThreadPoolExecutor thread_pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        long start = System.currentTimeMillis();

        for(int p = 1; p <= n; p++)
        {
            final int p2 = p;
			thread_pool.submit( () -> 
			{
				try
				{					
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

        //Инициирует упорядоченное отключение, при котором ранее отправленные задачи выполняются, но новые задачи не принимаются
        thread_pool.shutdown();
        try 
		{
            // пул не завершает все потоки сразу, как только кончились задачи ( Runnable или Callable )
            // потоки пула можно завершить только после таймаута
            // ждём завершения работы всех потоков ИЛИ Long.MAX_VALUE секунд и удаляем потоки
            thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);		
        } 
		catch (InterruptedException e) 
		{
            System.out.println( e.getStackTrace() );
        }
		
        long stop = System.currentTimeMillis();
        System.out.println( String.format("Время работы параллельного алгоритма: %7d ms", stop-start) );

        for (int i = 0; i < allMatches.size(); i++) 
        {
            System.out.println(i+1 + ": " + allMatches.get(i) + " ");
        }
    }
}
