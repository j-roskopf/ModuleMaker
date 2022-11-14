#!/bin/zsh

# This script will generate a new jar and run it. Make sure to execute it from this directory, and not the root directory.

# change file permissions for build folder
chmod 755 build/compose/jars

# delete current build if it exists - we assume only 1 jar at the end
rm -rf build/compose/jars/*

# package jar for current OS
./gradlew packageUberJarForCurrentOS

# store the name of the generated jar. this is a versioned jar, so we could hard code it, but would have to update every time we create a new version.
JAR_NAME=$(ls build/compose/jars)

# move it to the base directory - since a lot of the pathing is assumed / hard coded in module maker, this is how it has to be for the moment
cp -rp build/compose/jars/* ./

# run the jar
java -jar "$JAR_NAME"
