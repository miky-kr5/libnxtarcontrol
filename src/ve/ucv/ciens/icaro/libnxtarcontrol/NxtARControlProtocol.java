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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;

import lejos.nxt.BasicMotorPort;
import lejos.nxt.Battery;
import ve.ucv.ciens.icaro.libnxtarcontrol.DecodedControlAction.Action;
import ve.ucv.ciens.icaro.libnxtarcontrol.DecodedControlAction.Motor;

/**
 * <p>A wrapper around the NxtAR robot control protocol for the LejOS operating system.</p>
 * 
 * @see <a href="http://www.lejos.org">The LejOS operating system.</a>
 * @see <a href="https://github.com/sagge-miky/NxtAR-core">NxtAR-core Github repository.</a>
 * @author Miguel Angel Astor Romero
 * @version 1.0.1
 * @since December 15, 2014
 */
public class NxtARControlProtocol {
	private enum MotorPort{
		MOTOR_A, MOTOR_B, MOTOR_C;
	}

	private enum MotorAction{
		FORWARD, BACKWARD, STOP;
	}

	// Binary flags used for decoding messages.
	// Motor ports.
	private static final byte MOTOR_A     = (byte)0x01;
	private static final byte MOTOR_B     = (byte)0x02;
	private static final byte MOTOR_C     = (byte)0x04;
	// Direction of movement (forward or backward).
	private static final byte DIRECTION   = (byte)0x08;
	// Possible actions.
	private static final byte RECENTER    = (byte)0x10;
	private static final byte USER_1      = (byte)0x20;
	private static final byte USER_2      = (byte)0x40;
	private static final byte USER_3      = (byte)0x80;

	private DataInputStream inputStream;
	private LinkedList<UserActionListener> userActionListeners;

	/**
	 * <p>Create a new ARControl object.</p> 
	 * 
	 * @param inputStream An opened input stream used to read protocol messages from.
	 * @throws IllegalArgumentException If inputStream is null.
	 */
	public NxtARControlProtocol(final DataInputStream inputStream) throws IllegalArgumentException{
		if(inputStream == null)
			throw new IllegalArgumentException("Input stream is null.");

		this.inputStream = inputStream;
		this.userActionListeners = new LinkedList<UserActionListener>();
	}

	/**
	 * <p>Changes the input stream associated with this ARControl for the input stream passed as
	 * parameter. The currently set input stream is closed before replacing it.</p>
	 * 
	 * @param inputStream An opened input stream.
	 * @throws IOException If an error happened while closing the previous input stream.
	 * @throws IllegalArgumentException If the input stream is null.
	 */
	public void setInputStream(DataInputStream inputStream) throws IOException, IllegalArgumentException{
		if(inputStream == null)
			throw new IllegalArgumentException("Input stream is null.");

		try{
			this.inputStream.close();
		}catch(IOException io){
			throw io;
		}

		this.inputStream = null;
		this.inputStream = inputStream;
	}

	/**
	 * <p>Attempts to read a 2-byte message and returns it as is.</p>
	 * 
	 * @return The two bytes read from the associated connection as an array.
	 * @throws IOException If reading the message fails. It is the same IOException
	 * as thrown by {@link DataInput#readByte()} if any.
	 */
	public byte[] readRawControlMessage() throws IOException{
		byte[] msg = new byte[2];
		try{
			synchronized (inputStream) {
				msg[0] = inputStream.readByte();
				msg[1] = inputStream.readByte();
			}
		}catch (IOException io){
			throw io;
		}
		return msg;
	}

