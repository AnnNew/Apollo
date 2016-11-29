(function() {
    'use strict';
    angular
        .module('apolloApp')
        .factory('Doctor', Doctor);

    Doctor.$inject = ['$resource'];

    function Doctor ($resource) {
        var resourceUrl =  'api/doctors/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
