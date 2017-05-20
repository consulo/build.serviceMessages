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
 * Visitor which can be passed to {@link jetbrains.buildServer.messages.serviceMessages.ServiceMessage#visit(ServiceMessageVisitor)} method.
 * Depending on actual type of the service message corresponding method will be invoked.
 * If type of message is unknown {@link #visitServiceMessage(ServiceMessage)} will be called in the visitor.
 * <br/>
 * <br/>
 * see also {@link jetbrains.buildServer.messages.serviceMessages.DefaultServiceMessageVisitor}
 */
public interface ServiceMessageVisitor {
  void visitTestSuiteStarted(@NotNull TestSuiteStarted suiteStarted);

  void visitTestSuiteFinished(@NotNull TestSuiteFinished suiteFinished);

  void visitTestStarted(@NotNull TestStarted testStarted);

  void visitTestFinished(@NotNull TestFinished testFinished);

  void visitTestIgnored(@NotNull TestIgnored testIgnored);

  void visitTestStdOut(@NotNull TestStdOut testStdOut);

  void visitTestStdErr(@NotNull TestStdErr testStdErr);

  void visitTestFailed(@NotNull TestFailed testFailed);

  void visitPublishArtifacts(@NotNull PublishArtifacts publishArtifacts);

  void visitProgressMessage(@NotNull ProgressMessage progressMessage);

  void visitProgressStart(@NotNull ProgressStart progressStart);

  void visitProgressFinish(@NotNull ProgressFinish progressFinish);

  void visitBuildStatus(@NotNull BuildStatus buildStatus);

  void visitBuildNumber(@NotNull BuildNumber buildNumber);

  void visitBuildStatisticValue(@NotNull BuildStatisticValue buildStatsValue);

  void visitMessageWithStatus(@NotNull Message msg);

  void visitBlockOpened(@NotNull BlockOpened block);

  void visitBlockClosed(@NotNull BlockClosed block);

  void visitCompilationStarted(@NotNull CompilationStarted compileBlock);

  void visitCompilationFinished(@NotNull CompilationFinished compileBlock);

  void visitServiceMessage(@NotNull ServiceMessage msg);
}