	/**
	 * <p>Attempts to read, decode and execute a message, calling the user operation
	 * listeners if needed.</p>
	 *
	 * @return True if a message could be read, decoded and executed successfully. False otherwise.
	 * @throws IOException If reading the message fails. It is the same IOException
         * as thrown by {@link DataInput#readByte()} if any.
	 */
	public boolean readAndExecuteMessage() throws IOException{
		boolean success = false;
		byte[] msg;
		DecodedControlAction controlAction;

		try{
			msg = readRawControlMessage();
		}catch(IOException io){
			msg = null;
			throw io;
		}

		if(msg != null){
			try{
				controlAction = decodeMessage(msg);
			}catch(IllegalArgumentException ia){
				controlAction = null;
			}

			if(controlAction != null){
				switch(controlAction.action){
				case MOVE_BACKWARDS:
					switch(controlAction.motor){
					case MOTOR_A:
						controlMotor(MotorPort.MOTOR_A, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_AB:
						controlMotor(MotorPort.MOTOR_A, MotorAction.BACKWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_ABC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.BACKWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.BACKWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_AC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.BACKWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_B:
						controlMotor(MotorPort.MOTOR_B, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_BC:
						controlMotor(MotorPort.MOTOR_B, MotorAction.BACKWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.BACKWARD, controlAction.speed);
						break;
					case MOTOR_C:
						controlMotor(MotorPort.MOTOR_C, MotorAction.BACKWARD, controlAction.speed);
						break;
					}
					success = true;
					break;

				case MOVE_FORWARD:
					switch(controlAction.motor){
					case MOTOR_A:
						controlMotor(MotorPort.MOTOR_A, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_AB:
						controlMotor(MotorPort.MOTOR_A, MotorAction.FORWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_ABC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.FORWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.FORWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_AC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.FORWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_B:
						controlMotor(MotorPort.MOTOR_B, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_BC:
						controlMotor(MotorPort.MOTOR_B, MotorAction.FORWARD, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.FORWARD, controlAction.speed);
						break;
					case MOTOR_C:
						controlMotor(MotorPort.MOTOR_C, MotorAction.FORWARD, controlAction.speed);
						break;
					}
					success = true;
					break;

				case RECENTER:
					switch(controlAction.motor){
					case MOTOR_A:
						recenterMotor(MotorPort.MOTOR_A);
						break;
					case MOTOR_AB:
						recenterMotor(MotorPort.MOTOR_A);
						recenterMotor(MotorPort.MOTOR_B);
						break;
					case MOTOR_ABC:
						recenterMotor(MotorPort.MOTOR_A);
						recenterMotor(MotorPort.MOTOR_B);
						recenterMotor(MotorPort.MOTOR_C);
						break;
					case MOTOR_AC:
						recenterMotor(MotorPort.MOTOR_A);
						recenterMotor(MotorPort.MOTOR_C);
						break;
					case MOTOR_B:
						recenterMotor(MotorPort.MOTOR_B);
						break;
					case MOTOR_BC:
						recenterMotor(MotorPort.MOTOR_B);
						recenterMotor(MotorPort.MOTOR_C);
						break;
					case MOTOR_C:
						recenterMotor(MotorPort.MOTOR_C);
						break;
					}
					success = true;
					break;

				case STOP:
					switch(controlAction.motor){
					case MOTOR_A:
						controlMotor(MotorPort.MOTOR_A, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_AB:
						controlMotor(MotorPort.MOTOR_A, MotorAction.STOP, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_ABC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.STOP, controlAction.speed);
						controlMotor(MotorPort.MOTOR_B, MotorAction.STOP, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_AC:
						controlMotor(MotorPort.MOTOR_A, MotorAction.STOP, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_B:
						controlMotor(MotorPort.MOTOR_B, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_BC:
						controlMotor(MotorPort.MOTOR_B, MotorAction.STOP, controlAction.speed);
						controlMotor(MotorPort.MOTOR_C, MotorAction.STOP, controlAction.speed);
						break;
					case MOTOR_C:
						controlMotor(MotorPort.MOTOR_C, MotorAction.STOP, controlAction.speed);
						break;
					}
					success = true;
					break;

				case USER_1:
				case USER_2:
				case USER_3:
					notifyListeners(controlAction.action, controlAction.motor, controlAction.speed);
					success = true;
					break;
				}
			}
		}

		return success;
	}

	/**
	 * <p>Decodes a protocol message encoded as a byte array of two elements as specified
	 * in the package definition.</p>
	 * 
	 * <p>User actions have precedence over motor recentering and
	 * this in turn has precedence over other movement actions.
	 * User actions have precedence in decreasing order; that is, user action 1 has
	 * precedence over user actions 2 and 3, etc.</p>
	 * 
	 * <p>If the message indicates a movement (forward or backward) with all motors off,
	 * then it is interpreted as a request to stop all motors. A recenter or user action
	 * with all motors off will be decoded as is and must be interpreted by the user.</p>
	 * 
	 * @param message A byte array of size two encoding a message recognized by the protocol. If the array
	 * has 3 or more elements then only the first 2 are used during the decoding process. 
	 * @return A {@link DecodedControlAction} instance containing the decoded message.
	 * @throws IllegalArgumentException If the array is null or has less than 2 elements.
	 */
	public DecodedControlAction decodeMessage(byte[] message) throws IllegalArgumentException{
		Action action = Action.STOP;
		Motor motor = Motor.MOTOR_ABC;
		DecodedControlAction controlAction;

		if(message == null)
			throw new IllegalArgumentException("Message is null.");

		if(message.length < 2)
			throw new IllegalArgumentException("Message is too short. Length of message is " + message.length);

		// Decode the message.
		boolean motorA    = (message[0] & MOTOR_A)   > 0 ? true : false;
		boolean motorB    = (message[0] & MOTOR_B)   > 0 ? true : false;
		boolean motorC    = (message[0] & MOTOR_C)   > 0 ? true : false;
		boolean recenter  = (message[0] & RECENTER)  > 0 ? true : false;
		boolean user1     = (message[0] & USER_1)    > 0 ? true : false;
		boolean user2     = (message[0] & USER_2)    > 0 ? true : false;
		boolean user3     = (message[0] & USER_3)    > 0 ? true : false;
		int     direction = (message[0] & DIRECTION) > 0 ? BasicMotorPort.FORWARD : BasicMotorPort.BACKWARD;

		// Set the action flag. 
		if(user1 || user2 || user3){
			if     (user1) action = Action.USER_1;
			else if(user2) action = Action.USER_2;
			else if(user3) action = Action.USER_3;
		}else if(recenter){
			action = Action.RECENTER;
		}else{
			if(direction == BasicMotorPort.FORWARD){
				action = Action.MOVE_FORWARD;
			}else if(direction == BasicMotorPort.BACKWARD){
				action = Action.MOVE_BACKWARDS;
			}
		}

		// Set the motor flag.
		if(motorA && motorB && motorC){
			motor = Motor.MOTOR_ABC;
		}else if(motorA && motorB && !motorC){
			motor = Motor.MOTOR_AB;
		}else if(motorA && !motorB && motorC){
			motor = Motor.MOTOR_AC;
		}else if(!motorA && motorB && motorC){
			motor = Motor.MOTOR_BC;
		}else if(motorA && !motorB && !motorC){
			motor = Motor.MOTOR_A;
		}else if(!motorA && motorB && !motorC){
			motor = Motor.MOTOR_B;
		}else if(!motorA && !motorB && motorC){
			motor = Motor.MOTOR_C;
		}

		// Check for stop condition.
		if(action == Action.MOVE_FORWARD || action == Action.MOVE_BACKWARDS){
			if(!motorA && !motorB && !motorC){
				action = Action.STOP;
				motor = Motor.MOTOR_ABC;
			}
		}

		controlAction = new DecodedControlAction(action, motor, message[1]);

		return controlAction;
	}

	/**
	 * <p>Adds an {@link UserActionListener} to this object's listeners list calling it's 
	 * {@link UserActionListener#onListenerRegistered()} method. Adding a listener that
	 * is already registered does nothing.</p>
	 * 
	 * @param listener The listener to add.
	 * @throws IllegalArgumentException If listener is null.
	 */
	public synchronized void registerUserActionListener(UserActionListener listener) throws IllegalArgumentException{
		if(listener == null)
			throw new IllegalArgumentException("Listener is null.");

		if(!userActionListeners.contains(listener)){
			userActionListeners.add(listener);
			listener.onListenerRegistered();
		}
	}

	/**
	 * <p>Removes an {@link UserActionListener} from this object's listeners list calling it's 
	 * {@link UserActionListener#onListenerRemoved()} method. Removing a listener that
	 * is NOT on the list does nothing.</p>
	 * 
	 * @param listener The listener to remove.
	 * @throws IllegalArgumentException If listener is null.
	 */
	public synchronized void removeUserActionListener(UserActionListener listener) throws IllegalArgumentException{
		if(listener == null)
			throw new IllegalArgumentException("Listener is null.");

		if(userActionListeners.contains(listener)){
			userActionListeners.remove(listener);
			listener.onListenerRemoved();
		}
	}

	/**
	 * <p>Iterates over all the registered {@link UserActionListener} objects calling the methods
	 * respective to the {@link Action} received. Only user actions are processed, all other actions are
	 * ignored.</p>
	 * 
	 * @param userAction The action that triggered the notification.
	 */
	private void notifyListeners(Action userAction, Motor motorFlag, int speed){
		switch(userAction){
		case USER_1:
			for(UserActionListener listener : userActionListeners)
				listener.onUserAction1(motorFlag, speed);
			break;

		case USER_2:
			for(UserActionListener listener : userActionListeners)
				listener.onUserAction2(motorFlag, speed);
			break;

		case USER_3:
			for(UserActionListener listener : userActionListeners)
				listener.onUserAction3(motorFlag, speed);
			break;

		default:
			break;
		}
	}

	/**
	 * <p>Issues a movement command to the specified motor.</p>
	 * 
	 * @param port The motor port to control.
	 * @param action The direction of movement.
	 * @param speed The speed of movement.
	 */
	private void controlMotor(MotorPort port, MotorAction action, int speed){
		switch(port){
		case MOTOR_A:
			lejos.nxt.Motor.A.setSpeed(speed * Battery.getVoltage());
			switch(action){
			case BACKWARD:
				lejos.nxt.Motor.A.backward();
				break;
			case FORWARD:
				lejos.nxt.Motor.A.forward();
				break;
			case STOP:
				lejos.nxt.Motor.A.stop(true);
				break;
			}
			break;

		case MOTOR_B:
			lejos.nxt.Motor.B.setSpeed(speed * Battery.getVoltage());
			switch(action){
			case BACKWARD:
				lejos.nxt.Motor.B.backward();
				break;
			case FORWARD:
				lejos.nxt.Motor.B.forward();
				break;
			case STOP:
				lejos.nxt.Motor.B.stop(true);
				break;
			}
			break;

		case MOTOR_C:
			lejos.nxt.Motor.C.setSpeed(speed * Battery.getVoltage());
			switch(action){
			case BACKWARD:
				lejos.nxt.Motor.C.backward();
				break;
			case FORWARD:
				lejos.nxt.Motor.C.forward();
				break;
			case STOP:
				lejos.nxt.Motor.C.stop(true);
				break;
			}
			break;
		}
	}

	/**
	 * <p>Returns a motor to it's initial position. This method blocks until
	 * the motor is on position.</p>
	 * 
	 * @param port The motor to recenter.
	 */
	private void recenterMotor(MotorPort port){
		int rotation, tacho;

		switch(port){
		case MOTOR_A:
			lejos.nxt.Motor.A.setSpeed(50 * Battery.getVoltage());
			tacho = lejos.nxt.Motor.A.getTachoCount() % 360;
			rotation = -(tacho);
			lejos.nxt.Motor.A.rotate(rotation, false);
			break;

		case MOTOR_B:
			lejos.nxt.Motor.B.setSpeed(50 * Battery.getVoltage());
			tacho = lejos.nxt.Motor.B.getTachoCount() % 360;
			rotation = -(tacho);
			lejos.nxt.Motor.B.rotate(rotation, false);
			break;

		case MOTOR_C:
			lejos.nxt.Motor.C.setSpeed(50 * Battery.getVoltage());
			tacho = lejos.nxt.Motor.C.getTachoCount() % 360;
			rotation = -(tacho);
			lejos.nxt.Motor.C.rotate(rotation, false);
			break;
		}
	}
}
