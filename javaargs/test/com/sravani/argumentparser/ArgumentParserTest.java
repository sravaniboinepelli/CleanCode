package com.sravani.argumentparser;

import static com.sravani.argumentparser.ArgsException.ErrorCode.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;


public class ArgumentParserTest {

  @Test
  public void testCreateWithNoSchemaOrArguments() throws Exception {
    ArgumentParser args = new ArgumentParser("", new String[0]);
    assertEquals(0, args.nextArgument());
  }

  @Test
  public void testCreateWithNullSchema() throws Exception {
    try {
      ArgumentParser args = new ArgumentParser(null, new String[0]);
      assertEquals(0, args.nextArgument());
    } catch (ArgsException e) {
      assertEquals(MISSING_SCHEMA, e.getErrorCode());
    }
  }

  @Test
  public void testCreateWithNullArgument() throws Exception {
    try {
      ArgumentParser args = new ArgumentParser("f,n#", null);
      assertEquals(0, args.nextArgument());
    } catch (ArgsException e) {
      assertEquals(MISSING_ARGUMENT_LIST, e.getErrorCode());
    }
  }

  @Test
  public void testWithNoSchemaButWithOneArgument() throws Exception {
    try {
      new ArgumentParser("", new String[]{"-x"});
      fail();
    } catch (ArgsException e) {
      assertEquals(UNEXPECTED_ARGUMENT, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }
  }

  @Test
  public void testWithNoSchemaButWithMultipleArguments() throws Exception {
    try {
      new ArgumentParser("", new String[]{"-x", "-y"});
      fail();
    } catch (ArgsException e) {
      assertEquals(UNEXPECTED_ARGUMENT, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }

  }

  @Test
  public void testNonLetterSchema() throws Exception {
    try {
      new ArgumentParser("*", new String[]{});
      fail("ArgumentParser constructor should have thrown exception");
    } catch (ArgsException e) {
      assertEquals(INVALID_ARGUMENT_NAME, e.getErrorCode());
      assertEquals('*', e.getErrorArgumentId());
    }
  }

  @Test
  public void testInvalidArgumentFormat() throws Exception {
    try {
      new ArgumentParser("f~", new String[]{});
      fail("ArgumentParser constructor should have throws exception");
    } catch (ArgsException e) {
      assertEquals(INVALID_ARGUMENT_FORMAT, e.getErrorCode());
      assertEquals('f', e.getErrorArgumentId());
    }
  }

  @Test
  public void testSimpleBooleanPresent() throws Exception {
    ArgumentParser args = new ArgumentParser("x", new String[]{"-x"});
    assertEquals(true, args.getBoolean('x'));
    assertEquals(1, args.nextArgument());
  }

  @Test
  public void testSimpleStringPresent() throws Exception {
    ArgumentParser args = new ArgumentParser("x*", new String[]{"-x", "param"});
    assertTrue(args.has('x'));
    assertEquals("param", args.getString('x'));
    assertEquals(2, args.nextArgument());
  }

  @Test
  public void testMissingStringArgument() throws Exception {
    try {
      new ArgumentParser("x*", new String[]{"-x"});
      fail();
    } catch (ArgsException e) {
      assertEquals(MISSING_STRING, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }
  }

  @Test
  public void testSpacesInFormat() throws Exception {
    ArgumentParser args = new ArgumentParser("x, y", new String[]{"-xy"});
    assertTrue(args.has('x'));
    assertTrue(args.has('y'));
    assertEquals(1, args.nextArgument());
  }

  @Test
  public void testSimpleIntPresent() throws Exception {
    ArgumentParser args = new ArgumentParser("x#", new String[]{"-x", "42"});
    assertTrue(args.has('x'));
    assertEquals(42, args.getInt('x'));
    assertEquals(2, args.nextArgument());
  }

  @Test
  public void testInvalidInteger() throws Exception {
    try {
      new ArgumentParser("x#", new String[]{"-x", "Forty two"});
      fail();
    } catch (ArgsException e) {
      assertEquals(INVALID_INTEGER, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
      assertEquals("Forty two", e.getErrorParameter());
    }

  }

  @Test
  public void testMissingInteger() throws Exception {
    try {
      new ArgumentParser("x#", new String[]{"-x"});
      fail();
    } catch (ArgsException e) {
      assertEquals(MISSING_INTEGER, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }
  }

  @Test
  public void testSimpleDoublePresent() throws Exception {
    ArgumentParser args = new ArgumentParser("x##", new String[]{"-x", "42.3"});
    assertTrue(args.has('x'));
    assertEquals(42.3, args.getDouble('x'), .001);
  }

  @Test
  public void testInvalidDouble() throws Exception {
    try {
      new ArgumentParser("x##", new String[]{"-x", "Forty two"});
      fail();
    } catch (ArgsException e) {
      assertEquals(INVALID_DOUBLE, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
      assertEquals("Forty two", e.getErrorParameter());
    }
  }

  @Test
  public void testMissingDouble() throws Exception {
    try {
      new ArgumentParser("x##", new String[]{"-x"});
      fail();
    } catch (ArgsException e) {
      assertEquals(MISSING_DOUBLE, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }
  }

  @Test
  public void testStringArray() throws Exception {
    ArgumentParser args = new ArgumentParser("x[*]", new String[]{"-x", "alpha"});
    assertTrue(args.has('x'));
    String[] result = args.getStringArray('x');
    assertEquals(1, result.length);
    assertEquals("alpha", result[0]);
  }

  @Test
  public void testMissingStringArrayElement() throws Exception {
    try {
      new ArgumentParser("x[*]", new String[] {"-x"});
      fail();
    } catch (ArgsException e) {
      assertEquals(MISSING_STRING,e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
    }
  }

  @Test
  public void manyStringArrayElements() throws Exception {
    ArgumentParser args = new ArgumentParser("x[*]", new String[]{"-x", "alpha", "-x", "beta", "-x", "gamma"});
    assertTrue(args.has('x'));
    String[] result = args.getStringArray('x');
    assertEquals(3, result.length);
    assertEquals("alpha", result[0]);
    assertEquals("beta", result[1]);
    assertEquals("gamma", result[2]);
  }

  @Test
  public void MapArgument() throws Exception {
    ArgumentParser args = new ArgumentParser("f&", new String[] {"-f", "key1:val1,key2:val2"});
    assertTrue(args.has('f'));
    Map<String, String> map = args.getMap('f');
    assertEquals("val1", map.get("key1"));
    assertEquals("val2", map.get("key2"));
  }

  @Test(expected=ArgsException.class)
  public void malFormedMapArgument() throws Exception {
    ArgumentParser args = new ArgumentParser("f&", new String[] {"-f", "key1:val1,key2"});
  }

  @Test
  public void oneMapArgument() throws Exception {
    ArgumentParser args = new ArgumentParser("f&", new String[] {"-f", "key1:val1"});
    assertTrue(args.has('f'));
    Map<String, String> map = args.getMap('f');
    assertEquals("val1", map.get("key1"));
  }

  @Test
  public void testExtraArguments() throws Exception {
    ArgumentParser args = new ArgumentParser("x,y*", new String[]{"-x", "-y", "alpha", "beta"});
    assertTrue(args.getBoolean('x'));
    assertEquals("alpha", args.getString('y'));
    assertEquals(3, args.nextArgument());
  }

  @Test
  public void testExtraArgumentsThatLookLikeFlags() throws Exception {
    ArgumentParser args = new ArgumentParser("x,y", new String[]{"-x", "alpha", "-y", "beta"});
    assertTrue(args.has('x'));
    assertFalse(args.has('y'));
    assertTrue(args.getBoolean('x'));
    assertFalse(args.getBoolean('y'));
    assertEquals(1, args.nextArgument());
  }

  @Test
  public void testIntwithDoubleValue() throws Exception {
    try {
      new ArgumentParser("x#", new String[]{"-x", "4.3"});
      fail();
    } catch (ArgsException e) {
      assertEquals(INVALID_INTEGER, e.getErrorCode());
      assertEquals('x', e.getErrorArgumentId());
      assertEquals("4.3", e.getErrorParameter());
    }
  }

  @Test
  public void testRepeatedPrimitiveTypes() throws Exception {
    ArgumentParser args = new ArgumentParser("x#,y##,s*",
                        new String[]{"-x", "4", "-y", "3.4",
                                    "-x", "400", "-y", "9.56",
                                    "-s", "string1", "-s" , "string2"});
    assertEquals(400, args.getInt('x'));
    assertEquals(9.56, args.getDouble('y'), .001);
    assertEquals("string2", args.getString('s'));     
  
  }

  @Test
  public void testRepeatedNonPrimitiveTypes() throws Exception {
    ArgumentParser args = new ArgumentParser("x[*],m&",
                        new String[]{"-x", "string1", "-x", "test1:test2",
                                    "-m", "name:test1", "-m", "age:42",
                                    "-x", "string3", "-m", "addr:myaddr"});
  
    Map<String, String> map = args.getMap('m');
    assertEquals("test1", map.get("name"));
    assertEquals("42", map.get("age"));
    assertEquals("myaddr", map.get("addr"));
    
    String[] result = args.getStringArray('x');
    assertEquals(3, result.length);
    assertEquals("string1", result[0]);
    assertEquals("test1:test2", result[1]);
    assertEquals("string3", result[2]);    
  
  }

}
