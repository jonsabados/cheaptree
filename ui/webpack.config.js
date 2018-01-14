const webpack = require("webpack");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require("path");

const htmlPluginConfig = {
  filename: "wip.html",
  template: "app/index.html"
};

const config = {
  devtool: "inline-source-map",
  entry: [
    path.resolve(__dirname, "app/App.js"),
    path.resolve(__dirname, "app/css/application.scss"),
  ],
  output: {
    path: path.resolve(__dirname, "public/"),
    filename: "app.js"
  },
  plugins: [
    new ExtractTextPlugin("application.css"),
    new HtmlWebpackPlugin(htmlPluginConfig)
  ],
  module: {
    rules: [
      {
        test: /.jsx?$/,
        exclude: [path.resolve(__dirname, "node_modules")],
        loader: "babel-loader",
        query: {
          presets: ["env", "react", "stage-0"]
        }
      },
      {
        test: /\.scss$/,
        use: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use:
            [{
              loader: "css-loader",
              options: { sourceMap: true }
            },
             {
               loader: "sass-loader",
               options: { sourceMap: true }
             }]
        })
      },
      {
        test: /\.woff(\?v=\d+\.\d+\.\d+)?$/,
        use: "url-loader?limit=10000&mimetype=application/font-woff"
      },
      {
        test: /\.woff2(\?v=\d+\.\d+\.\d+)?$/,
        use: "url-loader?limit=10000&mimetype=application/font-woff"
      },
      {
        test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
        use: "url-loader?limit=10000&mimetype=application/octet-stream"
      },
      {
        test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
        use: "file-loader"
      },
      {
        test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
        use: "url-loader?limit=10000&mimetype=image/svg+xml"
      }
    ]
  },
  devServer: {
    contentBase: path.resolve(__dirname, "public"),
    historyApiFallback: true,
    compress: true
  }
};

if (process.env.NODE_ENV === "production") {
  config.devtool = false;
  config.plugins = [
    new ExtractTextPlugin("application.css"),
    new HtmlWebpackPlugin(htmlPluginConfig),
    new webpack.optimize.UglifyJsPlugin({ comments: false }),
    new webpack.DefinePlugin({
      "process.env": { NODE_ENV: JSON.stringify("production") }
    })
  ];
}

module.exports = config;
