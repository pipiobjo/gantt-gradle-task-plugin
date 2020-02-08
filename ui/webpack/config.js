const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');

module.exports = {

    entry: {
        app: './src/index.js'
    },
    devtool: 'inline-source-map',
    devServer: {
        port:3000,
        // contentBase: path.resolve(__dirname, '../webpack/devServerStaticContent/'),
        // watchContentBase: true,
        // watchContentBase: true,
        filename: '[name].bundle.js',
        // inline: true
        // hot: true
        watchOptions: {
            poll: true
        }

    },
    // plugins to handle the build process and cleanup stuff
    plugins: [
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            template: 'webpack/devServerStaticContent/index.html'
        })
    ],
    // enable loading and import of css files
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
            { test: /\.js$/, exclude: /node_modules/, loader: "babel-loader" }
        ],
    },
    output: {
        filename: '[name].bundle.js',
        // filename: 'bundle.js',
        path: path.resolve(__dirname, '../build/dist'),
    }
};