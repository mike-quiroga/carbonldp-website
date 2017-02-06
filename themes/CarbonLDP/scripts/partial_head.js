
hexo.extend.helper.register( "partial_head", function() {

var pathFn = require('path');
var _ = require('lodash');
var chalk = require('chalk');

    function partial_head(name, locals, options) {
        if (typeof name !== 'string') throw new TypeError('name must be a string!');

        options = options || {};

        var cache = options.cache;
        var viewDir = this.view_dir;
        var currentView = this.filename.substring(viewDir.length);
        var path = pathFn.join(pathFn.dirname(currentView), name);
        var view = ctx.theme.getView(path) || ctx.theme.getView(name);
        var viewLocals = { layout: false };

        if (!view) {
            return '';
        }

        if (options.only) {
            _.assign(viewLocals, locals);
        } else {
            _.assign(viewLocals, this, locals);
        }

        // Partial don't need layout
        viewLocals.layout = false;

        if (cache) {
            var cacheId = typeof cache === 'string' ? cache : view.path;

            return this.fragment_cache(cacheId, function() {
                return view.renderSync(viewLocals);
            });
        }

        return view.renderSync(viewLocals);
    };

});