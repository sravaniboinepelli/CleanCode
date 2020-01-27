package com.sravani.argumentparser;

import java.util.*;
import java.util.Optional;   


import static com.sravani.argumentparser.ArgsException.ErrorCode.*;

import com.sravani.argumentparser.ArgsException;

  /**
   * This class is used to parse arguments given as per the schema
   * supplied. It is currently supporting int, double, string
   * string array and map type of data. See constructor comments
   * for details on schema and arguments format.
   */
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

  private ListIterator<String> currentArgument;
  private ArgumentTypeHandler argTypeHandler;
 /**
   * Parse arguments based on the schema supplied. 
   * @param schema schema should be defined with any normal character
   *               followed by special characters(# for integer,
   *               ## for double, * for string, [*] for string array,
   *               & for map type of data)
   *               example:"f,s*,n#,a##,p[*],m&"
   * @param args   arguments to be parsed. they should be specfied with a
   *               "-" prefix followed by letter mentioned in schema followed by 
   *               actual value.
   *               example: -f -s Bob -n 1 -a 3.2 -p e1 -p e2 -p e3
   *               repeated letters will be added to data for string array and map
   *               and overwritten by last mentioned argument for others.
   */

  public ArgumentParser(String schema, String[] args) throws ArgsException {

    validateInputsForPresence(schema, args);
    schemaMap = new HashMap<Character, ArgumentType>();
    argsFound= new HashMap<Character, ArgumentType>();
    
    argTypeHandler = new ArgumentTypeHandler();

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
        throw e;
    }  catch (Exception e) {
         System.out.println("Schema Parsing failed with " + e.getMessage() );
    }
  }

  private void validateSchemaElementId(char elementId) throws ArgsException {
    if (!Character.isLetter(elementId))
      throw new ArgsException(INVALID_ARGUMENT_NAME, elementId, null);
  }
  private void validateInputsForPresence(String schema, 
                                        String[] args) throws ArgsException {
    Optional<String> optionSchema= Optional.ofNullable(schema);
    Optional<String[]> optionArgs= Optional.ofNullable(args);

    if (optionSchema.isPresent()== false){
       throw new ArgsException(MISSING_SCHEMA, "null");
    }
    if (optionArgs.isPresent()== false){
      throw new ArgsException(MISSING_ARGUMENT_LIST, "null");
    }
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
    if (type == null) {
      throw new ArgsException(UNEXPECTED_ARGUMENT, argChar, null);
    } else {
      argsFound.put(argChar, type);
      try {
        if (type != ArgumentType.BOOLEAN){
          setArgument(currentArgument, type, argChar); 
        }     
      } catch (ArgsException e) {
        e.setErrorArgumentId(argChar);
        throw e;
      }
    }
  }
  
  private ArgsException.ErrorCode getArgsExceptionCode(ArgumentType type){
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

  private void setArgument(Iterator<String> currentArgument,
               ArgumentType type, char argChar) throws ArgsException {
    String parameter = null;
    try {
      parameter = currentArgument.next();
      switch (type){
        case INTEGER:
          argTypeHandler.setIntArgs(parameter, argChar);
          break;
        case DOUBLE:
          argTypeHandler.setDoubleArgs(parameter, argChar);
          break;
        case STRING:     
          argTypeHandler.setStringArgs(parameter, argChar);
          break;
        case STRINGARRAY:
          argTypeHandler.setStringArrayArgs(parameter, argChar);
          break;
        case MAPTYPE:
          argTypeHandler.setMapArgs(parameter, argChar);
          break;
        default:
          throw new ArgsException(UNEXPECTED_ARGUMENT, argChar, null);
      }
      
    } catch (NoSuchElementException e) {
      throw new ArgsException(getArgsExceptionCode(type));
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
    if ((type != null) && (type == ArgumentType.BOOLEAN)) {
        return true;
    }
    return false;
  }

  public String getString(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.STRING)) {
        return argTypeHandler.getString(arg);
    }
    return "";
  }
  

  public int getInt(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.INTEGER)) {
         return argTypeHandler.getInt(arg);
    }
    return 0;
  }

  public double getDouble(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.DOUBLE)) {
        return argTypeHandler.getDouble(arg);
    }
    return 0;
  }

  public String[] getStringArray(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.STRINGARRAY)) {
        return argTypeHandler.getStringArray(arg);
    }
    return new String[0];
  }

  public Map<String, String> getMap(char arg) {
    ArgumentType type = argsFound.get(arg);
    if ((type != null) && (type == ArgumentType.MAPTYPE)) {
        return argTypeHandler.getMap(arg);
    }
    return new HashMap<>();
  }
  
}