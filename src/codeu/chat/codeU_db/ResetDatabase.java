package codeu.chat.codeU_db;
public class ResetDatabase
{
    public static void main( String args[] )
    {
        DataBaseConnection.open();
        DataBaseConnection.dropTables();
        DataBaseConnection.createTables();
        System.out.println("Finished Resetting Database Successfully!!!");
    }
}