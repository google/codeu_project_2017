package codeu.chat.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
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
    public TestTable(Database database) {
      super(new TestSchema(), database, "test");
    }
  }

  @Before
  public void setupDatabase() {
    database = new Database("test.db");
    schema = new TestSchema();
    schema.dropTable("test", database);
  }

  @Test
  public void testDatabaseValid() {
    Connection connection = database.getConnection();
    assertNotNull(connection);
  }

  @Test
  public void testCreateTable() {
    assertTrue(schema.createTable("test", database));
  }

  @Test
  public void testDropTable() {
    assertTrue(schema.dropTable("test", database));
  }

  @Test
  public void testDropTableObject() {
    TestTable table = new TestTable(database);
    assertTrue(table.destroy());
  }

  @Test
  public void testCreateObject() {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    assertTrue(table.create(fields) != -1);
  }

  @Test
  public void testFindObject() {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    assertNotNull(table.find(id));
  }

  @Test
  public void testFindObjectUnique() {
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
  public void testFindObjectField() {
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
  public void testFindObjectField2() {
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
  public void testFindObjectQuery() {
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
  public void testUpdateObject() {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    fields.put("bar", "earth");
    assertTrue(table.update(id, fields));
    List<DBObject<TestSchema>> objects = table.find("bar", "earth");
    assertTrue(objects.size() > 0);
  }

  @Test
  public void testDeleteObject() {
    TestTable table = new TestTable(database);
    Map<String, String> fields = new HashMap<String, String>();
    fields.put("foo", "hello");
    fields.put("bar", "world");
    int id = table.create(fields);
    assertTrue(id != -1);
    assertTrue(table.remove(id));
    assertNull(table.find(id));
  }

}
