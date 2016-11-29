(function() {
    'use strict';

    angular
        .module('apolloApp')
        .controller('DoctorDetailController', DoctorDetailController);

    DoctorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Doctor', 'Appointment'];

    function DoctorDetailController($scope, $rootScope, $stateParams, entity, Doctor, Appointment) {
        var vm = this;

        vm.doctor = entity;

        var unsubscribe = $rootScope.$on('apolloApp:doctorUpdate', function(event, result) {
            vm.doctor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
