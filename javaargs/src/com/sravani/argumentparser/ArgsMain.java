package com.sravani.argumentparser;
import java.util.*;


public class ArgsMain {
  public static void main(String[] args) {
    try {
      
      // Args arg = new Args("l,p#,d*", args);
      // Args arg = new Args("f,s*,n#,a##,p[*],m&", args);
      ArgumentParser arg = new ArgumentParser("f,s*,n#,a##,p[*],m&", args);

      // boolean logging = true;
      boolean logging = arg.getBoolean('f');
      int port = arg.getInt('n');
      double portDouble = arg.getDouble('a');
      String directory = arg.getString('s');
      String[] strings = arg.getStringArray('p');
      Map<String, String> map = arg.getMap('m');

      executeApplication(logging, port, directory);
      System.out.println("portDouble:" + portDouble);
      for (String entry : strings) {
         System.out.println("strings" + entry);
      }
      Set<String> keys = map.keySet();
      // Set<String> values = map.valueSet();
      for(String key: keys){
        System.out.println(key);
        System.out.println(map.get(key));
      }
        
      
    } catch (ArgsException e) {
      System.out.printf("Argument error: %s\n", e.getMessage());
    }
  }

  private static void executeApplication(boolean logging, int port, String directory) {
    System.out.printf("logging is %s, port:%d, directory:%s\n",logging, port, directory);
  }
  
}