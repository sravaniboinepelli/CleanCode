package com.sravani.argumentparser;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ArgumentParserTestMain {

  public static void main(String[] args) {
    Result result;    
    result = JUnitCore.runClasses(ArgsExceptionTest.class, ArgumentParserTest.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
    System.out.println(result.wasSuccessful());
  }
  
}