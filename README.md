# carbon-website
Source code for [carbonldp.com](https://carbonldp.com)

## Development Setup
1. Install Dependencies
    - [node.js](https://nodejs.org/en/)
    - gulp: `npm install gulp -g`
    - jspm: `npm install jspm -g`
2. cd into the project's root directory
3. run `npm install`
4. To start the application server run `npm start`

## Gulp Tasks

Gulp defines two tasks:
1. `build`: Same as `npm install`. Build the source code and prepare it for production (inside of the dist/ folder).
2. `serve`: Same as `npm start`. Will start a live-server instance with the source code.

## File Structure
- `dist`: Compiled files. Ready to be served.
- `node_modules`: npm dependencies (don't touch them)
- `scripts`: Auxiliary directory to store npm scripts
    - `copy-hooks.js`: npm script that will copy the `pre-commit` file to `.git`
    - `pre-commit`: bash script triggered before a `git commmit`. Will build the application and add everything in the `dist` folder to the commit.
- `src`: All source files
    - `app`: Source files for the Angular2 application
    - `assets`: Any asset (image, json, etc.). Before adding stylesheets think if they belong to a component, or can be added to the semantic-ui theme
        - `images`: General images
    - `jspm_packages`: jspm dependencies (don't touch them)
    - `semantic`: Source code for the semantic-ui theme
    - `typings`: TypeScript description files (partly managed by [tsd](https://github.com/DefinitelyTyped/tsd))
    - `config.js`: jspm configuration file
    - `index.html`: Entry point for the website
- `.gitignore`: Ignore file for git
- `gulpfile.js`: Gulp configuration file
- `package.json`: npm configuration file
- `semantic.json`: semantic-ui configuration file
- `tsd.json`: [tsd](https://github.com/DefinitelyTyped/tsd) configuration file
- `tslint.json`: [tslint](https://github.com/palantir/tslint) configuration file

## TODO
- Configure a test framework
- Configure code linting (tslint and sasslint)