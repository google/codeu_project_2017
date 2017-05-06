package codeu.chat.codeU_db;

public class CreateDatabase
{

    public static void main( String args[] )
    {
        DataBaseConnection.open();
        DataBaseConnection.createTables();
        System.out.println("Finished Creating Database Successfully!!!");
    }
}