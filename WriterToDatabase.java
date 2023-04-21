import java.io.IOException;
import java.util.List;
import java.sql.*;

public class WriterToDatabase 
{
    public WriterToDatabase() 
    {

    }

    /** Запись в базу SQLite 
     * databaseName - файл базы данных
     * allMatches - список куда сохранится скачанная информация
    */
    public void writeResultsToDatabase(String _databaseName, List<String> allMatches) throws IOException, SQLException, ClassNotFoundException 
    {
        String DB_URL = "jdbc:sqlite:" + _databaseName + ".db"; // Строка подключения к базе данных
        Connection conn = null;

        try 
        {
            // Подключение к базе данных
            // Если файла с базой данных нет, он будет создан
            conn = DriverManager.getConnection(DB_URL);

            // Создание таблицы новостей
            Statement createTable = conn.createStatement();
            createTable.execute("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL);");

            // Заполнение таблицы
            Statement insert = conn.createStatement();
            for (int i = 0; i < allMatches.size(); i++) 
            {
                insert.execute("INSERT INTO news (title) VALUES ('" + allMatches.get(i) + "')");
            }
        } 
        catch (SQLException e) 
        {
            throw new RuntimeException(e);
        } 
        finally 
        {
            if (conn != null)
                conn.close();
        }
        conn.close();
    }
}
