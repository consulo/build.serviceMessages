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

public abstract class DefaultServiceMessageVisitor implements ServiceMessageVisitor {
  public void visitTestSuiteStarted(@NotNull final TestSuiteStarted suiteStarted) {
  }

  public void visitTestSuiteFinished(@NotNull final TestSuiteFinished suiteFinished) {
  }

  public void visitTestStarted(@NotNull final TestStarted testStarted) {
  }

  public void visitTestFinished(@NotNull final TestFinished testFinished) {
  }

  public void visitTestIgnored(@NotNull final TestIgnored testIgnored) {
  }

  public void visitTestStdOut(@NotNull final TestStdOut testStdOut) {
  }

  public void visitTestStdErr(@NotNull final TestStdErr testStdErr) {
  }

  public void visitTestFailed(@NotNull final TestFailed testFailed) {
  }

  public void visitPublishArtifacts(@NotNull final PublishArtifacts publishArtifacts) {
  }

  public void visitProgressMessage(@NotNull final ProgressMessage progressMessage) {
  }

  public void visitProgressStart(@NotNull final ProgressStart progressStart) {
  }

  public void visitProgressFinish(@NotNull final ProgressFinish progressFinish) {
  }

  public void visitBuildStatus(@NotNull final BuildStatus buildStatus) {
  }

  public void visitBuildNumber(@NotNull final BuildNumber buildNumber) {
  }

  public void visitBuildStatisticValue(@NotNull final BuildStatisticValue buildStatsValue) {
  }

  public void visitMessageWithStatus(@NotNull final Message msg) {
  }

  public void visitBlockOpened(@NotNull final BlockOpened block) {
  }

  public void visitBlockClosed(@NotNull final BlockClosed block) {
  }

  public void visitCompilationStarted(@NotNull final CompilationStarted compileBlock) {
  }

  public void visitCompilationFinished(@NotNull final CompilationFinished compileBlock) {
  }

  public void visitServiceMessage(@NotNull final ServiceMessage msg) {
  }
}
