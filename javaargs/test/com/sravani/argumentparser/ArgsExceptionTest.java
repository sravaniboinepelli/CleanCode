package com.sravani.argumentparser;

import junit.framework.TestCase;

import static com.sravani.argumentparser.ArgsException.ErrorCode.*;

public class ArgsExceptionTest extends TestCase {

  

  public void testUnexpectedMessage() throws Exception {
    ArgsException e = new ArgsException(UNEXPECTED_ARGUMENT, 'x', null);
    assertEquals("Argument -x unexpected.", e.getMessage());
  }

  public void testMissingStringMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_STRING, 'x', null);
    assertEquals("Could not find string parameter for -x.", e.getMessage());
  }

  public void testInvalidIntegerMessage() throws Exception {
    ArgsException e = new ArgsException(INVALID_INTEGER, 'x', "Forty two");
    assertEquals("Argument -x expects an integer but was 'Forty two'.", e.getMessage());
  }

  public void testMissingIntegerMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_INTEGER, 'x', null);
    assertEquals("Could not find integer parameter for -x.", e.getMessage());
  }

  public void testInvalidDoubleMessage() throws Exception {
    ArgsException e = new ArgsException(INVALID_DOUBLE, 'x', "Forty two");
    assertEquals("Argument -x expects a double but was 'Forty two'.", e.getMessage());
  }

  public void testMissingDoubleMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_DOUBLE, 'x', null);
    assertEquals("Could not find double parameter for -x.", e.getMessage());
  }

  public void testMissingMapMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_MAP, 'x', null);
    assertEquals("Could not find map string for -x.", e.getMessage());
  }

  public void testMalformedMapMessage() throws Exception {
    ArgsException e = new ArgsException(MALFORMED_MAP, 'x', null);
    assertEquals("Map string for -x is not of form k1:v1,k2:v2...", e.getMessage());
  }

  public void testInvalidArgumentName() throws Exception {
    ArgsException e = new ArgsException(INVALID_ARGUMENT_NAME, '#', null);
    assertEquals("'#' is not a valid argument name.", e.getMessage());
  }

  public void testInvalidFormat() throws Exception {
    ArgsException e = new ArgsException(INVALID_ARGUMENT_FORMAT, 'x', "$");
    assertEquals("'$' is not a valid argument format.", e.getMessage());
  }
  public void testMissingSchemaMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_SCHEMA, "null");
    assertEquals("Schema need to be supplied. please supply schema string of form f,n#,d##",
                 e.getMessage());
  }
  public void testMissingArgumentsMessage() throws Exception {
    ArgsException e = new ArgsException(MISSING_ARGUMENT_LIST, "null");
    assertEquals("Argument list missing. Please supply argument string array of form -f -n 10, -d 3.5",
                 e.getMessage());
  }
}

