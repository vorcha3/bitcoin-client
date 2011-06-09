/**
 * Copyright 2011 
 *    Kara Rawson - Bat Country Entertainment (krawson@batcountryentertainment.com)
 *    Aleksey Krivosheev (paradoxs.mail@gmail.com)
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

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.bitcoin.client.exceptions.BitcoinClientException;
import org.bitcoin.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * A Java API for accessing a Bitcoin server.
 *
 * PLEASE NOTE: It doesn't use https for the communication, just http, but access to the Bitcoin server is
 * only allowed from localhost, so it shouldn't really matter.
 *
 * TODO: The API should really use BigDecimal instead of doubles.
 *
 * @see <a href="http://www.bitcoin.org/wiki/doku.php?id=api">Bitcoin API</a>
 * @author paradoxs
 * @author mats@henricson.se
 */
public class BitcoinClient {
    private HttpSession session = null;

    /**
     * Creates a BitcoinClient
     *
     * @param host the host machine where there's an executing bitcoind server
     * @param login the username to access the bitcoind server
     * @param password the password to access the bitcoind server
     * @param port the port number to the bitcoind server
     */
    public BitcoinClient(String host, String login, String password, int port) {
        try {
            Credentials credentials = new UsernamePasswordCredentials(login, password);
            URI uri = new URI("http", null, host, port, null, null, null);
            session = new HttpSession(uri, credentials);
        } catch (URISyntaxException e) {
            throw new BitcoinClientException("This host probably doesn't have correct syntax: " + host, e);
        }
    }

    /**
     * Creates a BitcoinClient with the default 8332 port number
     *
     * @param host the host machine where there's an executing bitcoind server
     * @param login the username to access the bitcoind server
     * @param password the password to access the bitcoind server
     */
    public BitcoinClient(String host, String login, String password) {
        this(host, login, password, 8332);
    }

