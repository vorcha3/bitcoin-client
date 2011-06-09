/**
 * Copyright 2010 Mats Henricson (mats@henricson.se)
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
 * Holds transaction information, returned from both #getTransaction(String)
 * and listTransactions(String, int). In the first case, the category may be
 * null.
 *
 * @author mats@henricson.se
 * @since 0.3.18
 */
public class TransactionInfo {
    private String category;     // Can be null, "generate", "send", "receive", or "move"
    private double amount;       // Can be positive or negative
    private double fee;          // Only for send, can be 0.0
    private long confirmations;  // only for generate/send/receive
    private String txId;         // only for generate/send/receive
    private String otherAccount; // only for move
    private String message;      // only for send, can be null
    private String to;           // only for send, can be null

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(long confirmations) {
        this.confirmations = confirmations;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public void setOtherAccount(String otherAccount) {
        this.otherAccount = otherAccount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "TransactionInfo{" +
                "category='" + category + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", confirmations=" + confirmations +
                ", txId='" + txId + '\'' +
                ", otherAccount='" + otherAccount + '\'' +
                ", message='" + message + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
