/**
 * Copyright 2010 Aleksey Krivosheev (paradoxs.mail@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.bitcoin.client;

/**
 * @see <a href="http://www.bitcoin.org/wiki/doku.php?id=api">Bitcoin API</a>
 * @author paradoxs
 */
public class ServerInfo {
    private String version = "";
    private double balance = 0;
    private long blocks = 0;
    private int connections = 0;
    private boolean isGenerateCoins = false;
    private int usedCPUs = -1;
    private double difficulty = 0;
    private long HashesPerSecond = 0;

    public String getVersion() {
        return version;
    }    

    public void setVersion(String version) {
        this.version = version;
    }

    public double getBalance() {
        return balance;
    }    

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getBlocks() {
        return blocks;
    }

    public void setBlocks(long blocks) {
        this.blocks = blocks;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public boolean isIsGenerateCoins() {
        return isGenerateCoins;
    }

    public void setIsGenerateCoins(boolean isGenerateCoins) {
        this.isGenerateCoins = isGenerateCoins;
    }

    public int getUsedCPUs() {
        return usedCPUs;
    }

    public void setUsedCPUs(int usedCPUs) {
        this.usedCPUs = usedCPUs;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public long getHashesPerSecond() {
        return HashesPerSecond;
    }

    public void setHashesPerSecond(long HashesPerSecond) {
        this.HashesPerSecond = HashesPerSecond;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "version='" + version + '\'' +
                ", balance=" + balance +
                ", blocks=" + blocks +
                ", connections=" + connections +
                ", isGenerateCoins=" + isGenerateCoins +
                ", usedCPUs=" + usedCPUs +
                ", difficulty=" + difficulty +
                ", HashesPerSecond=" + HashesPerSecond +
                '}';
    }
}
