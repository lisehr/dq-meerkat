package dqm.jku.trustkg.demos.alex;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;
import java.util.TreeMap;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.FulFill;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.MetaData;
import com.bigchaindb.model.Transaction;
import com.bigchaindb.util.Base58;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import okhttp3.Response;

/**
 * simple usage of BigchainDB Java driver (https://github.com/bigchaindb/java-bigchaindb-driver)
 * to create TXs on BigchainDB network
 * @author dev@bigchaindb.com
 *
 */
public class BigchainDBJavaDriverUsageExample {

    /**
     * main method 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String args[]) throws Exception {

        BigchainDBJavaDriverUsageExample examples = new BigchainDBJavaDriverUsageExample();
        
        //set configuration
        BigchainDBJavaDriverUsageExample.setConfig();
        
        //generate Keys
        KeyPair keys = BigchainDBJavaDriverUsageExample.getKeys();
        
        System.out.println(Base58.encode(keys.getPublic().getEncoded()));
        System.out.println(Base58.encode(keys.getPrivate().getEncoded()));
        
        // create New asset
        Map<String, String> assetData = new TreeMap<String, String>() {{
            put("name", "James Bond");
            put("age", "doesn't matter");
            put("purpose", "saving the world");
        }};
        System.out.println("(*) Assets Prepared..");

        // create metadata
        MetaData metaData = new MetaData();
        metaData.setMetaData("where is he now?", "Thailand");
        System.out.println("(*) Metadata Prepared..");
        
        //execute CREATE transaction
        String txId = examples.doCreate(assetData, metaData, keys);

        //create transfer metadata
        MetaData transferMetadata = new MetaData();
        transferMetadata.setMetaData("where is he now?", "Japan");
        System.out.println("(*) Transfer Metadata Prepared..");
        
        //let the transaction commit in block
        Thread.sleep(5000);

        //execute TRANSFER transaction on the CREATED asset
        examples.doTransfer(txId, transferMetadata, keys);    

    }

    private void onSuccess(Response response) {
        //TODO : Add your logic here with response from server
        System.out.println("Transaction posted successfully");
    }
    
    private void onFailure() {
        //TODO : Add your logic here
        System.out.println("Transaction failed");
    }
    
    private GenericCallback handleServerResponse() {
        //define callback methods to verify response from BigchainDBServer
        GenericCallback callback = new GenericCallback() {

            @Override
            public void transactionMalformed(Response response) {
                System.out.println("malformed " + response.message());
                onFailure();
            }

            @Override
            public void pushedSuccessfully(Response response) {
                System.out.println("pushedSuccessfully");
                onSuccess(response);
        }

            @Override
            public void otherError(Response response) {
                System.out.println("otherError" + response.message());
                onFailure();
            }
        };
        
        return callback;
    }
    
    
    /**
     * configures connection url and credentials
     */
    public static void setConfig() {
        BigchainDbConfigBuilder
        .baseUrl("http://localhost:9984/") //or use http://testnet.bigchaindb.com
        .addToken("app_id", "")
        .addToken("app_key", "").setup();

    }
    /**
     * generates EdDSA keypair to sign and verify transactions
     * @return KeyPair
     */
    public static KeyPair getKeys() {
        //  prepare your keys
        net.i2p.crypto.eddsa.KeyPairGenerator edDsaKpg = new net.i2p.crypto.eddsa.KeyPairGenerator();
        KeyPair keyPair = edDsaKpg.generateKeyPair();
        System.out.println("(*) Keys Generated..");
        return keyPair;

    }

    /**
     * performs CREATE transactions on BigchainDB network
     * @param assetData data to store as asset
     * @param metaData data to store as metadata
     * @param keys keys to sign and verify transaction
     * @return id of CREATED asset
     */
    public String doCreate(Map<String, String> assetData, MetaData metaData, KeyPair keys) throws Exception {

        try {
        //build and send CREATE transaction
        Transaction transaction = null;
        
             transaction = BigchainDbTransactionBuilder
                    .init()
                    .addAssets(assetData, TreeMap.class)
                    .addMetaData(metaData)
                    .operation(Operations.CREATE)
                    .buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
                    .sendTransaction(handleServerResponse());

            System.out.println("(*) CREATE Transaction sent.. - " + transaction.getId());
            return transaction.getId();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * performs TRANSFER operations on CREATED assets
     * @param txId id of transaction/asset
     * @param metaData data to append for this transaction
     * @param keys keys to sign and verify transactions
     */
    public void doTransfer(String txId, MetaData metaData, KeyPair keys) throws Exception {
        
        Map<String, String> assetData = new TreeMap<String, String>();
        assetData.put("id", txId);

        try {
            

            //which transaction you want to fulfill?
            FulFill fulfill = new FulFill();
            fulfill.setOutputIndex("0");
            fulfill.setTransactionId(txId);
            

            //build and send TRANSFER transaction
            Transaction transaction = BigchainDbTransactionBuilder
                    .init()
                    .addInput(null, fulfill, (EdDSAPublicKey) keys.getPublic())
                    .addOutput("0", (EdDSAPublicKey) keys.getPublic())
                    .addAssets(txId, String.class)
                    .addMetaData(metaData)
                    .operation(Operations.TRANSFER)
                    .buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
                    .sendTransaction(handleServerResponse());

            System.out.println("(*) TRANSFER Transaction sent.. - " + transaction.getId());


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}