package codeu.chat.codeU_db;

public class DropDatabase
{
    public static void main( String args[] )
    {
        DataBaseConnection.open();
        DataBaseConnection.dropTables();
        System.out.println("Finished Creating Database Successfully!!!");
    }
}