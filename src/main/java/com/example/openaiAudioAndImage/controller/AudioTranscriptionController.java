
package com.example.openaiAudioAndImage.controller;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/audio")
public class AudioTranscriptionController {

    private final OpenAiAudioTranscriptionModel model;

    public AudioTranscriptionController(
            OpenAiAudioTranscriptionModel model) {
        this.model = model;
    }

    @PostMapping(
            value = "/transcribe",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<byte[]> transcribe(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        var resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        var prompt = new AudioTranscriptionPrompt(resource);
        String vtt = model.call(prompt).getResult().getOutput();

        String filename = file.getOriginalFilename()
                .replaceFirst("(?i)\\.mp3$", "") + ".vtt";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/vtt"))
                .header("Content-Disposition",
                        "attachment; filename=\"transcription.vtt\"")
                .body(vtt.getBytes(StandardCharsets.UTF_8));
    }
}

