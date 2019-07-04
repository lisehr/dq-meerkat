package dqm.jku.trustkg.demos.alex;

import java.security.KeyPair;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import net.i2p.crypto.eddsa.KeyPairGenerator;

public class BigChainDBDemo {

  private static final String URL = "http://localhost:9984/";

  public static void main(String args[]) throws Exception {
    // setup connection for bigchaindb
    // IMPORTANT:
    try {
      BigchainDbConfigBuilder.baseUrl(URL).setup();
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

    // TODO: try out some operations with it
  }
}