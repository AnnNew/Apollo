(function() {
    'use strict';

    angular
        .module('apolloApp')
        .controller('AppointmentController', AppointmentController);

    AppointmentController.$inject = ['$scope', '$state', 'Appointment'];

    function AppointmentController ($scope, $state, Appointment) {
        var vm = this;
        
        vm.appointments = [];

        loadAll();

        function loadAll() {
            Appointment.query(function(result) {
                vm.appointments = result;
            });
        }
    }
})();
