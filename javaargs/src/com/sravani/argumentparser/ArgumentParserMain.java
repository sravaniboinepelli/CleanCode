package com.sravani.argumentparser;
import java.util.*;

public class ArgumentParserMain {
  public static void main(String[] args) {
    try {
      ArgumentParser arg;

      if (args.length == 0){
         String argument = "-f -s Bob -n 1 -a 3.2 -p e1 -p e2 -p e3";
         String [] arguments = argument.split(" ");
         arg = new ArgumentParser("f,s*,n#,a##,p[*],m&",
                                                 arguments);
      } else {
         // ArgumentParser arg = new ArgumentParser("l,p#,d*", args);
         // ArgumentParser arg = new ArgumentParser("f,s*,n#,a##,p[*],m&", args);
         arg = new ArgumentParser("f,s*,n#,a##,p[*],m&", args); 
      }
      boolean logging = arg.getBoolean('f');
      int port = arg.getInt('n');
      double portDouble = arg.getDouble('a');
      String directory = arg.getString('s');
      String[] strings = arg.getStringArray('p');
      Map<String, String> map = arg.getMap('m');

      System.out.printf("logging is %s, port:%d, directory:%s\n",logging, port, directory);
      System.out.println("portDouble:" + portDouble);
      System.out.println("string array data:");
      for (String entry : strings) {
         System.out.print(entry + " ");
      }
      System.out.println("");
      System.out.println("map data:");
      Set<String> keys = map.keySet();
      for(String key: keys){
        System.out.print(key + ":" + map.get(key));
      }
      System.out.println("");
      
    } catch (ArgsException e) {
      System.out.printf("Argument error: %s\n", e.getMessage());
    }
  }

 
  
}