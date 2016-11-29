(function() {
    'use strict';

    angular
        .module('apolloApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('doctor', {
            parent: 'entity',
            url: '/doctor',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Doctors'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/doctor/doctors.html',
                    controller: 'DoctorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('doctor-detail', {
            parent: 'entity',
            url: '/doctor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Doctor'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/doctor/doctor-detail.html',
                    controller: 'DoctorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Doctor', function($stateParams, Doctor) {
                    return Doctor.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('doctor.new', {
            parent: 'doctor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doctor/doctor-dialog.html',
                    controller: 'DoctorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstName: null,
                                lastName: null,
                                qualification: null,
                                yearsOfExperience: null,
                                speciality: null,
                                contactNum: null,
                                email: null,
                                gender: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('doctor', null, { reload: true });
                }, function() {
                    $state.go('doctor');
                });
            }]
        })
        .state('doctor.edit', {
            parent: 'doctor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doctor/doctor-dialog.html',
                    controller: 'DoctorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Doctor', function(Doctor) {
                            return Doctor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('doctor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('doctor.delete', {
            parent: 'doctor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/doctor/doctor-delete-dialog.html',
                    controller: 'DoctorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Doctor', function(Doctor) {
                            return Doctor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('doctor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
