package me.angeloo.mystica.Utility;

public class IconCalculator {

    public IconCalculator(){

    }

    public int calculate(int currentTime, int maxDuration){

        double percentCompleted = ((double) currentTime / maxDuration) * 100;

        return (int) Math.round((percentCompleted / 100) * 8);
    }

}
