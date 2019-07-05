package dqm.jku.trustkg.demos.alex;

import java.security.KeyPair;
import java.util.Map;
import java.util.TreeMap;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.MetaData;
import com.bigchaindb.model.ValidTransaction;
import com.bigchaindb.util.JsonUtils;
import com.bigchaindb.ws.MessageHandler;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;

/**
 * @author optimusseptim This class follows the actual demo from BigChainDB
 *         (https://www.bigchaindb.com/developers/guide/tutorial-piece-of-art/)
 *         to test the functionality of bigchaindb.
 */
public class BigChainDBDemo {

  private static final String URL = "http://localhost:9984/";

  public static void main(String args[]) throws Exception {
    // setup connection for bigchaindb
    // IMPORTANT: use BigChainDBServer for local tests, since online solution is
    // down resp. temporarily flushed
    try {
      BigchainDbConfigBuilder.baseUrl(URL).webSocketMonitor(new CustomWebMonitor()).setup();
      System.out.println("Connection to DB established!");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // generate keypair for user
    KeyPairGenerator edDsaKpg = new KeyPairGenerator();
    KeyPair alex = edDsaKpg.generateKeyPair();
    System.out.println("Keypair generated for user alex: " + alex.toString());
    System.out
        .println("Private Key: " + alex.getPrivate().toString() + " | Public Key: " + alex.getPublic().toString());

    // generate painting object from demo
    Painting p = new Painting("Mona Lisa", "Leonardo Da Vinci", "Firence", 1503);
    System.out.println("Generated: " + p.toString());

    // generate metaData
    MetaData metaData = new MetaData();
    metaData.setMetaData("price in euros", "10.000.000");

    // create in db
    BigchainDbTransactionBuilder.init().addAssets(p.createAssetFromData(), TreeMap.class).addMetaData(metaData)
        .operation(Operations.CREATE)
        .buildAndSign((EdDSAPublicKey) alex.getPublic(), (EdDSAPrivateKey) alex.getPrivate()).sendTransaction();
    
    //TODO: Test further!
  }

}

class Painting {
  String name;
  String author;
  String place;
  int year;

  public Painting(String name, String author, String place, int year) {
    this.name = name;
    this.author = author;
    this.place = place;
    this.year = year;
  }

  public String getName() {
    return name;
  }

  public String getAuthor() {
    return author;
  }

  public String getPlace() {
    return place;
  }

  public int getYear() {
    return year;
  }

  @Override
  public String toString() {
    return "Painting: " + name + ", " + author + ", " + place + ", " + year;
  }

  public Map<String, String> createAssetFromData() {
    Map<String, String> retVal = new TreeMap<String, String>();
    retVal.put("name", getName());
    retVal.put("author", getAuthor());
    retVal.put("place", getPlace());
    retVal.put("year", String.valueOf(getYear()));
    return retVal;
  }

}

class CustomWebMonitor implements MessageHandler {
  @Override
  public void handleMessage(String message) {
    JsonUtils.fromJson(message, ValidTransaction.class);
  }
}
