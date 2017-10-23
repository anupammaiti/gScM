package com.grailslab.content

import com.grailslab.ImageUtils
import com.grailslab.exceptions.FileUploadException
import com.grailslab.exceptions.GrailsLabServiceException
import com.grailslab.settings.Image
import com.mortennobel.imagescaling.ResampleOp
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.context.ServletContextHolder

import javax.imageio.IIOException
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.awt.image.BufferedImage

//@Transactional
class ImageService {
    static transactional = false
    def grailsApplication
    def fileService
    public void streamImageFromPath(HttpServletResponse response, Image image, Integer width, Integer height, Boolean crop = true) throws GrailsLabServiceException {
        String filePath = grailsApplication.config.app.uploads.images.filesystemPath+image.imagePath
        File originalImage = new File(filePath)
        response.contentType = image.getContentType()

        if (!width && !height) {
            response.outputStream << originalImage?.newInputStream()

        } else {
            try {
                BufferedImage originalBufferedImage = ImageIO.read(originalImage);


                int originalWidth = originalBufferedImage.getWidth()
                int originalHeight = originalBufferedImage.getHeight()

                ResampleOp resampleOp
                BufferedImage rescaledImage
                if (crop) {
                    rescaledImage = ImageUtils.crop(originalBufferedImage, (int) width, (int) height)

                } else {
                    double scaleFactor = determineImageScale(originalWidth, originalHeight, width, height)
                    resampleOp = new ResampleOp((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor))
                    rescaledImage = resampleOp.filter(originalBufferedImage, null)
                }
                //cache image for apache
                log.debug("++++++ Cache path: " + getCachePathForSize(image, width, height, crop))
                File outputFile = new File(getCachePathForSize(image, width, height, crop))
                new File(outputFile.getParent()).mkdirs()
                if (["jpg", "jpeg"].contains(image.imageExtension.toLowerCase())) {
                    ImageUtils.compressJpegFile(outputFile, rescaledImage, (float) 0.90f)
                } else {
                    ImageIO.write(rescaledImage, image.imageExtension, outputFile);
                }

                response.outputStream << new FileInputStream(outputFile);
            } catch (IIOException e) {
                log.error("Error: streamImageFromPath - Image ${image.imagePath} does not exist.")
                throw new GrailsLabServiceException("Image ${image.imagePath} does not exist. originalImage: "+originalImage )
            }
        }

    }
    public void streamImageFromIdentifier(HttpServletResponse response, Image image) throws GrailsLabServiceException {
        String filePath = grailsApplication.config.app.uploads.images.filesystemPath+image.imagePath
        try {
            File originalImage = new File(filePath)
            if(originalImage.exists()){
                response.contentType = image.getContentType()
                response.outputStream << originalImage?.newInputStream()
            }else {
                originalImage = new File(getNoSliderImage())
                response.contentType = "image/jpeg"
                response.outputStream << originalImage?.newInputStream()
            }
        }catch (IIOException e) {
            log.error("Error: streamImageFromIdentifier - Image ${image.imagePath} does not exist.")
            throw new GrailsLabServiceException("Image ${image.imagePath} does not exist." )
        }
    }

    public void streamImageFromIdentifierThumb(HttpServletResponse response, Image image) throws GrailsLabServiceException {
        String filePath = grailsApplication.config.app.uploads.images.filesystemPath+image.imagePathThumb
        try {
            File originalImage = new File(filePath)
            if(originalImage.exists()){
                response.contentType = image.getContentType()
                response.outputStream << originalImage?.newInputStream()
            }else {
                log.error("Error: streamImageFromIdentifierThumb - Image ${image.imagePath} does not exist.")
                throw new GrailsLabServiceException("Image ${image.imagePath} does not exist." )
            }
        }catch (IIOException e) {
            log.error("Error: streamImageFromIdentifierThumb - Image ${image.imagePath} does not exist.")
            throw new GrailsLabServiceException("Image ${image.imagePath} does not exist." )
        }
    }
    public void streamImage(HttpServletResponse response, Image image, Integer width, Integer height, Boolean crop = true) throws GrailsLabServiceException {
        String filePath = grailsApplication.config.app.uploads.images.filesystemPath+image.imagePath
        File originalImage = new File(filePath)
        response.contentType = image.getContentType()

        if (!width && !height) {
            response.outputStream << originalImage?.newInputStream()

        } else {
            try {
                BufferedImage originalBufferedImage = ImageIO.read(originalImage);


                int originalWidth = originalBufferedImage.getWidth()
                int originalHeight = originalBufferedImage.getHeight()

                ResampleOp resampleOp
                BufferedImage rescaledImage
                if (crop) {
                    rescaledImage = ImageUtils.crop(originalBufferedImage, (int) width, (int) height)

                } else {
                    double scaleFactor = determineImageScale(originalWidth, originalHeight, width, height)
                    resampleOp = new ResampleOp((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor))
                    rescaledImage = resampleOp.filter(originalBufferedImage, null)
                }
                //cache image for apache
                log.debug("++++++ Cache path: " + getCachePathForSize(image, width, height, crop))
                File outputFile = new File(getCachePathForSize(image, width, height, crop))
                new File(outputFile.getParent()).mkdirs()
                if (["jpg", "jpeg"].contains(image.imageExtension.toLowerCase())) {
                    ImageUtils.compressJpegFile(outputFile, rescaledImage, (float) 0.90f)
                } else {
                    ImageIO.write(rescaledImage, image.imageExtension, outputFile);
                }

                response.outputStream << new FileInputStream(outputFile);
            } catch (IIOException e) {
                log.error("Error: streamImage - Image ${image.imagePath} does not exist.")
                throw new GrailsLabServiceException("Image ${image.imagePath} does not exist. originalImage: "+originalImage )
            }
        }

    }
    private double determineImageScale(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        double scalex = (double) targetWidth / sourceWidth
        double scaley = (double) targetHeight / sourceHeight
        //		return Math.min(scalex, scaley)
        return Math.max(scalex, scaley)
    }
    private getCachePathForSize(Image image, Integer width, Integer height, Boolean crop = true) {
        String path = grailsApplication.config.app.uploads.images.filesystemPath

        path += "/crop/images" + image.identifier + "." + image.imageExtension
        return path
    }

    private String getNoSliderImage() {
        return ServletContextHolder.servletContext.getRealPath('/images') +File.separator+ "noSliderImage.jpg"
    }

}
