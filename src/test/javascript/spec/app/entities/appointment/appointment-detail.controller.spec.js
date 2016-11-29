'use strict';

describe('Controller Tests', function() {

    describe('Appointment Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAppointment, MockUser, MockDoctor;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAppointment = jasmine.createSpy('MockAppointment');
            MockUser = jasmine.createSpy('MockUser');
            MockDoctor = jasmine.createSpy('MockDoctor');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Appointment': MockAppointment,
                'User': MockUser,
                'Doctor': MockDoctor
            };
            createController = function() {
                $injector.get('$controller')("AppointmentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'apolloApp:appointmentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
