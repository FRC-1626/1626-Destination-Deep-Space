package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;

//  This class was made by Heitz
//  You know who to blame now
//  Represents a pair of motors connected to the PDB, and is used to check 
//  if the currents they are receiving are within 10% difference of each other
public class PairOfMotors {

    private static PowerDistributionPanel PDB = new PowerDistributionPanel();

    private String pairName;
    int breakerPort1;
    int breakerPort2;
    double allowedDifference;
    double previousdifference = 0;

    public PairOfMotors(String name, int breaker1, int breaker2) {

        pairName = name;
        breakerPort1 = breaker1;
        breakerPort2 = breaker2;
        allowedDifference=0.1;
    }

    public PairOfMotors(String name, int breaker1, int breaker2, double alDiff) {

        pairName = name;
        breakerPort1 = breaker1;
        breakerPort2 = breaker2;
        allowedDifference=alDiff;
    }

    public String getName() {
        return pairName;
    }

    public boolean isCurrentDifferent() {
        double current1 = PDB.getCurrent(breakerPort1);
        double current2 = PDB.getCurrent(breakerPort2);
        SmartDashboard.putString("DB/String 5", "" + current1);
        SmartDashboard.putString("DB/String 6", "" + current2);
        
        double differenceRatio = 0;
        differenceRatio = (Math.max(current1, current2) - Math.min(current1, current2) ) / Math.max(current1, current2);
        boolean TheyreDifferent = differenceRatio > allowedDifference;
        
        SmartDashboard.putString(
            "Motors/" + pairName, 
            "Current difference is %" + String.format("%.1f", 100.0*differenceRatio) );          
        if (TheyreDifferent) {
            if(differenceRatio > previousdifference && differenceRatio != 100)
            {
            SmartDashboard.putString(
                "Motors/" + pairName, 
                "Current difference is %" + String.format("%.1f", 100.0*differenceRatio) );
            previousdifference = differenceRatio;    
            } 
        }
        
        return TheyreDifferent;
    }

}