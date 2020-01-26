package com.sravani.argumentparser;

import java.util.*;

import static com.sravani.argumentparser.ArgsException.ErrorCode.*;

import com.sravani.argumentparser.ArgsException;


public class ArgumentParser {
  public enum ArgumentType {
    BOOLEAN {
        public String toString() {
            return "BOOLEAN";
        }
    },
  
    INTEGER {
        public String toString() {
            return "INTEGER";
        }
    },

    DOUBLE {
        public String toString() {
            return "DOUBLE";
        }
    },
    STRING {
        public String toString() {
            return "STRING";
        }
    },
    STRINGARRAY {
        public String toString() {
            return "STRINGARRAY";
        }
    },
    MAPTYPE {
          public String toString() {
            return "MAPTYPE";
          }
    }
  }

  private Map<Character, ArgumentType> schemaMap;

  private Map<Character, ArgumentType> argsFound;

  private Map<Character, String> intArgs;
  private Map<Character, String> doubleArgs;
  private Map<Character, String> stringArgs;
  private Map<Character, List<String>> stringArrayArgs;
  private Map<Character, Map<String, String>> mapArgs;

  private ListIterator<String> currentArgument;

  public ArgumentParser(String schema, String[] args) throws ArgsException {

    schemaMap = new HashMap<Character, ArgumentType>();
    argsFound= new HashMap<Character, ArgumentType>();

    intArgs = new HashMap<Character, String>();
    doubleArgs = new HashMap<Character, String>();
    stringArgs = new HashMap<Character, String>();
    stringArrayArgs = new  HashMap<Character, List<String>>();
    mapArgs = new HashMap<Character, Map<String, String>>();

    parseSchema(schema);
    parseArgumentStrings(Arrays.asList(args));
  
  }

  private void parseSchema(String schema) throws ArgsException {
    for (String element : schema.split(","))
      if (element.length() > 0)
        parseSchemaElement(element.trim());
  }

  private void parseSchemaElement(String element) throws ArgsException {
    try {
      char elementId = element.charAt(0);
      String elementTail = element.substring(1);
      validateSchemaElementId(elementId);
      if (elementTail.length() == 0)
        schemaMap.put(elementId, ArgumentType.BOOLEAN);
      else if (elementTail.equals("*"))
        schemaMap.put(elementId, ArgumentType.STRING);
      else if (elementTail.equals("#"))
        schemaMap.put(elementId, ArgumentType.INTEGER);
      else if (elementTail.equals("##"))
        schemaMap.put(elementId,ArgumentType.DOUBLE);
      else if (elementTail.equals("[*]"))
        schemaMap.put(elementId, ArgumentType.STRINGARRAY);
      else if (elementTail.equals("&"))
        schemaMap.put(elementId, ArgumentType.MAPTYPE);
      else
        throw new ArgsException(INVALID_ARGUMENT_FORMAT, elementId, elementTail);
    } catch (ArgsException e) {
         System.out.println("Check format of the schema" +
                    "valid schema example:(f,s*,n#,a##,p[*]) " + e.getMessage() );
    }  catch (Exception e) {
         System.out.println("Schema Parsing failed with " + e.getMessage() );
    }
  }

  private void validateSchemaElementId(char elementId) throws ArgsException {
    if (!Character.isLetter(elementId))
      throw new ArgsException(INVALID_ARGUMENT_NAME, elementId, null);
  }

  private void parseArgumentStrings(List<String> argsList) throws ArgsException {
    for (currentArgument = argsList.listIterator(); currentArgument.hasNext();) {
     
      String argString = currentArgument.next();
      if (argString.startsWith("-")) {
        parseArgumentCharacters(argString.substring(1));
      } else {
        currentArgument.previous();
        break;
      }
    }
  }

  private void parseArgumentCharacters(String argChars) throws ArgsException {
    for (int i = 0; i < argChars.length(); i++)
      parseArgumentCharacter(argChars.charAt(i));
  }

  private void parseArgumentCharacter(char argChar) throws ArgsException {
    ArgumentType type = schemaMap.get(argChar);
    // System.out.println("parse" + argChar + " " + schemaMap.get(argChar) + " ttt" + ArgumentType.BOOLEAN);
    if (type == null) {
      throw new ArgsException(UNEXPECTED_ARGUMENT, argChar, null);
    } else {
      argsFound.put(argChar, type);
      try {
        System.out.println("parseArgumentCharacter " + argChar + " " + type);
        if (type != ArgumentType.BOOLEAN){
          setArgument(currentArgument, type, argChar); 
        }     
      } catch (ArgsException e) {
        e.setErrorArgumentId(argChar);
        throw e;
      }
    }
  }
  
