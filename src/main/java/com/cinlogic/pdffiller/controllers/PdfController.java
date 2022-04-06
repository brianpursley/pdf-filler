/*
Copyright 2020 Brian Pursley

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.cinlogic.pdffiller.controllers;

import com.cinlogic.pdffiller.controllers.requests.FieldRequest;
import com.cinlogic.pdffiller.controllers.requests.FillRequest;
import com.cinlogic.pdffiller.entities.PdfDocument;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@Controller
public class PdfController {

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public ResponseEntity<byte[]> fill(@RequestBody FillRequest request) throws IOException {
        var fieldErrors = new ArrayList<String>();
        try (var pdf = new PdfDocument(request.getUrl())) {
            for (var name : request.getValues().keySet()) {
                try {
                    pdf.setFieldValue(name, request.getValues().get(name));
                } catch (Exception e) {
                    fieldErrors.add(e.getMessage());
                }
            }
            if (!fieldErrors.isEmpty()) {
                throw new IOException(String.join("\n", fieldErrors));
            }
            var filename = FilenameUtils.getName(request.getUrl());
            return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + filename).body(pdf.getBytes());
        }
    }

    @PostMapping(
            path = "/template",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<FillRequest> template(@RequestBody FieldRequest request) throws IOException {
        try (var pdf = new PdfDocument(request.getUrl())) {
            return ResponseEntity.ok(new FillRequest(){{
                setUrl(request.getUrl());
                setValues(pdf.getFields());
            }});
        }
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }

}
