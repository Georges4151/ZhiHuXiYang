#!/bin/bash

APP_NAME="elderly"
JAR_FILE="elderly-0.0.1-SNAPSHOT.jar"
LOG_FILE="app.log"

echo "===== 启动应用 ====="

# 停止旧进程
PID=$(ps -ef | grep $JAR_FILE | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "停止旧进程: $PID"
    kill -9 $PID
fi

# 启动新进程
echo "启动新进程..."
nohup java -Xms256m -Xmx512m -jar $JAR_FILE > $LOG_FILE 2>&1 &

echo "应用已启动，查看日志: tail -f $LOG_FILE"
