package edu.nr.robotics.subsystems.transfer;
 
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
 
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.lib.motorcontrollers.CTRECreator;
import edu.nr.lib.units.Acceleration;
import edu.nr.lib.units.Angle;
import edu.nr.lib.units.AngularAcceleration;
import edu.nr.lib.units.AngularSpeed;
import edu.nr.lib.units.Distance;
import edu.nr.lib.units.Speed;
import edu.nr.lib.units.Time;
import edu.nr.robotics.RobotMap;
import edu.nr.robotics.subsystems.EnabledSubsystems;
import edu.nr.robotics.subsystems.sensors.EnabledSensors;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
public class Transfer extends NRSubsystem{
 
    private static Transfer singleton;
 
    private TalonSRX transferTalon;
 
    public static final int VOLTAGE_COMPENSATION_LEVEL = 12;
    public static final double MIN_MOVE_VOLTAGE = 0.0;
    public static final int DEFAULT_TIMEOUT = 0;
 
    public static final double PUKE_PERCENT = -0.9;
    public static final Time PUKE_TIME = new Time(1, Time.Unit.SECOND);
 
    public static double F_POS_TRANSFER = 0;
    public static double P_POS_TRANSFER = 0;
    public static double I_POS_TRANSFER = 0;
    public static double D_POS_TRANSFER = 0;
 
    public static double F_VEL_TRANSFER = 0;
    public static double P_VEL_TRANSFER = 0;
    public static double I_VEL_TRANSFER = 0;
    public static double D_VEL_TRANSFER = 0;
 
    public static final double PROFILE_VEL_PERCENT_TRANSFER = 0.6; //change
    public static final double PROFILE_ACCEL_PERCENT_TRANSFER = 0.6;
 
    public static final int PEAK_CURRENT_TRANSFER = 60;
    public static final int CONTINUOUS_CURRENT_LIMIT_TRANSFER = 40;
 
    public static final NeutralMode NEUTRAL_MODE_TRANSFER = NeutralMode.Brake;
 
    public static final int PID_TYPE = 0;
 
    public static final int VEL_SLOT = 0;
    public static final int POS_SLOT = 1;
 
    public static Time VOLTAGE_RAMP_RATE_TRANSFER = new Time(0.05, Time.Unit.SECOND);
 
    public static final AngularSpeed MAX_SPEED_TRANSFER = new AngularSpeed(2000, Angle.Unit.ROTATION, Time.Unit.SECOND);
    public static final AngularAcceleration MAX_ACCEL_TRANSFER = new AngularAcceleration(2000, Angle.Unit.ROTATION, Time.Unit.SECOND, Time.Unit.SECOND);
 
    public static final double ENCODER_TICKS_PER_INCH_BALL_MOVED = 400; // not realy... have to change this one
    public static final double ENCODER_TICKS_PER_DEGREE = 2048 / 360;
 
    public static Distance distanceSetPoint = Distance.ZERO;
    public static Distance deltaDistance = Distance.ZERO;
 
    public static AngularSpeed speedSetPoint = AngularSpeed.ZERO;
 
    public static final Distance TRANSFER_DISTANCE = new Distance(8, Distance.Unit.INCH); // change for real robot
    public static double goalSpeed = 0;
 
    public static final Time TRANSFER_TIME = new Time(0.1, Time.Unit.SECOND);
    public static final Time TRANSFER_ALL_TIME = new Time(1, Time.Unit.SECOND);
 
    public static final int TRANSFER_THRESHOLD = 0;
 
    //need sensors for have a ball, tune transfer percent and time for transfer command
    
