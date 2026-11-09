package com.coungard.univer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Фоновые задачи по расписанию: уборка медиа-файлов ({@code MediaSweeper}, issue #137). */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
