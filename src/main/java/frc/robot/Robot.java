package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
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
import edu.wpi.first.cameraserver.*;
import edu.wpi.first.cameraserver.CameraServer;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import edu.wpi.first.cameraserver.*;
import com.zephyr.pixy.*;
import frc.robot.Toggle;
import frc.robot.PairOfMotors;
import java.util.List;

import javax.lang.model.util.ElementScanner6;

import java.util.ArrayList;

/*	

FIRST Robotics Team 1626
<Insert robot name here>
By Jonathan Heitz, Daniel Lucash, Christopher Nokes, Benjamin Ulrich, and SJHS Falcon Robotics

*/

public class Robot extends TimedRobot {

	private boolean prototype = false;
	private XboxController xbox;
	private Joystick driverLeft;
	private Joystick driverRight;
	private SpeedController frontLeftSpeed;
	private SpeedController frontRightSpeed;
	private SpeedController backLeftSpeed;
	private SpeedController backRightSpeed;
	private SpeedControllerGroup leftSpeed;
	private SpeedControllerGroup rightSpeed;
	private DifferentialDrive drive;
	private SpeedController ballHolder;
	private WPI_TalonSRX elevator;
	private WPI_TalonSRX leftArm;
	private WPI_TalonSRX rightArm;
//	private TalonSRX inOutMotor1;
//	private DoubleSolenoid elevatorBrake;
	int autoLoopCounter;
	private Thread autoThread;
	private ControlMode Current;
	public String gameData;
	private int startingPosition = 1;
	private ActionRecorder actions;
	private Pixy pixycam;
	private Compressor compressor;
	private AnalogInput pressureSensor;
	private Solenoid Solenoid0;
	private Solenoid Solenoid1;
	private Solenoid Solenoid2;
	private Solenoid Solenoid3;
	private double previousElevator;
	private PairOfMotors testPairOfMotors;
	private int Claw = 1;

	private List<PairOfMotors> motorPairList;

	Toggle backwards;
	Toggle doMotorBreakIn = new Toggle();
	Toggle clawState;

	@Override
	public void robotInit() {

		System.err.println("Starting the Deep Space Robot");
		CameraServer.getInstance().startAutomaticCapture();

		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);
		backwards = new Toggle();
		clawState = new Toggle();

		System.out.println("initializing actions...");
		actions = new ActionRecorder().
				setMethod(this, "robotOperation", DriverInput.class).
				setUpButton(xbox, 1).
				setDownButton(xbox, 2).
				setRecordButton(xbox, 3);
		
		System.out.println("initializing buttons...");
		DriverInput.nameInput("Driver-Left");
		DriverInput.nameInput("Driver-Right");
		DriverInput.nameInput("Driver-Left-Trigger");
		DriverInput.nameInput("Driver-Right-Trigger");
		DriverInput.nameInput("Operator-Left-Stick");
		DriverInput.nameInput("Operator-Left-Bumper");
		DriverInput.nameInput("Operator-Left-Trigger");
		DriverInput.nameInput("Operator-Right-Stick");
		DriverInput.nameInput("Operator-Right-Bumper");
		DriverInput.nameInput("Operator-Right-Trigger");
		DriverInput.nameInput("Operator-X-Button");
		DriverInput.nameInput("Operator-Y-Button");
		DriverInput.nameInput("Operator-A-Button");
		DriverInput.nameInput("Operator-B-Button");
		DriverInput.nameInput("Operator-Start-Button");
		DriverInput.nameInput("Operator-Back-Button");
		DriverInput.nameInput("Elevator-Forward");
		DriverInput.nameInput("Elevator-Back");
		DriverInput.nameInput("Operator-DPad");
		DriverInput.nameInput("Driver-Left-8");

		if (!prototype) {
			System.err.println("Initializing Speed Controllers");
			frontLeftSpeed		= new CANSparkMax(20, MotorType.kBrushless);
			backLeftSpeed		= new CANSparkMax(1, MotorType.kBrushless);
			frontRightSpeed		= new CANSparkMax(14, MotorType.kBrushless);
			backRightSpeed		= new CANSparkMax(15, MotorType.kBrushless);
			leftArm				= new WPI_TalonSRX(2); 
			rightArm			= new WPI_TalonSRX(3);
			elevator			= new WPI_TalonSRX(13);
			ballHolder			= new VictorSP(0);
		} else {
			System.err.println("Initializing Prototype Speed Controllers");
			frontLeftSpeed		= new WPI_TalonSRX(2);
			backLeftSpeed		= new WPI_TalonSRX(3);
			frontRightSpeed		= new CANSparkMax(14, MotorType.kBrushless);
			backRightSpeed		= new CANSparkMax(15, MotorType.kBrushless);
			elevator			= new WPI_TalonSRX(6); 
			ballHolder			= new WPI_TalonSRX(7);
		}

