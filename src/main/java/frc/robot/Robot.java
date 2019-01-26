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
import frc.robot.Toggle;
import edu.wpi.first.wpilibj.Solenoid;

public class Robot extends TimedRobot {

	private boolean prototype = true;
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

	private SpeedController leftElevator;
	private SpeedController rightElevator;
	private SpeedControllerGroup elevator;

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

	Toggle backwards;
	
	@Override
	public void robotInit() {

		System.err.println("Starting the Deep Space Robot");

		edu.wpi.first.cameraserver.CameraServer.getInstance().startAutomaticCapture();

		driverLeft = new Joystick(0);
		driverRight = new Joystick(1);
		xbox = new XboxController(2);

		backwards = new Toggle();

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

		if (!prototype)
		{
			System.err.println("Initializing Speed Controllers");
			frontLeftSpeed		= new WPI_TalonSRX(14);
			backLeftSpeed		= new WPI_TalonSRX(15);
			frontRightSpeed		= new WPI_TalonSRX(16);
			backRightSpeed		= new WPI_TalonSRX(1);
		} else {
			System.err.println("Initializing Prototype Speed Controllers");
			frontLeftSpeed		= new Talon(9);
			backLeftSpeed		= new Talon(8);
			frontRightSpeed		= new Talon(7);
			backRightSpeed		= new Talon(6);
		//	frontRightSpeed		= new CANSparkMax(7, MotorType.kBrushless);
		//	backRightSpeed		= new CANSparkMax(6, MotorType.kBrushless);
		}

		//leftElevator = new Talon(0); // unnammed until Talon identified.
		//rightElevator = new Talon(1);

		compressor = new Compressor(21);
		pressureSensor = new AnalogInput(0);
		
		Solenoid Solenoid0 = new Solenoid(21, 0);
		Solenoid0.set(true);
		Solenoid0.set(false);
	
		Solenoid Solenoid1 = new Solenoid(21, 1);
		Solenoid1.set(true);
		Solenoid1.set(false);
	

		Solenoid Solenoid2 = new Solenoid(21, 2);
		Solenoid2.set(true);
		Solenoid2.set(false);

		Solenoid Solenoid3 = new Solenoid(21, 3);
		Solenoid3.set(true);
		Solenoid3.set(false);

		System.err.println("Initializing Speeed Controllre Groups");
		leftSpeed = new SpeedControllerGroup(frontLeftSpeed, backLeftSpeed);
		rightSpeed = new SpeedControllerGroup(frontRightSpeed, backRightSpeed);
		
		drive = new DifferentialDrive(leftSpeed, rightSpeed);
		
		System.err.println("Initializing PixyCam");
		pixycam = new Pixy(Port.kOnboardCS0, 0);
		
	}

	@Override
	public void robotPeriodic() {}

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
				
		}catch (Exception e){
			
			System.out.println("AP: " + e.toString());
		     	
		}
		pixycam.getAllDetectedObjects();
		// pixycam.

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
					.withInput("Operator-X-Button",		xbox.getXButton())
					.withInput("Operator-Y-Button",		xbox.getYButton())
					.withInput("Operator-A-Button", 	xbox.getAButton())
					.withInput("Operator-B-Button",		xbox.getBButton())
					.withInput("Operator-Start-Button",	xbox.getRawButton(8))
					.withInput("Operator-Back-Button",	xbox.getRawButton(7))
					.withInput("Elevator-Back",  		xbox.getTriggerAxis(Hand.kLeft))	//mapped elevator down to left trigger
					.withInput("Elevator-Forward",		xbox.getTriggerAxis(Hand.kRight))	//and up to right trigger -Brad
					.withInput("Operator-DPad",			xbox.getPOV())
					.withInput("Driver-Left", 			driverLeft.getRawAxis(1))
					.withInput("Driver-Right", 			driverRight.getRawAxis(1))
					.withInput("Driver-Left-Trigger", 	driverLeft.getRawButton(1))
					.withInput("Driver-Right-Trigger", 	driverRight.getRawButton(1))
					.withInput("Operator-Left-Bumper",	xbox.getBumper(Hand.kLeft))
					.withInput("Operator-Right-Bumper", xbox.getBumper(Hand.kRight))
					.withInput("Driver-Left-8", 		driverLeft.getRawButton(8))
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

	@Override
	public void disabledInit() {actions.disabledInit();}

	@Override
	public void disabledPeriodic() {}

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

		SmartDashboard.putString("DB/String 0", Double.toString(DriverStation.getInstance().getMatchTime()));

	}

}
