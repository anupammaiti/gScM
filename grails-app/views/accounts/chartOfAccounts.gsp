<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Chart of Accounts</title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Accounts Chart "/>
<div class="row" id="create-form-holder" ng-app="chartOfAccountApp">
    <div ng-controller="account as account">
        <div class="col-sm-12" ng-form>
            <div class="panel">
                <header class="panel-heading">
                    Chart
                </header>
                <div class="panel-body">

                    <div class="alert alert-success" ng-show="account.showSuccessAlert">
                        <button type="button" class="close" data-ng-click="account.switchBool('showSuccessAlert')">×</button> <strong><i class="fa fa-check" aria-hidden="true"></i></strong> Save successfully !
                    </div>

                    <div class="alert alert-warning" ng-show="account.showWarningAlert">
                        <button type="button" class="close" data-ng-click="account.deleteAlert('showWarningAlert')">×</button> Deleted successfully !
                    </div>

                    <div class="alert alert-info" ng-show="account.showInfoAlert">
                        <button type="button" class="close" data-ng-click="account.updateAlert('showInfoAlert')">×</button> Update successfully !
                    </div>

                    <div class="alert alert-warning" ng-show="account.showAccountAlert">
                        <button type="button" class="close" data-ng-click="account.accountAlert('showAccountAlert')">×</button>{{account.msg}}
                    </div>


                    <form name="accountsForm" ng-submit="account.submitForm()" novalidate>
                    <div class="row form-horizontal col-md-7">
                        <input ng-model="account.accountId" type="text" class="hidden form-control form-group">

                            <label class="col-md-3 control-label">Account No</label>
                            <div class="col-md-9">
                                <input ng-model="account.accountNumber" name="accountNo" type="text"  class="form-control form-group" placeholder="Account" required>
                                <div ng-hide="account.hideErrorMessage" class="form-group" ng-class="{ 'has-error' : accountsForm.accountNo.$invalid}">
                                    <p ng-show="accountsForm.accountNo.$invalid" class="help-block">This field is required</p>
                                </div>
                            </div>
                            <label class="col-md-3 control-label">Description</label>
                            <div class="col-md-9">
                                <input ng-model="account.accountDescription" type="text" name="accountDescription" class="form-control form-group" placeholder="Description" required>
                                <div ng-hide="account.hideErrorMessage" class="form-group" ng-class="{ 'has-error' : accountsForm.accountDescription.$invalid}">
                                    <p ng-show="accountsForm.accountDescription.$invalid" class="help-block">This field is required</p>
                                </div>
                            </div>
                            <label class="col-md-3 control-label">Account Type</label>
                            <div class="col-md-9 form-group">
                                <label class="radio-inline" ng-repeat="accountType in accountTypes">
                                    <input type="radio"  ng-model="$parent.typeSelected" ng-value="accountType"/>{{accountType}}
                                </label>
                            </div>
                        <label class="col-md-3 control-label">Account Usage</label>
                            <div class="col-md-9 form-group">
                                <label class="radio-inline" ng-repeat="accountUsage in accountUsages">
                                    <input type="radio" ng-change='newValue(usageSelected)' ng-model="$parent.usageSelected" ng-value="accountUsage"/>{{accountUsage}}
                                </label>
                            </div>
                        <label class="col-md-3 control-label">Account Source</label>
                            <div class="col-md-9 form-group">
                                <label class="radio-inline paddingLeftLastChild" ng-repeat="accountSource in accountSources">
                                    <input type="radio" ng-change="showSubAccountBtn()" ng-model="$parent.sourceSelected" ng-value="accountSource"/>{{accountSource}}
                                </label>
                            </div>

                            <div class="form-group text-center">
                                <button ng-click="account.cancelAccount()" class="btn btn-default">Cancel</button>
                                <button type="button" ng-click="account.addChartOfAccount(account.accountButton)" class="btn btn-primary">{{account.accountButton}}</button>
                            </div>
                            <div class="col-md-12">
                                <table class="table table-striped">
                                    <thead>
                                    <tr>
                                        <th>Account Number</th>
                                        <th>Description</th>
                                        <th>Account Type</th>
                                        <th>Account Usage</th>
                                        <th>Account Source</th>
                                        <th>Action</th>
                                    </tr>
                                    </thead>
                                    <tbody ng-repeat="chart in account.chartOfAccountList" >
                                    <tr>
                                        <td>{{chart.accountNumber}}</td>
                                        <td>{{chart.accountDescription}}</td>
                                        <td>{{chart.typeSelected}}</td>
                                        <td>{{chart.usageSelected}}</td>
                                        <td>{{chart.sourceSelected}}</td>
                                        <td>
                                            <i class="fa fa-edit" ng-click="account.editChartOfAccount(chart)"></i>
                                            <i class="fa fa-close" ng-click="account.removeChartOfAccount(chart)"></i>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                    </div>
                    </form>

                        <div class="col-md-5" ng-hide="hideSubAccountForm">
                            <div class="form-horizontal" ng-form>
                                <input ng-model="account.subAccountId" type="text" class="hidden form-control form-group">
                                <input type="text" ng-model="mainAccountId" class="hidden form-control" placeholder="Account Id">
                                <div class="form-group" >
                                    <label class="col-md-4 control-label">Main Account</label>
                                    <div class="col-md-8 ">
                                        <input type="text" ng-model="mainAccountNumber" class=" form-control" placeholder="Main Account No" disabled>
                                    </div>
                                </div>
                                <form name="subAccountForm" ng-submit="account.submitForm()" novalidate>
                                <div class="form-group">
                                    <label class="col-md-4 control-label">Sub Account</label>
                                    <div class="col-md-8">
                                        <input type="text" ng-model="subAccountNo" name="subAccountNumber" class="form-control" placeholder="Account No" required>
                                        <div ng-hide="account.hideSubErrorMessage" class="form-group" ng-class="{ 'has-error' : subAccountForm.subAccountNumber.$invalid}">
                                            <p ng-show="subAccountForm.subAccountNumber.$invalid" class="help-block">This field is required</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-md-4 control-label">Description</label>
                                    <div class="col-md-8">
                                        <input type="text" ng-model="subAccountDescription" name="subAccountDes" class="form-control" placeholder="Description" required>
                                        <div ng-hide="account.hideSubErrorMessage" class="form-group" ng-class="{ 'has-error' : subAccountForm.subAccountDes.$invalid}">
                                            <p ng-show="subAccountForm.subAccountDes.$invalid" class="help-block">This field is required</p>
                                        </div>
                                    </div>
                                </div>
                            </form>
                            </div>
                            <div class="form-group text-center">
                                <button ng-click="account.cancelSubAccount()" class="btn btn-default">Cancel</button>
                                <button ng-click="account.saveSubAccount(mainAccountNumber)" class="btn btn-primary">{{account.subAccountButton}}</button>
                            </div>
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Account Number</th>
                                    <th>Description</th>
                                </tr>
                                </thead>
                                <tbody ng-repeat="sub in account.subAccountList">
                                <tr>
                                    <td>
                                     {{sub.xsub}}
                                    </td>
                                    <td>
                                       {{sub.xdesc}}
                                    </td>
                                    <td>
                                        <i class="fa fa-edit" ng-click="account.editSubAccount(sub)"></i>
                                        <i class="fa fa-close" ng-click="account.removeSubAccount(sub)"></i>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    angular.module('chartOfAccountApp', [ ])
            .controller('account', function ($http, $scope, $filter) {
                $scope.hideSubAccountForm = true;
                var account = this;
                account.accountButton = 'Add';
                account.subAccountButton = 'Add';
                account.hideErrorMessage = true;
                account.hideSubErrorMessage = true;
                account.chartOfAccountList = [
                    <g:each in="${glmstList}" var="glmst" status="i">
                    <g:if test="${i != 0}" >
                    ,
                    </g:if>
                    {accountId: ${glmst.id}, accountNumber: '${glmst.xacc}', accountDescription: '${glmst.xdesc}', typeSelected: '${glmst.xacctype}', usageSelected: '${glmst.xaccusage}', sourceSelected: '${glmst.xaccsource}'}
                    </g:each>
                ];

                account.subAccountList = [];
                resetMainAccountForm();
                $scope.accountTypes = ['Asset','Liability','Expenditure','Income'];
                $scope.accountUsages = ['Cash','Bank','AP','AR','Ledger'];
                $scope.accountSources = ['Customer', 'Employee', 'None', 'Sub-Account', 'Supplier', 'Student'];

                $scope.newValue = function(){
                    if ($scope.usageSelected == 'AP') {
                        $scope.sourceSelected = 'Supplier';
                    }
                    if ($scope.usageSelected == 'AR') {
                        $scope.sourceSelected = 'Customer';
                    }
                    if ($scope.usageSelected == 'Bank') {
                        $scope.sourceSelected = 'None';
                    }
                    if ($scope.usageSelected == 'Cash') {
                        $scope.sourceSelected = 'None';
                    }
                };

              /*  $scope.showSubAccountBtn = function () {
                  if($scope.sourceSelected == 'Sub-Account'){
                      account.accountButton = 'Add Sub-Account';
                  }
                };*/

                        account.addChartOfAccount = function (buttonLevel) {

                            account.hideErrorMessage = false;
                            if(account.accountNumber && account.accountDescription && $scope.typeSelected && $scope.usageSelected && $scope.sourceSelected) {
                                var accountId;
                                if(!account.accountId) {
                                    accountId = 0;
                                } else {
                                    accountId = account.accountId;
                                }
                                var  chartOfAccount = {
                                    accountId: accountId, accountNumber: account.accountNumber, accountDescription: account.accountDescription, typeSelected: $scope.typeSelected, usageSelected: $scope.usageSelected, sourceSelected: $scope.sourceSelected
                                };
                                $http.post('${createLink(controller: 'accounts', action: 'saveChart')}', chartOfAccount) .then(function(response) {
                                         if(!response.data.hasError) {
                                        chartOfAccount.accountId = response.data.accountId;
                                        if(accountId == 0) {
                                            account.chartOfAccountList.push(chartOfAccount);
                                        } else {
                                            var updateAccount = $filter('filter')(account.chartOfAccountList, function (d) {return d.accountId === accountId;})[0];
                                            var index = account.chartOfAccountList.indexOf(updateAccount);
                                            account.chartOfAccountList[index] = chartOfAccount;
                                        }
                                        account.showSuccessAlert = true;
                                        account.switchBool = function (value) {
                                            account[value] = !account[value];
                                        };
                                        account.showWarningAlert = false;
                                        account.showInfoAlert = false;
                                        account.showAccountAlert = false;

                                        resetMainAccountForm();

                                        if(buttonLevel == 'Add') {
                                            account.accountNumber = '';
                                            account.accountButton = 'Add';
                                        } else if(buttonLevel == 'Add Sub-Account'){
                                            $scope.mainAccountId = chartOfAccount.accountId;
                                            $scope.mainAccountNumber = account.accountNumber;
                                            account.accountNumber = '';
                                            $scope.hideSubAccountForm = false;
                                        } else if(buttonLevel == 'Update') {
                                            account.accountNumber = '';
                                            account.accountButton = 'Add';
                                            account.accountId = '';
                                            $scope.hideSubAccountForm = true;

                                            account.showInfoAlert = true;
                                            account.updateAlert = function (value) {
                                                account[value] = !account[value];
                                            };
                                            account.showSuccessAlert = false;
                                            account.showWarningAlert = false;
                                            account.showAccountAlert = false;

                                        }
                                             account.hideErrorMessage = true;
                                    }else {
                                             account.msg = response.data.errorMsg;

                                             account.showAccountAlert = true;
                                             account.accountAlert = function (value) {
                                                 account[value] = !account[value];
                                             };
                                             account.showSuccessAlert = false;
                                             account.showWarningAlert = false;
                                             account.showInfoAlert = false;

                                    }
                                });
                            }

                        };



                account.saveSubAccount = function (mainAccountNumber) {

                    account.hideSubErrorMessage = false;
                    if($scope.subAccountNo && $scope.subAccountDescription){
                        var subAccountId;
                        if(!account.subAccountId) {
                            subAccountId = 0;
                        } else {
                            subAccountId = account.subAccountId;
                        }
                        var saveSub = {
                            subAccountId: subAccountId, xsub: $scope.subAccountNo, xdesc: $scope.subAccountDescription, xaccId: $scope.mainAccountId
                        };
                        $http.post('${createLink(controller: 'accounts', action: 'saveSubAccount')}', saveSub) .then(function(response) {

                            if (!response.data.hasError){
                            saveSub.subAccountId = response.data.subAccountId;
                            if(subAccountId == 0) {
                                account.subAccountList.push(saveSub);

                                account.showSuccessAlert = true;
                                account.switchBool = function (value) {
                                    account[value] = !account[value];
                                };
                                account.showWarningAlert = false;
                                account.showInfoAlert = false;
                                account.showAccountAlert = false;

                                $scope.subAccountNo = '';
                                $scope.subAccountDescription = '';

                            } else {
                                var updateSubAccount = $filter('filter')(account.subAccountList, function (d) {return d.subAccountId === subAccountId;})[0];
                                var index = account.subAccountList.indexOf(updateSubAccount);
                                account.subAccountList[index] = saveSub;

                                account.showInfoAlert = true;
                                account.updateAlert = function (value) {
                                account[value] = !account[value];
                                };
                                account.showSuccessAlert = false;
                                account.showWarningAlert = false;
                                account.showAccountAlert = false;
                                account.subAccountId = '';

                                $scope.subAccountNo = '';
                                $scope.subAccountDescription = '';
                                account.subAccountButton = 'Add';
                                account.hideSubErrorMessage = true;
                                }
                                account.hideSubErrorMessage = true;
                            }else {
                                account.msg = response.data.errorMsg;

                                account.showAccountAlert = true;
                                account.accountAlert = function (value) {
                                    account[value] = !account[value];
                                };
                                account.showSuccessAlert = false;
                                account.showWarningAlert = false;
                                account.showInfoAlert = false;
                            }

                        });
                     }

                };

                account.editChartOfAccount = function (chart) {

                    if (!account.subAccountId){
                        $scope.sourceSelected = 'None';
                    }
                    $scope.subAccountBtn = true;
                    $scope.subAccountShow = true;
                    account.accountId = chart.accountId;
                    account.accountNumber = chart.accountNumber;
                    account.accountDescription = chart.accountDescription;
                    $scope.typeSelected = chart.typeSelected;
                    $scope.usageSelected = chart.usageSelected;
                    $scope.sourceSelected = chart.sourceSelected;
                    account.accountButton = 'Update';

                        var subAccountList = {
                            mainAccountId: chart.accountId
                        };
                        $http.post('${createLink(controller: 'accounts', action: 'getAccountList')}', subAccountList) .then(function(response) {
                            account.subAccountList = response.data;
                            $scope.mainAccountId = chart.accountId;
                            $scope.mainAccountNumber = chart.accountNumber;
                            $scope.hideSubAccountForm = false;
                            if(chart.sourceSelected !== 'Sub-Account'){
                                $scope.hideSubAccountForm = true;
                            }

                        });

                };

                account.editSubAccount = function (sub) {
                    account.subAccountButton = 'Update';
                    account.subAccountId = sub.subAccountId;
                    $scope.subAccountNo = sub.xsub;
                    $scope.subAccountDescription = sub.xdesc;
                    $scope.mainAccountId = sub.xaccId;

                };

                account.removeChartOfAccount = function (chart) {

                    var confirmDelete = confirm("Are you sure to deleted?");
                    if (confirmDelete == true){
                    var accountDeleted = {
                        accountId: chart.accountId
                    };


                    $http.post('${createLink(controller: 'accounts', action: 'deleteChart')}', accountDeleted) .then(function(response) {
                        if(!response.hasError) {
                            var index = account.chartOfAccountList.indexOf(chart);
                            account.chartOfAccountList.splice(index, 1);
                            $scope.hideSubAccountForm = true;
                        }
                    });
                    account.showWarningAlert = true;
                    account.deleteAlert = function (value) {
                        account[value] = !account[value];
                    };
                    account.showSuccessAlert = false;
                    account.showInfoAlert = false;
                    account.showAccountAlert = false;
                    }
                };

              account.removeSubAccount = function (sub) {

                  var confirmDelete = confirm("Are you sure to deleted?");
                  if (confirmDelete == true){
                    var subAccountDeleted = {
                        subAccountId: sub.subAccountId
                    };

                    $http.post('${createLink(controller: 'accounts', action: 'deleteSubAccount')}', subAccountDeleted) .then(function(response) {
                        if(!response.hasError) {
                            var index = account.subAccountList.indexOf(sub);
                            account.subAccountList.splice(index, 1);
                            $scope.subAccountNo = '';
                            $scope.subAccountDescription = '';
                        }
                    });
                  account.showWarningAlert = true;
                  account.deleteAlert = function (value) {
                      account[value] = !account[value];
                  };
                  account.showSuccessAlert = false;
                  account.showInfoAlert = false;
                  account.showAccountAlert = false;
                     }
                };

                account.cancelSubAccount = function () {
                    $scope.hideSubAccountForm = true;
                    resetMainAccountForm();
                };
                account.cancelAccount = function () {
                    window.location.reload();
                };
                account.cancelSubAccount = function () {
                    window.location.reload();
                };

                function resetMainAccountForm() {
                    $scope.typeSelected = 'Asset';
                    $scope.usageSelected = 'Ledger';
                    $scope.sourceSelected = 'None';
                    account.accountDescription = '';
                    account.accountNumber = '';
                }

            });
</script>
</body>
</html>


