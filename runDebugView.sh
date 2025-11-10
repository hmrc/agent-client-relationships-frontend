#!/usr/bin/env bash

sbt -Dapplication.router=testOnlyDoNotUseInAppConf.Routes -Ddebug.views=true run