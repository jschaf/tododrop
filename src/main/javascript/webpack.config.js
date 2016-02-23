var path = require('path');
var webpack = require('webpack');

const PATHS = {
    app: path.join(__dirname, 'app'),
    build: path.join(__dirname, 'build')
}

module.exports = {
    context: PATHS.app,

    entry: {
        javascript: "./index.tsx",
        html: "./index.html"
    },

    output: {
        filename: "bundle.js",
        path: PATHS.build
    },

    resolve: {
        extensions: ['', '.webpack.js', '.web.js', '.ts', '.js']
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
                test: /\.tsx?$/,
                loader: 'babel-loader!ts-loader'
            },
            {
                test: /\.html$/,
                loader: "file-loader?name=[name].[ext]"
            }
        ]
    }
};