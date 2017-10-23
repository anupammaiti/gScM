<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Chart of Accounts</title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbText="Voucher List" firstBreadCrumbUrl="${g.createLink(controller: 'accounts', action: 'voucher')}" breadCrumbTitleText="Create" SHOW_LIST_BTN="YES" listBtnText="Back" listLinkUrl="${g.createLink(controller: 'accounts', action: 'voucher')}" SHOW_PRINT_BTN="YES"/>
<div class="row" ng-app="voucherApp">
    <div ng-controller="createVoucher as createVoucher">
        <div class="col-md-12" ng-form>
            <section class="panel">
                <div class="panel-body">
                    <form class="form-horizontal col-md-12" name="voucherForm" ng-submit="addCreateVoucher(voucherForm.$valid)" novalidate>
                        <div class="form-group">
                            <div class="col-md-2">
                                <input type="text" ng-model="createVoucher.xdate" id="dateOfVoucher" name="dateOfVoucher" class="form-control" placeholder="dd/mm/yyyy" required>
                                <input type="number" ng-model="createVoucher.editIndex" class="form-control hidden">
                                <div ng-hide="hideErrorMessage" ng-class="{ 'has-error' : voucherForm.dateOfVoucher.$invalid}">
                                    <p ng-show="voucherForm.dateOfVoucher.$invalid" class="help-block">This field is required</p>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <select class="selectpicker form-control" ng-model="createVoucher.xtermgl">
                                    <option>INV</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <input type="text" ng-model="createVoucher.xvoucher" class="form-control" placeholder="No (Auto Generated)" disabled>
                            </div>
                            <div class="col-md-2">
                                <input type="text" ng-model="createVoucher.xref" class="form-control" placeholder="Reference">
                            </div>
                            <div class="col-md-2">
                                <input type="text" ng-model="createVoucher.xstatusjv" class="form-control balanced-status-style" placeholder="Voucher Status" disabled>
                            </div>
                            <div class="col-md-2">
                                <input type="button" ng-show="createVoucher.showPostbtn" class="btn btn-primary" value="{{createVoucher.postStatus}}" ng-click="createVoucher.saveVoucher(true)">
                                <input type="button" class="btn-warning"  ng-hide="createVoucher.hidePostedLabel"  value="Posted" disabled />
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12">
                                <textarea class="form-control" rows="2" ng-model="createVoucher.xnote" placeholder="Narration"></textarea>
                            </div>
                        </div>
                        <div class="form-group text-center">
                            <button type="submit" ng-show="createVoucher.showAddVoucherBtn" class="btn btn-primary">+ Add Voucher Details</button>
                        </div>
                    </form>
                    <hr>
                        <div class="form-horizontal col-md-12" ng-hide="createVoucher.hideVoucherDetailsForm">
                            <div class="form-group">
                                <label class="col-md-2 control-label">Account *</label>
                                <input type="number" ng-model="createVoucher.detailsId" class="form-control hidden">
                                <div class="col-md-4">
                                    <select class="selectpicker form-control" ng-change="createVoucher.changeAccount()" ng-model="createVoucher.accountId">
                                    <option ng-repeat="account in createVoucher.accountList"
                                             ng-value="account.accountId">{{account.accountNo}}</option>
                                    </select>
                                </div>
                                <div ng-hide="createVoucher.hideSubAccount">
                                    <label class="col-md-2 control-label">Sub-Account *</label>
                                    <div class="col-md-4">
                                        <select class="selectpicker form-control"  ng-model="createVoucher.xsub">
                                            <option ng-value="subAccount.xsub"
                                                ng-repeat="subAccount in createVoucher.subAccountList">{{subAccount.xsub}}</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-2 control-label">Payment *</label>
                                <div class="col-md-2">
                                    <label class="radio-inline">
                                        <input type="radio" ng-model="createVoucher.xpayType" ng-click="createVoucher.showCheck('Cash')" value="Cash">Cash
                                    </label>
                                    <label class="radio-inline">
                                        <input type="radio" ng-model="createVoucher.xpayType" ng-click="createVoucher.showCheck('Cheque')" value="Cheque">Cheque
                                    </label>
                                </div>
                                <div ng-hide="createVoucher.hideCheque">
                                    <label class="col-md-2 control-label">Cheque No *</label>
                                    <div class="col-md-3">
                                        <input type="text" ng-model="createVoucher.xcheque" class="form-control" placeholder="Cheque No">
                                    </div>
                                    <label class="col-md-1 control-label">Date *</label>
                                    <div class="col-md-2">
                                        <input type="text" ng-model="createVoucher.xchequeDate" id="dateOfCheque" class="form-control" placeholder="dd/mm/yyyy">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group" ng-hide="createVoucher.hideCurrency">
                                <label class="col-md-2 control-label">Currency</label>
                                <div class="col-md-4">
                                    <select class="selectpicker form-control" ng-model="createVoucher.xcur">
                                        <option value="BDT">BDT</option>
                                    </select>
                                </div>
                                <label class="col-md-2 control-label">Currency Rate</label>
                                <div class="col-md-4">
                                    <input type="number" ng-model="createVoucher.xexch" class="form-control" placeholder="Currency rate">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-2 control-label">Debit Amount *</label>
                                <div class="col-md-4">
                                    <input type="text" ng-model="createVoucher.debitAmount" class="form-control numericAmount"  placeholder="Debit Amount" ng-readonly="createVoucher.creditAmount">
                                </div>
                                <label class="col-md-2 control-label">Credit Amount *</label>
                                <div class="col-md-4">
                                    <input type="text" ng-model="createVoucher.creditAmount" class="form-control numericAmount" placeholder="Credit Amount" ng-readonly="createVoucher.debitAmount">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-md-2 control-label">Remarks</label>
                                <div class="col-md-9">
                                    <input type="text" ng-model="createVoucher.xremarks" class="form-control" placeholder="Remarks"/>
                                </div>
                                <div class="col-md-1">
                                    <button type="button" ng-show="createVoucher.showAddMoreBtn" ng-click="createVoucher.addVoucherDetails()" class="btn btn-primary" ng-bind="createVoucher.voucherDetailsBtn">Add</button>
                                </div>
                            </div>
                        </div>

                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>Account No</th>
                            <th>Sub-Account</th>
                            <th>Payment</th>
                            <th>Debit Amount</th>
                            <th>Credit Amount</th>
                            <th>Remark</th>
                            <th ng-hide="createVoucher.hideActionOfTable">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="voucher in createVoucher.voucherList">
                            <td>{{voucher.xacc}}</td>
                            <td>{{voucher.xsub}}</td>
                            <td>{{voucher.xpayType}}</td>
                            <td>{{voucher.debitAmount}}</td>
                            <td>{{voucher.creditAmount}}</td>
                            <td>{{voucher.xremarks}}</td>
                            <td ng-hide="createVoucher.hideActionData">
                                <i class="fa fa-edit" ng-click="createVoucher.editVoucher(voucher, $index)"></i>
                                <i class="fa fa-close" ng-click="createVoucher.removeVoucher(voucher, $index)"></i>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <div class="form-group text-right">
                        <button type="button" ng-show="createVoucher.showSaveVoucherBtn" ng-click="createVoucher.saveVoucher(false)" class="btn btn-primary" ng-bind="createVoucher.voucherDetailsSaveBtn">save</button>
                    </div>
                    </div>
            </section>
        </div>
    </div>
