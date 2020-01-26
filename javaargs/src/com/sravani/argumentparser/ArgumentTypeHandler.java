package com.sravani.argumentparser;

import java.util.*;

import static com.sravani.argumentparser.ArgsException.ErrorCode.*;


public class ArgumentTypeHandler {
  
  private Map<Character, String> intArgs;
  private Map<Character, String> doubleArgs;
  private Map<Character, String> stringArgs;
  private Map<Character, List<String>> stringArrayArgs;
  private Map<Character, Map<String, String>> mapArgs;


  public ArgumentTypeHandler() throws ArgsException {

    intArgs = new HashMap<Character, String>();
    doubleArgs = new HashMap<Character, String>();
    stringArgs = new HashMap<Character, String>();
    stringArrayArgs = new  HashMap<Character, List<String>>();
    mapArgs = new HashMap<Character, Map<String, String>>();
  
  }

  public void setIntArgs(String parameter, char argChar) throws ArgsException {
    try {
      int intValue = Integer.parseInt(parameter);
      intArgs.put(argChar, parameter);
    } catch (NumberFormatException e) {
      throw new ArgsException(INVALID_INTEGER, parameter);
    }
  }

  public void setDoubleArgs(String parameter, char argChar) throws ArgsException {
    try {
      double doubleValue = Double.parseDouble(parameter);
      doubleArgs.put(argChar, parameter);
    } catch (NumberFormatException e) {
       throw new ArgsException(INVALID_DOUBLE, parameter);
    }
   
  }
  public void setStringArgs(String parameter, char argChar) throws ArgsException {
     stringArgs.put(argChar, parameter);
   
  }
  public void setStringArrayArgs(String parameter, char argChar) throws ArgsException {
    List<String> strings;
    
    strings = stringArrayArgs.get(argChar);
    if (strings == null) {
        strings = new ArrayList<String>();
    }
    strings.add(parameter);
    stringArrayArgs.put(argChar, strings);
  
  }
  
  public void setMapArgs(String parameter, char argChar) throws ArgsException {
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

  public String getString(char arg) {
    return stringArgs.get(arg);
  }
  
  public int getInt(char arg) {
    return Integer.parseInt(intArgs.get(arg));
  }

  public double getDouble(char arg) {
    return Double.parseDouble(doubleArgs.get(arg)); 
  }

  public String[] getStringArray(char arg) {
    return stringArrayArgs.get(arg).toArray(new String[0]);
  }

  public Map<String, String> getMap(char arg) {  
    return mapArgs.get(arg); 
  }
  
  
}