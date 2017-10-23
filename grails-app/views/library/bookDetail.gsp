<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Book Details</title>
    <meta name="layout" content="moduleLibraryLayout"/>
</head>
<body>
<grailslab:breadCrumbActions breadCrumbTitleText="Book" SHOW_CREATE_BTN="YES" createButtonText="Add Book"/>

<div class="row" id="create-form-holder" style="display: none;" >
    <div class="col-md-12">
        <section class="panel">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-10 col-md-offset-1">
                        <form class="cmxform form-horizontal" id="create-form">
                            <div class="row">
                                <input type="hidden" name="id" id="id"/>
                                <div class="form-group">
                                    <label for="name" class="col-md-1 control-label">Name*</label>
                                        <div class="col-md-5">
                                            <input class="form-control" type="text" name="name" id="name" placeholder="Name" required="true"
                                                    tabindex="2"/>
                                        </div>
                                    <label for="category" class="col-md-1 control-label">Category*</label>
                                    <div class="col-md-5">
                                        <g:select class=" form-control" id="category"  name='category' tabindex="1"
                                                  noSelection="${['': 'Select Category...']}"
                                                  from="${categoryList}"
                                                  optionKey="id" optionValue="name" required="required">
                                        </g:select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="title" class="col-md-1 control-label">Title</label>
                                    <div class="col-md-5">
                                        <input class="form-control" type="text" name="title" id="title" placeholder="Title/Bangla Name"
                                               tabindex="2"/>
                                    </div>
                                    <label for="bookPublisher" class="col-md-1 control-label">Publisher</label>
                                    <div class="col-md-5">
                                        <input type="hidden" class="form-control" id="bookPublisher" name="bookPublisher" tabindex="2" />
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="author" class="col-md-1 control-label">Author*</label>
                                    <div class="col-md-5">
                                        <input type="hidden" class="form-control" id="author" name="author" tabindex="2" />
                                    </div>
                                    <label for="language" class="col-md-1 control-label">Language*</label>
                                    <div class="col-md-5">
                                        <input class="form-control" type="text" name="language" id="language" placeholder="language" value="Bangla"
                                               tabindex="2"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="addAuthor" class="col-md-1 control-label">Other Author(s)</label>
                                    <div class="col-md-5">
                                        <input class="form-control" type="text" name="addAuthor" id="addAuthor" placeholder="Add other author(s) Name(If any)"
                                               tabindex="2"/>
                                    </div>
                                </div>

                                <div class="pull-right">
                                    <button class="btn btn-primary" tabindex="7" id="form-submit-btn" type="submit">Save</button>
                                    <button class="btn btn-default cancel-btn" tabindex="8"
                                            type="reset">Cancel</button>
                                </div>

                            </div>

                </form>
                </div>
                </div>
            </div>
        </section>
    </div>
</div>

<div class="row">
    <div class="col-sm-12">
        <section class="panel">
            <header class="panel-heading">
                Book Details List
            </header>

            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover table-bordered" id="list-table">
                        <thead>
                        <tr>
                            <th>Sl</th>
                            <th >Name</th>
                            <th>Author</th>
                            <th>Publisher</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>

