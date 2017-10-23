<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Grails Lab Payment</title>
    <meta name="layout" content="moduleAccountsLayout"/>
</head>
<body>
<grailslab:breadCrumbActions firstBreadCrumbText="Grails Lab Payment List" firstBreadCrumbUrl="${g.createLink(controller: 'glabPayment', action: 'index')}" breadCrumbTitleText="Create" listLinkUrl="${g.createLink(controller: 'glabPayment', action: 'index')}"/>
<div class="row" id="createGlabPayment" ng-app="grailslabPaymentApp">
    <div ng-controller="grailslabPayment as grailslabPayment">
        <input id="paymentListId" type="text" class="form-control hidden" value="${glabPaymentId}">
        <div class="col-md-12" ng-form>
            <section class="panel">
            <header class="panel-heading">
                Grailslab Voucher
            </header>
            <div class="panel-body">
                <form class="form-horizontal col-md-12" role="form" id="glabPaymentForm" name="glabPaymentForm">
                    <div class="form-group">
                        <label class="col-md-2 control-label">Invoice No</label>
                        <div class="col-md-4">
                            <input ng-model="grailslabPayment.invoiceNo" name="noOfInvo" id="noOfInvo" type="text" class="form-control" placeholder="Invoice No">
                        </div>
                        <label class="col-md-2 control-label">Invoice Date</label>
                        <div class="col-md-4">
                            <input ng-model="grailslabPayment.invoiceDate" name="dateOfInvoice" id="dateOfInvoice" type="text" class="form-control" placeholder="Invoice Date">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-2 control-label">Due Date</label>
                        <div class="col-md-4">
                            <input ng-model="grailslabPayment.dueDate" name="dateOfDue" id="dateOfDue" type="text" class="form-control" placeholder="Due Date">
                        </div>
                        <label class="col-md-2 control-label">Description</label>
                        <div class="col-md-4">
                            <textarea ng-model="grailslabPayment.sortDesc" name="sortDescription" id="sortDescription" type="text" class="form-control" placeholder="Account Close Date"></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-2 control-label">Account Details</label>
                        <div class="col-md-4">
                            <textarea ng-model="grailslabPayment.accountNo"  name="noOfAccount" id="noOfAccount" class="form-control" rows="2" placeholder="Account Details"></textarea>
                        </div>
                        <label class="col-md-2 control-label">Term</label>
                        <div class="col-md-4">
                            <textarea ng-model="grailslabPayment.terms" name="term" id="term" rows="2" class="form-control" placeholder="Term"></textarea>
                        </div>
                    </div>
                    <div class="form-group">

                        <label class="col-md-2 control-label">Notes</label>
                        <div class="col-md-4">
                            <textarea ng-model="grailslabPayment.notes" name="note" id="note" rows="2" class="form-control" placeholder="Notes"></textarea>
                        </div>
                    </div>

                    <div class="form-group after-border">
                        <label class="control-label label-size-color">Payment Details</label>
                    </div>
                    <input ng-model="grailslabPayment.paymentId" type="text" class="form-control hidden">
                    <input ng-model="grailslabPayment.editIndex" type="number" class="form-control hidden">
                    <div class="form-group">
                        <label class="col-md-1 control-label">Description</label>
                        <div class="col-md-11">
                            <input ng-model="grailslabPayment.description" name="desc" id="desc" type="text" class="form-control" placeholder="Description">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-1 control-label">Rate</label>
                        <div class="col-md-3">
                            <input ng-model="grailslabPayment.rate" ng-change="grailslabPayment.rAmount()" name="amountRate" id="amountRate" type="text" class="form-control inputNumeric" placeholder="Rate">
                        </div>
                        <label class="col-md-1 control-label">Quantity</label>
                        <div class="col-md-3">
                            <input ng-model="grailslabPayment.qty" ng-change="grailslabPayment.qAmount()" name="quantity" id="quantity" type="text" class="form-control inputNumeric" placeholder="Quantity">
                        </div>
                        <label class="col-md-1 control-label">Amount</label>
                        <div class="col-md-3">
                            <input ng-model="grailslabPayment.amount" name="amounts" id="amounts" type="text" class="form-control inputNumeric" placeholder="Amount" disabled>
                        </div>
                    </div>
                    <div class="form-group text-right">
                        <button ng-click="grailslabPayment.addGlabVoucher()" type="button" class="btn btn-primary">{{grailslabPayment.addBtn}}</button>
                    </div>
                </form>
                    <div class="col-md-12">
                        <table class="table table-hover table-striped">
                            <thead>
                            <tr>
                                <th>Description</th>
                                <th>Rate</th>
                                <th>Quantity</th>
                                <th>Amount</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr ng-repeat="payment in grailslabPayment.paymentList">
                                <td>{{payment.description}}</td>
                                <td>{{payment.rate}}</td>
                                <td>{{payment.qty}}</td>
                                <td>{{payment.amount}}</td>
                                <td><i class="fa fa-edit " ng-click="grailslabPayment.editTData(payment, $index)"></i>
                                    <i class=" fa fa-close " ng-click="grailslabPayment.removeTData(payment, $index)"></i>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="form-group text-right">
                        <button ng-click="grailslabPayment.saveGlabVoucher()" type="button" class="btn btn-primary">save</button>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>

