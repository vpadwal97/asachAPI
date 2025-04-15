package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.model.PersonForm;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/form")
@CrossOrigin // Allow access from the frontend
public class FormController {

    private static final String FILE_PATH = "databaseJsons/form-data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // POST: Save form data and append to list in JSON file
    @PostMapping("/saveForm")
    public String saveForm(@RequestBody PersonForm form) {
        System.out.println("Received raw request: " + form);

        // DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        // String isoTime = LocalDateTime.now().format(formatter);
        // form.setTimestamp(isoTime);

        form.setTimestamp(LocalDateTime.now().toString());
        form.setId(UUID.randomUUID().toString()); // Generate unique ID

        File file = new File(FILE_PATH);
        File folder = file.getParentFile(); // Get folder (databaseJsons)

        if (!folder.exists()) {
            folder.mkdirs(); // Create folder if missing
        }

        try {
            // Read existing data from the file (if exists)
            List<PersonForm> formList;
            if (file.exists()) {
                formList = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, PersonForm.class));
            } else {
                formList = new ArrayList<>();
            }

            // Add the new form to the list
            formList.add(form);

            // Write the updated list back to the file
            objectMapper.writeValue(file, formList);

            return "Form saved successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error saving form.";
        }
    }

    // GET: Retrieve all saved form submissions
    @GetMapping("/getSaveFormData")
    public List<PersonForm> getForms() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return null; // No forms saved yet

        try {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PersonForm.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
