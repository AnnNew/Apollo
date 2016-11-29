(function() {
    'use strict';
    angular
        .module('apolloApp')
        .factory('Appointment', Appointment);

    Appointment.$inject = ['$resource', 'DateUtils'];

    function Appointment ($resource, DateUtils) {
        var resourceUrl =  'api/appointments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.appointmentStart = DateUtils.convertDateTimeFromServer(data.appointmentStart);
                        data.appointmentEnd = DateUtils.convertDateTimeFromServer(data.appointmentEnd);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
