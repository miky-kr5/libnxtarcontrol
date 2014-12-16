/**
 * <p>This package contains a wrapper library and reference implementation of the NxtAR
 * robot control protocol for the LejOS operating system.</p>
 * 
 * <p>The protocol is based on 2 byte long data messages that encode a set of motor ports, actions
 * on those motors and the speed of the motors. The first byte encodes a set actions and motors, while the
 * second byte contains an integer number representing the speed to set to the enabled motors. The first
 * byte's bits encode the following data (from least significant to most significant bit): </p>
 * 
 * <ul>
 * 	<li>Motor port A enabled.</li>
 * 	<li>Motor port B enabled.</li>
 * 	<li>Motor port C enabled.</li>
 * 	<li>Direction of movement. 0 for backward movement, 1 for forward movement.</li>
 * 	<li>Request to return the enabled motors to their initial positions.</li>
 * 	<li>Customizable user action 1.</li>
 * 	<li>Customizable user action 2.</li>
 * 	<li>Customizable user action 3.</li>
 * </ul>
 * 
 * <p>This bits are decoded using the binary masks defined in {@link ve.ucv.ciens.icaro.libnxtarcontrol.NxtARControlProtocol}.
 * This mechanism allows to set the state or execute an action on multiple motors simultaneously. The user actions are
 * executed using callback objects registered with the aforementioned class.</p>
 * 
 * <p>The {@link ve.ucv.ciens.icaro.libnxtarcontrol.NxtARControlProtocol} class includes methods to
 * read, decode and execute a protocol message, independently of how those messages are generated. An example
 * would be messages read from a Bluetooth or USB stream, as well as messages generated by the application
 * that is using the library.</p>
 */
package ve.ucv.ciens.icaro.libnxtarcontrol;