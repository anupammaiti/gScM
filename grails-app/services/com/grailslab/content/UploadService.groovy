package com.grailslab.content

import com.grailslab.exceptions.FileUploadException
import com.grailslab.gschoolcore.AcademicYear
import com.grailslab.gschoolcore.ActiveStatus
import com.grailslab.settings.Image
import com.grailslab.settings.School
import grails.transaction.Transactional
import org.springframework.transaction.TransactionDefinition
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.nio.file.Files

class UploadService {
    static transactional = false
    def fileService
    def grailsApplication
    def springSecurityService
    def schoolService

    private static final int IMG_WIDTH = 30;
    private static final int IMG_HEIGHT = 30;

    @Transactional
    Image uploadImage(HttpServletRequest request, String fieldName, String uploadCategory) throws FileUploadException {
        CommonsMultipartFile f = request.getFile(fieldName)
        if (f.empty) {
            throw new FileUploadException('Upload Not successfull')
        }
        String extension = fileService.getExtensionFromPath(f.originalFilename)
//        todo: validate content type
        def allowedImageExtensions = ['png', 'jpg', 'jpeg', 'gif']
        if (!extension || !allowedImageExtensions.contains(extension.toLowerCase())) {
            throw new FileUploadException('upload.error.wrongDatatype: Extension "' + extension + '" is not allowed. Allowed extensions are: ' + allowedImageExtensions)
        }

        Image image = new Image(imageExtension: extension)
        image.imagePath = image.createImagePath(uploadCategory, extension)
        def absolutePath = grailsApplication.config.app.uploads.images.filesystemPath + image.imagePath
        File uploadedFile = new File(absolutePath)
        //create parent directory if it does not exist
        new File(uploadedFile.getParent()).mkdirs()

        InputStream inputStream = selectInputStream(request, fieldName)
        upload(inputStream, uploadedFile)

        if (uploadedFile?.size() > 0) {
            image.save()
            //log.info "Image UPLOAD OK"

        } else {
            image.discard()
            throw new FileUploadException('Upload Not successfull')
        }
        return image
    }
    String uploadImageWithThumbStr(HttpServletRequest request, String fieldName, String uploadCategory){
        Image uploadImage = uploadImageWithThumb(request, fieldName, uploadCategory)
        return uploadImage?.identifier
    }
    @Transactional
    Image uploadImageWithThumb(HttpServletRequest request, String fieldName, String uploadCategory) throws FileUploadException {
        CommonsMultipartFile f = request.getFile(fieldName)
        if (f.empty) {
            throw new FileUploadException('Upload Not successfull')
        }
        if (f.size > 1000000) {
            throw new FileUploadException('Maximum upload size exceds')
        }
        String extension = fileService.getExtensionFromPath(f.originalFilename)
        //todo: validate content type
        def allowedImageExtensions = ['png', 'jpg', 'jpeg', 'gif']
        if (!extension || !allowedImageExtensions.contains(extension.toLowerCase())) {
            throw new FileUploadException('upload.error.wrongDatatype: Extension "' + extension + '" is not allowed. Allowed extensions are: ' + allowedImageExtensions)
        }
        Image image = new Image(imageExtension: extension)
        image.imagePath = image.createImagePath(uploadCategory, extension)
        image.imagePathThumb = image.createImagePathThumb(uploadCategory, extension)


        def absolutePath = grailsApplication.config.app.uploads.images.filesystemPath + image.imagePath
        def absolutePathThumb = grailsApplication.config.app.uploads.images.filesystemPath + image.imagePathThumb
        File uploadedFile = new File(absolutePath)
        //create parent directory if it does not exist
        new File(uploadedFile.getParent()).mkdirs()

        InputStream inputStream = selectInputStream(request, fieldName)
        upload(inputStream, uploadedFile)

        try {

            BufferedImage originalImage = ImageIO.read(new File(absolutePath));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

//            BufferedImage resizeImage = resizeImage(originalImage, type, height, width);
            ImageIO.write(originalImage, extension.toLowerCase(), new File(absolutePath));

            BufferedImage thumbImage = thumbImage(originalImage, type);
            ImageIO.write(thumbImage, extension.toLowerCase(), new File(absolutePathThumb));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (uploadedFile?.size() > 0) {

            image.validate()
            if (image.hasErrors()){
            }
            image.save()
            //log.info "Image UPLOAD OK"

        } else {
            image.discard()
            throw new FileUploadException('Upload Not successfull')
        }
        return image
    }

    Image uploadImageWithThumb(HttpServletRequest request, String fieldName, String uploadCategory, int height, int width) throws FileUploadException {
        CommonsMultipartFile f = request.getFile(fieldName)
        if (f.empty) {
            throw new FileUploadException('Upload Not successfull')
        }
        if (f.size > 1000000) {
            throw new FileUploadException('Maximum upload size exceds')
        }
        String extension = fileService.getExtensionFromPath(f.originalFilename)
        //todo: validate content type
        def allowedImageExtensions = ['png', 'jpg', 'jpeg', 'gif']
        if (!extension || !allowedImageExtensions.contains(extension.toLowerCase())) {
            throw new FileUploadException('upload.error.wrongDatatype: Extension "' + extension + '" is not allowed. Allowed extensions are: ' + allowedImageExtensions)
        }

        Image image = new Image(imageExtension: extension)
        image.imagePath = image.createImagePath(uploadCategory, extension)
        image.imagePathThumb = image.createImagePathThumb(uploadCategory, extension)


        def absolutePath = grailsApplication.config.app.uploads.images.filesystemPath + image.imagePath
        def absolutePathThumb = grailsApplication.config.app.uploads.images.filesystemPath + image.imagePathThumb
        File uploadedFile = new File(absolutePath)
        //create parent directory if it does not exist
        new File(uploadedFile.getParent()).mkdirs()

        InputStream inputStream = selectInputStream(request, fieldName)
        upload(inputStream, uploadedFile)

        try {

            BufferedImage originalImage = ImageIO.read(new File(absolutePath));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImage = resizeImage(originalImage, type, height, width);
            ImageIO.write(resizeImage, extension.toLowerCase(), new File(absolutePath));

            BufferedImage thumbImage = thumbImage(originalImage, type);
            ImageIO.write(thumbImage, extension.toLowerCase(), new File(absolutePathThumb));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (uploadedFile?.size() > 0) {
            image.save()
            //log.info "Image UPLOAD OK"

        } else {
            image.discard()
            throw new FileUploadException('Upload Not successfull')
        }
        return image
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int height, int width) {

        int originalHeight = originalImage.getHeight()
        int originalWidth = originalImage.getWidth()
        int finalHeight
        int finalWidth
        double ratioWidth
        double ratioHeight

        if (originalHeight <= height || originalWidth <= width) {
            finalHeight = originalHeight
            finalWidth = originalWidth
        } else {
            ratioWidth = originalWidth / width
            ratioHeight = originalHeight / height

            if (ratioWidth > ratioHeight) {
                finalHeight = (int) originalHeight / ratioHeight
                finalWidth = (int) originalWidth / ratioHeight
            } else {
                finalHeight = (int) originalHeight / ratioWidth
                finalWidth = (int) originalWidth / ratioWidth
            }
        }

        BufferedImage resizeImage = new BufferedImage(finalWidth, finalHeight, type);
        Graphics2D g = resizeImage.createGraphics();
        g.drawImage(originalImage, 0, 0, finalWidth, finalHeight, null);
        g.dispose();

        return resizeImage;
    }

    private static BufferedImage thumbImage(BufferedImage originalImage, int type) {
        int originalHeight = originalImage.getHeight()
        int originalWidth = originalImage.getWidth()
        int finalHeight
        int finalWidth
        double ratioWidth
        double ratioHeight

        if (originalHeight < IMG_HEIGHT || originalWidth < IMG_WIDTH) {
            finalHeight = originalHeight
            finalWidth = originalWidth
        } else {
            ratioWidth = originalWidth / IMG_HEIGHT
            ratioHeight = originalHeight / IMG_WIDTH

            if (ratioWidth > ratioHeight) {
                finalHeight = (int) originalHeight / ratioHeight
                finalWidth = (int) originalWidth / ratioHeight
            } else {
                finalHeight = (int) originalHeight / ratioWidth
                finalWidth = (int) originalWidth / ratioWidth
            }
        }

        BufferedImage resizeImage = new BufferedImage(finalWidth, finalHeight, type);
        Graphics2D g = resizeImage.createGraphics();
        g.drawImage(originalImage, 0, 0, finalWidth, finalHeight, null);
        g.dispose();

        return resizeImage;
    }

    public InputStream selectInputStream(HttpServletRequest request, String fileUploadField = "qqfile") {
        if (request instanceof MultipartHttpServletRequest) {
            MultipartFile uploadedFile = ((MultipartHttpServletRequest) request).getFile(fileUploadField)
            return uploadedFile.inputStream
        }
        return request.inputStream
    }

    void upload(InputStream inputStream, File file) throws FileUploadException {
        try {
            file << inputStream
        } catch (Exception e) {
            throw new FileUploadException(e)
        }

    }
//    @Transactional(propagation = TransactionDefinition.PROPAGATION_REQUIRES_NEW)
    def deleteImage(String imagePath) {
        Image image = Image.findByIdentifier(imagePath)
        if(image){
            image.activeStatus = ActiveStatus.DELETE
            image.save()
            return true
        }
        return false
    }

    def getImageInputStream(String imagePath){
        Image image = Image.findByIdentifier(imagePath)
        if(!image){
            return null
        }
        def absolutePath = grailsApplication.config.app.uploads.images.filesystemPath+image?.imagePath
        try {
            def fileImage = new File(absolutePath)
            return fileImage?.newInputStream()
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null
    }
}