		compressor = new Compressor();
		pressureSensor = new AnalogInput(0);
		
		Solenoid Solenoid0 = new Solenoid(0);
		Solenoid0.set(true);
		Solenoid0.set(false);
	
		    
		Solenoid Solenoid1 = new Solenoid(1);
		Solenoid1.set(true);
		Solenoid1.set(false);

		Solenoid Solenoid2 = new Solenoid(2);
		Solenoid2.set(true);
		Solenoid2.set(false);

		Solenoid Solenoid3 = new Solenoid(3);
		Solenoid3.set(true);
		Solenoid3.set(false);

		System.err.println("Initializing Drive Train");
		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
		System.err.println("Initializing PixyCam");
		pixycam = new Pixy(Port.kOnboardCS0, 0);

//		testPairOfMotors = new PairOfMotors(2, 3);

		
		ballHolder.setInverted(true);
//		frontElevator.follow(backElevator);
		double value = 1; 
//		backElevator.configSetParameter(ParamEnum.eClearPositionOnQuadIdx, value, 0x00, 0x00, 10);
//		backElevator.configSetParameter(ParamEnum.eClearPositionOnLimitF, value, 0x00, 0x00, 10);
//		backElevator.configSetParameter(ParamEnum.eClearPositionOnLimitR, value, 0x00, 0x00, 10);


		motorPairList = new ArrayList<PairOfMotors>();

		motorPairList.add(new PairOfMotors("LeftDrive", 0, 1));
		motorPairList.add(new PairOfMotors("RightDrive", 14, 15));
		motorPairList.add(new PairOfMotors("ArmDrive", 6,7));
		motorPairList.add(new PairOfMotors("Climb", 12,13));

		for (PairOfMotors motorPair : motorPairList) {
            SmartDashboard.putString(
                "Motors/" + motorPair.getName(), 
                "No motor current differences detected");
		}
	

	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void autonomousInit() {
		
		autoLoopCounter = 0;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		actions.autonomousInit(gameData.substring(0,2));

  	}

	@Override
	public void autonomousPeriodic() {
		
		String itemLocationString = gameData.substring(0, 2) + startingPosition;
		try{
			if (actions != null) actions.longPlayback(this, -1);
			else Timer.delay(0.010);
		}catch (Exception e) { 
			System.out.println("AP: " + e.toString()); 
		} pixycam.getAllDetectedObjects();

  	}

	@Override
	public void teleopInit() {
		DriverInput.setRecordTime();
		actions.teleopInit();
	}

