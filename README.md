# carbonldp-website

Source code for [carbonldp.com](https://carbonldp.com)

## Development 

### Setup

1. Install dependencies
    - [node.js](https://nodejs.org/en/)
    - gulp: `npm install gulp -g`
    - jspm: `npm install jspm -g`
    - typings: `npm install -g typings`
2. Clone dependency projects alongside the project's directory:
    - CarbonLDP-JS-SDK: `gulp clone https://github.com/CarbonLDP/CarbonLDP-JS-SDK.git`
    - carbon-panel: `gulp clone https://github.com/CarbonLDP/carbon-panel.git`
    - angular2-carbonldp: `gulp clone https://github.com/CarbonLDP/angular2-carbonldp.git`
    
    You should end up with the following directories in the same directory:
    - `carbon-website`
    - `carbon-panel`
    - `CarbonLDP-JS-SDK`
    - `angular2-carbonldp`
3. cd into `carbon-website`
4. run `npm install && typings install`
5. To start the application server run `npm start` or `gulp serve`

### Gulp Tasks

Gulp defines two tasks:

1. `build`: Same as `npm install`. Build the source code and prepare it for production (inside of the dist/ folder).
2. `serve`: Same as `npm start`. Will start a live-server instance with the source code.

### File Structure

- `.idea`: WebStorm shared configuration files (things like code style, and project structure)
- `config`: Configuration files that are used when compiling the application
- `dist`: Distribution related files
    - `site`: Compiled files. Ready to be served
    - `Dockerfile`: Docker file used to create the docker image
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
- `CHANGELOG.md`: File to track package changes
- `gulpfile.js`: Gulp configuration file
- `jspm.browser.js`: JSPM browser specific configuration file
- `jspm.config.js`: JSPM general configuration file
- `package.json`: npm configuration file (it also contains JSPM dependency registry)
- `README.md`: === this
- `semantic.json`: semantic-ui configuration file
- `tsconfig.json`: TypeScript compiler configuration file
- `tslint.json`: [tslint](https://github.com/palantir/tslint) configuration file
- `typings.json`: [typings](https://github.com/typings/typings) configuration file

## TODO

- Configure a test framework
- Configure code linting (tslint and sasslint)
- Rename `gulp` tasks to comply with the latest naming convention and document them
