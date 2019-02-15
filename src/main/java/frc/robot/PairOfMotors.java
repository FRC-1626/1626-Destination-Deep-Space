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

    private PowerDistributionPanel PDB = new PowerDistributionPanel(22);
    public double Current;
    int x, y;

    public PairOfMotors(int Breaker1, int Breaker2) {

        x = Breaker1;
        y = Breaker2;
   
    }

    public boolean isCurrentDifferent() {
        double xCurrent = PDB.getCurrent(x);
        double yCurrent = PDB.getCurrent(y);

        SmartDashboard.putString("DB/String 5", "" + xCurrent);
        SmartDashboard.putString("DB/String 6", "" + yCurrent);

        boolean TheyreDifferent =
            (xCurrent > (1.1 * yCurrent)) ||
            (yCurrent > (1.1 * xCurrent));

        if (TheyreDifferent) return true;
        else return false;

    }

}