    private Transfer(){
        if(EnabledSubsystems.TRANSFER_ENABLED){
            transferTalon = CTRECreator.createMasterTalon(RobotMap.TRANSFER_TALON);
 
            transferTalon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, PID_TYPE, DEFAULT_TIMEOUT);
 
            transferTalon.config_kF(POS_SLOT, F_POS_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kP(POS_SLOT, P_POS_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kI(POS_SLOT, I_POS_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kD(POS_SLOT, D_POS_TRANSFER, DEFAULT_TIMEOUT);
 
            transferTalon.config_kF(VEL_SLOT, F_VEL_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kP(VEL_SLOT, P_VEL_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kI(VEL_SLOT, I_VEL_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.config_kD(VEL_SLOT, D_VEL_TRANSFER, DEFAULT_TIMEOUT);
 
 
            transferTalon.setNeutralMode(NEUTRAL_MODE_TRANSFER);
 
            transferTalon.setInverted(false);
            //Change to Talon Version
            transferTalon.setSensorPhase(false);
 
            transferTalon.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT_TRANSFER);
 
            transferTalon.enableVoltageCompensation(true);
            transferTalon.configVoltageCompSaturation(VOLTAGE_COMPENSATION_LEVEL, DEFAULT_TIMEOUT);
            
            transferTalon.enableCurrentLimit(true);
            transferTalon.configPeakCurrentLimit(PEAK_CURRENT_TRANSFER, DEFAULT_TIMEOUT);
            transferTalon.configPeakCurrentDuration(CONTINUOUS_CURRENT_LIMIT_TRANSFER, DEFAULT_TIMEOUT);
 
            transferTalon.configClosedloopRamp(VOLTAGE_RAMP_RATE_TRANSFER.get(Time.Unit.SECOND), DEFAULT_TIMEOUT);
            transferTalon.configOpenloopRamp(VOLTAGE_RAMP_RATE_TRANSFER.get(Time.Unit.SECOND), DEFAULT_TIMEOUT);
            transferTalon.getSensorCollection().setQuadraturePosition(0, DEFAULT_TIMEOUT);
 
            if(EnabledSubsystems.TRANSFER_DUMB_ENABLED){
                transferTalon.set(ControlMode.PercentOutput, 0);
            }else{
                transferTalon.set(ControlMode.Velocity, 0);
            }
 
        }
        smartDashboardInit();
 
    }
 
    public static Transfer getInstance(){
        if(singleton == null){
            init();
        }
        return singleton;
    }
 
    private synchronized static void init(){
        if(singleton == null){
            singleton = new Transfer();
        }
    }
 
    public void disable(){
        if(transferTalon != null){
            transferTalon.set(ControlMode.PercentOutput, 0);
            //set position to current position
        }
    }
 
    public void smartDashboardInit(){
        if(EnabledSubsystems.TRANSFER_SMARTDASHBOARD_DEBUG_ENABLED){
 
            if(transferTalon != null){
                SmartDashboard.putNumber("Transfer Encoder Position", transferTalon.getSelectedSensorPosition());
                SmartDashboard.putNumber("Transfer Set Position", distanceSetPoint.get(Distance.Unit.INCH));
                
                SmartDashboard.putNumber("F_POS_TRANSFER: ", F_POS_TRANSFER);
                SmartDashboard.putNumber("P_POS_TRANSFER: ", P_POS_TRANSFER);
                SmartDashboard.putNumber("I_POS_TRANSFER: ", I_POS_TRANSFER);
                SmartDashboard.putNumber("D_POS_TRANSFER: ", D_POS_TRANSFER);
 
                SmartDashboard.putNumber("F_VEL_TRANSFER: ", F_VEL_TRANSFER);
                SmartDashboard.putNumber("P_VEL_TRANSFER: ", P_VEL_TRANSFER);
                SmartDashboard.putNumber("I_VEL_TRANSFER: ", I_VEL_TRANSFER);
                SmartDashboard.putNumber("D_VEL_TRANSFER: ", D_VEL_TRANSFER);
 
                SmartDashboard.putNumber("Transfer Delta Position", deltaDistance.get(Distance.Unit.INCH));
                SmartDashboard.putNumber("Transfer Goal Speed", goalSpeed);
 
            }
        }
    }
 
    public void smartDashboardInfo(){
 
        if(EnabledSubsystems.TRANSFER_SMARTDASHBOARD_DEBUG_ENABLED){
            if(transferTalon != null){
 
                F_POS_TRANSFER = SmartDashboard.getNumber("F_POS_TRANSFER: ", F_POS_TRANSFER);
                P_POS_TRANSFER = SmartDashboard.getNumber("P_POS_TRANSFER: ", P_POS_TRANSFER);
                I_POS_TRANSFER = SmartDashboard.getNumber("I_POS_TRANSFER: ", I_POS_TRANSFER);
                D_POS_TRANSFER = SmartDashboard.getNumber("D_POS_TRANSFER: ", D_POS_TRANSFER);
 
                F_VEL_TRANSFER = SmartDashboard.getNumber("F_VEL_TRANSFER: ", F_VEL_TRANSFER);
                P_VEL_TRANSFER = SmartDashboard.getNumber("P_VEL_TRANSFER: ", P_VEL_TRANSFER);
                I_VEL_TRANSFER = SmartDashboard.getNumber("I_VEL_TRANSFER: ", I_VEL_TRANSFER);
                D_VEL_TRANSFER = SmartDashboard.getNumber("D_VEL_TRANSFER: ", D_VEL_TRANSFER);
 
                SmartDashboard.putNumber("Transfer Current", transferTalon.getStatorCurrent());
 
                deltaDistance = new Distance(SmartDashboard.getNumber("Transfer Delta Position", deltaDistance.get(Distance.Unit.INCH)), Distance.Unit.INCH);
                goalSpeed = SmartDashboard.getNumber("Transfer Goal Speed", goalSpeed);
                
                SmartDashboard.putBoolean("Transfer Sensor", EnabledSensors.TransferSensor.get());
            }
        }
    }
 
    public int getEncoderPosition(){
        if(transferTalon != null){
            return transferTalon.getSelectedSensorPosition();
        }
        return 0;
    }
 
    public void setMotorSpeedInPercent(double percent){
        if(transferTalon != null){
            transferTalon.set(ControlMode.PercentOutput, percent);
            speedSetPoint = MAX_SPEED_TRANSFER.mul(percent);
        }
    }
 
    public AngularSpeed getSpeed(){
        if(transferTalon != null){
            return new AngularSpeed(transferTalon.getSelectedSensorVelocity(), Angle.Unit.TRANSFER_ENCODER_TICK, Time.Unit.HUNDRED_MILLISECOND);
        }
        return AngularSpeed.ZERO;
    }
 
    public void setSpeed(AngularSpeed targetSpeed){
        if(transferTalon != null){
            speedSetPoint = targetSpeed;
 
            transferTalon.set(ControlMode.PercentOutput, targetSpeed.div(MAX_SPEED_TRANSFER));
        }
    }
 
    public void periodic(){
        //check sensors and see if we can kick a ball into the indexer
        //Yo soy intelligente.
 
    }
 
    public boolean hasBall(){
        return EnabledSensors.TransferSensor.get();
    }
    
}
 
 