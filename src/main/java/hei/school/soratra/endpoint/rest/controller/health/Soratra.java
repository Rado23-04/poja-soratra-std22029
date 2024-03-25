package hei.school.soratra.endpoint.rest.controller.health;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Soratra {
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    // Bucket name
    private final String bucketName = "preprod-bucket-poja-soratra-std22029-bucket-1zagkz3vfuqr";
    @PutMapping("/soratra/{id}")
    public ResponseEntity<Object> putSoratra(@RequestBody String phrase,
                                             @PathVariable String id) {
        String objectKey = id + ".txt";
        s3Client.putObject(bucketName, objectKey, phrase);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/soratra/{id}")
    public ResponseEntity<Object> getSoratra(@PathVariable String id) {
        String originalObjectKey = id + ".txt";
        S3Object originalObject = s3Client.getObject(bucketName, originalObjectKey);
        S3Object transformedObject = null;

        StringBuilder originalPhraseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(originalObject.getObjectContent()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                originalPhraseBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }

        String transformedPhrase = originalPhraseBuilder.toString().toUpperCase();

        String transformedObjectKey = id + "_transformed.txt";
        s3Client.putObject(bucketName, transformedObjectKey, transformedPhrase);

        GeneratePresignedUrlRequest originalUrlRequest = new GeneratePresignedUrlRequest(bucketName, originalObjectKey);
        GeneratePresignedUrlRequest transformedUrlRequest = new GeneratePresignedUrlRequest(bucketName, transformedObjectKey);
        URL originalUrl = s3Client.generatePresignedUrl(originalUrlRequest);
        URL transformedUrl = s3Client.generatePresignedUrl(transformedUrlRequest);

        Map<String, String> response = new HashMap<>();
        response.put("original_url", ((URL) originalUrl).toString());
        response.put("transformed_url", transformedUrl.toString());

        return ResponseEntity.ok(response);
}
}
