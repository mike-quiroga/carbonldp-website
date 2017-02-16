#!/bin/bash

npm install

# TODO: This shouldn't be needed. Find out why it's not normally being executed after npm install
npm run-script postinstall

node_modules/.bin/gulp