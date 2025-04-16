package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.chat.model.PersonForm;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/form")
@CrossOrigin
public class FormController {

    private static final String FILE_PATH = "databaseJsons/form-data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/saveForm")
    public ResponseEntity<?> saveForm(
            @RequestPart("form") PersonForm form,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        if (form.getFname().isBlank() || form.getLname().isBlank() || form.getDob().isBlank()
                || form.getGender().isBlank()) {
            return ResponseEntity.badRequest().body("Required fields are missing.");
        }

        form.setId(UUID.randomUUID().toString());
        form.setTimestamp(LocalDateTime.now().toString());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imagePath = saveImage(imageFile);
                form.setImagePath(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image.");
            }
        }

        return saveFormToFile(form);
    }

    @PutMapping("/editForm")
    public ResponseEntity<?> editForm(
            @RequestPart("form") PersonForm updatedForm,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("No data found.");
        }

        try {
            List<PersonForm> formList = readFormList(file);
            PersonForm updatedResult = null;

            for (int i = 0; i < formList.size(); i++) {
                PersonForm existingForm = formList.get(i);
                if (existingForm.getId().equals(updatedForm.getId())) {
                    existingForm.setFname(updatedForm.getFname());
                    existingForm.setMname(updatedForm.getMname());
                    existingForm.setLname(updatedForm.getLname());
                    existingForm.setDob(updatedForm.getDob());
                    existingForm.setGender(updatedForm.getGender());
                    existingForm.setTimestamp(LocalDateTime.now().toString());

                    if (imageFile != null && !imageFile.isEmpty()) {
                        // Delete old image
                        if (existingForm.getImagePath() != null) {
                            File oldImageFile = new File("uploads" + existingForm.getImagePath());
                            if (oldImageFile.exists())
                                oldImageFile.delete();
                        }

                        String imagePath = saveImage(imageFile);
                        existingForm.setImagePath(imagePath);
                    }

                    formList.set(i, existingForm);
                    updatedResult = existingForm;
                    break;
                }
            }

            if (updatedResult != null) {
                objectMapper.writeValue(file, formList);
                // return ResponseEntity.ok("Form updated successfully!");
                return ResponseEntity.ok(updatedResult);

            } else {
                return ResponseEntity.badRequest().body("Form with ID not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating form.");
        }
    }

    @PutMapping("/reorderForms")
    public ResponseEntity<?> reorderForms(@RequestBody List<PersonForm> orderedForms) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("No data found.");
        }

        try {
            List<PersonForm> currentList = readFormList(file);
            List<PersonForm> reorderedList = new ArrayList<>();

            for (PersonForm form : orderedForms) {
                currentList.stream()
                        .filter(f -> f.getId().equals(form.getId()))
                        .findFirst()
                        .ifPresent(reorderedList::add);
            }

            objectMapper.writeValue(file, reorderedList);
            return ResponseEntity.ok(reorderedList);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reordering forms.");
        }
    }

    @GetMapping("/getSaveFormData")
    public ResponseEntity<?> getForms() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return ResponseEntity.ok(new ArrayList<>());

        try {
            List<PersonForm> forms = readFormList(file);
            return ResponseEntity.ok(forms);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading forms.");
        }
    }

    @DeleteMapping("/deleteForm/{id}")
    public ResponseEntity<?> deleteForm(@PathVariable String id) {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return ResponseEntity.badRequest().body("No data found.");

        try {
            List<PersonForm> formList = readFormList(file);
            Iterator<PersonForm> iterator = formList.iterator();
            boolean removed = false;

            while (iterator.hasNext()) {
                PersonForm form = iterator.next();
                if (form.getId().equals(id)) {
                    if (form.getImagePath() != null) {
                        File image = new File("uploads" + form.getImagePath());
                        if (image.exists())
                            image.delete();
                    }

                    iterator.remove();
                    removed = true;
                    break;
                }
            }

            if (removed) {
                objectMapper.writeValue(file, formList);
                return ResponseEntity.ok("Form deleted successfully!");
            } else {
                return ResponseEntity.badRequest().body("Form with ID not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting form.");
        }
    }

    private ResponseEntity<?> saveFormToFile(PersonForm form) {
        File file = new File(FILE_PATH);
        File folder = file.getParentFile();

        if (!folder.exists())
            folder.mkdirs();

        try {
            List<PersonForm> formList;
            if (file.exists()) {
                formList = readFormList(file);
            } else {
                formList = new ArrayList<>();
            }

            formList.add(form);
            objectMapper.writeValue(file, formList);
            return ResponseEntity.ok(form);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving form.");
        }
    }

    private List<PersonForm> readFormList(File file) throws IOException {
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PersonForm.class));
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String month = String.format("%02d", LocalDateTime.now().getMonthValue());
        String uploadDir = "uploads/images/forms/" + year + "/" + month;
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists())
            uploadPath.mkdirs();

        String extension = Objects.requireNonNull(imageFile.getOriginalFilename())
                .substring(imageFile.getOriginalFilename().lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;
        String fullPath = uploadDir + "/" + filename;

        Files.copy(imageFile.getInputStream(), Paths.get(fullPath), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/images/forms/" + year + "/" + month + "/" + filename;
    }
}