<script>
    var bookDetailsTable;
    jQuery(function ($) {
        bookDetailsTable = $('#list-table').DataTable({
            "sDom": "<'row'<'col-md-6 category-filter-holder'><'col-md-6'f>r>t<'row'<'col-md-3'l><'col-md-3'i><'col-md-6'p>>",
            "bAutoWidth": true,
            "scrollX": false,
            "bServerSide": true,
            "iDisplayLength": 25,
            "aaSorting": [0, 'asc'],
            "sAjaxSource": "${g.createLink(controller: 'library',action: 'bookDetailList')}",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "categoryName", "value": $('#categoryFilter').val()});
            },
            "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                if (aData.DT_RowId == undefined) {
                    return true;
                }
                $('td:eq(4)', nRow).html(getGridActionBtns(nRow, aData, 'editBook,delete'));
                return nRow;
            },

            "aoColumns": [
                null,
                null,
                null,
                null,
                {"bSortable": false}
            ]
        });
        $('#list-table_wrapper div.category-filter-holder').html('<div class="col-md-6"><select id="categoryFilter" class="form-control" name="categoryFilter"><option value="">All Category</option><g:each in="${categoryList}" var="bookCategory"><option value="${bookCategory.id}">${bookCategory.name}</option> </g:each></select></div>');

        $('#categoryFilter').on('change', function (e) {
            showLoading("#data-table-holder");
            bookDetailsTable.draw(false);
            hideLoading("#data-table-holder");
        });
        $('.create-new-btn').click(function (e) {
            $("#create-form-holder").toggle(500);
            $("#form-submit-btn").html("Save");

            $("#id").val("");
            $("#name").val("")
            $("#title").val("")
            $("#bookPublisher").val("")
            $("#author").val("")
            $("#addAuthor").val("")
            $("#language").val("")
            $("#quantity").val("")
            $("#select2-chosen-1").empty().append("Search for a Author [Name]");
            $("#s2id_author").css("pointer-events","auto");
            $("#s2id_category").css("pointer-events","auto");
            $("#select2-chosen-2").empty().append("Search for a Publisher [Name]");
            $("#s2id_bookPublisher").css("pointer-events","auto");
            $("#name").focus();
            e.preventDefault();
        });
        $(".cancel-btn").click(function () {
            var validator = $("#create-form").validate();
            validator.resetForm();
            $("#id").val('');
            $("#create-form-holder").hide(500);
        });


        $("#create-form").submit(function(e) {
            e.preventDefault();
            $.ajax({
                url: "${createLink(controller: 'library', action: 'bookDetailSave')}",
                type: 'post',
                dataType: "json",
                data: $("#create-form").serialize(),
                success: function (data) {
                    if (data.isError == false) {
                        clearForm($("#create-form"));
                        bookDetailsTable.draw(false);
                        showSuccessMsg(data.message);
                        $("#create-form-holder").hide(500);
                    } else {
                        showErrorMsg(data.message);
                    }

                },
                failure: function (data) {
                }
            })
            return false;
        });

        $('#list-table').on('click', 'a.edit-reference', function (e) {
            $("#create-form-holder").show(500);
            $("#name").focus();
            var control = this;
            var referenceId = $(control).attr('referenceId');
            jQuery.ajax({
                type: 'POST',
                dataType: 'JSON',
                url: "${g.createLink(controller: 'library',action: 'bookDetailEdit')}?id=" + referenceId,
                success: function (data, textStatus) {

                    if (data.isError == false) {
                     //   alert(JSON.stringify(data))
                      //  alert(data.obj.bookPublisher.id)
                        $("#form-submit-btn").html("Edit");
                        $("#id").val(data.obj.id)

                        $('#hiddenId').val(data.obj.id);
                        $('#name').val(data.obj.name);
                        $('#title').val(data.obj.title);
                        $('#category').val(data.obj.category.id);
                        $('#author').val(data.obj.author.id);
                        $("#select2-chosen-1").empty().append(data.bookAuthor);
                        $('#bookPublisher').val(data.obj.bookPublisher.id);
                        $("#select2-chosen-2").empty().append(data.bookPublisher);
                        $('#addAuthor').val(data.obj.addAuthor);
                        $('#language').val(data.obj.language);
                        $("#quantity").val(data.obj.quantity)
                        $("#create-form-holder").show(500);
                        $("#name").focus();

                    } else {
                        showErrorMsg(data.message);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
            e.preventDefault();

        });

        $('#author').select2({
            placeholder: "Search for a Author [name, country]",
            allowClear: true,
            minimumInputLength:3,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "${g.createLink(controller: 'remote',action: 'authorTypeAheadList')}",
                dataType: 'json',
                quietMillis: 250,
                data: function (term, page) {
                    return {
                        q: term
                    };
                },
                results: function (data, page) { // parse the results into the format expected by Select2.
                    // since we are using custom formatting functions we do not need to alter the remote JSON data
                    return { results: data.items };
                },
                cache: true
            },
            formatResult: repoFormatResult, // omitted for brevity, see the source of this page
            formatSelection: repoFormatSelection,  // omitted for brevity, see the source of this page
            dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
            escapeMarkup: function (m) { return m; } // we do not want to escape markup since we are displaying html in results
        });
        $('#bookPublisher').select2({
            placeholder: "Search for a Publisher [name, country]",
            allowClear: true,
            minimumInputLength:3,
            ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                url: "${g.createLink(controller: 'remote',action: 'publisherTypeAheadList')}",
                dataType: 'json',
                quietMillis: 250,
                data: function (term, page) {
                    return {
                        q: term
                    };
                },
                results: function (data, page) { // parse the results into the format expected by Select2.
                    // since we are using custom formatting functions we do not need to alter the remote JSON data
                    return { results: data.items };
                },
                cache: true
            },
            formatResult: repoFormatResult, // omitted for brevity, see the source of this page
            formatSelection: repoFormatSelection,  // omitted for brevity, see the source of this page
            dropdownCssClass: "bigdrop", // apply css that makes the dropdown taller
            escapeMarkup: function (m) { return m; } // we do not want to escape markup since we are displaying html in results
        });
        $('#list-table').on('click', 'a.delete-reference', function (e) {
            var selectRow = $(this).parents('tr');
            var confirmDel = confirm("Are you sure?");
            if (confirmDel == true) {
                var control = this;
                var referenceId = $(control).attr('referenceId');
                jQuery.ajax({
                    type: 'POST',
                    dataType: 'JSON',
                    url: "${g.createLink(controller: 'library',action: 'bookDetailInactive')}?id=" + referenceId,
                    success: function (data, textStatus) {
                        if (data.isError == false) {
                            bookDetailsTable.draw(false);
                            showSuccessMsg(data.message);
                        } else {
                            showErrorMsg(data.message);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                    }
                });
            }
            e.preventDefault();
        });
    });

</script>
</body>
</html>