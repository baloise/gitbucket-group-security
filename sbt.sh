#!/bin/sh
java -Dsbt.log.noformat=true -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -Xmx512M -Xss2M -jar `dirname $0`/sbt-launch-0.13.9.jar "$@"
cp target/scala-2.11/gitbucket-sec-extension_2.11-4.2.0.jar ~/.gitbucket/plugins
