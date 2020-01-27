# Cleaned up version of javaargs code 
  This code parses the command line arguments based on the schema supplied.
## Features implemented
  Following features are iplemented.
  1. Parse schema and store the letter to data type mapping.
  2. Store value mentioned in command line as per the data type mapping provided by schema for the character given in the command line.
  3. boolean, int, double, String, StringArray and map (key value pairs) are supported.
  4. Schema format for different data types:
         boolean: no special character after character ex:f
         integer: # after character ex:n#
         double:  ## after character ex:d##
         string: * after character ex:s*
         string array: [*] after character ex:a[*]
         map: & after character ex:m&
         example  schema string to support for all the above: "f,s*,n#,a##,p[*],m&" 
  5. command line arguments format:
      -"charactermentioned in schema" value. example: -f -n 1 -a 3.6 -s test1 -p str1 - str2 -m key1:value1
  6. repeat of character stores value associated with last occurence of character for int, double, string 
  7. repeat of character  adds all the values for map and string array.

## instructions on requirements, execution and unit testing
### Requirements
    Requires junit-4 and hamcrest-core and ant for compilation.
### execution
  1. Go to the root directory(contains build.xml)
  2. run 'ant compile'
  3. run 'ant jar'
  4. run 'java -cp build/jar/args.jar com.sravani.argumentparser.ArgumentParserMain -f -s Bob -n         1 -a 3.2 -p e1 -p e2 -p e3'
  5. 
### Unit Testing
  1. For Parser testing run 'java -cp "lib/junit-4.13.jar:lib/hamcrest-core-1.3.jar:build/jar/args.jar" ./test/com/sravani/argumentparser/ArgumentParserTest.java'
  2. For Exception checking run 'java -cp "lib/junit-4.13.jar:lib/hamcrest-core-1.3.jar:build/jar/args.jar" ./test/com/sravani/argumentparser/ArgsExceptionTest.java'
    
## characteristics of clean code implemented
  1. Simple, readble code, changed code to restucture in to one main parser class that does  parsing of both shema  and command line arguments and makes use of data type handler class. This class does type validations for int, double.  It also does type format validation for map and stores the value in its local map. and provides getter and setters.
  2. Small functions, with fewer arguments doing what the name suggests.
  3. clean Comments: code explains itself, followed java doc style for classes to explain the class functionality and public functions specify input parameter formats. Usage instructions in Readme.
  4. clean tests: test readability over performance, one test case is not dependent on the other, one functionality per test case, self validating test case.
  5. Error handling: using exceptions over error codes, validation of inputs, try catch usage
  6. Optional class usage instead of nullpointer exception handling.??
  7. Follows Visual design code pronciples. 


