System.register(["express", "compression", "yargs", "opn"], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var express_1, compression_1, yargs_1, opn_1;
    var argv, AppBooter;
    function generateRandomNumber(min, max) {
        min = Math.ceil(min);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
    return {
        setters:[
            function (express_1_1) {
                express_1 = express_1_1;
            },
            function (compression_1_1) {
                compression_1 = compression_1_1;
            },
            function (yargs_1_1) {
                yargs_1 = yargs_1_1;
            },
            function (opn_1_1) {
                opn_1 = opn_1_1;
            }],
        execute: function() {
            argv = yargs_1.default
                .usage("Usage: -r rootDirectory [-b baseURL]")
                .describe("root", "Active profile to load configuration from")
                .demand("root", "You need to specify a root directory for the server to serve files from")
                .describe("port", "Port for the server to listen on")
                .number("port")
                .default("port", generateRandomNumber(3000, 4000))
                .describe("route-table", "JSON file to pull routes metadata from")
                .demand("route-table", "You need to specify the JSON file where the route metadata is stored")
                .describe("base", "Base URL the website is going to load resource from")
                .default("base", "/")
                .describe("open-browser", "Open browser window automatically after server starts")
                .default("open-browser", false)
                .argv;
            AppBooter = (function () {
                function AppBooter(options) {
                    this.options = options;
                    this.options = this.validateOptions(options);
                }
                AppBooter.prototype.init = function () {
                    var _this = this;
                    console.log(process.env.NODE_ENV);
                    this.getRouteTable().then(function (routeMap) {
                        _this.app = express_1.default();
                        _this.app.set("views", _this.options.root + _this.options.base);
                        _this.app.set("view engine", "ejs");
                        _this.app.use(compression_1.default());
                        _this.app.use(express_1.default.static(_this.options.root));
                        _this.registerDynamicRoutes(routeMap);
                        _this.app.get("*", function (request, response) {
                            // TODO: Make file location configurable
                            response.render("index.ejs", {
                                title: "CarbonLDP",
                                description: "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web"
                            });
                        });
                        _this.app.listen(_this.options.port, function () {
                            console.log("Server's up! Listening on port " + _this.options.port);
                        });
                    }).then(function () {
                        if (_this.options.openBrowser)
                            _this.openBrowser();
                    }).catch(console.error);
                };
                AppBooter.prototype.getRouteTable = function () {
                    return SystemJS.import("" + this.options.root + this.options.base + this.options.routeTable + "!");
                };
                AppBooter.prototype.validateOptions = function (options) {
                    options.root = options.root.endsWith("/") ? options.root.substring(0, options.root.length - 1) : options.root;
                    options.base = options.base.startsWith("/") ? options.base : "/" + options.base;
                    options.base = options.base.endsWith("/") ? options.base : options.base + "/";
                    return options;
                };
                AppBooter.prototype.registerDynamicRoutes = function (routes, base) {
                    if (base === void 0) { base = this.options.base; }
                    var _loop_1 = function(routeName) {
                        if (!routes.hasOwnProperty(routeName))
                            return "continue";
                        var route = routes[routeName];
                        var routePath = base + routeName;
                        routePath = routePath ? routePath : "/";
                        // TODO: Configure defaults on another place
                        var title = route.data ? route.data.title : "Carbon LDP";
                        var description = route.data && route.data.description ? route.data.description : "Carbon LDP is a Linked Data Platform for building web apps that manage and link data within your enterprise and across the World Wide Web";
                        if (route.children)
                            this_1.registerDynamicRoutes(route.children, routePath.endsWith("/") ? routePath : routePath + "/");
                        this_1.app.get(routePath, function (request, response) {
                            // TODO: Make file location configurable
                            response.render("index.ejs", {
                                title: title,
                                description: description
                            });
                        });
                    };
                    var this_1 = this;
                    for (var routeName in routes) {
                        _loop_1(routeName);
                    }
                };
                AppBooter.prototype.openBrowser = function () {
                    var _this = this;
                    return new Promise(function (resolve, reject) {
                        opn_1.default("http://localhost:" + _this.options.port + _this.options.base, { wait: false }, function (error) {
                            if (error)
                                reject(error);
                            else
                                resolve();
                        });
                    });
                };
                return AppBooter;
            }());
            exports_1("AppBooter", AppBooter);
            (new AppBooter(argv)).init();
        }
    }
});
