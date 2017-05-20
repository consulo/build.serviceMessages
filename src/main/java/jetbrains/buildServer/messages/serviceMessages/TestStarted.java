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

import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestStarted extends BaseTestMessage {
  // reflection
  TestStarted() {
  }

  public TestStarted(@NotNull final String name, boolean captureStdOutput, @Nullable String locationHint) {
    super(ServiceMessageTypes.TEST_STARTED, name, createMap(captureStdOutput, locationHint));
  }

  private static Map<String, String> createMap(final boolean captureStdOutput, @Nullable final String locationHint) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    if (captureStdOutput) res.put("captureStandardOutput", "true");
    if (locationHint != null) res.put("locationHint", locationHint);
    return res;
  }

  /**
   * If this parameter value is true all standard output messages received
   * inside the test opening/closing service messages are treated as test output and all standard
   * errors are treated as test error output.
   * @return the "captureStandardOutput" parameter value (false by default)
   */
  public boolean isCaptureStandardOutput() {
    final String capture = getAttributeValue("captureStandardOutput");
    return capture != null && capture.equalsIgnoreCase("true");
  }

  /** This parameter allows to proved a hint for IDE when opening this test. Sometimes it is not enough to
   * provide only the test name for this purpose
   * @return ideHint for opening this test in IDE, or null if there is no hint
   * */
  @Nullable
  public String getLocationHint() {
    return getAttributeValue("locationHint");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitTestStarted(this);
  }
}