	@Override
	public void teleopPeriodic() {
	
		try {

			actions.input(new DriverInput()
				.withInput("Operator-X-Button",		xbox.getXButton()) // used - lift movement
				.withInput("Operator-Y-Button",		xbox.getYButton()) // used - claw
				.withInput("Operator-A-Button", 	xbox.getAButton()) // used - lift movement
				.withInput("Operator-B-Button",		xbox.getBButton()) // used - lift movement
				.withInput("Operator-Start-Button",	xbox.getRawButton(8))
				.withInput("Operator-Back-Button",	xbox.getRawButton(7))
				.withInput("Elevator-Back",  		xbox.getTriggerAxis(Hand.kLeft))	// used - elevator back
				.withInput("Elevator-Forward",		xbox.getTriggerAxis(Hand.kRight))	// used - elevator forward
				.withInput("Operator-DPad",			xbox.getPOV()) // used - set elevator position
				.withInput("Driver-Left", 			driverLeft.getRawAxis(1)) // used - drives left side
				.withInput("Driver-Right", 			driverRight.getRawAxis(1)) // used - drives right side
				.withInput("Driver-Left-Trigger", 	driverLeft.getRawButton(1))
				.withInput("Driver-Right-Trigger", 	driverRight.getRawButton(1))
				.withInput("Operator-Left-Bumper",	xbox.getBumper(Hand.kLeft)) // used - ball scoop
				.withInput("Operator-Right-Bumper", xbox.getBumper(Hand.kRight)) // used - ball scoop
				.withInput("Driver-Left-8", 		driverLeft.getRawButton(8)) // used - enables backwards
				.withInput("Operator-Left-Stick",	xbox.getY(Hand.kLeft)) // used - arm movement
			);	
		
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		pixycam.getAllDetectedObjects();
		
	}

	public void disabledInit() {
		actions.disabledInit();
	}

	public void disabledPeriodic() {
		actions.disabledPeriodic();
	}

	public void robotOperation(DriverInput input) {
		
		SmartDashboard.putString("DB/String 1", "" + gameData + startingPosition);
		
		double leftAxis = input.getAxis("Driver-Left");
		double rightAxis = input.getAxis("Driver-Right");
		leftAxis = Math.abs(Math.pow(leftAxis, 2)) * leftAxis/Math.abs(leftAxis);
		rightAxis = Math.abs(Math.pow(rightAxis, 2)) * rightAxis/Math.abs(rightAxis);

		backwards.input(input.getButton("Driver-Left-8"));
		SmartDashboard.putBoolean("DB/LED 1", backwards.getState());

		if (!backwards.getState()) drive.tankDrive(-1 * 1 * leftAxis, -1 * 1 * rightAxis, false);
		else drive.tankDrive(1 * rightAxis, 1 * leftAxis, false);

//		SmartDashboard.putString("DB/String 0", Double.toString(DriverStation.getInstance().getMatchTime()));

		double elevatorAxis = input.getAxis("Elevator-Forward") - input.getAxis("Elevator-Back");
		if (Math.abs(elevatorAxis) > 0.10) {
//			if (Math.abs(previousElevator) < 0.10) elevatorBrake.set (Value.kForward);
			elevator.set(ControlMode.PercentOutput, elevatorAxis);	
		} else {
//			elevatorBrake.set(Value.kReverse);
			elevator.set(ControlMode.PercentOutput, 0);
		}

		previousElevator = elevatorAxis;
//		SmartDashboard.putString("DB/String 0", Double.toString(DriverStation.getInstance().getMatchTime()));

		int dpadAxis = (int) input.getAxis("Operator-DPad");
		switch(dpadAxis){
		case 0:
			elevator.set(ControlMode.Position, 0);
			break;
		case 90:
			elevator.set(ControlMode.Position, 100);
			break;
		case 180:
			elevator.set(ControlMode.Position, 200);
			break;
		case 270:
			elevator.set(ControlMode.Position, 300);
			break;
		}
		
		
		if(input.getAxis("Operator-Left-Stick") != 0)
			{
			leftArm.set(input.getAxis("Operator-Left-Stick"));
			rightArm.set(input.getAxis("Operator-Left-Stick") * -1);
			}
		else
			{
			leftArm.set(0);
			rightArm.set(0);
			}

		clawState.input(input.getButton("Operator-Y-Button"));
		if(clawState.getState())
			{
			Claw = Claw*-1;
			if(Claw == 1)
				{
				Solenoid0.set(true);
				Solenoid1.set(true);
				Solenoid2.set(true);
				Solenoid3.set(true);
				}
			else
				{
				Solenoid0.set(false);
				Solenoid1.set(false);
				Solenoid2.set(false);
				Solenoid3.set(false);
				}
			}
		
		if(input.getButton("Operator-Right-Bumper"))
			{
			ballHolder.set(.99);
			}
		else if(input.getButton("Operator-Left-Bumper"))
			{
			ballHolder.set(-.99);
			}
		else
			{
			ballHolder.set(0);
			}
		
		
		SmartDashboard.putString("DB/String 6", "" + elevator.getSelectedSensorPosition(0));

		if (input.getButton("Operator-X-Button")) {
			ballHolder.set(1.0);
		} else if (input.getButton("Operator-A-Button")) {
			ballHolder.set(-.50);
		} else if (input.getButton("Operator-B-Button")) {
			ballHolder.set(-1.0);
		} else {
			ballHolder.set(0.0);	
		}

		for (PairOfMotors motorPair : motorPairList) {
			motorPair.isCurrentDifferent();
			}
	
	}



/*

clawState.input(input.getButton("Operator-Y-Button"));
if(clawState.getState())
	{
	Claw = Claw*-1;
	if(Claw == 1)
		{
			Solenoid0.set(true);
			Solenoid1.set(true);
			Solenoid2.set(true);
		}
	else
		{
			Solenoid0.set(false);
			Solenoid1.set(false);
			Solenoid2.set(false);
		}
	}

*/


//	public boolean PDBCurrentCheck(double percent) {
//
//		for (int i = 0; i < 15; i++) {
//			for (int j = 0; j < 15; j++) {
//				if (
//					PDB.getCurrent(i) - PDB.getCurrent(j) > 0.1 * PDB.getCurrent(i) ||
//					PDB.getCurrent(i) - PDB.getCurrent(j) > 0.1 * PDB.getCurrent(j)
//				) return true;
//			}
//		}
//		return false;
//
//	}

}
