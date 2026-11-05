package com.coungard.univer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Платформа, с которой оставлен отзыв. В JSON — строчными буквами, как их передаёт мобильный клиент
 * ({@code Platform.OS} в React Native); в БД — именем константы.
 */
public enum FeedbackPlatform {

  @JsonProperty("ios")
  IOS,

  @JsonProperty("android")
  ANDROID
}
