'use strict';

describe('Controller Tests', function() {

    describe('Doctor Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDoctor, MockAppointment;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDoctor = jasmine.createSpy('MockDoctor');
            MockAppointment = jasmine.createSpy('MockAppointment');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Doctor': MockDoctor,
                'Appointment': MockAppointment
            };
            createController = function() {
                $injector.get('$controller')("DoctorDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'apolloApp:doctorUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
