package codeu.chat.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public final class DatabaseTest {

  private Database database;
  private TestSchema schema;

  private class TestSchema extends Schema {
    public TestSchema() {
      addField("foo", "VARCHAR(255)");
      addField("bar", "VARCHAR(255)");
    }
  }

  private class TestTable extends Table<TestSchema> {
    public TestTable(Database database) throws SQLException {
      super(new TestSchema(), database, "test");
    }
  }

  @Before
  public void setupDatabase() throws SQLException {
    database = new Database("test.db");
    schema = new TestSchema();
    schema.dropTable("test", database);
  }

  @Test
  public void testDatabaseValid() {
    Connection connection = database.getConnection();
  }

  @Test
  public void testCreateTable() throws SQLException {
    schema.createTable("test", database);
  }

  @Test
  public void testDropTable() throws SQLException {
    schema.dropTable("test", database);
  }

  @Test
  public void testDropTableObject() throws SQLException {
    TestTable table = new TestTable(database);
    table.destroy();
  }

  @Test
  public void testCreateObject() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    assertTrue(table.create(fields) != -1);
  }

  @Test
  public void testFindObject() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    assertNotNull(table.find(id));
  }

  @Test
  public void testFindObjectUnique() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    List<DBObject<TestSchema>> objects = table.find("_id", String.valueOf(id));
    assertTrue(objects.size() == 1);
  }

  @Test
  public void testFindObjectField() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    List<DBObject<TestSchema>> objects = table.find("foo", "hello");
    assertTrue(objects.size() > 0);
  }

  @Test
  public void testFindObjectField2() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    List<DBObject<TestSchema>> objects = table.find("foo", "not hello");
    assertTrue(objects.size() == 0);
  }

  @Test
  public void testFindObjectQuery() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    List<DBObject<TestSchema>> objects = table.findQuery("foo = 'hello'");
    assertTrue(objects.size() > 0);
  }

  @Test
  public void testUpdateObject() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    fields.put("bar", "earth");
    table.update(id, fields);
    List<DBObject<TestSchema>> objects = table.find("bar", "earth");
    assertTrue(objects.size() > 0);
  }

  @Test
  public void testDeleteObject() throws SQLException {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    table.remove(id);
    assertNull(table.find(id));
  }

}
