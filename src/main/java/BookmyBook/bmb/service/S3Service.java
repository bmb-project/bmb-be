package BookmyBook.bmb.service;

import BookmyBook.bmb.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            System.out.println("File uploaded successfully. ETag: " + response.eTag());
        } catch (SdkException e) {
            throw new ExceptionResponse(500, "이미지 업로드 실패", "FAILED_TO_UPLOAD");
        }

        // Return the URL of the uploaded file
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, s3Client.serviceClientConfiguration().region().id(), fileName);
    }
}
