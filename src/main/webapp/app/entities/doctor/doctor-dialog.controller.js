(function() {
    'use strict';

    angular
        .module('apolloApp')
        .controller('DoctorDialogController', DoctorDialogController);

    DoctorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Doctor', 'Appointment'];

    function DoctorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Doctor, Appointment) {
        var vm = this;

        vm.doctor = entity;
        vm.clear = clear;
        vm.save = save;
        vm.appointments = Appointment.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.doctor.id !== null) {
                Doctor.update(vm.doctor, onSaveSuccess, onSaveError);
            } else {
                Doctor.save(vm.doctor, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('apolloApp:doctorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
