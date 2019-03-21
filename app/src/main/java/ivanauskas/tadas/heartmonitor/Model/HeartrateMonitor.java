package ivanauskas.tadas.heartmonitor.Model;


import java.util.Random;

public class HeartrateMonitor {

    private Random rng;

    public HeartrateMonitor(){
        rng = new Random();
    }


    /**
     * TODO update this class
     * now it returns random number
     * */
    public double getHeartRate(){
        double rate = 80+rng.nextInt(120);
        return rate;
    }
}
