<%@ page contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<html>
<head>
    <meta name="layout" content="open-ltpl"/>
    <title>Notice</title>

    <style>
    #noticeDiv {
        max-height: 250px;
        max-width: 250px;
        min-height: 250px;
        min-width: 250px;
        overflow: hidden;
    }
    </style>

</head>

<body>
<section class="pageheader-default text-center">
    <div class="semitransparentbg">
        <h1 class="animated fadeInLeftBig notransition">Notice Board (Click notice for detail)</h1>
    </div>
</section>

<div class="wrapsemibox">
    <section class="container animated fadeInDown notransition" id="notice_board">
        <div class="row blogindex">
            <!-- MAIN -->
            <div class="col-md-12">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="wrapper wrapper-content  animated flipInX">
                            <ul class="notes">
                                <g:each in="${noticeList}" var="dataSet">
                                    <li>

                                        <a href="#" class="detailShow" id="${dataSet?.id}" title="Click to read more..">
                                            <div id="noticeDiv" class="nDiv${dataSet?.id}">
                                                <strong>Published on - ${dataSet?.publishDate}</strong>
                                                <h4> <span style="color: #007AFF">${dataSet?.title}</span></h4>
                                                %{--${raw(dataSet?.body)}--}%
                                                ${dataSet?.body}
                                                %{--${dataSet?.body.substring(0, dataSet?.body.length() > 150 ? 150 : dataSet?.body.length())}.........--}%
                                            </div>
                                        </a>
                                    </li>
                                </g:each>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Notice.....</h4>
            </div>

            <div class="modal-body">

                Details.....

            </div>
        </div>
    </div>
</div>

<script>
    jQuery(function ($) {
        $('.detailShow').click(function (e) {

            $("#myModal").modal("show");
            var value = $(this).attr('id');
            var htmlText = $(".nDiv" + value).html();
            $('.modal-body').html(htmlText);
            e.preventDefault();
        });
    });
</script>
</body>
</html>