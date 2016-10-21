# carbonldp-website

[![Build Status](https://travis-ci.org/CarbonLDP/carbonldp-website.svg)](https://travis-ci.org/CarbonLDP/carbonldp-website)

Source code for [carbonldp.com](https://carbonldp.com)

## Development 

### Setup

1. Install dependencies
    - [node.js](https://nodejs.org/en/)
    - gulp: `npm install gulp -g`
    - jspm: `npm install jspm -g`
2. (optional) Clone dependency projects alongside the project's directory:
    - CarbonLDP-JS-SDK: `gulp clone https://github.com/CarbonLDP/CarbonLDP-JS-SDK.git`
    - carbonldp-panel: `gulp clone https://github.com/CarbonLDP/carbonldp-panel.git`
    - angular2-carbonldp: `gulp clone https://github.com/CarbonLDP/angular2-carbonldp.git`
    
    You should end up with the following directories in the same directory:
    - `carbonldp-website`
    - `carbonldp-panel`
    - `CarbonLDP-JS-SDK`
    - `angular2-carbonldp`
    
    After this, cd into `carbonldp-website`
3. run `npm install`
4. run `jspm install`
5. (optional) To make changes to any dependency project visible to this project, you must link them using the following commands:
    - `jspm link ../carbonldp-panel/dist`
    - `jspm link ../CarbonLDP-JS-SDK/dist`
    - `jspm link ../angular2-carbonldp/dist`
    
    Answer `no` to any change JSPM suggests to make follow by a `yes` when it asks to confirm the decision.
    For more information see: [JSPM link](http://jspm.io/0.17-beta-guide/linking.html)
6. To start the application server run `npm start` or `gulp serve`

### Gulp Tasks

Gulp defines two tasks:

1. `build`: Same as `npm install`. Build the source code and prepare it for production (inside of the dist/ folder).
2. `serve`: Same as `npm start`. Will start a live-server instance with the source code.

### File Structure

- `.idea`: WebStorm shared configuration files (things like code style, and project structure)
- `config`: Configuration files that are used when compiling the application
- `dist`: Distribution related files
    - `site`: Compiled files. Ready to be served
    - `index.ejs.html`: Template to create the compiled `site/index.html` file
    - `nginx.conf`: Configuration file for the nginx server inside the docker image
- `jspm_packages`: jspm dependencies (don't touch them)
- `node_modules`: npm dependencies (don't touch them)
- `src`: All source files
    - `app`: Source files for the Angular2 application
    - `assets`: Any asset (image, json, etc.). Before adding stylesheets think if they belong to a component, or can be added to the semantic-ui theme
        - `images`: General images
    - `semantic`: Source code for the semantic-ui theme
    - `index.html`: Entry point for the website
- `typings`: TypeScript description files (partly managed by [typings](https://github.com/typings/typings))
    - `custom`: Directory to store custom description files
    - `typings.d.ts`: Main description file. Aggregates all other description files
- `.gitignore`: Ignore file for git
- `.gitmodules`: git submodules configuration file
- `.travis.yml`: Travis configuration file
- `CHANGELOG.md`: File to track package changes
- `Dockerfile`: Docker file used to create the docker image
- `gulpfile.js`: Gulp configuration file
- `jspm.config.js`: JSPM configuration file
- `LICENSE`: Da rulz
- `package.json`: npm configuration file (it also contains JSPM dependency registry)
- `README.md`: === this
- `semantic.json`: semantic-ui configuration file
- `tsconfig.json`: TypeScript compiler configuration file
- `tslint.json`: [tslint](https://github.com/palantir/tslint) configuration file

## TODO

- Configure a test framework
- Configure code linting (tslint and sasslint)
- Document gulp tasks
- Divide gulpfile into separate files