  private ArgsException.ErrorCode getArgsExceptionCode(ArgumentType type,
                               NoSuchElementException e){
    ArgsException.ErrorCode errCode = OK;         
    switch (type){
      case INTEGER:
        errCode = MISSING_INTEGER;
        break;
      case DOUBLE:
        errCode = MISSING_DOUBLE;
        break;
      case STRING:
      case STRINGARRAY:
        errCode = MISSING_STRING;
        break;         
      case MAPTYPE:
        errCode = MISSING_MAP;
        break;
      default:
        
    }
    return errCode;
  }
  private void setIntArgs(String parameter, char argChar) throws ArgsException {
    try {
      int intValue = Integer.parseInt(parameter);
      intArgs.put(argChar, parameter);
    } catch (NumberFormatException e) {
      throw new ArgsException(INVALID_INTEGER, parameter);
    }
  }

  private void setDoubleArgs(String parameter, char argChar) throws ArgsException {
    try {
      double doubleValue = Double.parseDouble(parameter);
      doubleArgs.put(argChar, parameter);
    } catch (NumberFormatException e) {
       throw new ArgsException(INVALID_DOUBLE, parameter);
    }
   
  }
  private void setStringArrayArgs(String parameter, char argChar) throws ArgsException {
    List<String> strings;
    
    strings = stringArrayArgs.get(argChar);
    if (strings == null) {
        strings = new ArrayList<String>();
    }
    strings.add(parameter);
    stringArrayArgs.put(argChar, strings);
  
  }
  
  private void setMapArgs(String parameter, char argChar) throws ArgsException {
    Map<String, String> map = mapArgs.get(argChar);
    String[] mapEntries = parameter.split(",");
    for (String entry : mapEntries) {
        String[] entryComponents = entry.split(":");
        if (entryComponents.length != 2)
          throw new ArgsException(MALFORMED_MAP);
        if (map  == null){
          map  = new HashMap<>();           
        }
        map.put(entryComponents[0], entryComponents[1]);
    }
    mapArgs.put(argChar, map);
   
  }

  private void setArgument(Iterator<String> currentArgument,
               ArgumentType type, char argChar) throws ArgsException {
    String parameter = null;
    try {
      parameter = currentArgument.next();
      switch (type){
        case INTEGER:
         setIntArgs(parameter, argChar);
          break;
        case DOUBLE:
          setDoubleArgs(parameter, argChar);
          break;
        case STRING:     
          stringArgs.put(argChar, parameter);
          break;
        case STRINGARRAY:
          setStringArrayArgs(parameter, argChar);
          break;
        case MAPTYPE:
          setMapArgs(parameter, argChar);
          break;
        default:
          throw new ArgsException(UNEXPECTED_ARGUMENT, argChar, null);
      }
      
    } catch (NoSuchElementException e) {
      throw new ArgsException(getArgsExceptionCode(type, e));
    }
  }
  
  public boolean has(char arg) {
    return (argsFound.get(arg) != null);
  }

  public int nextArgument() {
    return currentArgument.nextIndex();
  }

  public boolean getBoolean(char arg) {
    ArgumentType type = argsFound.get(arg);
    // System.out.println(arg + " " + argsFound.get(arg) + " ttt" + ArgumentType.BOOLEAN);
    if ((type != null) && (type == ArgumentType.BOOLEAN)) {
        return true;
    }
    return false;
  }

  public String getString(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.STRING)) {
        return stringArgs.get(arg);
    }
    return "";
  }
  

  public int getInt(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.INTEGER)) {
         return Integer.parseInt(intArgs.get(arg));
    }
    return 0;
  }

  public double getDouble(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.DOUBLE)) {
        return Double.parseDouble(doubleArgs.get(arg));
    }
    return 0;
  }

  public String[] getStringArray(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.STRINGARRAY)) {
        return stringArrayArgs.get(arg).toArray(new String[0]);
    }
    return new String[0];
  }

  public Map<String, String> getMap(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.MAPTYPE)) {
        return mapArgs.get(arg);
    }
    return new HashMap<>();
  }
}