var exec = require('cordova/exec');

var PLUGIN_NAME = 'bubicardscan';

var BubiCardScan = {
    init: function (success, error) {
        exec(success, error, PLUGIN_NAME, 'init');
    }
};

module.exports = BubiCardScan;