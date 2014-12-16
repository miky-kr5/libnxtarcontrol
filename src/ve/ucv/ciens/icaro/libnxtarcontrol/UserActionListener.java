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

import ve.ucv.ciens.icaro.libnxtarcontrol.DecodedControlAction.Action;
import ve.ucv.ciens.icaro.libnxtarcontrol.DecodedControlAction.Motor;

/**
 * <p>An object to be notified when an user action has been received in a protocol message.</p>
 * 
 * @author Miguel Angel Astor Romero
 * @version 1.0
 * @since December 16, 2014
 */
public interface UserActionListener {
	/**
	 * <p>Executes a set of instructions just after the listener has been registered with an {@link NxtARControlProtocol instance}.</p>
	 */
	public void onListenerRegistered();

	/**
	 * <p>Executes a set of instructions when a {@link DecodedControlAction} is decoded with {@link Action#USER_1}.</p>
	 */
	public void onUserAction1(Motor motorFlag, int speed);

	/**
	 * <p>Executes a set of instructions when a {@link DecodedControlAction} is decoded with {@link Action#USER_2}.</p>
	 */
	public void onUserAction2(Motor motorFlag, int speed);

	/**
	 * <p>Executes a set of instructions when a {@link DecodedControlAction} is decoded with {@link Action#USER_3}.</p>
	 */
	public void onUserAction3(Motor motorFlag, int speed);

	/**
	 * <p>Executes a set of instructions just after the listener has been removed from an {@link NxtARControlProtocol instance}.</p>
	 */
	public void onListenerRemoved();
}
