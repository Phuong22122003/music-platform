package com.MusicPlatForm.user_library_service.configuration;
import feign.RequestTemplate;
import feign.form.FormData;
import feign.form.spring.SpringFormEncoder;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SpringFormMultipartEncoder extends SpringFormEncoder {

    // Constructor that uses SpringFormEncoder for basic functionality
    public SpringFormMultipartEncoder() {
        super();
    }

    @Override
    public void encode(Object object, Type bodyType, feign.RequestTemplate template) {
        // If the object is a list, process each item
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            for (Object item : list) {
                if (item instanceof MultipartFile) {
                    // Handle MultipartFile specifically
                    try {
                        encodeMultipartFile((MultipartFile) item, template);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Handle other types of objects, such as TrackRequest, normally
                    super.encode(item, bodyType, template);
                }
            }
        } else {
            // For non-list objects, use the default encoding behavior
            super.encode(object, bodyType, template);
        }
    }

    private void encodeMultipartFile(MultipartFile file, RequestTemplate template) throws IOException {
        // Convert MultipartFile to byte array
        byte[] fileBytes = file.getBytes();

        // Creating FormData with the required fields
        FormData formData = new FormData(
                file.getContentType(), // Content type of the file
                file.getOriginalFilename(), // Original file name
                fileBytes // File data as byte array
        );

        // Convert FormData to byte[] to add to the RequestTemplate body
        byte[] formDataBytes = toByteArray(formData);

        // Adding the form data as byte[] to the request template
        template.body(formDataBytes, StandardCharsets.UTF_8);
    }

    private byte[] toByteArray(FormData formData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Write form data properties as key-value pairs
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"; // Boundary used in multipart/form-data
        byteArrayOutputStream.write(("--" + boundary + "\r\n").getBytes());
        byteArrayOutputStream.write(("Content-Disposition: form-data; name=\"" + formData.getFileName() + "\"; filename=\"" + formData.getFileName() + "\"\r\n").getBytes());
        byteArrayOutputStream.write(("Content-Type: " + formData.getContentType() + "\r\n\r\n").getBytes());

        // Write file bytes to the output stream
        byteArrayOutputStream.write(formData.getData());
        byteArrayOutputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

        return byteArrayOutputStream.toByteArray();
    }
}