    /**
     * Returns the list of addresses with the given label
     *
     * @param label Name of label
     * @return list of addresses for that label
     * @deprecated Use #getAddressesByAccount(String) instead
     */
    @Deprecated
    public List<String> getAddressesByLabel(String label) {
        try {
            JSONArray parameters = new JSONArray().put(label);
            JSONObject request = createRequest("getaddressesbylabel", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = (JSONArray)response.get("result");
            int size = result.length();

            List<String> list = new ArrayList<String>();

            for (int i = 0; i < size; i++) {
                list.add(result.getString(i));
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Got incorrect JSON for this label: " + label, e);
        }
    }

    /**
     * Returns the list of addresses for the given account
     *
     * @param account name of account, if null or empty it means the default account
     * @return list of addresses for the given account
     * @since 0.3.18
     */
    public List<String> getAddressesByAccount(String account) {
        if (account == null) {
            account = "";      // The default account
        }

        try {
            JSONArray parameters = new JSONArray().put(account);
            JSONObject request = createRequest("getaddressesbyaccount", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = (JSONArray)response.get("result");
            int size = result.length();

            List<String> list = new ArrayList<String>();

            for (int i = 0; i < size; i++) {
                list.add(result.getString(i));
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Got incorrect JSON for this account: " + account, e);
        }
    }

    /**
     * Returns the available balance for the default account
     *
     * @return the balance for the default account
     */
    public double getBalance() {
        try {
            JSONObject request = createRequest("getbalance");
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting balance", e);
        }
    }

    /**
     * Returns the available balance for an account
     *
     * @param account the name of the account, or the default account if null or empty
     * @return the balance
     * @since 0.3.18
     */
    public double getBalance(String account) {
        if (account == null) {
            account = "";      // The default account
        }

        try {
            JSONArray parameters = new JSONArray().put(account);
            JSONObject request = createRequest("getbalance", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting balance", e);
        }
    }


    /**
     * Returns the number of blocks in the longest block chain
     * 
     * @return the number of blocks
     */
    public int getBlockCount() {
        try {
            JSONObject request = createRequest("getblockcount");
            JSONObject response = session.sendAndReceive(request);

            return  response.getInt("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting block count", e);
        }
    }

    /**
     * Returns the block number of the latest block in the longest block chain
     *
     * @return the block number
     */
    public int getBlockNumber() {
        try {
            JSONObject request = createRequest("getblocknumber");
            JSONObject response = session.sendAndReceive(request);

            return response.getInt("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the block number", e);
        }
    }

    /**
     * Returns the number of connections to other nodes
     *
     * @return the number of connections
     */
    public int getConnectionCount() {
        try {
            JSONObject request = createRequest("getconnectioncount");
            JSONObject response = session.sendAndReceive(request);

            return response.getInt("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the number of connections", e);
        }
    }

    /**
     * Returns the current number of hashes computed per second
     *
     * @return the number of computed hashes per second
     * @since 0.3.18
     */
    public long getHashesPerSecond() {
        try {
            JSONObject request = createRequest("gethashespersec");
            JSONObject response = session.sendAndReceive(request);

            return response.getLong("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the number of calculated hashes per second", e);
        }
    }

    /**
     * Returns the proof-of-work difficulty as a multiple of the minimum difficulty
     *
     * @return the current difficulty
     */
    public double getDifficulty() {
        try {
            JSONObject request = createRequest("getdifficulty");
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the difficulty", e);
        }
    }

    /**
     * Returns boolean true if server is trying to generate bitcoins, false otherwise
     *
     * @return true if server is trying to generate bitcoins, false otherwise
     */
    public boolean getGenerate() {
        try {
            JSONObject request = createRequest("getgenerate");
            JSONObject response = session.sendAndReceive(request);

            return response.getBoolean("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting whether the server is generating coins or not", e);
        }
    }

    /**
     * Turn on/off coins generation
     *
     * @param isGenerate on - true, off - false
     * @param processorsCount proccesorsCount processors, -1 is unlimited
     */
    public void setGenerate(boolean isGenerate, int processorsCount) {
        try {
            JSONArray parameters = new JSONArray().put(isGenerate).put(processorsCount);
            JSONObject request = createRequest("setgenerate", parameters);
            session.sendAndReceive(request);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when setting whether the server is generating coins or not", e);
        }
    }

    /**
     * Return server information, about balance, connections, blocks...etc.
     *
     * @return server information
     */
    public ServerInfo getServerInfo() {
        try {
            JSONObject request = createRequest("getinfo");
            JSONObject response = session.sendAndReceive(request);
            JSONObject result = (JSONObject) response.get("result");

            ServerInfo info = new ServerInfo();
            info.setBalance        (result.getDouble ("balance"));
            info.setBlocks         (result.getLong   ("blocks"));
            info.setConnections    (result.getInt    ("connections"));
            info.setDifficulty     (result.getDouble ("difficulty"));
            info.setHashesPerSecond(result.getLong   ("hashespersec"));
            info.setIsGenerateCoins(result.getBoolean("generate"));
            info.setUsedCPUs       (result.getInt    ("genproclimit"));
            info.setVersion        (result.getString ("version"));            

            return info;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the server info", e);
        }
    }

    /**
     * Returns the label associated with the given address
     *
     * @param address the address we want the label for
     * @return the label associated with a certain address
     * @deprecated Use #getAccount(String) instead
     */
    @Deprecated
    public String getLabel(String address) {
        try {
            JSONArray parameters = new JSONArray().put(address);
            JSONObject request = createRequest("getlabel", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the label associated with this address: " + address, e);
        }
    }

    /**
     * Returns the account associated with the given address
     *
     * @param address the address for which we want to lookup the account
     * @return the account associated with a certain address
     * @since 0.3.18
     */
    public String getAccount(String address) {
        try {
            JSONArray parameters = new JSONArray().put(address);
            JSONObject request = createRequest("getaccount", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the account associated with this address: " + address, e);
        }
    }

    /**
     * Sets the label associated with the given address
     *
     * @param address the address we want to set a label for
     * @param label if null then label remove
     * @deprecated Use #setAccountForAddress(String, String) instead
     */
    @Deprecated
    public void setLabelForAddress(String address, String label) {
        try {
            JSONArray parameters = new JSONArray().put(address).put(label);
            JSONObject request = createRequest("setlabel", parameters);
            session.sendAndReceive(request);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when setting the label associated with a given address", e);
        }
    }

    /**
     * Sets the account associated with the given address, or removes the address if the account is null
     *
     * @param address the address to be added or removed
     * @param account if null then address is removed
     * @since 0.3.18
     */
    public void setAccountForAddress(String address, String account) {
        try {
            JSONArray parameters = new JSONArray().put(address).put(account);
            JSONObject request = createRequest("setaccount", parameters);
            session.sendAndReceive(request);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when setting the account associated with a given address", e);
        }
    }

    /**
     * Returns a new bitcoin address for receiving payments. If [label] is
     * specified (recommended), it is added to the address book so payments
     * received with the address will be labeled.
     *
     * @param label if not null(recommended), address will be labeled
     * @return the new bitcoin address for receiving payments
     * @deprecated Use #getAccountAddress(String) instead
     */
    @Deprecated
    public String getNewAddress(String label) {
        try {
            JSONArray parameters = new JSONArray().put(label);
            JSONObject request = createRequest("getnewaddress", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the new bitcoin address for receiving payments", e);
        }
    }

    /**
     * Returns a new bitcoin address for receiving payments. If account is null
     * or an empty string, then that means the default account.
     *
     * @param account the name of the account for which we want a new receiving address.
     *                The address can be null or the empty string, which means the default account.
     * @return the new bitcoin address for receiving payments to this account
     * @since 0.3.18
     */
    public String getAccountAddress(String account) {
        if (account == null) {
            account = "";      // The default account
        }

        try {
            JSONArray parameters = new JSONArray().put(account);
            JSONObject request = createRequest("getaccountaddress", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the new bitcoin address for receiving payments", e);
        }
    }

    /**
     * Returns the total amount received by bitcoinaddress in transactions
     *
     * @param address the address we want to know the received amount for
     * @param minimumConfirmations the minimum number of confirmations for a transaction to count
     * @return total amount received
     */
    public double getReceivedByAddress(String address, long minimumConfirmations) {
        try {
            JSONArray parameters = new JSONArray().put(address).put(minimumConfirmations);
            JSONObject request = createRequest("getreceivedbyaddress", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the total amount received by bitcoinaddress", e);
        }
    }

    /**
     * Returns the total amount received by addresses with label in transactions
     *
     * @param label the label we want to know the received amount for
     * @param minimumConfirmations the minimum number of confirmations for a transaction to count
     * @return total amount received
     * @deprecated Use #getReceivedByAccount(String, long) instead
     */
    @Deprecated
    public double getReceivedByLabel(String label, long minimumConfirmations) {
        try {
            JSONArray parameters = new JSONArray().put(label).put(minimumConfirmations);
            JSONObject request = createRequest("getreceivedbylabel", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the total amount received with label: " + label, e);
        }
    }

    /**
     * Returns the total amount received by addresses for account in transactions
     *
     * @param account the name of the account, or the default account if null or empty string
     * @param minimumConfirmations minimum number of confirmations for the transaction to be included in the received amount
     * @return total amount received for this account
     * @since 0.3.18
     */
    public double getReceivedByAccount(String account, long minimumConfirmations) {
        try {
            JSONArray parameters = new JSONArray().put(account).put(minimumConfirmations);
            JSONObject request = createRequest("getreceivedbyaccount", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getDouble("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the total amount received for account: " + account, e);
        }
    }

    /**
     * Return help for a command
     *
     * @param command the command
     * @return the help text
     */
    public String help(String command) {
        try {
            JSONArray parameters = new JSONArray().put(command);
            JSONObject request = createRequest("help", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting help for a command", e);
        }
    }

    /**
     * Info about all received transactions by address
     *
     * @param minimumConfirmations is the minimum number of confirmations before payments are included
     * @param includeEmpty whether to include addresses that haven't received any payments
     * @return info about all received transactions by address
     */
    public List<AddressInfo> listReceivedByAddress(long minimumConfirmations, boolean includeEmpty) {
        try {
            JSONArray parameters = new JSONArray().put(minimumConfirmations).put(includeEmpty);
            JSONObject request = createRequest("listreceivedbyaddress", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = response.getJSONArray("result");
            int size = result.length();
            List<AddressInfo> list = new ArrayList<AddressInfo>();

            for (int i = 0; i < size; i++) {
                AddressInfo info = new AddressInfo();
                JSONObject jObject = result.getJSONObject(i);
                info.setAddress(jObject.getString("address"));
                info.setAccount(jObject.getString("account"));
                info.setAmount(jObject.getDouble("amount"));
                info.setConfirmations(jObject.getLong("confirmations"));
                list.add(info);
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting info about all received transactions by address", e);
        }
    }

    /**
     * Info about all received transactions by label
     *
     * @param minimumConfirmations is the minimum number of confirmations before payments are included
     * @param includeEmpty whether to include labels that haven't received any payments
     * @return info about all received transactions by label
     * @deprecated Use #listReceivedByAccount(long, boolean) instead
     */
    @Deprecated
    public List<LabelInfo> listReceivedByLabel(long minimumConfirmations, boolean includeEmpty) {
        try {
            JSONArray parameters = new JSONArray().put(minimumConfirmations).put(includeEmpty);
            JSONObject request = createRequest("listreceivedbylabel", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = response.getJSONArray("result");
            int size = result.length();

            List<LabelInfo> list = new ArrayList<LabelInfo>(size);

            for (int i = 0; i < size; i++) {
                LabelInfo info = new LabelInfo();
                JSONObject jObject = result.getJSONObject(i);
                info.setLabel        (jObject.getString("label"));
                info.setAmount       (jObject.getDouble("amount"));
                info.setConfirmations(jObject.getLong("confirmations"));
                list.add(info);
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the received amount by label", e);
        }
    }

    /**
     * Info about all received transactions by account
     *
     * @param minimumConfirmations is the minimum number of confirmations before payments are included
     * @param includeEmpty whether to include accounts that haven't received any payments
     * @return info about the received amount by account
     * @since 0.3.18
     */
    public List<AccountInfo> listReceivedByAccount(long minimumConfirmations, boolean includeEmpty) {
        try {
            JSONArray parameters = new JSONArray().put(minimumConfirmations).put(includeEmpty);
            JSONObject request = createRequest("listreceivedbyaccount", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = response.getJSONArray("result");
            int size = result.length();

            List<AccountInfo> list = new ArrayList<AccountInfo>(size);

            for (int i = 0; i < size; i++) {
                AccountInfo info = new AccountInfo();
                JSONObject jObject = result.getJSONObject(i);
                info.setAccount      (jObject.getString("account"));
                info.setAmount       (jObject.getDouble("amount"));
                info.setConfirmations(jObject.getLong("confirmations"));
                list.add(info);
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting the received amount by account", e);
        }
    }

    /**
     * Returns a list of at most <code>count</code> number of the last transactions for an account
     *
     * @param account the account related to the transactions, the default account if null or empty
     * @param count the maximum number of transactions returned, must be > 0
     * @return a list of at most <code>count</code> number of the last transactions for an account
     * @since 0.3.18
     */
    public List<TransactionInfo> listTransactions(String account, int count) {
        if (account == null) {
            account = "";
        }

        if (count <= 0) {
            throw new BitcoinClientException("count must be > 0");
        }

        try {
            JSONArray parameters = new JSONArray().put(account).put(count);
            JSONObject request = createRequest("listtransactions", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONArray result = response.getJSONArray("result");
            int size = result.length();

            List<TransactionInfo> list = new ArrayList<TransactionInfo>(size);

            for (int i = 0; i < size; i++) {
                JSONObject jObject = result.getJSONObject(i);
                TransactionInfo info = parseTransactionInfoFromJson(jObject);
                list.add(info);
            }

            return list;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting transactions for account: " + account, e);
        }
    }

    /**
     * Returns transaction information for a specific transaction ID
     *
     * @param txId the transaction ID
     * @return information about the transaction with that ID
     * @since 0.3.18
     */
    public TransactionInfo getTransaction(String txId) {
        try {
            JSONArray parameters = new JSONArray().put(txId);
            JSONObject request = createRequest("gettransaction", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONObject result = (JSONObject) response.get("result");

            return parseTransactionInfoFromJson(result);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting transaction info for this id: " + txId, e);
        }
    }

    private TransactionInfo parseTransactionInfoFromJson(JSONObject jObject) throws JSONException {
        TransactionInfo info = new TransactionInfo();

        info.setAmount(jObject.getDouble("amount"));

        if (!jObject.isNull("category")) {
            info.setCategory(jObject.getString("category"));
        }

        if (!jObject.isNull("fee")) {
            info.setFee(jObject.getDouble("fee"));
        }

        if (!jObject.isNull("message")) {
            info.setMessage(jObject.getString("message"));
        }

        if (!jObject.isNull("to")) {
            info.setTo(jObject.getString("to"));
        }

        if (!jObject.isNull("confirmations")) {
            info.setConfirmations(jObject.getLong("confirmations"));
        }

        if (!jObject.isNull("txid")) {
            info.setTxId(jObject.getString("txid"));
        }

        if (!jObject.isNull("otheraccount")) {
            info.setOtherAccount(jObject.getString("otheraccount"));
        }

        return info;
    }

    /**
     * Returns a list of at most 10 of the last transactions for an account
     *
     * @param account the account related to the transactions
     * @return a list of at most 10 of the last transactions for an account
     * @since 0.3.18
     */
    public List<TransactionInfo> listTransactions(String account) {
        return listTransactions(account, 10);
    }

    /**
     * Returns a list of at most 10 of the last transactions for the default account
     *
     * @return a list of at most 10 of the last transactions for the default account
     * @since 0.3.18
     */
    public List<TransactionInfo> listTransactions() {
        return listTransactions("", 10);
    }

    /**
     * Return work information.
     *
     * @return work information
     */
    public WorkInfo getWork() {
        try {
            JSONObject request = createRequest("getwork");
            JSONObject response = session.sendAndReceive(request);
            JSONObject result = (JSONObject) response.get("result");

            WorkInfo info = new WorkInfo();
            info.setMidstate(result.getString("midstate"));
            info.setData(result.getString("data"));
            info.setHash1(result.getString("hash1"));
            info.setTarget(result.getString("target"));

            return info;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when getting work info", e);
        }
    }

    /**
     * Tries to solve the block and returns true if it was successful
     *
     * @return true if the block was solved, false otherwise
     */
    public boolean getWork(String block) {
        try {
            JSONArray parameters = new JSONArray().put(block);
            JSONObject request = createRequest("getwork", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getBoolean("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when trying to solve a block with getwork", e);
        }
    }

    /**
     * Sends amount from the server's available balance to bitcoinAddress.
     *
     * @param bitcoinAddress the bitcoinAddress to which we want to send bitcoins
     * @param amount the amount we wish to send, rounded to the nearest 0.01
     * @param comment a comment for this transfer, can be null
     * @param commentTo a comment to this transfer, can be null
     * @return the transaction ID for this transfer of Bitcoins
     */
    public String sendToAddress(String bitcoinAddress, double amount, String comment, String commentTo) {
        amount = checkAndRound(amount);

        try {
            JSONArray parameters = new JSONArray().put(bitcoinAddress).put(amount)
                                                  .put(comment).put(commentTo);
            JSONObject request = createRequest("sendtoaddress", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when sending bitcoins", e);
        }
    }

    /**
     * Sends amount from account's balance to bitcoinAddress.
     * This method will fail if there is less than amount Bitcoins with minimumConfirmations
     * confirmations in the account's balance (unless account is the empty-string-named default
     * account; it behaves like the #sendToAddress() method). Returns transaction ID on success.
     *
     * @param account the account we wish to send from, the default account if null or empty string
     * @param bitcoinAddress the address to which we want to send Bitcoins
     * @param amount the amount we wish to send, rounded to the nearest 0.01
     * @param minimumConfirmations minimum number of confirmations for a transaction to count
     * @param comment a comment for this transfer, can be null
     * @param commentTo a comment to this transfer, can be null
     * @return the transaction ID for this transfer of Bitcoins
     * @since 0.3.18
     */
    public String sendFrom(String account, String bitcoinAddress, double amount, int minimumConfirmations,
                           String comment, String commentTo) {
        if (account == null) {
            account = "";
        }

        if (minimumConfirmations <= 0) {
            throw new BitcoinClientException("minimumConfirmations must be > 0");
        }

        amount = checkAndRound(amount);

        try {
            JSONArray parameters = new JSONArray().put(account).put(bitcoinAddress).put(amount)
                                                  .put(minimumConfirmations).put(comment).put(commentTo);
            JSONObject request = createRequest("sendfrom", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getString("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when sending bitcoins with sendFrom()", e);
        }
    }

    /**
     * Moves Bitcoins from one account to another on the same Bitcoin client.
     * This method will fail if there is less than amount Bitcoins with minimumConfirmations
     * confirmations in the fromAccount's balance. Returns transaction ID on success.
     *
     * @param fromAccount the account we wish to move from, the default account if null or empty string
     * @param toAccount the account we wish to move to, the default account if null or empty string
     * @param amount the amount we wish to move, rounded to the nearest 0.01
     * @param minimumConfirmations minimum number of confirmations for a transaction to count
     * @param comment a comment for this move, can be null
     * @return the transaction ID for this move of Bitcoins
     * @since 0.3.18
     */
    public boolean move(String fromAccount, String toAccount, double amount, int minimumConfirmations, String comment) {
        if (fromAccount == null) {
            fromAccount = "";
        }

        if (toAccount == null) {
            toAccount = "";
        }

        if (minimumConfirmations <= 0) {
            throw new BitcoinClientException("minimumConfirmations must be > 0");
        }

        amount = checkAndRound(amount);

        try {
            JSONArray parameters = new JSONArray().put(fromAccount).put(toAccount).put(amount)
                                                  .put(minimumConfirmations).put(comment);
            JSONObject request = createRequest("move", parameters);
            JSONObject response = session.sendAndReceive(request);

            return response.getBoolean("result");
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when moving " + amount + " bitcoins from account: '" +
                                             fromAccount + "' to account: '" + toAccount + "'", e);
        }
    }

    private double checkAndRound(double amount) {
        if (amount < 0.01) {
            throw new BitcoinClientException("The current machinery doesn't support transactions of less than 0.01 Bitcoins");
        }

        if (amount > 21000000.0) {
            throw new BitcoinClientException("Sorry dude, can't transfer that many Bitcoins");
        }

        amount = roundToTwoDecimals(amount);
        return amount;
    }

    /**
     * Stops the bitcoin server
     */
    public void stop() {
        try {
            JSONObject request = createRequest("stop");
            session.sendAndReceive(request);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when stopping the bitcoin server", e);
        }
    }

    /**
     * Validates a Bitcoin address
     *
     * @param address the address we want to validate
     */
    public ValidatedAddressInfo validateAddress(String address) {
        try {
            JSONArray parameters = new JSONArray().put(address);
            JSONObject request = createRequest("validateaddress", parameters);
            JSONObject response = session.sendAndReceive(request);
            JSONObject result = (JSONObject) response.get("result");

            ValidatedAddressInfo info = new ValidatedAddressInfo();
            info.setIsValid(result.getBoolean("isvalid"));

            if (info.getIsValid()) {
                // The data below is only sent if the address is valid
                info.setIsMine(result.getBoolean("ismine"));
                info.setAddress(result.getString ("address"));
            }

            return info;
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when validating an address", e);
        }
    }

    /**
     * Copies the wallet.dat file to a backup destination
     *
     * @param destination the directory we wish to backup the wallet to
     */
    public void backupWallet(String destination) {
        try {
            JSONArray parameters = new JSONArray().put(destination);
            JSONObject request = createRequest("backupwallet", parameters);
            session.sendAndReceive(request);
        } catch (JSONException e) {
            throw new BitcoinClientException("Exception when backing up the wallet", e);
        }
    }

    /**
     * Rounds a double to the nearest dwo decimals, rounding UP.
     * Not proud of this code, but it works.
     */
    protected static double roundToTwoDecimals(double amount) {
        BigDecimal amountTimes100 = new BigDecimal(amount * 100 + 0.5);
        BigDecimal roundedAmountTimes100 = new BigDecimal(amountTimes100.intValue());
        BigDecimal roundedAmount = roundedAmountTimes100.divide(new BigDecimal(100.0));

        return roundedAmount.doubleValue();
    }

    private JSONObject createRequest(String functionName, JSONArray parameters) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("jsonrpc", "2.0");
        request.put("id",      UUID.randomUUID().toString());
        request.put("method", functionName);
        request.put("params",  parameters);

        return request;
    }

    private JSONObject createRequest(String functionName) throws JSONException {
        return createRequest(functionName, new JSONArray());
    }
}
