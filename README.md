NxtAR: A generic software architecture for Augmented Reality based mobile robot control.
========================================================================================

libnxtarcontrol
---------------

### Abstract ###

NxtAR is a generic software architecture for the development of Augmented Reality games
and applications centered around mobile robot control. This is a reference implementation
with support for [LEGO Mindstorms NXT][1] mobile robots.

### Module description ###

The **libnxtarcontrol** library is an stand-alone implementation of the robot control protocol
used by the different applications in the NxtAR reference implementation, in particular, the
control protocol used by the [NxtAR-bot][2] application. This library is intended for use with
the [LejOS][3] operating system.

### Module installation and usage. ###

Add the libnxtarcontrol_XXXXXX.jar file to your LejOS project's classpath and compile your
application normally, following the LejOS project [guidelines][4].

 [1]: http://www.lego.com/en-us/mindstorms/?domainredir=mindstorms.lego.com
 [2]: https://github.com/sagge-miky/NxtAR-bot
 [3]: http://www.lejos.org/nxj.php
 [4]: http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/CompileAndRun.htm
