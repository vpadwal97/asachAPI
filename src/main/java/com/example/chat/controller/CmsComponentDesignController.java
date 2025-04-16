package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import com.example.chat.model.CmsComponentDesign;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/CmsComponentDesignController")
@CrossOrigin
public class CmsComponentDesignController {

    private static final String FILE_PATH = "databaseJsons/CmsComponentDesignController-data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestPart("form") CmsComponentDesign form,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        if (form.getComponentName().isBlank() || form.getComponentCode().isBlank() || form.getComponentType().isBlank()) {
            return ResponseEntity.badRequest().body("Required fields are missing.");
        }

        form.setId(UUID.randomUUID().toString());
        form.setTimestamp(LocalDateTime.now().toString());

        return createToFile(form);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> edit(
            @RequestPart("form") CmsComponentDesign updatedForm,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("No data found.");
        }

        try {
            List<CmsComponentDesign> formList = readFormList(file);
            CmsComponentDesign updatedResult = null;

            for (int i = 0; i < formList.size(); i++) {
                CmsComponentDesign existingForm = formList.get(i);
                if (existingForm.getId().equals(updatedForm.getId())) {
                    existingForm.setComponentName(updatedForm.getComponentName());
                    existingForm.setComponentCode(updatedForm.getComponentCode());
                    existingForm.setComponentType(updatedForm.getComponentType());
                    existingForm.setIsActive(updatedForm.getIsActive());
                    existingForm.setTimestamp(LocalDateTime.now().toString());

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

    @PutMapping("/reorder")
    public ResponseEntity<?> reorder(@RequestBody List<CmsComponentDesign> orderedCmsComponentDesignController) {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("No data found.");
        }

        try {
            List<CmsComponentDesign> currentList = readFormList(file);
            List<CmsComponentDesign> reorderedList = new ArrayList<>();

            for (CmsComponentDesign form : orderedCmsComponentDesignController) {
                currentList.stream()
                        .filter(f -> f.getId().equals(form.getId()))
                        .findFirst()
                        .ifPresent(reorderedList::add);
            }

            objectMapper.writeValue(file, reorderedList);
            return ResponseEntity.ok(reorderedList);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reordering cmsComponentDesignController.");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCmsComponentDesignController() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return ResponseEntity.ok(new ArrayList<>());

        try {
            List<CmsComponentDesign> cmsComponentDesignController = readFormList(file);
            return ResponseEntity.ok(cmsComponentDesignController);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading cmsComponentDesignController.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return ResponseEntity.badRequest().body("No data found.");

        try {
            List<CmsComponentDesign> formList = readFormList(file);
            Iterator<CmsComponentDesign> iterator = formList.iterator();
            boolean removed = false;

            while (iterator.hasNext()) {
                CmsComponentDesign form = iterator.next();
                if (form.getId().equals(id)) {

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

    private ResponseEntity<?> createToFile(CmsComponentDesign form) {
        File file = new File(FILE_PATH);
        File folder = file.getParentFile();

        if (!folder.exists())
            folder.mkdirs();

        try {
            List<CmsComponentDesign> formList;
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

    private List<CmsComponentDesign> readFormList(File file) throws IOException {
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, CmsComponentDesign.class));
    }
}
