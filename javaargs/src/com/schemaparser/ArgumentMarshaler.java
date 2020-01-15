package com.schemaparser;

import java.util.Iterator;

public interface ArgumentMarshaler {
  void set(Iterator<String> currentArgument) throws ArgsException;
}
