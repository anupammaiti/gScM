package com.grailslab

import java.text.NumberFormat

class CustomImageTagLib {
    static namespace = "imgSrc"
        Closure fromIdentifier = { attrs ->
            if (attrs.imagePath == null) {
                throwTagError("Tag [imagePath] is missing required attribute [image]")
            }
            def uri = createLink(controller: 'image', action: 'streamImageFromIdentifier', params: [ 'imagePath': attrs.imagePath], absolute: true)
            out << uri.encodeAsHTML()
        }
    Closure fromIdentifierThumb = { attrs ->
            if (attrs.imagePath == null) {
                throwTagError("Tag [imagePath] is missing required attribute [image]")
            }
            def uri = createLink(controller: 'image', action: 'streamImageFromIdentifierThumb', params: [ 'imagePath': attrs.imagePath], absolute: true)
            out << uri.encodeAsHTML()
        }
    Closure fromPath = { attrs ->
        if (attrs.imagePath == null) {
            throwTagError("Tag [imagePath] is missing required attribute [image]")
        }
        String imagePath = attrs.imagePath
        def uri = createLink(controller: 'image', action: 'streamImageFromPath', params: ['crop': attrs.crop, 'height': attrs.height, 'width': attrs.width, 'imagePath': imagePath], absolute: true)
        out << uri.encodeAsHTML()
    }
}
