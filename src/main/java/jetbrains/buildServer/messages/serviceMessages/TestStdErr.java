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

import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class TestStdErr extends BaseTestMessage {
  // reflection
  TestStdErr() {
  }

  public TestStdErr(@NotNull final String name, @NotNull final String testErrOutput) {
    super(ServiceMessageTypes.TEST_STD_ERR, name, Collections.singletonMap("out", testErrOutput));
  }

  public String getStdErr() {
    return getAttributeValue("out");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitTestStdErr(this);
  }
}
