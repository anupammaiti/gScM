package com.grailslab.content

import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Image

class ImageController {
    def fileService
    def imageService
    def streamImageFromIdentifier() {
        String identifier = params.imagePath
        Image image = Image.findByIdentifier(identifier)
        if (image && image.activeStatus==ActiveStatus.ACTIVE) {
            try {
                imageService.streamImageFromIdentifier(response, image)
            } catch (Exception e) {
                log.error("ERROR: ImageController.streamImageFromIdentifier "+e.getMessage())
            }
        } else {
            return response.sendError(404, "Image not found")
        }
    }

    def streamImageFromIdentifierThumb() {
        String identifier = params.imagePath
        Image image = Image.findByIdentifier(identifier)
        if (image && image.activeStatus==ActiveStatus.ACTIVE) {
            try {
                imageService.streamImageFromIdentifierThumb(response, image)
            } catch (Exception e) {
                log.error("ERROR: ImageController.streamImageFromIdentifierThumb "+e.getMessage())
            }

        } else {
            return response.sendError(404, "Image not found")
        }
    }
    def streamImageFromPath() {
        String identifier = fileService.getPathWithoutExtension(params.imagePath)
        Image image = Image.findByIdentifier(identifier)
        if (image && image.activeStatus==ActiveStatus.ACTIVE) {
            Integer width = params.width ? Integer.parseInt(params.width) : null
            Integer height = params.height ? Integer.parseInt(params.height) : null
            Boolean crop = params.crop == "true"
            try {
                imageService.streamImage(response, image, width, height, crop)
            } catch (Exception e) {
                log.error("ERROR: streamImageFromPath "+e.getMessage())
            }
        } else {
            return response.sendError(404, "Image not found")
        }
    }
    def streamImage() {
        String identifier = fileService.getPathWithoutExtension(params.image)
        Image image = Image.findByIdentifier(identifier)
        if (image && image.activeStatus==ActiveStatus.ACTIVE) {
            Integer width = params.width ? Integer.parseInt(params.width) : null
            Integer height = params.height ? Integer.parseInt(params.height) : null
            Boolean crop = params.crop == "true"
            try {
                imageService.streamImage(response, image, width, height, crop)
            } catch (Exception e) {
                log.error("ERROR: ImageController.streamImage "+e.getMessage())
            }
        } else {
            return response.sendError(404, "Image not found")
        }
    }
}
