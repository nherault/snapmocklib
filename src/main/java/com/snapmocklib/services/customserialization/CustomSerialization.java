package com.snapmocklib.services.customserialization;

import java.util.List;

public interface CustomSerialization {

  List<String> getReturnTypes(Object result);

  Object recordValue(Object result);

  Object playbackValue(Object result);
}
