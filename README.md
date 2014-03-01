This program is called Qingxiang Jia's Magic Piano. When you open the program, use W, A, S, D, I, and O to
navigate (I = zoom in, O = zoom out). Press key 1 to 7 will cause the piano's keys been
pressed, and by doing this you can make nice music! The idea is totally original. The music
sound is create by GarageBand, legally obtained by me, and then processed by Audacity.
All the objects except the spiral, are created by me using blender. The spiral is obtained
from the Turbo Squid, by Will_LaPuerta, free of charge.

The Face.java, Shape.java, and ShapeLoader.java are created by me learning the LWJGL official
tutorial. However, I did not copy the exact code from the tutorial, instead, I built my own code
from scratch.

I have tested that the sound DOES NOT work on the CLIC linux machines. The reason is that Linux uses
PulseAudio to handle the code, and however, the supoort is not concrete on Linux. As Professor Zheng
suggested, I have made sure that my program runs just fine without sound (I mean, yes, there will
be some warining on terminal output telling you there is some problem with the audio.), but the program
will not be interrupted and all the graphics are rendered as expected. To have the sound, please test
the program on OS X. Thank you for grading!

Compiling instructions:
On CLIC Linux machines, use the following lines for compiling and running:

javac -cp ".:./lwjgl-2.9.1/jar/lwjgl.jar:./lwjgl-2.9.1/jar/lwjgl_util.jar" MainProgram.java

java -cp ".:./lwjgl-2.9.1/jar/lwjgl.jar:./lwjgl-2.9.1/jar/lwjgl_util.jar:./lwjgl-2.9.1/native/linux" -Djava.library.path=./lwjgl-2.9.1/native/linux MainProgram

Becasue Professor Zheng said he (or the TA) will run the program on OS X just to test the sound, so I also
include here the comamnds for compilling and running the program on a mac:

javac -cp ".:./lwjgl-2.9.1/jar/lwjgl.jar:./lwjgl-2.9.1/jar/lwjgl_util.jar" MainProgram.java

java -cp ".:./lwjgl-2.9.1/jar/lwjgl.jar:./lwjgl-2.9.1/jar/lwjgl_util.jar:./lwjgl-2.9.1/native/macosx" -Djava.library.path=./lwjgl-2.9.1/native/macosx MainProgram
