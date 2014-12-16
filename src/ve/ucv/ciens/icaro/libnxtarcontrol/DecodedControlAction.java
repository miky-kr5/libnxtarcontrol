/* Copyright (c) 2014, Miguel Angel Astor Romero
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 */
package ve.ucv.ciens.icaro.libnxtarcontrol;

/**
 * <p>An immutable and pure data class that represents an action decoded from a protocol
 * data message.</p>
 * 
 * @author Miguel Angel Astor Romero
 * @version 1.0
 * @since December 15, 2014
 */
public class DecodedControlAction{
	/**
	 * <p>All recognized actions.</p>
	 * 
	 * @author Miguel Angel Astor Romero
	 * @version 1.0
	 * @since December 16, 2014
	 */
	public enum Action{
		MOVE_FORWARD, MOVE_BACKWARDS, STOP, RECENTER, USER_1, USER_2, USER_3;
	}

	/**
	 * <p>All motor ports and possible combinations without repetitions.<p>
	 * 
	 * @author Miguel Angel Astor Romero
	 * @version 1.0
	 * @since December 16, 2014
	 */
	public enum Motor{
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_AB, MOTOR_AC, MOTOR_BC, MOTOR_ABC;
	}

	// Data fields.
	public final Action action;
	public final Motor motor;
	public final int speed;

	/**
	 * <p>Create a new ControlAction object using {@link DecodedControlAction#STOP} as
	 * the default action, {@link DecodedControlAction#MOTOR_ABC} as motor flag, and
	 * 0 as default speed.</p>
	 */
	public DecodedControlAction(){
		this(Action.STOP, Motor.MOTOR_ABC, 0);
	}

	/**
	 * <p>Create a new ControlAction object using the specified action. The motor 
	 * flag is set to {@link DecodedControlAction#MOTOR_ABC} and the speed is set to 100.</p>
	 * 
	 * @param action The action flag to set.
	 */
	public DecodedControlAction(Action action){
		this(action, Motor.MOTOR_ABC, 100);
	}

	/**
	 * <p>Create a new ControlAction object using the specified action and motor flag. The
	 * speed is set to 100.</p>
	 * 
	 * @param action The action flag to set.
	 * @param motor The motor flag to set.
	 */
	public DecodedControlAction(Action action, Motor motor){
		this(action, motor, 100);
	}

	/**
	 * <p>Create a new ControlAction object using the specified action, motor flag and speed.</p>
	 * 
	 * @param action The action flag to set.
	 * @param motor The motor flag to set.
	 * @param speed The speed to set. Will be clamped to the range [-100, 100].
	 */
	public DecodedControlAction(Action action, Motor motor, int speed){
		this.action = action;
		this.motor  = motor;
		this.speed  = speed < -100 ? -100 : (speed > 100 ? 100 : speed);
	}
}