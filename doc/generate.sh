#! /bin/bash
# Executed from inside the doc directory so the relative paths work.

javadoc -d ./ -sourcepath ../Project/TaskMan/src/ -subpackages ca.cmput301.team13.taskman
