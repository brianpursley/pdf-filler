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

package com.cinlogic.pdffiller.entities;

import com.cinlogic.pdffiller.exceptions.FieldNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;

public class PdfDocument implements Closeable {

    private final PDDocument doc;
    private final PDAcroForm form;

    public PdfDocument(String url) throws IOException {
        this(new URL(url).openStream());
    }

    public PdfDocument(InputStream pdfStream) throws IOException {
        doc = PDDocument.load(pdfStream);
        form = doc.getDocumentCatalog().getAcroForm();
    }

    public LinkedHashMap<String, Object> getFields() {
        var iter = doc.getDocumentCatalog().getAcroForm().getFieldTree().iterator();
        var result = new LinkedHashMap<String, Object>();
        while (iter.hasNext()) {
            var field = iter.next();
            if (!field.isReadOnly() && field.getFieldType() != null) {
                result.put(field.getFullyQualifiedName(), field.getValueAsString());
            }
        }
        return result;
    }

    public byte[] getBytes() throws IOException {
        try (var os = new ByteArrayOutputStream()) {
            doc.save(os);
            return os.toByteArray();
        }
    }

    public void setFieldValue(String name, Object value) throws IOException {
        var field = form.getField(name);
        if (field == null) {
            throw new FieldNotFoundException(name);
        }
        field.setValue(value == null ? "" : value.toString());
    }

    @Override
    public void close() throws IOException {
        doc.close();
    }
}
