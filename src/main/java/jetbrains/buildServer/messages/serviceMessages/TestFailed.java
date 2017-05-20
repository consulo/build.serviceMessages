/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.messages.serviceMessages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestFailed extends BaseTestMessage {
  // reflection
  TestFailed() {
  }

  public TestFailed(@NotNull final String name, @Nullable final Throwable exception) {
    super(ServiceMessageTypes.TEST_FAILED, name, createMap(exception));
  }

  private static Map<String, String> createMap(final Throwable exception) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    if (exception == null) {
      return res;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    exception.printStackTrace(new PrintStream(out));
    String stacktrace = out.toString();

    res.put("message", exception.getMessage());
    res.put("details", stacktrace);

    return res;
  }

  public boolean isComparisonFailure() {
    return "comparisonFailure".equals(getAttributeValue("type"));
  }

  public String getFailureMessage() {
    return getAttributeValue("message");
  }

  public String getStacktrace() {
    return getAttributeValue("details");
  }

  public String getExpected() {
    return getAttributeValue("expected");
  }

  public String getActual() {
    return getAttributeValue("actual");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitTestFailed(this);
  }
}