</div>

<script>
    $('.numericAmount').numeric();
    angular.module('voucherApp', [])
        .controller('createVoucher', function ($http, $scope, $filter) {
            var createVoucher = this;

            createVoucher.hideActionData = false;
            createVoucher.hideActionOfTable = false;
            createVoucher.hidePostedLabel= true;
            createVoucher.showPostbtn = true;
            createVoucher.hideCurrency = true;
            createVoucher.accountList = [
                <g:each in="${glmstList}" var="glmst" status="i">
                <g:if test="${i != 0}" >
                ,
                </g:if>
                {accountId: ${glmst.id}, accountNo: '${glmst.xacc + ' - ' +glmst.xdesc}', xacc: '${glmst.xacc}', xaccsource: '${glmst.xaccsource}', xacctype: '${glmst.xacctype}'}
                </g:each>
            ];


            <g:if test="${glHeader != null}" >
                createVoucher.showAddVoucherBtn = false;
                createVoucher.hideVoucherDetailsForm = false;
                createVoucher.showSaveVoucherBtn = true;
                createVoucher.showAddMoreBtn = true;
                createVoucher.voucherDetailsSaveBtn = 'Update';
                createVoucher.xvoucher = '${glHeader?.xvoucher}';
                createVoucher.xref = '${glHeader?.xref}';
                createVoucher.xdate = '${glHeader?.xdate?.format('dd/MM/yyyy')}';
                createVoucher.xyear = '${glHeader?.xyear}';
                createVoucher.xper = '${glHeader?.xper}';
                createVoucher.xpostFlag = ${glHeader?.xpostFlag};
                createVoucher.xstatusjv = '${glHeader?.xstatusjv}';
                createVoucher.xtermgl = '${glHeader?.xtermgl}';
                createVoucher.xnote = '${glHeader?.xnote}';
                createVoucher.xaction = '${glHeader?.xaction}';

                <g:if test="${gldetailsList != null}">
                    createVoucher.voucherList = [];
                    <g:each in="${gldetailsList}" var="gldetails" status="i">
                        var backendXbase  = ${gldetails.xbase};
                        var backendCredit = '', backendDebit = '';
                        if(backendXbase < 0) { backendCredit = (0-backendXbase); }
                        else {
                            backendDebit = backendXbase;
                        }
                        var data = {
                            id: ${gldetails.id},
                            xvoucher: '${gldetails.xvoucher}',
                            xacc: '${gldetails.xacc}',
                            xsub: '${gldetails.xsub}',
                            xproject: '${gldetails.xproject}',
                            xcur: '${gldetails.xcur}',
                            xexch: ${gldetails.xexch},
                            xprime: ${gldetails.xprime},
                            xbase: ${gldetails.xbase},
                            xremarks: '${gldetails.xremarks}',
                            xpayType: '${gldetails.xpayType}',
                            xcheque: '${gldetails.xcheque}',
                            xchequeDate: '${gldetails.xchequeDate?.format('dd/MM/yyyy')}',
                            creditAmount: backendCredit,
                            debitAmount: backendDebit
                        };
                        createVoucher.voucherList[${i}] = data;
                    </g:each>
                </g:if>
                <g:else>
                    createVoucher.voucherList = [];
                </g:else>
                createVoucher.xstatusjv = '${glHeader?.xstatusjv}';
            </g:if>
            <g:else>
                createVoucher.showAddVoucherBtn = true;
                createVoucher.hideVoucherDetailsForm = true;
                createVoucher.showSaveVoucherBtn = false;
                createVoucher.showAddMoreBtn = false;
                createVoucher.voucherList = [];
                createVoucher.xstatusjv = 'Balanced';
                createVoucher.voucherDetailsSaveBtn = 'Save';
            </g:else>
            $scope.hideErrorMessage = true;
            createVoucher.hideCheque = true;
            createVoucher.hideSubAccount = true;
            createVoucher.voucherDetailsBtn = 'Add';
            createVoucher.subAccountList = [];
            createVoucher.xacc = '';
            createVoucher.xprime = 0;
            createVoucher.xcur = 'BDT';
            createVoucher.xexch = 1;
            createVoucher.totalCredit = 0;
            createVoucher.totalDebit = 0;
            createVoucher.postStatus = 'POST';
            createVoucher.xpayType = 'Cash';
            createVoucher.xtermgl = 'INV';

            $scope.addCreateVoucher = function (isValid) {
                if (isValid){
                    createVoucher.hideVoucherDetailsForm = false;
                    createVoucher.showAddVoucherBtn = false;
                    createVoucher.showSaveVoucherBtn = true;
                    createVoucher.showAddMoreBtn = true;
                }else {
                    $scope.hideErrorMessage = false;
                }
            };



            createVoucher.showCheck = function (value) {
                createVoucher.hideCheque = (value == "Cash");
            };


            createVoucher.changeAccount = function () {
              var account = searchFromArray(createVoucher.accountList, 'accountId', createVoucher.accountId);

                if(account.xaccsource == 'Sub-Account') {
                    $http.get('${createLink(controller: 'accounts', action: 'getSubAccountList')}?accountId=' + createVoucher.accountId) .then(function(response) {
                        if(response.data.jsonGlSubList) {
                            if(response.data.jsonGlSubList.length > 0) {
                                createVoucher.subAccountList = response.data.jsonGlSubList;
                                createVoucher.hideSubAccount = false;
                            } else {
                                createVoucher.hideSubAccount = true;
                            }
                        }

                    });
                }

                else {
                    createVoucher.hideSubAccount = true;
                }
            };

            createVoucher.addVoucherDetails = function () {

                if((createVoucher.xacc || createVoucher.accountId) && (createVoucher.debitAmount ||
                    createVoucher.creditAmount) && createVoucher.xpayType  && createVoucher.xcur && createVoucher.xexch
                    && (createVoucher.hideCheque || (createVoucher.xcheque && createVoucher.xchequeDate))
                    && (createVoucher.hideSubAccount || createVoucher.xsub)){
                    if(createVoucher.creditAmount) {
                        createVoucher.xprime = -createVoucher.creditAmount;
                    } else {
                        createVoucher.xprime = createVoucher.debitAmount;
                    }

                    createVoucher.xacc = searchFromArray(createVoucher.accountList, 'accountId', createVoucher.accountId).xacc;
                    var voucherDetails = {
                        id: createVoucher.detailsId,
                        xvoucher: createVoucher.xvoucher,
                        xacc: createVoucher.xacc,
                        xsub: createVoucher.xsub,
                        xproject: '',
                        xcur: createVoucher.xcur,
                        xexch: createVoucher.xexch,
                        xprime: createVoucher.xprime,
                        xbase: (createVoucher.xprime * createVoucher.xexch),
                        xremarks: createVoucher.xremarks,
                        xpayType: createVoucher.xpayType,
                        xcheque: createVoucher.xcheque,
                        xchequeDate: createVoucher.xchequeDate,
                        creditAmount: createVoucher.creditAmount,
                        debitAmount: createVoucher.debitAmount

                    };

                    if(createVoucher.editIndex) {
                        createVoucher.voucherList[createVoucher.editIndex] = voucherDetails;
                    } else {
                        if(createVoucher.editIndex === undefined) {
                            createVoucher.voucherList.push(voucherDetails);
                        } else if(createVoucher.editIndex === '') {
                            createVoucher.voucherList.push(voucherDetails);
                        } else {
                            createVoucher.voucherList[createVoucher.editIndex] = voucherDetails;
                        }
                    }
                    setVoucherSTatus();
                    createVoucher.accountId = '';
                    createVoucher.xsub = '';
                    createVoucher.xpayType = '';
                    createVoucher.debitAmount = '';
                    createVoucher.creditAmount = '';
                    createVoucher.xremarks = '';
                    createVoucher.xcheque = '';
                    createVoucher.xchequeDate = '';
                    createVoucher.xacc = '';
                    createVoucher.xpayType = 'Cash';
                    createVoucher.hideCheque = true;
                    createVoucher.hideSubAccount = true;
                    createVoucher.voucherDetailsBtn = 'Add';
                    createVoucher.editIndex = '';

                    if (createVoucher.xstatusjv == 'Balanced'){
                        createVoucher.showPostbtn = true;
                    }else {
                        createVoucher.showPostbtn = false;
                    }

                } else {
                    alert('Please fill the required field !')
                }
            };

            createVoucher.removeVoucher = function (voucher, index) {
                createVoucher.voucherList.splice(index,1);
                setVoucherSTatus();
                if (createVoucher.xstatusjv == 'Balanced'){
                    createVoucher.showPostbtn = true;
                }else {
                    createVoucher.showPostbtn = false;
                }
            };

            createVoucher.editVoucher = function (voucher, index) {
                createVoucher.editIndex = index;
                createVoucher.voucherDetailsBtn = 'Update';
                createVoucher.accountId = searchFromArray(createVoucher.accountList, 'xacc', voucher.xacc).accountId;
                createVoucher.xsub = voucher.xsub;
                createVoucher.xpayType = voucher.xpayType;
                if(createVoucher.xsub) {
                    createVoucher.hideSubAccount = false;
                } else {
                    createVoucher.hideSubAccount = true;
                }
                if(createVoucher.xpayType == 'Cheque') {
                    createVoucher.hideCheque = false;
                } else {
                    createVoucher.hideCheque = true;
                }
                createVoucher.xcheque = voucher.xcheque;
                createVoucher.xchequeDate = voucher.xchequeDate;
                createVoucher.creditAmount = voucher.creditAmount;
                createVoucher.debitAmount = voucher.debitAmount;
                createVoucher.xremarks = voucher.xremarks;
                createVoucher.detailsId = voucher.id;
            };

            createVoucher.saveVoucher = function (xpostFlag) {
                if(createVoucher.voucherList.length > 0) {
                        var xyear = 0;
                        var xper = 0;
                        if(createVoucher.xdate) {
                            var xyearStr = createVoucher.xdate.split('/')[2];
                            var xperStr = createVoucher.xdate.split('/')[1];
                            if(xyearStr && xperStr) {
                                var xyear = parseInt(xyearStr);
                                var xper = parseInt(xperStr);
                            }
                        }

                        if(xyear && xper) {

                            var voucher = {
                                xvoucher : createVoucher.xvoucher,
                                xref: createVoucher.xref,
                                xdate: createVoucher.xdate,
                                xyear: xyear,
                                xper: xper,
                                xpostFlag: xpostFlag,
                                xstatusjv: createVoucher.xstatusjv,
                                xtermgl: createVoucher.xtermgl,
                                xnote: createVoucher.xnote,
                                xaction: '',
                                voucherDetails: createVoucher.voucherList
                            };

                            $http.post('${createLink(controller: 'accounts', action: 'saveVoucher')}', voucher) .then(function(response) {
                                if(!response.data.hasError) {
                                    window.location.href = '${createLink(controller: 'accounts', action: 'voucher')}';
                                } else {
                                    alert(response.data.message);
                                }
                            });
                        }  else {
                            alert('Please enter valid date & try again');
                        }
                } else {

                    alert('Add some voucher & try again');
                }
            };
            if (createVoucher.xpostFlag == 1){
                createVoucher.showAddMoreBtn = false;
                createVoucher.showSaveVoucherBtn = false;
                createVoucher.showPostbtn = false;
                createVoucher.hidePostedLabel = false;
                createVoucher.hideVoucherDetailsForm = true;
                createVoucher.hidePostedLabel = false;
                createVoucher.hideActionOfTable = true;
                createVoucher.hideActionData = true;
            }else if (createVoucher.xstatusjv !== 'Balanced'){
                      createVoucher.showPostbtn = false;
            }

            function setVoucherSTatus() {
                createVoucher.totalCredit = 0;
                createVoucher.totalDebit = 0;
                createVoucher.voucherList.forEach(function(element) {
                    if(element.creditAmount){
                        createVoucher.totalCredit += parseFloat(element.creditAmount);
                    }
                    if(element.debitAmount){
                        createVoucher.totalDebit += parseFloat(element.debitAmount);
                    }
                });
                if(createVoucher.totalCredit == createVoucher.totalDebit) {
                    createVoucher.xstatusjv = 'Balanced';
                } else {
                    createVoucher.xstatusjv = 'Out of Balanced';
                }
            }

            function searchFromArray(array, key, value) {
                for(var i = 0 ; i< array.length; i++){
                    if(array[i][key] == value)
                        return array[i];
                }
                return null
            }

        });
    $('#dateOfVoucher').datepicker({
        format: 'dd/mm/yyyy',
        endDate: new Date(),
        autoclose: true
    });
    $('#dateOfCheque').datepicker({
        format: 'dd/mm/yyyy',
        endDate: new Date(),
        autoclose: true
    });
</script>
</body>
</html>



