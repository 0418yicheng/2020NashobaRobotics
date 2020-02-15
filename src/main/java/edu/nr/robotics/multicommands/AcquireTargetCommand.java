package edu.nr.robotics.multicommands;

import edu.nr.lib.commandbased.NRCommand;
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.lib.network.LimelightNetworkTable;
import edu.nr.robotics.OI;
import edu.nr.robotics.subsystems.hood.Hood;
import edu.nr.robotics.subsystems.shooter.Shooter;
import edu.nr.robotics.subsystems.turret.Turret;

public class AcquireTargetCommand extends NRCommand
{
    public AcquireTargetCommand()
    {
        super(new NRSubsystem[] {Hood.getInstance(), Turret.getInstance()});
    }

    @Override
    protected void onExecute()
    {
        if(!OI.getInstance().getManualMode())
        {
            Shooter.getInstance().setMotorSpeed(Shooter.SHOOT_SPEED);

            Turret.getInstance().setAngle(LimelightNetworkTable.getInstance().getHorizOffset().add(Turret.getInstance().getAngle()));

            //Hood Limelight Command when done
        }
    }

    @Override
    protected boolean isFinishedNR()
    {
        if(OI.getInstance().getManualMode())
            return true;
        return false;
    }
}