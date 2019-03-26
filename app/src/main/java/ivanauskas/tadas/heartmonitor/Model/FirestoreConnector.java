package ivanauskas.tadas.heartmonitor.Model;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FirestoreConnector {
    private FirebaseFirestore db;
    private Map<String, Object> data;

    private String email;


    public FirestoreConnector(String email){
        db = FirebaseFirestore.getInstance();
        this.email = email;
    }

    public void update(float movement, double rate){
        data= new HashMap<>();
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        data.put(timeStamp,rate);

        String state = movement>3? "moving":"idle";

        String path = "data/"+email+"/"+new SimpleDateFormat("yyyy-MM-dd:HH").format(new Date());
        db.collection(path).document(state).set(data, SetOptions.merge());
    }

    public void updateUser(String userID, HashMap data){
        String path = "users/"+userID;
        db.collection("users").document(userID).set(data, SetOptions.merge());

    }

    // data/email/yyyy-MM-dd:hh


}
