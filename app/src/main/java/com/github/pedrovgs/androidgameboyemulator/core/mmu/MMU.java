/*
 * Copyright (C) 2015 Pedro Vicente Gomez Sanchez.
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

package com.github.pedrovgs.androidgameboyemulator.core.mmu;

public class MMU {

  private final byte[] memory = new byte[65536];

  public byte readByte(int address) {
    return memory[address];
  }

  public int readWord(int address) {
    byte firstByte = readByte(address);
    int secondByte = readByte(address + 1) << 8;
    return firstByte + secondByte;
  }

  public void writeByte(int address, byte value) {
    memory[address] = value;
  }

  public void writeWord(int address, int value) {
    byte firstByte = (byte) (value & 0xFF);
    writeByte(address, firstByte);
    byte secondByte = (byte) (value >> 8);
    writeByte(address + 1, secondByte);
  }

  public void reset() {
    for (int i = 0; i < memory.length; i++) {
      memory[0] = 0;
    }
  }
}
