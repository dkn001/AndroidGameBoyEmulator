/*
 * Copyright (C) 2015 Pedro Vicente Gomez Sanchez.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.github.pedrovgs.androidgameboyemulator.core.processor;

import com.github.pedrovgs.androidgameboyemulator.core.mmu.MMU;
import com.github.pedrovgs.androidgameboyemulator.core.processor.isa.Instruction;

public class GBZ80 {

  public static final int ZERO_FLAG_Z = 0x80;
  public static final int SUBSTRACT_FLAG_N = 0x40;
  public static final int HALF_CARRY_FLAG_H = 0x20;
  public static final int CARRY_FLAG_C = 0x10;
  private static final int INITIAL_PROGRAM_COUNTER_VALUE = 0x100;
  private static final int INITIAL_STACK_POINTER_VALUE = 0xFFFE;

  private final Clock clock;
  private final InstructionsPool instructionsPool;

  private byte[] registers;
  private int programCounter;
  private int stackPointer;
  private int lastInstructionExecutionTime;

  public GBZ80() {
    this.clock = new Clock();
    this.instructionsPool = new InstructionsPool();
    this.registers = new byte[8];
    this.programCounter = INITIAL_PROGRAM_COUNTER_VALUE;
    this.stackPointer = INITIAL_STACK_POINTER_VALUE;
  }

  public byte get8BitRegisterValue(Register register) {
    validate8BitRegister(register);

    return registers[register.getRegisterIndex()];
  }

  public void set8BitRegisterValue(Register register, byte value) {
    validate8BitRegister(register);

    registers[register.getRegisterIndex()] = value;
  }

  public int get16BitRegisterValue(Register register) {
    validate16BitRegister(register);

    int registerIndex = register.getRegisterIndex();
    int firstPart = registers[registerIndex] << 8 & 0xff00;
    int secondPart = registers[registerIndex + 1] & 0x00ff;
    return firstPart + secondPart;
  }

  public void set16BitRegisterValue(Register register, int value) {
    validate16BitRegister(register);

    byte firstRegisterValue = (byte) (value & 0xff00);
    byte secondRegisterValue = (byte) (value & 0x00ff);
    registers[register.getRegisterIndex()] = firstRegisterValue;
    registers[register.getRegisterIndex() + 1] = secondRegisterValue;
  }

  public void execute(int rawInstruction, MMU mmu) {
    Instruction instruction =
        instructionsPool.getInstructionFromRawValue(rawInstruction, this, mmu);
    instruction.execute();
  }

  public void updateClock() {
    clock.incrementClockM(lastInstructionExecutionTime);
  }

  public int getProgramCounter() {
    return programCounter;
  }

  public void incrementProgramCounter() {
    this.programCounter++;
  }

  public void incrementProgramCounterTwice() {
    this.programCounter = programCounter + 2;
  }

  public void setProgramCounter(int programCounter) {
    this.programCounter = programCounter;
  }

  public int getStackPointer() {
    return stackPointer;
  }

  public void setLastInstructionExecutionTime(int lastInstructionExecutionTime) {
    this.lastInstructionExecutionTime = lastInstructionExecutionTime;
  }

  public int getLastInstructionExecutionTime() {
    return lastInstructionExecutionTime;
  }

  private void validate8BitRegister(Register register) {
    int registerOrdinal = register.ordinal();
    int first16BitRegister = registers.length;
    if (registerOrdinal >= first16BitRegister) {
      throw new IllegalArgumentException(
          "You can't access to a 8 bit register with the register key: " + register);
    }
  }

  private void validate16BitRegister(Register register) {
    int registerOrdinal = register.ordinal();
    int last8BitRegisterOrdinal = registers.length - 1;
    if (registerOrdinal <= last8BitRegisterOrdinal) {
      throw new IllegalArgumentException(
          "You can't access to a 16 bit register with the register key: " + register);
    }
  }
}
