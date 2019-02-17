package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import edu.wpi.first.cameraserver.*;
import com.zephyr.pixy.*;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Robot;
import frc.robot.Toggle;

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

        double differenceRatio = (Math.max(current1, current2) - Math.min(current1, current2) ) / Math.max(current1, current2);
        boolean TheyreDifferent = differenceRatio > allowedDifference;

        if (TheyreDifferent) {
            
                SmartDashboard.putString(
                    "Motors/" + pairName, 
                    "Current difference is " + String.format("%.2f", 100.0*differenceRatio) );
             
        }
        
        return TheyreDifferent;
    }

}