<%@ page defaultCodec="none" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="open-ltpl"/>
    <title>Managing Committee</title>
</head>
<body>

<div class="wrapbox">

    <section class="pageheader-default text-center">
        <div class="semitransparentbg">
            <h1 class="animated fadeInLeftBig notransition">Managing Committee</h1>
        </div>
    </section>

    <div class="wrapsemibox">
        <div class="container animated fadeInUpNow notransition fadeInUp topspace10">
            <g:each in="${managingCommittee}" var="openOvice" status="i">
                <section class="grayarea">
                    <div class="cbp-qtrotator animated fadeInLeftNow notransition fadeInLeft">
                        <blockquote>
                            <p class="bigquote text-left">
                                <img alt="${openOvice.name}" class="pull-right testavatar" width="150" height="150" src="${imgSrc.fromIdentifier(imagePath: openOvice.imagePath)}">
                                <i class="colortext quoteicon"></i>${openOvice.description}<i class="colortext quoteicon"></i>
                            </p>
                        </blockquote>
                    </div>
                </section>
            </g:each>
        </div>

    </div>
</div>
</body>
</html>
