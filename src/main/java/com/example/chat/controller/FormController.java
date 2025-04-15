package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // PUT: Edit existing form by ID
    @PutMapping("/editForm")
    public String editForm(@RequestBody PersonForm updatedForm) {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return "No data found.";

        try {
            List<PersonForm> formList = readFormList(file);
            boolean updated = false;

            for (int i = 0; i < formList.size(); i++) {
                if (formList.get(i).getId().equals(updatedForm.getId())) {
                    updatedForm.setTimestamp(LocalDateTime.now().toString()); // Update timestamp
                    formList.set(i, updatedForm);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                objectMapper.writeValue(file, formList);
                return "Form updated successfully!";
            } else {
                return "Form with ID not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error updating form.";
        }
    }

    // DELETE: Remove form by ID
    @DeleteMapping("/deleteForm/{id}")
    public String deleteForm(@PathVariable String id) {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return "No data found.";

        try {
            List<PersonForm> formList = readFormList(file);
            Iterator<PersonForm> iterator = formList.iterator();
            boolean removed = false;

            while (iterator.hasNext()) {
                PersonForm form = iterator.next();
                if (form.getId().equals(id)) {
                    iterator.remove();
                    removed = true;
                    break;
                }
            }

            if (removed) {
                objectMapper.writeValue(file, formList);
                return "Form deleted successfully!";
            } else {
                return "Form with ID not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error deleting form.";
        }
    }

    // Helper to read JSON data
    private List<PersonForm> readFormList(File file) throws IOException {
        if (file.exists()) {
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PersonForm.class));
        } else {
            return new ArrayList<>();
        }
    }
}
