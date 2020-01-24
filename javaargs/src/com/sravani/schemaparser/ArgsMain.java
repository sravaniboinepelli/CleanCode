package com.sravani.schemaparser;

public class ArgsMain {
  public static void main(String[] args) {
    try {
      System.out.println(args[0]);
      Args arg = new Args("l,p#,d*", args);
      // Args arg = new Args("p#,d*", args);
      // boolean logging = true;
      boolean logging = arg.getBoolean('l');
      int port = arg.getInt('p');
      String directory = arg.getString('d');
      executeApplication(logging, port, directory);
    } catch (ArgsException e) {
      System.out.printf("Argument error: %s\n", e.errorMessage());
    }
  }

  private static void executeApplication(boolean logging, int port, String directory) {
    System.out.printf("logging is %s, port:%d, directory:%s\n",logging, port, directory);
  }
}