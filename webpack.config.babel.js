`use strict`;

import webpack from 'webpack';
import path from 'path';

module.exports = (() => {
    let basePath = path.join(__dirname, 'src/main/resources/static'),
        joinPath = (rootPath, ...p) => {
            p.forEach((value, idx, arr) => arr[idx] = path.join(basePath, rootPath, value));
            return p;
        },
        src = (...p) => joinPath('src', ...p),
        config = {
            entry : {
                // map
                'app': src('/App.js')
            },
            output : {
                path : path.join(basePath, 'dist'),
                filename : '[name].js',
                publicPath : '/dist'
            },
            plugins : [
            ],
            module: {
                rules: [
                    {
                        test: /\.js$/,
                        exclude: /node_modules/,
                        loader: ['react-hot-loader/webpack', 'babel-loader']
                    },
                    {
                        test: /\.scss|\.css$/,
                        use: [
                            'style-loader',
                            'css-loader',
                            'sass-loader'
                        ]
                    }
                ]
            }

        };


    if (process.env.NODE_ENV == 'local') {
        let url = `localhost`,
            protocol = `http`,
            devPort = 8090,
            proxyPort = 8080,
            demoEntry = {
                'demoFileupload': src('/demo/fileupload.js'),
                'demoImageView': src('/demo/imageView.js'),
                'demoTest': src('/demo/test.js')
            };

        config.entry = Object.assign({}, demoEntry, config.entry);
        config.devtool = 'inline-source-map';
        config.devServer = {
            inline: true,
            hot: true,
            historyApiFallback: true,
            host: url,
            port: devPort,
            proxy: {
                "!/dist/js/**": `${protocol}://${url}:${proxyPort}`,
                secure: false,
                changeOrigin: true,
            }
        };

        Object.keys(config.entry).forEach(key => {
            config.entry[key].push(`react-hot-loader/patch`);
            config.entry[key].push(`webpack-dev-server/client?${protocol}://${url}:${devPort}`);
            config.entry[key].push('webpack/hot/only-dev-server');
        });

        config.plugins.push(new webpack.HotModuleReplacementPlugin());
    }

    return config;
})();
