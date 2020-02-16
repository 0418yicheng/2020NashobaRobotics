package edu.nr.robotics;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.nr.lib.motorcontrollers.SparkMax;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.ietf.jgss.Oid;

import edu.nr.lib.commandbased.DoNothingCommand;
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.lib.interfaces.Periodic;
import edu.nr.lib.interfaces.SmartDashboardSource;
import edu.nr.lib.network.LimelightNetworkTable;
import edu.nr.lib.network.LimelightNetworkTable.Pipeline;
import edu.nr.lib.units.AngularSpeed;
import edu.nr.lib.units.Angle;
import edu.nr.lib.units.Distance;
import edu.nr.lib.units.Time;
import edu.nr.lib.units.Time.Unit;
import edu.nr.robotics.subsystems.EnabledSubsystems;
import edu.nr.robotics.subsystems.climbdeploy.ClimbDeploy;
import edu.nr.robotics.subsystems.climbdeploy.ClimbDeploySmartDashboardCommand;
import edu.nr.robotics.subsystems.colorwheel.ColorWheel;
import edu.nr.robotics.subsystems.colorwheel.ColorWheelRotateCommand;
import edu.nr.robotics.subsystems.colorwheel.TargetColorCommand;
import edu.nr.robotics.subsystems.drive.CSVSaverDisable;
import edu.nr.robotics.subsystems.drive.CSVSaverEnable;
import edu.nr.robotics.subsystems.drive.Drive;
import edu.nr.robotics.subsystems.drive.DriveForwardBasicSmartDashboardCommand;
import edu.nr.robotics.subsystems.drive.DriveToBallCommand;
import edu.nr.robotics.subsystems.drive.EnableMotionProfileSmartDashboardCommand;
import edu.nr.robotics.subsystems.drive.EnableReverseTwoDMotionProfileSmartDashboardCommand;
import edu.nr.robotics.subsystems.drive.EnableTwoDMotionProfileSmartDashboardCommand;
import edu.nr.robotics.subsystems.drive.TurnSmartDashboardCommand;
import edu.nr.robotics.subsystems.hood.Hood;
import edu.nr.robotics.subsystems.hood.SetHoodAngleSmartDashboardCommand;
import edu.nr.robotics.subsystems.transfer.Transfer;
import edu.nr.robotics.subsystems.transfer.TransferCommand;
import edu.nr.robotics.subsystems.indexer.IndexerDeltaPositionSmartDashboardCommand;
import edu.nr.robotics.subsystems.indexer.IndexerSetVelocityCommand;
import edu.nr.robotics.subsystems.indexer.IndexerSetVelocitySmartDashboardCommand;
import edu.nr.robotics.subsystems.intake.Intake;
import edu.nr.robotics.subsystems.sensors.EnabledSensors;
import edu.nr.robotics.subsystems.sensors.ISquaredCSensor;
import edu.nr.robotics.subsystems.shooter.SetShooterSpeedSmartDashboardCommand;
import edu.nr.robotics.subsystems.shooter.Shooter;
import edu.nr.robotics.subsystems.turret.DeltaTurretAngleSmartDashboardCommand;
import edu.nr.robotics.subsystems.turret.SetTurretAngleSmartDashboardCommand;
import edu.nr.robotics.subsystems.turret.SetTurretLimelightCommand;
import edu.nr.robotics.subsystems.turret.Turret;
import edu.nr.robotics.subsystems.turret.TurretLimelightCommand;
import edu.nr.robotics.subsystems.winch.SetWinchPositionCommand;
import edu.nr.robotics.subsystems.winch.WinchClimbRetractCommand;
import edu.nr.robotics.subsystems.indexer.Indexer;
import edu.nr.robotics.subsystems.indexer.IndexerDeltaPositionCommand;
import edu.nr.robotics.subsystems.indexer.IndexerDeltaPositionSmartDashboardCommand;
import edu.nr.robotics.subsystems.indexer.IndexerSetVelocityCommand;
import edu.nr.robotics.subsystems.indexer.IndexerSetVelocitySmartDashboardCommand;



