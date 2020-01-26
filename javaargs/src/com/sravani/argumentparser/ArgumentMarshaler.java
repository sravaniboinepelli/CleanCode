package com.sravani.argumentparser;

import java.util.Iterator;

public interface ArgumentMarshaler {
  void set(Iterator<String> currentArgument) throws ArgsException;
}
