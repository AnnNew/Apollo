(function() {
    'use strict';

    angular
        .module('apolloApp')
        .controller('AppointmentDetailController', AppointmentDetailController);

    AppointmentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Appointment', 'User', 'Doctor'];

    function AppointmentDetailController($scope, $rootScope, $stateParams, entity, Appointment, User, Doctor) {
        var vm = this;

        vm.appointment = entity;

        var unsubscribe = $rootScope.$on('apolloApp:appointmentUpdate', function(event, result) {
            vm.appointment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
