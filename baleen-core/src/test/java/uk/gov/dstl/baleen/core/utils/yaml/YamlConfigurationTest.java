// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.utils.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/** Tests {@link YamlConfiguration}. */
public class YamlConfigurationTest {

  private static final String YAMLCONFIGURATION_YAML = "yamlconfiguration.yaml";

  private static final String MISSING_KEY = "example.notthere";
  private static final String INT_KEY = "example.count";
  private static final String LIST_KEY = "example.list";

  private YamlConfiguration config;

  @Before
  public void setup() throws IOException {
    config = new YamlConfiguration(YamlConfigurationTest.class, YAMLCONFIGURATION_YAML);
  }

  @Test
  public void testNonExistentFile() {
    try {
      new YamlConfiguration(new File("missing.yaml"));
    } catch (IOException ioe) {
      return;
    }

    fail("Didn't throw expected IOException");
  }

  @Test
  public void testReadString() throws IOException {
    YamlConfiguration yc =
        new YamlConfiguration("example:\n  color: red\n  count: 7\n  list:\n  - a\n  - b\n  - c");
    assertTrue(yc.getAsList("example.list").containsAll(Arrays.asList("a", "b", "c")));
    assertEquals("red", yc.get("example.color").get());
    assertEquals(7, yc.get("example.count").get());
  }

  @Test
  public void testGetAsList() {
    List<String> list = config.getAsList(LIST_KEY);
    assertTrue(list.containsAll(Arrays.asList("a", "b", "c")));

    list = config.getAsList(MISSING_KEY);
    assertTrue(list.isEmpty());
  }

  @Test
  public void testGetAsListOfMaps() {
    List<Map<String, Object>> listMap = config.getAsListOfMaps("annotators");

    assertFalse(listMap.isEmpty());
  }

  @Test
  public void testGet() {
    assertTrue(config.get(INT_KEY).isPresent());
    assertEquals(7, (int) config.get(INT_KEY).get());
    assertFalse(config.get(MISSING_KEY).isPresent());
  }

  @Test
  public void testGetWithDefault() {
    assertEquals(7, (int) config.get(INT_KEY, 4));
    assertEquals(1, (int) config.get(MISSING_KEY, 1));
  }

  @Test
  public void testCleanTabs() throws IOException {
    assertEquals(
        "test: \"Hello\\tWorld!\"\n" + "testing:\n" + "- 1.. 2..  3..   4..\n",
        new YamlConfiguration("\t\ttest: Hello\tWorld!\n\t\ttesting:\n\t\t- 1.. 2..  3..   4..   ")
            .toString());
  }
}
