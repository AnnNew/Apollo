(function() {
    'use strict';

    angular
        .module('apolloApp')
        .controller('DoctorDeleteController',DoctorDeleteController);

    DoctorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Doctor'];

    function DoctorDeleteController($uibModalInstance, entity, Doctor) {
        var vm = this;

        vm.doctor = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Doctor.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
