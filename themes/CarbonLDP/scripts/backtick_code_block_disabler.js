// Hexo has a filter that will wrap code blocks in tables with line numbers
// This code disables that filter
const backtickCodeBlock = require( "hexo/lib/plugins/filter/before_post_render/backtick_code_block" );

hexo.extend.filter.unregister( "before_post_render", backtickCodeBlock );