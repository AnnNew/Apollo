(function() {
    'use strict';

    angular
        .module('apolloApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('appointment', {
            parent: 'entity',
            url: '/appointment',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Appointments'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/appointment/appointments.html',
                    controller: 'AppointmentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('appointment-detail', {
            parent: 'entity',
            url: '/appointment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Appointment'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/appointment/appointment-detail.html',
                    controller: 'AppointmentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Appointment', function($stateParams, Appointment) {
                    return Appointment.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('appointment.new', {
            parent: 'appointment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/appointment/appointment-dialog.html',
                    controller: 'AppointmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                appointmentStart: null,
                                appointmentEnd: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('appointment', null, { reload: true });
                }, function() {
                    $state.go('appointment');
                });
            }]
        })
        .state('appointment.edit', {
            parent: 'appointment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/appointment/appointment-dialog.html',
                    controller: 'AppointmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Appointment', function(Appointment) {
                            return Appointment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('appointment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('appointment.delete', {
            parent: 'appointment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/appointment/appointment-delete-dialog.html',
                    controller: 'AppointmentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Appointment', function(Appointment) {
                            return Appointment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('appointment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
