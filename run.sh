#!/bin/bash

workingDir=$(dirname $0)
( cd $workingDir && java -jar target/cat-api-1.0-SNAPSHOT-jar-with-dependencies.jar $1 )