<script>
    var app = angular.module('grailslabPaymentApp', []);
        app.controller('grailslabPayment', function ($scope, $http, $filter) {
        var grailslabPayment = this;
            grailslabPayment.addBtn = 'Add';
            grailslabPayment.paymentList = [];

            <g:if test="${glabPaymentList != null}" >
            grailslabPayment.invoiceNo = '${glabPaymentList?.invoiceNo}';
            grailslabPayment.invoiceDate = '${glabPaymentList?.invoiceDate.format('dd/MM/yyyy')}';
            grailslabPayment.dueDate = '${glabPaymentList?.dueDate?.format('dd/MM/yyyy')}';
            grailslabPayment.sortDesc = '${glabPaymentList?.sortDesc}';
            grailslabPayment.accountNo = '${glabPaymentList?.accountNo}';
            grailslabPayment.terms = '${glabPaymentList?.terms}';
            grailslabPayment.notes = '${glabPaymentList?.notes}';
            </g:if>

        if ($('#paymentListId').val()){
            var  paymentListId = $('#paymentListId').val();
            $http.get('${createLink(controller: 'glabPayment', action: 'paymentDetailsList')}?paymentListId=' + paymentListId) .then(function (response) {

                grailslabPayment.paymentList = response.data.glabDetails;
            });
        }

            grailslabPayment.rAmount = function () {
                grailslabPayment.amount =  grailslabPayment.qty * grailslabPayment.rate
            };
            grailslabPayment.qAmount = function () {
                grailslabPayment.amount =  grailslabPayment.qty * grailslabPayment.rate
            };

        grailslabPayment.addGlabVoucher = function () {
            if (grailslabPayment.description && grailslabPayment.rate && grailslabPayment.qty && grailslabPayment.amount){
                  var glabVoucherDetails = {
                    description: grailslabPayment.description,
                    rate: grailslabPayment.rate,
                    qty: grailslabPayment.qty,
                    amount: grailslabPayment.amount
                         };
                if(grailslabPayment.editIndex > 0) {
                    var payment =  grailslabPayment.paymentList[grailslabPayment.editIndex];
                    payment.description = grailslabPayment.description ;
                    payment.rate = grailslabPayment.rate ;
                    payment.qty = grailslabPayment.qty ;
                    payment.amount = grailslabPayment.amount;
                    grailslabPayment.paymentList[grailslabPayment.editIndex] = payment;
                } else {
                    if(grailslabPayment.editIndex === undefined) {
                        grailslabPayment.paymentList.push(glabVoucherDetails);
                    } else if(grailslabPayment.editIndex === '') {
                        grailslabPayment.paymentList.push(glabVoucherDetails);
                    } else {
                        grailslabPayment.paymentList[grailslabPayment.editIndex] = glabVoucherDetails;
                    }
                }

                grailslabPayment.description = '';
                grailslabPayment.rate = '';
                grailslabPayment.qty = '';
                grailslabPayment.amount = '';
                grailslabPayment.addBtn = 'Add';
                grailslabPayment.editIndex = '';
            }
        };
            grailslabPayment.saveGlabVoucher = function () {
                var id;
                if (!$('#paymentListId').val()){
                    id = 0;
                }else {
                    id = $('#paymentListId').val();
                }
                var saveGlabVoucher = {
                    id: id,
                    invoiceNo: grailslabPayment.invoiceNo,
                    invoiceDate: grailslabPayment.invoiceDate,
                    dueDate: grailslabPayment.dueDate,
                    sortDesc: grailslabPayment.sortDesc,
                    accountNo: grailslabPayment.accountNo,
                    terms: grailslabPayment.terms,
                    notes: grailslabPayment.notes,
                    paymentList: grailslabPayment.paymentList
                };
                $http.post('${createLink(controller: 'glabPayment', action: 'save')}', saveGlabVoucher) .then(function (response) {

                    if(!response.data.hasError) {
                        window.location.href = '${createLink(controller: 'glabPayment', action: 'index')}';
                        showSuccessMsg(response.data.successMsg);
                    }

                    })
            };
            grailslabPayment.removeTData = function (payment) {
                var confiremDelete = confirm("Are you sure to delete?");
                if (confiremDelete == true){
                    var deleteId = {
                        id: payment.id
                    };
                    $http.post('${createLink(controller: 'glabPayment', action: 'deleteDetails')}', deleteId) .then(function(response) {
                        var index =  grailslabPayment.paymentList.indexOf(payment);
                        grailslabPayment.paymentList.splice(index, 1);
                        showInfoMsg(response.data.message)
                    })
                }
            };

            grailslabPayment.editTData = function (payment, index) {
                grailslabPayment.editIndex = index;
                grailslabPayment.addBtn = 'Update';
                grailslabPayment.description = payment.description;
                grailslabPayment.rate = payment.rate;
                grailslabPayment.qty = payment.qty;
                grailslabPayment.amount = payment.amount;

            }
    });

    $('#dateOfDue').datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    });
    $('#dateOfInvoice').datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    });
    $('.inputNumeric').numeric();
</script>
</body>
</html>