/*import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;*/
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
public class Robot extends TimedRobot {
 
    private static Robot singleton;

    private static double period = 0.02;

    //TalonFX tester;

    //private CANSparkMax protoShooter1;
    //private CANSparkMax //protoShooter2;

    /*public static double F_VEL_SHOOTER = 0;
    public static double P_VEL_SHOOTER = 0;
    public static double I_VEL_SHOOTER = 0;
    public static double D_VEL_SHOOTER = 0;

    public static AngularSpeed shooterSetSpeed;*/

    double dt;
    double dtTot = 0;
    int count = 0;
 
    private double prevTime = 0;

    //private CANSparkMax protoSparkMax1;
    //private CANSparkMax protoSparkMax2;

    private Command autonomousCommand;
   
    public double autoWaitTime;
    //public Compressor robotCompressor;
 
    public synchronized static Robot getInstance() {
        return singleton;
    }
 
    public void robotInit() {
        singleton = this;
 
        m_period = period; // period that the code runs at
 
        smartDashboardInit();
        //autoChooserInit();
        GameData.init();
        ColorWheel.init();
        //OI.init();
        //Winch.init();
        //ClimbDeploy.init();
        //Drive.init();
        //Turret.init();
        //Shooter.init();
        //Hood.init();
        //Intake.init();
        //robotCompressor = new Compressor(RobotMap.PCM_ID);
        //robotCompressor.start();
        //Indexer.init();
        //Transfer.init();
 
        //CameraInit();
 
        LimelightNetworkTable.getInstance().lightLED(true);
        LimelightNetworkTable.getInstance().setPipeline(Pipeline.Target);

        //tester = new TalonFX(20);

        //tester.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
        //tester.config_kF(0, 1);
        //tester.config_kP(0, 0.01);
 
        //System.out.println("end of robot init");
    }
 
    public void autoChooserInit() {
        
    }
 
