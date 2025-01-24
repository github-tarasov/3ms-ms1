package org.ms.ms1.controller;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ms.dto.Message;
import org.ms.ms1.service.MS1Service;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
public class MS1Controller {

    private final MS1Service ms1Service;

    @GetMapping("/start")
    @WithSpan("Start interaction")
    @Operation(summary = "Начать цикл взаимодействия")
    public void start() {
        log.debug("[GET] /start");
        ms1Service.start();
    }

    @GetMapping("/stop")
    @WithSpan("Stop interaction")
    @Operation(summary = "Остановить цикл (все запущенные циклы) взаимодействия")
    public void stop() {
        log.debug("[GET] /stop");
        ms1Service.stop();
    }

    @PostMapping("/store")
    @WithSpan("Store interaction")
    @Operation(summary = "Сохранить и продолжить цикл взаимодействия")
    public void store(@RequestBody @SpanAttribute Message message) {
        log.debug("[POST] /store: {}", message);
        ms1Service.store(message);
    }
}