/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.vm.operations;

import org.hyperledger.besu.ethereum.vm.Code;
import org.hyperledger.besu.ethereum.vm.EVM;
import org.hyperledger.besu.ethereum.vm.GasCalculator;
import org.hyperledger.besu.ethereum.vm.MessageFrame;

import org.apache.tuweni.units.bigints.UInt256;

public class JumpSubOperation extends AbstractFixedCostOperation {

  public JumpSubOperation(final GasCalculator gasCalculator) {
    super(0x5e, "JUMPSUB", 1, 0, true, 1, gasCalculator, gasCalculator.getHighTierGasCost());
  }

  @Override
  public OperationResult execute(final MessageFrame frame, final EVM evm) {
    final Code code = frame.getCode();

    if (frame.isReturnStackFull()) {
      return OVERFLOW_RESPONSE;
    }

    final UInt256 location = UInt256.fromBytes(frame.popStackItem());
    if (!code.isValidJumpSubDestination(evm, frame, location)) {
      return INVALID_JUMP_DESTINATION;
    }

    frame.pushReturnStackItem(frame.getPC() + 1);
    frame.setPC(location.intValue() + 1);

    return successResponse;
  }
}
