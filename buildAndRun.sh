#!/bin/bash

workingDir=$(dirname $0)
( cd $workingDir && ./build.sh && ./run.sh $1 )