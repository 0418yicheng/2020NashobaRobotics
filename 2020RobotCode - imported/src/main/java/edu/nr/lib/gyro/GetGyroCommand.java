package edu.nr.lib.gyro;

import edu.nr.lib.commandbased.NRCommand;

public class GetGyroCommand extends NRCommand {
	
	public GetGyroCommand() {
		super();
	}

	GyroCorrection gyro;
	
	protected void onStart() {
		gyro = new GyroCorrection();
	}

	public GyroCorrection getCorrection() {
		return gyro;
	}
}
