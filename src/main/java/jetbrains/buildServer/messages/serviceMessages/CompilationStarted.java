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

/**
 * Represents beginning of the compilation block
 * @since 6.0
 */
public class CompilationStarted extends BaseCompilationBlockMessage {
  // reflection
  CompilationStarted() {
  }

  /**
   * Creates new compilationStarted service message.
   * @param compilerName name of the compiler to use in the build log (javac, jikes, groovyc and so on)
   */
  public CompilationStarted(@NotNull String compilerName) {
    super(ServiceMessageTypes.COMPILATION_STARTED, compilerName);
  }

  @Override
  public void visit(@NotNull final ServiceMessageVisitor visitor) {
    visitor.visitCompilationStarted(this);
  }
}
