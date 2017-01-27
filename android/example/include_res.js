exports.cliVersion = '>=3.X';
path = require('path'),

exports.init = function (logger, config, cli, appc) {
    cli.on('build.android.aapt', {
        pre: function(data, next) {

            var args = data.args[1];

            if (args.indexOf('--auto-add-overlay') < 0) {
                args.push('--auto-add-overlay');
            }
            var libResPath = path.join(__dirname, '../../../..', 'modules', 'android');

            var externalLibraries = [
                {
                    javaClass:'com.vervewireless.advert',
                    resPath: path.join(libResPath, 'ti.dfp', '2.1.0', 'platform', 'android', 'library_res', 'Verve_AdSDK_3.3.0')
                },
                {

                    javaClass:'android.support.v7.mediarouter',
                    resPath: path.join(libResPath, 'com.cbcnewmedia.cast', '1.0.0', 'platform', 'android', 'library_res', 'mediarouter-v7-24.0.0')
                },
                {
                    javaClass:'com.google.android.gms',
                    resPath: path.join(libResPath, 'com.cbcnewmedia.cast', '1.0.0', 'platform', 'android', 'library_res', 'cast-framework-10.0.1')
                }
            ];

            // --extra-packages can be defined just once
            if (args.indexOf('--extra-packages') < 0) {
                args.push('--extra-packages');
                args.push('');
            }

            var namespaceIndex = args.indexOf('--extra-packages') + 1;

            externalLibraries.forEach(function(lib) {
                if (args[namespaceIndex].indexOf(lib.javaClass) < 0){
                    args[namespaceIndex].length && (args[namespaceIndex] += ':');
                    args[namespaceIndex] += lib.javaClass;
                }

                if (args.indexOf(lib.resPath) < 0) {
                    args.push('-S');
                    args.push(lib.resPath);
                }
            });

            next(null, data);

        }
    });
};
