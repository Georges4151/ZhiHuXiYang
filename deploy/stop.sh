#!/bin/bash

JAR_FILE="elderly-0.0.1-SNAPSHOT.jar"

echo "===== 停止应用 ====="

PID=$(ps -ef | grep $JAR_FILE | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "停止进程: $PID"
    kill -9 $PID
    echo "应用已停止"
else
    echo "应用未运行"
fi
