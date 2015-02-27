#!/bin/bash

THISDIR=`dirname $0`
CLASSPATH=$THISDIR/lib/gluegen-rt.jar:$THISDIR/lib/jogl-all.jar:$THISDIR/lib/gluegen-rt-natives-linux-amd64.jar:$THISDIR/lib/jogl-all-natives-linux-amd64.jar:$THISDIR/bin/:$THISDIR/../gs-core-2.x/bin:$THISDIR/../gs-algo/bin

java -cp $CLASSPATH -ea org.graphstream.nui.Demo
