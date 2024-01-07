package com.tfmunir.encryption_tool.repository.facade;

import com.tfmunir.encryption_tool.repository.S3ClientRepository;
import com.tfmunir.encryption_tool.service.IFileSecureCypher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class S3ClientRepositoryImpl implements S3ClientRepository {

    private static final Logger log = LoggerFactory.getLogger(S3ClientRepositoryImpl.class);
    private static final String SLASH = "/";
    private final S3Client s3Client;
    private final IFileSecureCypher cypher;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(String username, MultipartFile file) {

        String key = username + SLASH + file.getOriginalFilename();

        log.info("Subiendo fichero a S3: ");
        log.info("- Nombre del fichero: {}", file.getOriginalFilename());
        log.info("- Tamaño: {}", file.getSize());

        if (doesObjectExists(key)) {
            return key;
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(cypher.encrypFileBytes(file.getBytes())));
            log.info("Fichero subido correctamente");
            return ResponseEntity.ok(key).toString();
        } catch (IOException | S3Exception ex) {
            log.error("Error encontrado mientras se subia el fichero a S3: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error encontrado mientras se subia el fichero a S3");
        }

    }

    @Override
    public String downloadFile(String username, String filename) {

        String key = username + SLASH + filename;

        log.info("Descargando fichero de S3: ");
        log.info("- Nombre del fichero: {}", filename);

        if (!doesObjectExists(key)) {
            return FILE_NOT_FOUND;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 FileOutputStream fileOutputStream =
                         new FileOutputStream(new File(System.getProperty("user.home"), "Downloads/" + filename))) {
                byte[] readBuf = new byte[1024];
                int bytesRead;

                while ((bytesRead = object.read(readBuf)) != -1) {
                    byteArrayOutputStream.write(readBuf, 0, bytesRead);
                }

                byte[] encryptedData = byteArrayOutputStream.toByteArray();
                byte[] data = cypher.decryptFileBytes(encryptedData);

                fileOutputStream.write(data);

                log.info("Fichero descargado correctamente");

                return key;
            }

        } catch (S3Exception | IOException ex) {
            log.error("Error encontrado mientras se descargaba el fichero de S3: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error encontrado mientras se descargaba el fichero de S3");
        }
    }

    @Override
    public String deleteFile(String username, String filename) {

        String key = username + SLASH + filename;

        log.info("Eliminando fichero de S3: ");
        log.info("- Nombre del fichero: {}", filename);

        if (!doesObjectExists(key)) {
            return FILE_NOT_FOUND;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Fichero eliminado correctamente");
            return key;
        } catch (S3Exception ex) {
            log.error("Error encontrado mientras se eliminaba el fichero de S3: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error encontrado mientras se eliminaba el fichero de S3");
        }

    }

    @Override
    public String renameFile(String username, String oldFilename, String newFilename) {

        String oldKey = username + SLASH + oldFilename;
        String newKey = username + SLASH + newFilename;

        log.info("Renombrando fichero de S3: ");
        log.info("- Nombre del fichero: {}", oldFilename);

        if (!doesObjectExists(oldKey)) {
            return FILE_NOT_FOUND;
        }

        try {

            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(oldKey)
                    .destinationBucket(bucketName)
                    .destinationKey(newKey)
                    .build();

            s3Client.copyObject(copyObjectRequest);
            deleteFile(username, oldFilename);
            log.info("Fichero renombtado correctamente");

            return newKey;
        } catch (S3Exception ex) {
            log.error("Error encontrado mientras se renombraba el fichero de S3: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error encontrado mientras se renombraba el fichero de S3");
        }

    }

    private boolean doesObjectExists(String key) {

        log.info("Comprobando si el objeto {} existe en el bucket", key);
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();

        try {
            s3Client.headObject(headObjectRequest);
            log.warn("El objeto {} existe en el bucket", key);
            return true;
        } catch (NoSuchKeyException ex) {
            log.warn("El objeto {} NO existe en el bucket", key);
            return false;
        }

    }

    public List<String> listUserFiles(String username) {

        log.info("Listando objetos para el usuario {}", username);

        String key = username + SLASH;

        try {

            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request
                    .builder()
                    .bucket(bucketName)
                    .prefix(key)
                    .build();

            List<S3Object> s3Objects = s3Client.listObjectsV2(listObjectsRequest).contents();

            List<String> filenames = new ArrayList<>();

            for (S3Object object : s3Objects) {
                filenames.add(object.key());
            }

            log.info("Listado finalizado correctamente");

            return filenames;

        } catch (S3Exception ex) {
            log.error("Error encontrado mientras se listaba los objetos de S3: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error encontrado mientras se listaba los objetos de S3");
        }
    }

}