    public void smartDashboardInit() {
 
        SmartDashboard.putData(new CSVSaverEnable());
        SmartDashboard.putData(new CSVSaverDisable());
        SmartDashboard.putNumber("Auto Wait Time", 0);
  

        if (EnabledSubsystems.DRIVE_SMARTDASHBOARD_DEBUG_ENABLED) {
            SmartDashboard.putNumber("Right Current", Drive.getInstance().getRightCurrent());
            SmartDashboard.putNumber("Left Current", Drive.getInstance().getLeftCurrent());


            SmartDashboard.putData(new DriveForwardBasicSmartDashboardCommand());
            SmartDashboard.putData(new EnableMotionProfileSmartDashboardCommand());
            SmartDashboard.putData(new TurnSmartDashboardCommand());
            SmartDashboard.putData(new EnableTwoDMotionProfileSmartDashboardCommand());
            SmartDashboard.putData(new EnableReverseTwoDMotionProfileSmartDashboardCommand());
        }
 
        if(EnabledSubsystems.TURRET_SMARTDASHBOARD_DEBUG_ENABLED) {
            SmartDashboard.putData(new SetTurretAngleSmartDashboardCommand());
            SmartDashboard.putData(new DeltaTurretAngleSmartDashboardCommand());
            SmartDashboard.putData(new SetTurretLimelightCommand());
        }
 
        if(EnabledSubsystems.SHOOTER_SMARTDASHBOARD_DEBUG_ENABLED){
            SmartDashboard.putData(new SetShooterSpeedSmartDashboardCommand());
        }
        if(EnabledSubsystems.INDEXER_SMARTDASHBOARD_DEBUG_ENABLED){
            SmartDashboard.putData(new IndexerDeltaPositionSmartDashboardCommand());
            SmartDashboard.putData(new IndexerSetVelocitySmartDashboardCommand());
        }

        if(EnabledSubsystems.SHOOTER_SMARTDASHBOARD_DEBUG_ENABLED){
            //SmartDashboard.putData(new SetShooterSpeedSmartDashboardCommand());
            //SmartDashboard.putNumber("Prototype Speed Percent: ", 0);
            //SmartDashboard.putNumber("1 Prototype Current Reading: ", 0);
            //SmartDashboard.putNumber("2 Prototype Current Reading: ", 0);
        }

        if(EnabledSubsystems.CLIMB_DEPLOY_SMARTDASHBOARD_DEBUG_ENABLED)
        {
            SmartDashboard.putData(new ClimbDeploySmartDashboardCommand());
        }
    
        if(EnabledSubsystems.WINCH_SMARTDASHBOARD_DEBUG_ENABLED)
        {
            SmartDashboard.putData(new SetWinchPositionCommand());
            SmartDashboard.putData(new WinchClimbRetractCommand());
        }

        if(EnabledSubsystems.TRANSFER_SMARTDASHBOARD_DEBUG_ENABLED)
        {
            //SmartDashboard.putData(new TransferCommand2(Transfer.TRANSFER_TIME));

            //RIP TransferCommand2. You made your country proud
            SmartDashboard.putData(new TransferCommand(Transfer.TRANSFER_TIME));
        }

        if(EnabledSubsystems.COLOR_WHEEL_SMARTDASHBOARD_ENABLED){
            SmartDashboard.putData(new ColorWheelRotateCommand());
            SmartDashboard.putData(new TargetColorCommand());
        }

        //SmartDashboard.putNumber("F TESTER", 0);
        //SmartDashboard.putNumber("P TESTER", 0);
        //SmartDashboard.putNumber("MOTOR PERCENT", 0);
        //SmartDashboard.putNumber("TESTER SENSOR POSITION", 0);
    }
        
    
 
        @Override
        public void disabledInit() {
            for(NRSubsystem subsystem : NRSubsystem.subsystems) {
                subsystem.disable();
            }
        }
        @Override
        public void testInit() {
            enabledInit();
        }
 
        public void disabledPeriodic() {
 
        }
 
        public void autonomousInit() {
            enabledInit();
 
            
 
        }
 
        public void autonomousPeriodic() {
 
        }
 
        public void teleopInit() {
            enabledInit();
            //new CancelAllCommand().start(); maybe? depending on gameplay
 
           // LimelightNetworkTable.getInstance().lightLED(true);
           // LimelightNetworkTable.getInstance().lightLED(false);
        }
 
        public void teleopPeriodic() {
 
            dt = edu.wpi.first.wpilibj.Timer.getFPGATimestamp() - prevTime;
            prevTime = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();
            dtTot += dt;
            count++;
 
            if (count % 100 == 0) {
                //System.out.println(dtTot / 100);
                dtTot = 0;
                count = 0;
            }

            //tester.config_kF(0, SmartDashboard.getNumber("F TESTER", 0));
            //tester.config_kP(0, SmartDashboard.getNumber("P TESTER", 0));
            
            //tester.set(ControlMode.PercentOutput, SmartDashboard.getNumber("MOTOR PERCENT", 0));

            //SmartDashboard.putNumber("TESTER SENSOR POSITION", tester.getSensorCollection().getIntegratedSensorPosition());
        }

       /*public void CameraInit() {
            new Thread(() -> {
                UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
                camera.setResolution(720, 1080);
                
            }).start();
            
        }*/
 
        @Override
        public void testPeriodic() {

        } 
        @Override
        public void robotPeriodic() {
 
            CommandScheduler.getInstance().run();
            Periodic.runAll();
            SmartDashboardSource.runAll();
 
 
        }
 
        public void enabledInit() {

        }
    
        public Command getAutoCommand() {
           return new DoNothingCommand();
        }
 
        public double getPeriod() {
            return period;
        }
 
    }