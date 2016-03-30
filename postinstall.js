/**
 * Executes post install operations in a cross-platform compatible way.
 * This script is called from package.json postinstall hook.
 * Tested in Windows 10 and Linux VM
 */

console.log('>> Executing postinstall.js...');

const exec = require('child_process').exec;
const path = require('path');

var jspmPath = path.resolve('.','node_modules','.bin','jspm');
var gulpPath = path.resolve('.','node_modules','.bin','gulp');
var command; 

function executeCommand(cmd) {
	console.log('-- postinstall.js: ' + cmd);
	exec(cmd);
}

executeCommand( jspmPath + ' config registries.github.remote https://github.jspm.io' );
executeCommand( jspmPath + ' config registries.github.auth Q2FyYm9uRGVwbG95ZXI6NGMxZjMzYTMyZTI3OGU0ODBmZmY2MDg1MWEyNTZlMzcxNzJkZTllOA==' );
executeCommand( jspmPath + ' config registries.github.maxRepoSize 0' );
executeCommand( jspmPath + ' config registries.github.handler jspm-github' );
executeCommand( jspmPath + ' install' );
executeCommand( gulpPath + ' copy-node-dependencies' );

console.log('-- postinstall.js is wrapping up...');