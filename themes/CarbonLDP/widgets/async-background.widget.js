module.exports = {
	selector: "[data-async-background]",
	render: ( widget, data, element, page, document, _ ) => {
		return Promise.resolve();
	},
	scripts: [
		{
			sourceURL: require.resolve( "./async-background.js" )
		}
	]
};
