#!/bin/bash

cd /usr/src/project

echo "Installing Node dependencies..."
npm install > /dev/null
echo "Node dependencies installed"

echo "Installing jspm dependencies..."
jspm install --yes > /dev/null
echo "jspm dependencies installed"

echo "Linking related projects..."
echo "Linking carbonldp-panel..."
jspm link carbonldp-panel/dist --yes > /dev/null
echo "carbonldp-panel linked"
echo "Related projects linked"

echo "Installing definition files..."
typings install
echo "Definition files installed"

gulp build --profile prod
