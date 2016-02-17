var path = require('path');
var webpack = require('webpack');


module.exports = {
    context: __dirname + "/app",

    entry: {
        javascript: "./app.js",
        html: "./index.html"
    },

    output: {
        filename: "bundle.js",
        path: __dirname + "/dist"
    },

    module: {
        loaders: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                loader: "babel-loader",
                query: {
                    presets: ['es2015', 'react']
                }
            },
            {
                test: /\.html$/,
                loader: "file?name=[name].[ext]",
            }
        ]
    }
};