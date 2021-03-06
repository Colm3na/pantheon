/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.vm.ehalt;

import tech.pegasys.pantheon.ethereum.vm.EVM;
import tech.pegasys.pantheon.ethereum.vm.ExceptionalHaltReason;
import tech.pegasys.pantheon.ethereum.vm.MessageFrame;
import tech.pegasys.pantheon.ethereum.vm.Operation;

import java.util.EnumSet;
import java.util.Optional;

public class StackOverflowExceptionalHaltPredicate implements ExceptionalHaltPredicate {
  public static final int MAX_STACK_SIZE = 1024;

  @Override
  public Optional<ExceptionalHaltReason> exceptionalHaltCondition(
      final MessageFrame frame, final EnumSet<ExceptionalHaltReason> prevReasons, final EVM evm) {
    final Operation op = frame.getCurrentOperation();
    final boolean condition = frame.stackSize() + op.getStackSizeChange() > MAX_STACK_SIZE;
    return condition ? Optional.of(ExceptionalHaltReason.TOO_MANY_STACK_ITEMS) : Optional.empty();
  }
}
