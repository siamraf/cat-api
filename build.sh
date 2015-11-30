#!/bin/bash

workingDir=$(dirname $0)
( cd $workingDir && mvn clean package )