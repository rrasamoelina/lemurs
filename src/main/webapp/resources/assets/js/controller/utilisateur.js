/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
app.controller("utilisateur",function($scope,$http){
    $scope.liste=[];
    $scope.user={};
//    $scope.recherche="";
    getall();
    
    function getall(){
        $http({
            method : 'POST',
            url : 'findAllUtilisateur',
            data : angular.toJson($scope.darwin),
            headers : {
                'Accept': 'application/json',
                'Content-Type' : 'application/json'
            }
        }).then(function success(response){
            $scope.liste=response.data;
        },function error(response){
            console.log(response);
        });
    }
    
    $scope.delete = function(id) {
        $('#modal-delete-utilisateur').modal({backdrop: 'static'});
        $("#boutonDel").html("<button type='button' id='boutonDel' onclick = \"del("+id+")\" class='btn btn-success btn-sm' data-dismiss='modal'>Continuer</button>");
    };        
    
    $scope.rechercher=function(){
        $http({
            method : 'POST',
            url : 'rechercherUtilisateur',
            data : angular.toJson($scope.user),
            headers : {
                'Accept': 'application/json',
                'Content-Type' : 'application/json'
            }
        }).then(function success(response){
            $scope.liste=response.data;
//            $scope.recherche=$scope.darwin.scientificname;
        },function error(response){
            console.log(response.statusText);
        });
    };        
//    
//    $scope.editer=function(darwin){
//        $("#editOrnew").modal({backdrop : 'static'});
//        $scope.form=angular.copy(darwin);
//    };
//    
//    $scope.annuler=function(){
//        $("#editOrnew").modal("hide");
//        $scope.form={};
//    };
//    
//    $scope.save=function(){
//        $http({
//            method : 'POST',
//            url : 'saveDwc',
//            data : angular.toJson($scope.form),
//            headers : {
//                'Accept': 'application/json',
//                'Content-Type' : 'application/json'
//            }
//        }).then(function success(response){
//            $scope.form={};
//            $("#editOrnew").modal("hide");
//            getall();
//        },function error(response){
//            console.log(response.statusText);
//        });
//    };
//    
//    $scope.delete=function(darwin){
//        $http({
//            method : 'POST',
//            url : 'deleteDwc',
//            data : angular.toJson(darwin),
//            headers : {
//                'Accept': 'application/json',
//                'Content-Type' : 'application/json'
//            }
//        }).then(function success(response){
//            getall();
//        },function error(response){
//            console.log(response.statusText);
//        });
//    };
});

