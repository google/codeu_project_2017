package codeu.chat.database;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import codeu.chat.util.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DatabaseAccess {
    private final String account = "/home/greg/Dropbox/gproj/codeu_project_2017/third_party/codeu-56fe6-firebase-adminsdk-6et4u-e78898c410.json";
    private final String databaseUrl = "https://codeu-56fe6.firebaseio.com";

    public void initialize() {
        try {
            FileInputStream serviceAccount =
                new FileInputStream(account);
            // LOG.info("Using service account key is: %s", account);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            FirebaseApp.initializeApp(options);
            //LOG.info("Service account key valid");
            //LOG.info("Database initiliazed");
        } catch (IOException e) {
            //LOG.error("Failed to load service account key: %s", e.toString());
            System.out.println(e.toString());
        }
    }
}